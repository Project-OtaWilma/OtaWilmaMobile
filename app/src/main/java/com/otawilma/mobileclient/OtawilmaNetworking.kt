package com.otawilma.mobileclient

import android.util.Log
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.otawilma.mobileclient.dataClasses.ScheduleItem
import com.otawilma.mobileclient.parsesrs.LessonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate

interface OtawilmaNetworking:LessonParser {


    // Returns if the Otawilma-server can be reached
    suspend fun testToken(token : String) : Boolean{
        val request = Request.Builder().url("$OTAWILMA_API_URL/authenticate").header("token",token).post("".toRequestBody()).build()
        client.newCall(request).execute().use {
            return it.isSuccessful
        }

    }

    // Returns if Otawilma can reach the wilma-server
    suspend fun pingWilma():Boolean{
        return false
    }

    // Passes the userName and password as a request and expects back a success and a token
    suspend fun login (userName:String, password:String):Pair<Boolean,String>{

        // Create a JSON object for the user
        val user=JSONObject()
        user.put("username",userName)
        user.put("password",password)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody:RequestBody= user.toString().toRequestBody(mediaType)
        val request = Request.Builder().url("$OTAWILMA_API_URL/login").post(requestBody).build()

        client.newCall(request).execute().use {
            //Log.d("Networking",it.body!!.string())
            if (it.isSuccessful) {
                val token = Gson().fromJson<Map<String,String>>(it.body!!.string(), Map::class.java)["token"]
                return Pair(true,token!!)
            }
            return Pair(false,"THE FUCK YOU EXPECT HERE TO BE? A FUCKING TOKEN?")
        }


    }

    suspend fun logout (token: String) : Boolean{
        val request = Request.Builder().url("$OTAWILMA_API_URL/logout").header("token",token).build()

        client.newCall(request).execute().use {
            return it.isSuccessful
        }
    }

    // Return the schedule for a week in the given date
    suspend fun getScheduleOfAWeek(date:LocalDate): Pair<Boolean,List<ScheduleItem>>{
        val dateString = "${date.month}-${date.dayOfMonth}-${date.year}"
        val request = Request.Builder().url("$OTAWILMA_API_URL/schedule/week/$dateString").header("token",
            tokenGlobal).build()

        client.newCall(request).execute().use {
            if (!it.isSuccessful){
                return Pair(false, listOf())
            }
            val body = it.body ?: return Pair(false, listOf())
            val bodyString = body.string()
            Log.d("Networking",bodyString)


            /* The keys are the days and the body look as follows:

             "2022-12-04": {
            "day": { // contains the informatiom about the specific day
                "date": 0, // index of the day. Sunday is '0' and Saturday '6'.
                "caption": "Su 4.12", // Caption of the day in [Ww dd.mm] format
                "full": "Sunnuntai 2022-12-04" // Full caption of the day [] [Ww yyyy-mm-dd] format
            },
            "lessons": [], // List of lessons during this day
            "exams": [] // LIst of exams marked for this day
        },

             */
            val dayListJson =
                Gson().fromJson<Map<String,LinkedTreeMap<String,Any>>>(bodyString, Map::class.java)["days"]?.let { it1 ->
                    JSONObject(
                        it1
                    )
                }
            val lessonList = parseSlotToLesson(dayListJson)
            return Pair(true,lessonList)
        }
    }
}
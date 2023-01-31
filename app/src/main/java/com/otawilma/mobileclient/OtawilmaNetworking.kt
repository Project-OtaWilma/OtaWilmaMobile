package com.otawilma.mobileclient

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.otawilma.mobileclient.activities.LoginActivity
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.parsesrs.LessonParser
import com.otawilma.mobileclient.parsesrs.MessageParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

interface OtawilmaNetworking : LessonParser, MessageParser {

    fun handleInvalidToken(context: Context){
        Toast.makeText(context, "Not possible to get token so please log in:", Toast.LENGTH_SHORT).show()
        startActivity(context, Intent(context, LoginActivity::class.java),null)
    }

    suspend fun getToken() : String? {

        // If we have a token, then very good
        if (tokenGlobal != null) return tokenGlobal!!

        if (testToken(encryptedPreferenceStorage.otaWilmaToken)) return encryptedPreferenceStorage.otaWilmaToken

        // If we can automatically Login, then still good
        val userName = encryptedPreferenceStorage.userName
        val pwd = encryptedPreferenceStorage.passWord
        if (sharedPreferences.autoLogin && userName != null && pwd != null) {
            Log.d("RequestTracking", "Another Login request")
            tokenGlobal = login(userName, pwd)
            encryptedPreferenceStorage.otaWilmaToken = tokenGlobal
            return tokenGlobal
        }
        // If we cant, then shit
        return null
    }

    // Returns if the Otawilma-server can be reached
    suspend fun testToken(token : String?) : Boolean{
        if (token == null) return false
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
    suspend fun login (userName:String, password:String) : String?{

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
                return Gson().fromJson<Map<String, String>>(
                    it.body!!.string(),
                    Map::class.java
                )["token"]
            }
            return null
        }


    }

    suspend fun logout (token: String) : Boolean{
        val request = Request.Builder().url("$OTAWILMA_API_URL/logout").header("token",token).build()

        client.newCall(request).execute().use {
            return it.isSuccessful
        }
    }

    // Return the schedule for a week in the given date
    suspend fun getScheduleOfAWeek(token : String,date:LocalDate): List<SchoolDay>?{
        val dateString = "${date.month}-${date.dayOfMonth}-${date.year}"
        val request = Request.Builder().url("$OTAWILMA_API_URL/schedule/week/$dateString").header("token",
            token).build()

        client.newCall(request).execute().use {
            if (!it.isSuccessful) {
                return null
            }
            val body = it.body ?: return null
            val bodyString = body.string()
            Log.d("Networking", bodyString)


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
                Gson().fromJson<Map<String, LinkedTreeMap<String, Any>>>(
                    bodyString,
                    Map::class.java
                )["days"]?.let { it1 ->
                    JSONObject(
                        it1
                    )
                }
            return parseSlotToLesson(dayListJson)
        }
    }

    // Find all of your new messages and returns them in the message format without bodies
    //suspend fun getNewMessages(limit: Int) : List<MessageItem>

    // Get messages from the latest to until
    suspend fun getMessages(token : String, until : Int) : Pair<Boolean,List<Message>>{
        val request = Request.Builder().url("$OTAWILMA_API_URL/messages/inbox?limit=$until").header("token", token).build()
        client.newCall(request).execute().use {

            // If it fails
            if (!it.isSuccessful) return Pair(false, emptyList())

            // If the body is empty then you must be very new
            val body = it.body?.string() ?: return Pair(true, emptyList())

            Log.d("Networking", body)

            return Pair(true, parseMessageList(JSONArray(body)))

        }
    }

    // Get appointments from the latest to until
    //suspend fun getAppointments(until: Int) : List<Appointment>


    // Get the message body
    suspend fun getMessageBody(token : String, message: Message) : Message? {
        val request = Request.Builder().url("$OTAWILMA_API_URL/messages/${message.id}").header("token", token).build()

        client.newCall(request).execute().use {
            if (!it.isSuccessful) return null
            val body = it.body?.string() ?: return null

            return makeMessageGoToGym(JSONArray(body)[0] as JSONObject, message)
        }
    }
}
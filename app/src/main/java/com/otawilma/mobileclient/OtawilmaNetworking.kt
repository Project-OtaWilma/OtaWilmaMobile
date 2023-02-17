package com.otawilma.mobileclient

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.otawilma.mobileclient.activities.LoginActivity
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.parsesrs.LessonParser
import com.otawilma.mobileclient.parsesrs.MessageParser
import com.otawilma.mobileclient.storage.EncryptedPreferenceStorage
import com.otawilma.mobileclient.storage.PreferenceStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate

interface OtawilmaNetworking : LessonParser, MessageParser {

    suspend fun repeatUntilSuccess(context : Context, token: String, handle : suspend (String) -> Unit){
        var token = token
        while (true) {
            try {
                handle(token)
                break
            } catch (e : Exception){
                when (e){
                    is InvalidTokenNetworkException -> token = invalidateTokenAndGetNew(context)
                    is UnknownHostException, is SocketTimeoutException -> delay(100)
                    is OtaWilmaDownException -> delay(100)
                    else -> throw e
                }
            }
        }
    }

    suspend fun handleInvalidToken(context: Context) : String{
        CoroutineScope(Dispatchers.Main).launch{
            Toast.makeText(context, "Not possible to get token so please log in:", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        while (true){
            try {return getToken(context)}
                catch (_: NoStoredTokenException){
                delay(10)
            }
        }


    }

    suspend fun invalidateTokenAndGetNew(context: Context) : String {
        val encryptedPreferenceStorage = EncryptedPreferenceStorage(context)
        tokenGlobal = null
        encryptedPreferenceStorage.otaWilmaToken = null
        return waitUntilToken(context)
    }

    suspend fun waitUntilToken(context: Context) : String{
        while (true){
            try {return getToken(context)}
            catch (e: Exception){
                when(e){
                    is NoStoredTokenException -> return handleInvalidToken(context)
                    is OtaWilmaDownException, is WilmaDownException, is SocketTimeoutException-> delay(100)
                    else -> throw e
                }
            }
        }
    }

    // Returns a token or gets one from the server
    suspend fun getToken(context: Context) : String {

        val encryptedPreferenceStorage = EncryptedPreferenceStorage(context)
        val sharedPreferences = PreferenceStorage(context)

        // If we have a token, then very good
        if (tokenGlobal != null) return tokenGlobal!!

        // If we have a stored token, then still fairly good
        val temp = encryptedPreferenceStorage.otaWilmaToken
        if (temp != null) {
            tokenGlobal = temp
            return temp
        }

        // If we can automatically Login, then still good
        val userName = encryptedPreferenceStorage.userName
        val pwd = encryptedPreferenceStorage.passWord
        if (sharedPreferences.autoLogin && userName != null && pwd != null) {
            Log.d("RequestTracking", "Another Login request")
            tokenGlobal = login(userName, pwd)
            encryptedPreferenceStorage.otaWilmaToken = tokenGlobal
            return tokenGlobal!!
        }
        // If we cant, then shit
        throw NoStoredTokenException("No token exists and one cannot be retried")
    }

    fun checkCode(code : Int){
        when (code) {
            200 -> return
            404, 500 -> throw OtaWilmaDownException("Otawilma is down")
            501 -> throw WilmaDownException("Wilma is down")
            401 -> throw InvalidTokenNetworkException("The token is invalid")
            429 -> throw RateLimitException("Rate-limit has been exceeded")
            else -> throw Exception("Received unexpected code from server code: $code")

        }
    }

    // Returns if the Otawilma-server can be reached and the token is valid
    suspend fun testToken(token : String?) : Boolean {
        if (token == null) return false
        val request = Request.Builder().url("$OTAWILMA_API_URL/authenticate").header("token",token).post("".toRequestBody()).build()
        client.newCall(request).execute().use {
            if (it.isSuccessful){
                return true
            } else {
                Log.d("Networking", "The request was no successful: ${it.message} with the code of ${it.code} ")
                return false
            }
        }
    }

    // Returns if Otawilma can reach the wilma-server
    suspend fun pingWilma() : Boolean{
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
            checkCode(it.code)
            return Gson().fromJson<Map<String, String>>(
                it.body!!.string(),
                Map::class.java
            )["token"]
        }
    }

    suspend fun logout (token: String){
        val request = Request.Builder().url("$OTAWILMA_API_URL/logout").post("".toRequestBody()).header("token",token).build()

        client.newCall(request).execute().use {
            checkCode(it.code)
        }
    }

    // Return the schedule for a week in the given date
    suspend fun getScheduleOfAWeek(token : String,date:LocalDate): List<SchoolDay>?{
        val dateString = "${date.month}-${date.dayOfMonth}-${date.year}"
        val request = Request.Builder().url("$OTAWILMA_API_URL/schedule/week/$dateString").header("token",
            token).build()

        client.newCall(request).execute().use {
            checkCode(it.code)
            val body = it.body ?: return null
            val bodyString = body.string()
            Log.d("Networking", bodyString)


            /* The keys are the days and the body look as follows:

             "2022-12-04": {
            "day": { // contains the information about the specific day
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

    // Get messages from the latest to until
    suspend fun getMessages(token : String, until : Int) : List<Message>{
        val request = Request.Builder().url("$OTAWILMA_API_URL/messages/inbox?limit=$until").header("token", token).build()
        client.newCall(request).execute().use {

            // If it fails
            checkCode(it.code)

            // If the body is empty then you must be very new
            val body = it.body?.string() ?: return emptyList()

            Log.d("Networking", body)

            return parseMessageList(JSONArray(body))

        }
    }

    // Get appointments from the latest to until
    //suspend fun getAppointments(until: Int) : List<Appointment>

    // Get the message body
    suspend fun getMessageBody(token : String, message: Message) : Message? {
        val request = Request.Builder().url("$OTAWILMA_API_URL/messages/${message.id}").header("token", token).build()

        client.newCall(request).execute().use {
            checkCode(it.code)
            val body = it.body?.string() ?: return null
            return getMessageBody(JSONArray(body)[0] as JSONObject, message)
        }
    }
}
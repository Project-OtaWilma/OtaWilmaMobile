package com.otawilma.mobileclient.parsesrs

import android.util.Log
import com.otawilma.mobileclient.dataClasses.ClassRoom
import com.otawilma.mobileclient.dataClasses.Lesson
import com.otawilma.mobileclient.dataClasses.Teacher
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

interface LessonParser {

    fun parseSlotToLesson(dayListJson: JSONObject?):List<Lesson>{
        val lessonMutableList = mutableListOf<Lesson>()
        if (dayListJson==null) {
            throw Exception("You passed a null object to parseSlotToLesson")
        }
        Log.d("Parsing", "Parsing $dayListJson")


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

        dayListJson.keys().forEach {

            // Get the date for later
            val date=LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // Get a list of the active timeSlots for the given date
            val timeSlotJsonArray = (dayListJson[it]as JSONObject)["lessons"] as JSONArray
            for (timeSlotIndex in 0 until timeSlotJsonArray.length()){

                // Get the data about the timeslots
                val timeSlot = timeSlotJsonArray[timeSlotIndex] as JSONObject
                val startTime = LocalTime.parse(timeSlot["start"] as String)
                val endTime = LocalTime.parse(timeSlot["end"] as String)

                // Get and iterate the list of groups for the given timeslot
                val groupsList  = timeSlot["groups"] as JSONArray

                for (groupListIndex in 0 until groupsList.length()){
                    val group = groupsList[groupListIndex] as JSONObject
                    val courseCode = group["code"] as String
                    val courseName = group["name"] as String

                    val teacherJSONArray = group["teachers"] as JSONArray
                    val teacherMutableList = mutableListOf<Teacher>()
                    val classRoomJSONArray = group["rooms"] as JSONArray
                    val classRoomMutableList = mutableListOf<ClassRoom>()

                    for (teacherJSONArrayIndex in 0 until teacherJSONArray.length()){
                        val teacher = teacherJSONArray[teacherJSONArrayIndex] as JSONObject
                        val teacherName = teacher["name"] as String
                        val teacherId= (teacher["id"] as Double).toInt()
                        val teacherCaption = teacher["caption"] as String
                        teacherMutableList.add(Teacher(teacherId,teacherCaption,teacherName))
                    }

                    for (classRoomJSONArrayIndex in 0 until classRoomJSONArray.length()){
                        val classRoom = classRoomJSONArray[classRoomJSONArrayIndex] as JSONObject
                        val classRoomName = classRoom["name"] as String
                        val classRoomId= (classRoom["id"] as Double).toInt()
                        val classRoomCaption = classRoom["caption"] as String
                        classRoomMutableList.add(ClassRoom(classRoomId,classRoomCaption,classRoomName))
                    }
                    lessonMutableList.add(
                        Lesson(
                        courseCode,
                        courseName,
                        startTime,
                        endTime,
                        classRoomMutableList,
                        teacherMutableList,
                        date

                    )
                    )

                }

                Log.d("Parsing","Parsed lesson $startTime and $endTime")
            }

        }

        return lessonMutableList.toList()
        }
}
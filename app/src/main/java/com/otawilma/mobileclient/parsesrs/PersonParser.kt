package com.otawilma.mobileclient.parsesrs

import com.otawilma.mobileclient.dataClasses.Person
import com.otawilma.mobileclient.dataClasses.Personnel
import com.otawilma.mobileclient.dataClasses.Student
import com.otawilma.mobileclient.dataClasses.Teacher
import org.json.JSONArray
import org.json.JSONObject

interface PersonParser {

    fun parsePersonList(personJSONArray: JSONArray) : List<Person>{
        val personMutableList : MutableList<Person> = mutableListOf()
        for (index in 0 until personJSONArray.length()){
            personMutableList.add(parsePerson(personJSONArray[index] as JSONObject))
        }

        return personMutableList
    }

    fun parsePerson(personJSONObject: JSONObject) : Person{
        val name = personJSONObject["name"] as String
        val href = personJSONObject["href"] as String

        with(href){
            when {
                contains("teachers") -> {
                    val namePair = splitStaffName(name)
                    val id = split("/").last().toInt()
                    return Teacher(namePair.first, id, namePair.second)
                }
                contains("personnel") -> {
                    val namePair = splitStaffName(name)
                    val id = split("/").last().toInt()
                    return Personnel(namePair.first, id, namePair.second)
                }
                else -> {
                    val namePair = splitStudentName(name)
                    return Student(namePair.first, namePair.second)
                }
            }
        }
    }

    // Returns Pair<Name, Caption>
    fun splitStaffName(teacherNameString: String) : Pair<String,String>{
        val splitString = teacherNameString.split("(")
        val name = splitString[0].trim()
        val caption = splitString[1].removeSuffix(")").trim()
        return Pair(name,caption)
    }

    // Returns Pair<Name, Group>
    fun splitStudentName(studentNameString: String) : Pair<String, String>{
        val splitString = studentNameString.split(",")
        val name = splitString[0]
        val group = splitString[1]
        return Pair(name, group)

    }
}
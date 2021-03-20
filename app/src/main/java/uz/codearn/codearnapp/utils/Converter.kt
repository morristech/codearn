package uz.codearn.codearnapp.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {

    @TypeConverter
    fun fromQuestionOptionsList(value: List<String>): String {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toQuestionOptionsList(value: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
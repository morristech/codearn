package uz.codearn.codearnapp.model

import androidx.room.TypeConverters
import uz.codearn.codearnapp.utils.ListConverter

@TypeConverters(ListConverter::class)
data class Question(
    val correctAnswer: Int = 0,
    val programmingLanguage: String = "",
    val questionOptions: List<String> = listOf(),
    val questionText: String = "",
    val addedDate: Long? = null
)

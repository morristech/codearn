package uz.codearn.codearnapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import uz.codearn.codearnapp.model.CodingPath
import uz.codearn.codearnapp.model.Question
import uz.codearn.codearnapp.utils.ListConverter

@TypeConverters(ListConverter::class)
@Entity
data class DatabaseQuestion constructor(
    @PrimaryKey
    var questionText: String = "",
    var correctAnswer: Int = 0,
    var programmingLanguage: String = "",
    var questionOptions: List<String> = listOf(),
    var addedDate: Long? = null
)

@Entity
data class DatabaseCodingPath constructor(
    @PrimaryKey
    var name: String = "",
    var cost: Int = 0,
    var description: String = "",
    var imgUrl: String = "",
    var available: Boolean = true,
    var free: Boolean = true,
    var new: Boolean = true,
    var order: Int = 0
)

fun List<DatabaseQuestion>.asQuestionDomainModel(): List<Question> {
    return map {
        Question(
            it.correctAnswer,
            it.programmingLanguage,
            it.questionOptions,
            it.questionText,
            it.addedDate
        )
    }
}

fun List<DatabaseCodingPath>.asCodingPathDomainModel(): List<CodingPath> {
    return map {
        CodingPath(
            it.cost, it.description, it.imgUrl, it.available, it.free, it.new, it.name, it.order
        )
    }
}

fun List<Question>.asQuestionDatabaseModel(): Array<DatabaseQuestion> {
    return map {
        DatabaseQuestion(
            it.questionText,
            it.correctAnswer,
            it.programmingLanguage,
            it.questionOptions,
            it.addedDate
        )
    }.toTypedArray()
}

fun List<CodingPath>.asCodingPathDatabaseModel(): Array<DatabaseCodingPath> {
    return map {
        DatabaseCodingPath(
            it.name,
            it.cost,
            it.description,
            it.imgUrl,
            it.available,
            it.free,
            it.new,
            it.order
        )
    }.toTypedArray()
}
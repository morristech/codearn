package uz.codearn.codearnapp.model

data class Question(
    val correctAnswer:Int = 0,
    val programmingLanguage:String = "",
    val questionOptions:List<String> = listOf(),
    val questionText:String = "",
    val solvedTimes:Int = 0
)

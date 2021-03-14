package uz.codearn.codearnapp.model

data class CodingPath(
    var cost:Int = 0,
    val description:String = "",
    val imgUrl:String = "",
    val available:Boolean = true,
    var free:Boolean = true,
    val new:Boolean = true,
    val name:String = "",
    var order:Int = 0
)
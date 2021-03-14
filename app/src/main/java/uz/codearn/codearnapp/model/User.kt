package uz.codearn.codearnapp.model

data class User(
    var userId: String = "",
    var phoneNumber:String = "",
    var displayName:String = "Foydalanuvchi",
    var score:Int = 0,
    var profilePhotoIndex:Int = 0,
    var purchasedPaths:List<String> = listOf("Python"),
    var level:String = "Yangi o'rganuvchi"
)
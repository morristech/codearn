package uz.codearn.codearnapp.constants

import uz.codearn.codearnapp.R


object Constants {

    const val refreshDataCounter = "REFRESH_DATA_COUNTER"
    const val refreshQuestionsCount = "REFRESH_QUESTIONS_COUNT"
    const val refreshCodingPathsCount = "REFRESH_CODING_PATHS_COUNT"

    fun getAllProfilePics() = arrayListOf(
        R.drawable.man1,
        R.drawable.man2,
        R.drawable.man3,
        R.drawable.man4,
        R.drawable.man5,
        R.drawable.woman1,
        R.drawable.woman2,
        R.drawable.woman3,
        R.drawable.woman4,
        R.drawable.woman5
    )

}
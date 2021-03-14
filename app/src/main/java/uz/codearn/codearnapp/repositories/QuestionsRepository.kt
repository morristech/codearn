package uz.codearn.codearnapp.repositories

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query.Direction.ASCENDING
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import uz.codearn.codearnapp.model.Question
import java.lang.Exception

class QuestionsRepository(private val questionsRef:CollectionReference) {

    suspend fun getQuestionsByProgrammingLang(programmingLang:String):List<Question>{
        val questionsList = mutableListOf<Question>()
        try {
            val querySnapshot = questionsRef.orderBy("solvedTimes", ASCENDING).whereEqualTo("programmingLanguage",programmingLang).limit(10).get().await()
            for (document in querySnapshot){
                document.toObject<Question>().let {
                    questionsList.add(it)
                }
            }
        }catch (e:Exception){
            Log.d("QuestionsRepository", "getQuestionsByProgrammingLang: ${e.message}")
        }
        return questionsList
    }

}
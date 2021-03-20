package uz.codearn.codearnapp.repositories

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query.Direction.ASCENDING
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.db.asQuestionDatabaseModel
import uz.codearn.codearnapp.db.question.QuestionsDatabase
import uz.codearn.codearnapp.model.Question

class QuestionsRepository(
    private val database: QuestionsDatabase,
    private val questionsRef: CollectionReference
) {

    suspend fun refreshQuestions() {
        try {
            withContext(Dispatchers.IO) {
                val querySnapshot = questionsRef.orderBy("addedDate", ASCENDING).get().await()
                val listQuestions = arrayListOf<Question>()
                for (document in querySnapshot) {
                    document.toObject<Question>().let {
                        listQuestions.add(it)
                    }
                }
                database.questionDao.insertQuestions(*listQuestions.asQuestionDatabaseModel())
            }
        }catch (e:Exception){
            Log.e("QuestionsRepository", "getQuestionsByProgrammingLang: ${e.message}")
        }
    }

}
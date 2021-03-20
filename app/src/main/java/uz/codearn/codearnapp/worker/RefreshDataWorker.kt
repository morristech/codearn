package uz.codearn.codearnapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.codearn.codearnapp.db.codingpath.getCodingPathsDatabase
import uz.codearn.codearnapp.db.question.getQuestionsDatabase
import uz.codearn.codearnapp.repositories.CodingPathRepository
import uz.codearn.codearnapp.repositories.QuestionsRepository

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val questionsDatabase = getQuestionsDatabase(applicationContext)
        val codingPathsDatabase = getCodingPathsDatabase(applicationContext)
        val codingPathsRepository =
            CodingPathRepository(codingPathsDatabase, Firebase.firestore.collection("codingPaths"))
        val questionsRepository =
            QuestionsRepository(questionsDatabase, Firebase.firestore.collection("questions"))
        return try {
            codingPathsRepository.refreshCodingPaths()
            questionsRepository.refreshQuestions()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
package uz.codearn.codearnapp.db.question

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import uz.codearn.codearnapp.db.DatabaseQuestion
import uz.codearn.codearnapp.model.Question

@Dao
interface QuestionDao {
    @Query("select * from databasequestion")
    fun getQuestions(): LiveData<List<DatabaseQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(vararg questions: DatabaseQuestion)

    @Query("select * from databasequestion where programmingLanguage = :programmingLang order by addedDate desc limit 10")
    suspend fun get10Question(programmingLang: String): List<Question>
}

@Database(entities = [DatabaseQuestion::class], version = 1)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract val questionDao: QuestionDao
}

private lateinit var INSTANCE_QUESTION: QuestionsDatabase

fun getQuestionsDatabase(context: Context): QuestionsDatabase {
    synchronized(QuestionsDatabase::class.java) {
        if (!::INSTANCE_QUESTION.isInitialized) {
            INSTANCE_QUESTION = Room.databaseBuilder(
                context.applicationContext,
                QuestionsDatabase::class.java,
                "questions"
            )
                .build()
        }
    }
    return INSTANCE_QUESTION
}
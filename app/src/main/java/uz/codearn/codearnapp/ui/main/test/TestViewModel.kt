package uz.codearn.codearnapp.ui.main.test

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import uz.codearn.codearnapp.constants.Constants
import uz.codearn.codearnapp.db.question.getQuestionsDatabase
import uz.codearn.codearnapp.model.Question
import uz.codearn.codearnapp.repositories.QuestionsRepository

class TestViewModel(private val programmingLang: String, app: Application) : AndroidViewModel(app) {

    private val questionsRef = Firebase.firestore.collection("questions")
    private val questionsDatabase = getQuestionsDatabase(app.applicationContext)
    private val questionsRepository = QuestionsRepository(questionsDatabase, questionsRef)
    private val sharedPreferences =
        app.getSharedPreferences(Constants.refreshDataCounter, Context.MODE_PRIVATE)

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
        get() = _questions

    init {
        viewModelScope.launch {
            val refreshQuestionsCount = sharedPreferences.getInt(Constants.refreshQuestionsCount, 0)
            val editor = sharedPreferences.edit()
            if (refreshQuestionsCount !in 1..1000) {
                questionsRepository.refreshQuestions()
                editor.putInt(Constants.refreshQuestionsCount, 1)
            } else {
                editor.putInt(Constants.refreshQuestionsCount, (refreshQuestionsCount + 1))
            }
            editor.apply()
            _questions.value = questionsDatabase.questionDao.get10Question(programmingLang)
        }
    }

}

class TestViewModelFactory(private val programmingLang: String, private val app: Application) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            return TestViewModel(programmingLang, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
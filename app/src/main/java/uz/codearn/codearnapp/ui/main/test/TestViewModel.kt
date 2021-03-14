package uz.codearn.codearnapp.ui.main.test

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import uz.codearn.codearnapp.repositories.QuestionsRepository
import uz.codearn.codearnapp.model.Question

class TestViewModel(programmingLang: String) : ViewModel() {

    private val questionsRef = Firebase.firestore.collection("questions")
    private val questionsRepository = QuestionsRepository(questionsRef)

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
        get() = _questions

    init {
        viewModelScope.launch {
            _questions.value = questionsRepository.getQuestionsByProgrammingLang(programmingLang)
        }
    }

}

class TestViewModelFactory(private val programmingLang: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            return TestViewModel(programmingLang) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
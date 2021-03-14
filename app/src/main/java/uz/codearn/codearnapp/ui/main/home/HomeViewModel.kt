package uz.codearn.codearnapp.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import uz.codearn.codearnapp.repositories.CodingPathRepository
import uz.codearn.codearnapp.model.CodingPath

class HomeViewModel : ViewModel() {

    private val codingPathsRef = Firebase.firestore.collection("codingPaths")
    private val repository = CodingPathRepository(codingPathsRef, this)

    private val _codingPaths = MutableLiveData<List<CodingPath>>()
    val codingPaths: LiveData<List<CodingPath>>
        get() = _codingPaths

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        viewModelScope.launch {
            _codingPaths.value = repository.getAllCodingPaths()
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}
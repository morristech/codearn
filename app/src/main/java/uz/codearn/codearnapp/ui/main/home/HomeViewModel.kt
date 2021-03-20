package uz.codearn.codearnapp.ui.main.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import uz.codearn.codearnapp.constants.Constants
import uz.codearn.codearnapp.db.codingpath.getCodingPathsDatabase
import uz.codearn.codearnapp.repositories.CodingPathRepository

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val codingPathsRef = Firebase.firestore.collection("codingPaths")
    private val codingPathsDatabase = getCodingPathsDatabase(app.applicationContext)
    private val repository = CodingPathRepository(codingPathsDatabase, codingPathsRef)
    private val sharedPreferences =
        app.getSharedPreferences(Constants.refreshDataCounter, Context.MODE_PRIVATE)

    init {
        viewModelScope.launch {
            var refreshCodingPathsCount =
                sharedPreferences.getInt(Constants.refreshCodingPathsCount, 0)
            val editor = sharedPreferences.edit()
            if (refreshCodingPathsCount !in 1..1000) {
                repository.refreshCodingPaths()
                editor.putInt(Constants.refreshCodingPathsCount, 1)
            } else {
                refreshCodingPathsCount++
                editor.putInt(Constants.refreshCodingPathsCount, refreshCodingPathsCount)
            }
            editor.apply()
        }
    }

    val codingPaths = repository.codingPaths
}

class HomeViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
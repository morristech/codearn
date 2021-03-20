package uz.codearn.codearnapp.ui.main.profile

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.model.User

class ProfileViewModel(val app: Application) : AndroidViewModel(app) {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                val user = Firebase.auth.currentUser!!
                val userDataQuery =
                    Firebase.firestore.collection("users").whereEqualTo("userId", user.uid).get()
                        .await().documents[0]
                _userData.value = userDataQuery.toObject<User>()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        app,
                        "Internetga ulaning va qayta urinib ko'ring!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

class ProfileViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
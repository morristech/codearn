package uz.codearn.codearnapp.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.codearn.codearnapp.model.User

class ProfileViewModel : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    init {
        viewModelScope.launch {
            val userDataQuery =
                Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                    .get().await()
            _userData.value = userDataQuery.toObject<User>()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val userDataQuery =
                Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                    .get().await()
            _userData.value = userDataQuery.toObject<User>()
        }
    }
}
package uz.codearn.codearnapp.ui.main.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import uz.codearn.codearnapp.repositories.UsersRepository
import uz.codearn.codearnapp.model.User

class LeaderboardViewModel : ViewModel() {

    private val usersRef = Firebase.firestore.collection("users")
    private val usersRepository = UsersRepository(usersRef, this)

    private val _topUsers = MutableLiveData<List<User>>()
    val topUsers: LiveData<List<User>>
        get() = _topUsers

    private val _usersLoading = MutableLiveData<Boolean>()
    val usersLoading: LiveData<Boolean>
        get() = _usersLoading

    init {
        viewModelScope.launch {
            _topUsers.value = usersRepository.getTopUsers()
        }
    }

    fun setLoading(isLoading: Boolean) {
        _usersLoading.value = isLoading
    }
}
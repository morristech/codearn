package uz.codearn.codearnapp.repositories

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import uz.codearn.codearnapp.model.User
import uz.codearn.codearnapp.ui.main.leaderboard.LeaderboardViewModel

class UsersRepository(
    private val usersRef: CollectionReference,
    private val viewModel: LeaderboardViewModel
) {

    suspend fun getTopUsers(): List<User> {
        viewModel.setLoading(true)
        val topUsers = mutableListOf<User>()
        try {
            val querySnapshot =
                usersRef.orderBy("score", Query.Direction.DESCENDING).limit(20).get().await()
            for (document in querySnapshot) {
                document.toObject<User>().let {
                    topUsers.add(it)
                }
            }
        } catch (e: Exception) {
            Log.d("UsersRepository", "getTopUsers: ${e.message}")
        }
        return topUsers.also { viewModel.setLoading(false) }
    }

}
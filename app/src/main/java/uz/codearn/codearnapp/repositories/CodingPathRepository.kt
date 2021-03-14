package uz.codearn.codearnapp.repositories

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import uz.codearn.codearnapp.model.CodingPath
import uz.codearn.codearnapp.ui.main.home.HomeViewModel

class CodingPathRepository(private val codingPathsRef: CollectionReference, private val viewModel:HomeViewModel) {

    suspend fun getAllCodingPaths(): List<CodingPath> {
        viewModel.setLoading(true)
        val codingPathList = mutableListOf<CodingPath>()
        try {
            val querySnapshot =
                codingPathsRef.orderBy("order", Query.Direction.ASCENDING).get().await()
            for (document in querySnapshot) {
                document.toObject<CodingPath>().let {
                    codingPathList.add(it)
                }
            }
        } catch (e: Exception) {
            Log.d("CodingPathRepository", "getAllCodingPaths: ${e.message}")
        }
        return codingPathList.also { viewModel.setLoading(false) }
    }

}
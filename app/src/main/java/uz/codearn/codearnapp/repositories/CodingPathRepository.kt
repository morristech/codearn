package uz.codearn.codearnapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.db.asCodingPathDatabaseModel
import uz.codearn.codearnapp.db.asCodingPathDomainModel
import uz.codearn.codearnapp.db.codingpath.CodingPathsDatabase
import uz.codearn.codearnapp.model.CodingPath

class CodingPathRepository(
    private val codingPathsDatabase: CodingPathsDatabase,
    private val codingPathsRef: CollectionReference
) {

    val codingPaths: LiveData<List<CodingPath>> =
        Transformations.map(codingPathsDatabase.codingPathDao.getCodingPaths()) {
            it.asCodingPathDomainModel()
        }

    suspend fun refreshCodingPaths() {
        try {
            withContext(Dispatchers.IO) {
                val querySnapshot =
                    codingPathsRef.orderBy("order", Query.Direction.ASCENDING).get().await()
                val listCodingPaths = arrayListOf<CodingPath>()
                for (document in querySnapshot) {
                    document.toObject<CodingPath>().let {
                        listCodingPaths.add(it)
                    }
                }
                codingPathsDatabase.codingPathDao.insertCodingPaths(*listCodingPaths.asCodingPathDatabaseModel())
            }
        } catch (e: Exception) {
            Log.d("CodingPathRepository", "getAllCodingPaths: ${e.message}")
        }
    }

}
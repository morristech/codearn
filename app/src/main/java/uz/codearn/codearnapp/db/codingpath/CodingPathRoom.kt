package uz.codearn.codearnapp.db.codingpath

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import uz.codearn.codearnapp.db.DatabaseCodingPath

@Dao
interface CodingPathDao {
    @Query("select * from databasecodingpath")
    fun getCodingPaths(): LiveData<List<DatabaseCodingPath>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCodingPaths(vararg codingPaths: DatabaseCodingPath)
}

@Database(entities = [DatabaseCodingPath::class], version = 1)
abstract class CodingPathsDatabase : RoomDatabase() {
    abstract val codingPathDao: CodingPathDao
}

private lateinit var INSTANCE_CODING_PATH: CodingPathsDatabase

fun getCodingPathsDatabase(context: Context): CodingPathsDatabase {
    synchronized(CodingPathsDatabase::class.java) {
        if (!::INSTANCE_CODING_PATH.isInitialized) {
            INSTANCE_CODING_PATH = Room.databaseBuilder(
                context.applicationContext,
                CodingPathsDatabase::class.java,
                "codingpaths"
            )
                .build()
        }
    }
    return INSTANCE_CODING_PATH
}
package com.gymmanager.android.db

import androidx.room.*
import com.gymmanager.android.model.Rutina
import com.gymmanager.android.model.Ejercicio

@Entity(tableName = "rutinas_offline")
data class RutinaEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val descripcion: String,
    val fechaAsignacion: Long?
)

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutinas(rutinas: List<RutinaEntity>)

    @Query("SELECT * FROM rutinas_offline")
    suspend fun getAllRutinas(): List<RutinaEntity>

    @Query("DELETE FROM rutinas_offline")
    suspend fun clearAll()
}

@Database(entities = [RutinaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gymmanager_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

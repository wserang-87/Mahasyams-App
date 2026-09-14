package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        KhatEntity::class,
        HijaiyahEntity::class,
        MateriEntity::class,
        TugasEntity::class,
        SubmissionEntity::class,
        NotifikasiEntity::class,
        AppConfigEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun khatDao(): KhatDao
    abstract fun hijaiyahDao(): HijaiyahDao
    abstract fun materiDao(): MateriDao
    abstract fun tugasDao(): TugasDao
    abstract fun submissionDao(): SubmissionDao
    abstract fun notifikasiDao(): NotifikasiDao
    abstract fun appConfigDao(): AppConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mahasyams_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            DatabaseInitializer.seedDatabase(getDatabase(context))
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

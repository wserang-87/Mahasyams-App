package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY role ASC, nama ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserById(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = 'SISWA' ORDER BY nama ASC")
    fun getAllSiswa(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)
}

@Dao
interface KhatDao {
    @Query("SELECT * FROM khat")
    fun getAllKhat(): Flow<List<KhatEntity>>

    @Query("SELECT * FROM khat WHERE id = :id LIMIT 1")
    fun getKhatById(id: String): Flow<KhatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKhat(khat: KhatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKhat(khatList: List<KhatEntity>)

    @Update
    suspend fun updateKhat(khat: KhatEntity)

    @Query("DELETE FROM khat WHERE id = :id")
    suspend fun deleteKhat(id: String)
}

@Dao
interface HijaiyahDao {
    @Query("SELECT * FROM hijaiyah")
    fun getAllHijaiyah(): Flow<List<HijaiyahEntity>>

    @Query("SELECT * FROM hijaiyah WHERE id = :id LIMIT 1")
    fun getHijaiyahById(id: String): Flow<HijaiyahEntity?>

    @Query("SELECT * FROM hijaiyah WHERE khat = :khat")
    fun getHijaiyahByKhat(khat: String): Flow<List<HijaiyahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHijaiyah(letters: List<HijaiyahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: HijaiyahEntity)

    @Update
    suspend fun updateLetter(letter: HijaiyahEntity)

    @Query("DELETE FROM hijaiyah WHERE id = :id")
    suspend fun deleteLetter(id: String)
}

@Dao
interface MateriDao {
    @Query("SELECT * FROM materi ORDER BY urutan ASC, createdAt DESC")
    fun getAllMateri(): Flow<List<MateriEntity>>

    @Query("SELECT * FROM materi WHERE id = :id LIMIT 1")
    fun getMateriById(id: String): Flow<MateriEntity?>

    @Query("SELECT * FROM materi WHERE khatId = :khatId ORDER BY urutan ASC")
    fun getMateriByKhat(khatId: String): Flow<List<MateriEntity>>

    @Query("SELECT * FROM materi WHERE hurufId = :hurufId ORDER BY urutan ASC")
    fun getMateriByHuruf(hurufId: String): Flow<List<MateriEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMateri(materi: MateriEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMateri(materiList: List<MateriEntity>)

    @Update
    suspend fun updateMateri(materi: MateriEntity)

    @Query("DELETE FROM materi WHERE id = :id")
    suspend fun deleteMateri(id: String)
}

@Dao
interface TugasDao {
    @Query("SELECT * FROM tugas ORDER BY deadline ASC")
    fun getAllTugas(): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE id = :id LIMIT 1")
    fun getTugasById(id: String): Flow<TugasEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTugas(tugas: TugasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTugas(tugasList: List<TugasEntity>)

    @Update
    suspend fun updateTugas(tugas: TugasEntity)

    @Query("DELETE FROM tugas WHERE id = :id")
    suspend fun deleteTugas(id: String)
}

@Dao
interface SubmissionDao {
    @Query("SELECT * FROM submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE siswaId = :siswaId ORDER BY submittedAt DESC")
    fun getSubmissionsBySiswa(siswaId: String): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE id = :id LIMIT 1")
    fun getSubmissionById(id: String): Flow<SubmissionEntity?>

    @Query("SELECT * FROM submissions WHERE tugasId = :tugasId ORDER BY submittedAt DESC")
    fun getSubmissionsByTugas(tugasId: String): Flow<List<SubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSubmissions(submissions: List<SubmissionEntity>)

    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)

    @Query("DELETE FROM submissions WHERE id = :id")
    suspend fun deleteSubmission(id: String)
}

@Dao
interface NotifikasiDao {
    @Query("SELECT * FROM notifikasi WHERE userId = :userId OR userId = 'ALL' ORDER BY createdAt DESC")
    fun getNotifikasiForUser(userId: String): Flow<List<NotifikasiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifikasi(notifikasi: NotifikasiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotifikasi(notifikasiList: List<NotifikasiEntity>)

    @Query("UPDATE notifikasi SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<AppConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: AppConfigEntity)
}

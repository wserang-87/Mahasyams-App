package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.*
import java.util.UUID

class CalligraphyRepository(private val db: AppDatabase) {

    // Current logged in user (in-memory state with initial default as Ahmad - Siswa, easily switchable to Guru or Admin)
    private val _currentUser = MutableStateFlow(
        User(
            uid = "siswa-01",
            nama = "Ahmad Al-Mubarok",
            email = "ahmad@mahasyams.id",
            username = "ahmad",
            role = "SISWA",
            kelas = "IX A",
            guruPembimbing = "Ustadz Ridwan Al-Khathath",
            status = "Aktif"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    // Users
    val allUsers: Flow<List<User>> = db.userDao().getAllUsers().map { list -> list.map { it.toDomain() } }
    val allSiswa: Flow<List<User>> = db.userDao().getAllSiswa().map { list -> list.map { it.toDomain() } }

    fun getUserById(uid: String): Flow<User?> = db.userDao().getUserById(uid).map { it?.toDomain() }

    suspend fun saveUser(user: User) {
        db.userDao().insertUser(UserEntity.fromDomain(user))
    }

    suspend fun deleteUser(uid: String) {
        db.userDao().deleteUser(uid)
    }

    // Khat
    val allKhat: Flow<List<Khat>> = db.khatDao().getAllKhat().map { list -> list.map { it.toDomain() } }

    fun getKhatById(id: String): Flow<Khat?> = db.khatDao().getKhatById(id).map { it?.toDomain() }

    suspend fun saveKhat(khat: Khat) {
        db.khatDao().insertKhat(KhatEntity.fromDomain(khat))
    }

    suspend fun deleteKhat(id: String) {
        db.khatDao().deleteKhat(id)
    }

    // Hijaiyah
    val allHijaiyah: Flow<List<HijaiyahLetter>> = db.hijaiyahDao().getAllHijaiyah().map { list -> list.map { it.toDomain() } }

    fun getHijaiyahById(id: String): Flow<HijaiyahLetter?> = db.hijaiyahDao().getHijaiyahById(id).map { it?.toDomain() }

    fun getHijaiyahByKhat(khat: String): Flow<List<HijaiyahLetter>> = db.hijaiyahDao().getHijaiyahByKhat(khat).map { list -> list.map { it.toDomain() } }

    suspend fun saveLetter(letter: HijaiyahLetter) {
        db.hijaiyahDao().insertLetter(HijaiyahEntity.fromDomain(letter))
    }

    suspend fun updateLetter(letter: HijaiyahLetter) {
        db.hijaiyahDao().updateLetter(HijaiyahEntity.fromDomain(letter))
    }

    // Materi
    val allMateri: Flow<List<Materi>> = db.materiDao().getAllMateri().map { list -> list.map { it.toDomain() } }

    fun getMateriById(id: String): Flow<Materi?> = db.materiDao().getMateriById(id).map { it?.toDomain() }

    fun getMateriByHuruf(hurufId: String): Flow<List<Materi>> = db.materiDao().getMateriByHuruf(hurufId).map { list -> list.map { it.toDomain() } }

    suspend fun saveMateri(materi: Materi) {
        db.materiDao().insertMateri(MateriEntity.fromDomain(materi))
    }

    suspend fun deleteMateri(id: String) {
        db.materiDao().deleteMateri(id)
    }

    // Tugas
    val allTugas: Flow<List<Tugas>> = db.tugasDao().getAllTugas().map { list -> list.map { it.toDomain() } }

    fun getTugasById(id: String): Flow<Tugas?> = db.tugasDao().getTugasById(id).map { it?.toDomain() }

    suspend fun saveTugas(tugas: Tugas) {
        db.tugasDao().insertTugas(TugasEntity.fromDomain(tugas))
        // Kirim notifikasi tugas baru
        val notif = NotifikasiEntity(
            id = UUID.randomUUID().toString(),
            userId = "ALL",
            judul = "Tugas Baru: ${tugas.judul}",
            pesan = "Ustadz telah menambahkan tugas kaligrafi baru untuk ${tugas.khatId.uppercase()}.",
            tipe = "tugas",
            isRead = false,
            createdAt = System.currentTimeMillis()
        )
        db.notifikasiDao().insertNotifikasi(notif)
    }

    suspend fun deleteTugas(id: String) {
        db.tugasDao().deleteTugas(id)
    }

    // Submissions (Latihan)
    val allSubmissions: Flow<List<Submission>> = db.submissionDao().getAllSubmissions().map { list -> list.map { it.toDomain() } }

    fun getSubmissionsBySiswa(siswaId: String): Flow<List<Submission>> =
        db.submissionDao().getSubmissionsBySiswa(siswaId).map { list -> list.map { it.toDomain() } }

    fun getSubmissionById(id: String): Flow<Submission?> =
        db.submissionDao().getSubmissionById(id).map { it?.toDomain() }

    suspend fun submitKarya(submission: Submission) {
        db.submissionDao().insertSubmission(SubmissionEntity.fromDomain(submission))
        // Notifikasi ke guru
        val notif = NotifikasiEntity(
            id = UUID.randomUUID().toString(),
            userId = "guru-01",
            judul = "Pengumpulan Latihan Baru",
            pesan = "${submission.namaSiswa} (${submission.kelas}) telah mengumpulkan tugas '${submission.tugasJudul}'.",
            tipe = "tugas",
            isRead = false,
            createdAt = System.currentTimeMillis()
        )
        db.notifikasiDao().insertNotifikasi(notif)
    }

    suspend fun koreksiKarya(submissionId: String, nilai: Int, komentar: String, guruKorektor: String) {
        val existing = db.submissionDao().getSubmissionById(submissionId).firstOrNull()
        if (existing != null) {
            val updated = existing.copy(
                status = "Sudah Dikoreksi",
                nilai = nilai,
                komentar = komentar,
                guruKorektor = guruKorektor,
                dikoreksiAt = System.currentTimeMillis()
            )
            db.submissionDao().updateSubmission(updated)

            // Kirim notifikasi hasil koreksi ke siswa
            val notif = NotifikasiEntity(
                id = UUID.randomUUID().toString(),
                userId = existing.siswaId,
                judul = "Latihan Anda telah dikoreksi! 📝",
                pesan = "Tugas '${existing.tugasJudul}' mendapatkan nilai $nilai. Buka menu Latihan Saya untuk ulasan lengkap.",
                tipe = "koreksi",
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
            db.notifikasiDao().insertNotifikasi(notif)
        }
    }

    // Notifikasi
    fun getNotifikasiForUser(userId: String): Flow<List<Notifikasi>> =
        db.notifikasiDao().getNotifikasiForUser(userId).map { list -> list.map { it.toDomain() } }

    suspend fun markNotifikasiRead(id: String) {
        db.notifikasiDao().markAsRead(id)
    }

    // App Config
    val appConfig: Flow<AppConfig> = db.appConfigDao().getConfig().map {
        it?.toDomain() ?: AppConfig()
    }

    suspend fun updateAppConfig(config: AppConfig) {
        db.appConfigDao().insertConfig(AppConfigEntity.fromDomain(config))
    }
}

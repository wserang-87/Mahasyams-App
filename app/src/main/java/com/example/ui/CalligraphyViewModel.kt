package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.CalligraphyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class CalligraphyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalligraphyRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CalligraphyRepository(db)
    }

    val currentUser: StateFlow<User> = repository.currentUser
    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSiswa: StateFlow<List<User>> = repository.allSiswa
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val khatList: StateFlow<List<Khat>> = repository.allKhat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val hijaiyahList: StateFlow<List<HijaiyahLetter>> = repository.allHijaiyah
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val materiList: StateFlow<List<Materi>> = repository.allMateri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tugasList: StateFlow<List<Tugas>> = repository.allTugas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSubmissions: StateFlow<List<Submission>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appConfig: StateFlow<AppConfig> = repository.appConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfig())

    // Submissions for current student
    val mySubmissions: StateFlow<List<Submission>> = combine(allSubmissions, currentUser) { subs, user ->
        subs.filter { it.siswaId == user.uid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Submissions pending correction for teachers
    val pendingCorrections: StateFlow<List<Submission>> = allSubmissions.map { subs ->
        subs.filter { it.status == "Menunggu Koreksi" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for current user
    val notifications: StateFlow<List<Notifikasi>> = currentUser.flatMapLatest { user ->
        repository.getNotifikasiForUser(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation and Selection State
    val isLoggedIn = MutableStateFlow(false)
    val currentScreen = MutableStateFlow("LOGIN")
    val selectedMateri = MutableStateFlow<Materi?>(null)
    val selectedTugas = MutableStateFlow<Tugas?>(null)
    val selectedSubmission = MutableStateFlow<Submission?>(null)
    val selectedHijaiyah = MutableStateFlow<HijaiyahLetter?>(null)
    val selectedKhat = MutableStateFlow<Khat?>(null)

    // Filter and Selection States
    val searchQuery = MutableStateFlow("")
    val filterKhat = MutableStateFlow("SEMUA")
    val selectedKhatInHijaiyah = MutableStateFlow("Naskhi")
    val filterHurufInMateri = MutableStateFlow("SEMUA")

    fun switchUser(user: User) {
        repository.setCurrentUser(user)
    }

    fun login(usernameOrEmail: String, role: String? = null): Boolean {
        val users = allUsers.value
        val matched = users.firstOrNull {
            (it.email.equals(usernameOrEmail.trim(), ignoreCase = true) ||
             it.username.equals(usernameOrEmail.trim(), ignoreCase = true)) &&
            (role == null || it.role.equals(role, ignoreCase = true))
        } ?: users.firstOrNull {
            it.email.contains(usernameOrEmail.trim(), ignoreCase = true) ||
            it.username.contains(usernameOrEmail.trim(), ignoreCase = true)
        } ?: if (role != null) {
            users.firstOrNull { it.role.equals(role, ignoreCase = true) }
        } else null

        if (matched != null) {
            loginAsUser(matched)
            return true
        }
        return false
    }

    fun loginAsUser(user: User) {
        switchUser(user)
        isLoggedIn.value = true
        currentScreen.value = if (user.role == "ADMIN") "ADMIN" else "DASHBOARD"
    }

    fun logout() {
        isLoggedIn.value = false
        currentScreen.value = "LOGIN"
    }

    fun setKhatInHijaiyah(khatName: String) {
        selectedKhatInHijaiyah.value = khatName
    }

    fun navigateTo(screen: String) {
        currentScreen.value = screen
    }

    fun openMateriDetail(materi: Materi) {
        selectedMateri.value = materi
        currentScreen.value = "DETAIL_MATERI"
    }

    fun openTugasDetail(tugas: Tugas) {
        selectedTugas.value = tugas
        currentScreen.value = "DETAIL_TUGAS"
    }

    fun openKoreksiDetail(submission: Submission) {
        selectedSubmission.value = submission
        currentScreen.value = "DETAIL_KOREKSI"
    }

    fun selectHijaiyah(letter: HijaiyahLetter?) {
        selectedHijaiyah.value = letter
    }

    fun selectKhat(khat: Khat?) {
        selectedKhat.value = khat
    }

    fun updateLetterFile(
        letterId: String,
        fileUrl: String,
        fileName: String,
        mimeType: String,
        catatanKaidahBaru: String? = null
    ) {
        viewModelScope.launch {
            val existing = hijaiyahList.value.firstOrNull { it.id == letterId }
            if (existing != null) {
                val updated = existing.copy(
                    fileUrl = fileUrl,
                    fileName = fileName,
                    mimeType = mimeType,
                    catatanKaidah = catatanKaidahBaru ?: existing.catatanKaidah
                )
                repository.updateLetter(updated)
                if (selectedHijaiyah.value?.id == letterId) {
                    selectedHijaiyah.value = updated
                }
            }
        }
    }

    fun submitKarya(
        tugas: Tugas,
        catatanSiswa: String,
        drawingData: String,
        fileUrl: String,
        fileName: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val submission = Submission(
                id = UUID.randomUUID().toString(),
                tugasId = tugas.id,
                tugasJudul = tugas.judul,
                siswaId = user.uid,
                namaSiswa = user.nama,
                kelas = user.kelas,
                khatId = tugas.khatId,
                fileUrl = fileUrl,
                drawingData = drawingData,
                catatanSiswa = catatanSiswa,
                fileName = if (fileName.isNotBlank()) fileName else "latihan_khat_${user.username}.png",
                mimeType = if (drawingData.isNotBlank()) "canvas/drawing" else "image/png",
                submittedAt = System.currentTimeMillis(),
                status = "Menunggu Koreksi"
            )
            repository.submitKarya(submission)
            navigateTo("LATIHAN_SAYA")
        }
    }

    fun simpanKoreksi(submissionId: String, nilai: Int, komentar: String) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.koreksiKarya(
                submissionId = submissionId,
                nilai = nilai,
                komentar = komentar,
                guruKorektor = user.nama
            )
            navigateTo("KOREKSI")
        }
    }

    fun tambahMateri(
        judul: String,
        khatId: String,
        khatNama: String,
        hurufId: String = "",
        hurufNama: String = "",
        deskripsi: String,
        tipeSumber: String,
        fileUrl: String,
        fileName: String,
        mimeType: String = "application/pdf"
    ) {
        viewModelScope.launch {
            val materi = Materi(
                id = UUID.randomUUID().toString(),
                judul = judul,
                khatId = khatId,
                khatNama = khatNama,
                hurufId = hurufId,
                hurufNama = hurufNama,
                deskripsi = deskripsi,
                tipeSumber = tipeSumber,
                fileUrl = fileUrl,
                fileName = fileName,
                mimeType = mimeType,
                createdBy = currentUser.value.nama
            )
            repository.saveMateri(materi)
        }
    }

    fun hapusMateri(id: String) {
        viewModelScope.launch {
            repository.deleteMateri(id)
        }
    }

    fun tambahTugas(
        judul: String,
        deskripsi: String,
        materiId: String,
        khatId: String,
        deadlineDays: Int,
        sourceType: String,
        fileUrl: String,
        fileName: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val tugas = Tugas(
                id = UUID.randomUUID().toString(),
                judul = judul,
                deskripsi = deskripsi,
                materiId = materiId,
                khatId = khatId,
                guruId = currentUser.value.uid,
                guruNama = currentUser.value.nama,
                mulai = now,
                deadline = now + (deadlineDays * 24 * 60 * 60 * 1000L),
                sourceType = sourceType,
                fileUrl = fileUrl,
                fileName = fileName
            )
            repository.saveTugas(tugas)
        }
    }

    fun hapusTugas(id: String) {
        viewModelScope.launch {
            repository.deleteTugas(id)
        }
    }

    fun tambahUser(nama: String, email: String, username: String, role: String, kelas: String) {
        viewModelScope.launch {
            val user = User(
                uid = UUID.randomUUID().toString(),
                nama = nama,
                email = email,
                username = username,
                role = role,
                kelas = kelas,
                guruPembimbing = if (role == "SISWA") "Ustadz Ridwan Al-Khathath" else "-",
                status = "Aktif"
            )
            repository.saveUser(user)
        }
    }

    fun hapusUser(uid: String) {
        viewModelScope.launch {
            repository.deleteUser(uid)
        }
    }

    fun updateConfig(config: AppConfig) {
        viewModelScope.launch {
            repository.updateAppConfig(config)
        }
    }

    fun markNotifikasiRead(id: String) {
        viewModelScope.launch {
            repository.markNotifikasiRead(id)
        }
    }
}

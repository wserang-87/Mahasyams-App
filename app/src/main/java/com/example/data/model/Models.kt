package com.example.data.model

data class User(
    val uid: String,
    val nama: String,
    val email: String,
    val username: String,
    val role: String, // "ADMIN", "GURU", "SISWA"
    val kelas: String,
    val fotoURL: String = "",
    val guruPembimbing: String = "",
    val status: String = "Aktif",
    val createdAt: Long = System.currentTimeMillis()
)

data class Khat(
    val id: String,
    val nama: String,
    val namaArab: String,
    val sejarah: String,
    val karakteristik: String,
    val ciriKhas: String,
    val tokoh: String,
    val contohDeskripsi: String,
    val contohTeksArab: String,
    val imageUrl: String = ""
)

data class HijaiyahLetter(
    val id: String,
    val huruf: String,
    val nama: String,
    val khat: String = "Naskhi",
    val tunggalTeks: String,
    val tunggalDesc: String,
    val awalTeks: String,
    val awalDesc: String,
    val tengahTeks: String,
    val tengahDesc: String,
    val akhirTeks: String,
    val akhirDesc: String,
    val catatanKaidah: String,
    val fileUrl: String = "",
    val fileName: String = "",
    val mimeType: String = "image/png"
)

data class Materi(
    val id: String,
    val judul: String,
    val khatId: String,
    val khatNama: String,
    val hurufId: String = "",
    val hurufNama: String = "",
    val deskripsi: String,
    val tipeSumber: String, // "LINK" atau "UPLOAD"
    val fileUrl: String,
    val filePath: String = "",
    val fileName: String = "",
    val mimeType: String = "application/pdf",
    val thumbnailUrl: String = "",
    val videoUrl: String = "",
    val urutan: Int = 1,
    val status: String = "Aktif",
    val createdBy: String = "Ustadz Ridwan Al-Khathath",
    val createdAt: Long = System.currentTimeMillis()
)

data class Tugas(
    val id: String,
    val judul: String,
    val deskripsi: String,
    val materiId: String = "",
    val khatId: String,
    val guruId: String,
    val guruNama: String,
    val mulai: Long,
    val deadline: Long,
    val sourceType: String = "LINK", // "LINK" atau "UPLOAD"
    val fileUrl: String = "",
    val storagePath: String = "",
    val fileName: String = "",
    val status: String = "Aktif",
    val createdAt: Long = System.currentTimeMillis()
)

data class Submission(
    val id: String,
    val tugasId: String,
    val tugasJudul: String,
    val siswaId: String,
    val namaSiswa: String,
    val kelas: String,
    val khatId: String,
    val fileUrl: String = "",
    val drawingData: String = "", // Calligraphy stroke points or text
    val catatanSiswa: String = "",
    val fileName: String = "karya_kaligrafi.png",
    val mimeType: String = "image/png",
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "Menunggu Koreksi", // "Menunggu Koreksi", "Sudah Dikoreksi"
    val nilai: Int? = null,
    val komentar: String? = null,
    val guruKorektor: String? = null,
    val dikoreksiAt: Long? = null
)

data class Notifikasi(
    val id: String,
    val userId: String,
    val judul: String,
    val pesan: String,
    val tipe: String, // "tugas", "koreksi", "deadline", "info"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class AppConfig(
    val id: Int = 1,
    val appName: String = "Mahasyams",
    val appNameArab: String = "مَهَا شَمْس",
    val tagline: String = "Belajar Kaligrafi, Menulis dengan Seni dan Adab",
    val logoUrl: String = "",
    val deskripsi: String = "Aplikasi pembelajaran kaligrafi digital (Learning Management System) untuk guru dan santri/siswa nusantara.",
    val versi: String = "1.0.0",
    val kontak: String = "admin@mahasyams.id"
)

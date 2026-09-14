package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val nama: String,
    val email: String,
    val username: String,
    val role: String, // "ADMIN", "GURU", "SISWA"
    val kelas: String,
    val fotoURL: String = "",
    val guruPembimbing: String = "",
    val status: String = "Aktif",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = User(uid, nama, email, username, role, kelas, fotoURL, guruPembimbing, status, createdAt)
    companion object {
        fun fromDomain(d: User) = UserEntity(d.uid, d.nama, d.email, d.username, d.role, d.kelas, d.fotoURL, d.guruPembimbing, d.status, d.createdAt)
    }
}

@Entity(tableName = "khat")
data class KhatEntity(
    @PrimaryKey val id: String,
    val nama: String,
    val namaArab: String,
    val sejarah: String,
    val karakteristik: String,
    val ciriKhas: String,
    val tokoh: String,
    val contohDeskripsi: String,
    val contohTeksArab: String,
    val imageUrl: String = ""
) {
    fun toDomain() = Khat(id, nama, namaArab, sejarah, karakteristik, ciriKhas, tokoh, contohDeskripsi, contohTeksArab, imageUrl)
    companion object {
        fun fromDomain(d: Khat) = KhatEntity(d.id, d.nama, d.namaArab, d.sejarah, d.karakteristik, d.ciriKhas, d.tokoh, d.contohDeskripsi, d.contohTeksArab, d.imageUrl)
    }
}

@Entity(tableName = "hijaiyah")
data class HijaiyahEntity(
    @PrimaryKey val id: String,
    val huruf: String,
    val nama: String,
    val khat: String,
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
) {
    fun toDomain() = HijaiyahLetter(id, huruf, nama, khat, tunggalTeks, tunggalDesc, awalTeks, awalDesc, tengahTeks, tengahDesc, akhirTeks, akhirDesc, catatanKaidah, fileUrl, fileName, mimeType)
    companion object {
        fun fromDomain(d: HijaiyahLetter) = HijaiyahEntity(d.id, d.huruf, d.nama, d.khat, d.tunggalTeks, d.tunggalDesc, d.awalTeks, d.awalDesc, d.tengahTeks, d.tengahDesc, d.akhirTeks, d.akhirDesc, d.catatanKaidah, d.fileUrl, d.fileName, d.mimeType)
    }
}

@Entity(tableName = "materi")
data class MateriEntity(
    @PrimaryKey val id: String,
    val judul: String,
    val khatId: String,
    val khatNama: String,
    val hurufId: String = "",
    val hurufNama: String = "",
    val deskripsi: String,
    val tipeSumber: String,
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
) {
    fun toDomain() = Materi(id, judul, khatId, khatNama, hurufId, hurufNama, deskripsi, tipeSumber, fileUrl, filePath, fileName, mimeType, thumbnailUrl, videoUrl, urutan, status, createdBy, createdAt)
    companion object {
        fun fromDomain(d: Materi) = MateriEntity(d.id, d.judul, d.khatId, d.khatNama, d.hurufId, d.hurufNama, d.deskripsi, d.tipeSumber, d.fileUrl, d.filePath, d.fileName, d.mimeType, d.thumbnailUrl, d.videoUrl, d.urutan, d.status, d.createdBy, d.createdAt)
    }
}

@Entity(tableName = "tugas")
data class TugasEntity(
    @PrimaryKey val id: String,
    val judul: String,
    val deskripsi: String,
    val materiId: String = "",
    val khatId: String,
    val guruId: String,
    val guruNama: String,
    val mulai: Long,
    val deadline: Long,
    val sourceType: String = "LINK",
    val fileUrl: String = "",
    val storagePath: String = "",
    val fileName: String = "",
    val status: String = "Aktif",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Tugas(id, judul, deskripsi, materiId, khatId, guruId, guruNama, mulai, deadline, sourceType, fileUrl, storagePath, fileName, status, createdAt)
    companion object {
        fun fromDomain(d: Tugas) = TugasEntity(d.id, d.judul, d.deskripsi, d.materiId, d.khatId, d.guruId, d.guruNama, d.mulai, d.deadline, d.sourceType, d.fileUrl, d.storagePath, d.fileName, d.status, d.createdAt)
    }
}

@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey val id: String,
    val tugasId: String,
    val tugasJudul: String,
    val siswaId: String,
    val namaSiswa: String,
    val kelas: String,
    val khatId: String,
    val fileUrl: String = "",
    val drawingData: String = "",
    val catatanSiswa: String = "",
    val fileName: String = "karya_kaligrafi.png",
    val mimeType: String = "image/png",
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "Menunggu Koreksi",
    val nilai: Int? = null,
    val komentar: String? = null,
    val guruKorektor: String? = null,
    val dikoreksiAt: Long? = null
) {
    fun toDomain() = Submission(id, tugasId, tugasJudul, siswaId, namaSiswa, kelas, khatId, fileUrl, drawingData, catatanSiswa, fileName, mimeType, submittedAt, status, nilai, komentar, guruKorektor, dikoreksiAt)
    companion object {
        fun fromDomain(d: Submission) = SubmissionEntity(d.id, d.tugasId, d.tugasJudul, d.siswaId, d.namaSiswa, d.kelas, d.khatId, d.fileUrl, d.drawingData, d.catatanSiswa, d.fileName, d.mimeType, d.submittedAt, d.status, d.nilai, d.komentar, d.guruKorektor, d.dikoreksiAt)
    }
}

@Entity(tableName = "notifikasi")
data class NotifikasiEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val judul: String,
    val pesan: String,
    val tipe: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Notifikasi(id, userId, judul, pesan, tipe, isRead, createdAt)
    companion object {
        fun fromDomain(d: Notifikasi) = NotifikasiEntity(d.id, d.userId, d.judul, d.pesan, d.tipe, d.isRead, d.createdAt)
    }
}

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val appName: String = "Mahasyams",
    val appNameArab: String = "مَهَا شَمْس",
    val tagline: String = "Belajar Kaligrafi, Menulis dengan Seni dan Adab",
    val logoUrl: String = "",
    val deskripsi: String = "Aplikasi Pembelajaran Kaligrafi Digital untuk Guru dan Siswa Nusantara.",
    val versi: String = "1.0.0",
    val kontak: String = "admin@mahasyams.id"
) {
    fun toDomain() = AppConfig(id, appName, appNameArab, tagline, logoUrl, deskripsi, versi, kontak)
    companion object {
        fun fromDomain(d: AppConfig) = AppConfigEntity(d.id, d.appName, d.appNameArab, d.tagline, d.logoUrl, d.deskripsi, d.versi, d.kontak)
    }
}

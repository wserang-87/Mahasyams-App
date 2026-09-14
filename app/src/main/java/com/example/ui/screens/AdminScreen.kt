package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    var currentTab by remember { mutableStateOf(0) } // 0 = Manajemen User, 1 = Pengaturan Aplikasi, 2 = Database Materi & Huruf
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showAddMateriDialog by remember { mutableStateOf(false) }
    val materiList by viewModel.materiList.collectAsState()
    val khatList by viewModel.khatList.collectAsState()
    val hijaiyahList by viewModel.hijaiyahList.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Panel Admin Mahasyams",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
            Text(
                text = "Manajemen akun, konfigurasi branding dan pengaturan sistem",
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = currentTab,
            containerColor = SlateSurface,
            contentColor = EmeraldPrimary
        ) {
            Tab(
                selected = currentTab == 0,
                onClick = { currentTab = 0 },
                text = { Text("User (${allUsers.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = currentTab == 1,
                onClick = { currentTab = 1 },
                text = { Text("Pengaturan", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = currentTab == 2,
                onClick = { currentTab = 2 },
                text = { Text("Upload Materi & Huruf", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (currentTab == 0) {
            // Manajemen User
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Pengguna Sistem",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SlateTextPrimary
                )
                Button(
                    onClick = { showAddUserDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah User", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allUsers, key = { it.uid }) { user ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.nama,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SlateTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = when (user.role) {
                                            "ADMIN" -> AmberContainer
                                            "GURU" -> SkyContainer
                                            else -> EmeraldContainer
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = user.role,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (user.role) {
                                                "ADMIN" -> OnAmberContainer
                                                "GURU" -> OnSkyContainer
                                                else -> OnEmeraldContainer
                                            },
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${user.email} • Kelas ${user.kelas}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            if (user.uid != currentUser.uid) {
                                IconButton(onClick = { viewModel.hapusUser(user.uid) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = StatusError)
                                }
                            }
                        }
                    }
                }
            }
        } else if (currentTab == 1) {
            // Pengaturan Aplikasi & Branding (Masterplan Section 47, 48)
            var appName by remember { mutableStateOf(appConfig.appName) }
            var tagline by remember { mutableStateOf(appConfig.tagline) }
            var deskripsi by remember { mutableStateOf(appConfig.deskripsi) }
            var kontak by remember { mutableStateOf(appConfig.kontak) }
            var isSavedNotice by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Identitas & Branding",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldDark
                        )

                        OutlinedTextField(
                            value = appName,
                            onValueChange = { appName = it },
                            label = { Text("Nama Aplikasi") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tagline,
                            onValueChange = { tagline = it },
                            label = { Text("Tagline Aplikasi") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = deskripsi,
                            onValueChange = { deskripsi = it },
                            label = { Text("Deskripsi Singkat") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = kontak,
                            onValueChange = { kontak = it },
                            label = { Text("Kontak Support / Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                viewModel.updateConfig(
                                    appConfig.copy(
                                        appName = appName,
                                        tagline = tagline,
                                        deskripsi = deskripsi,
                                        kontak = kontak
                                    )
                                )
                                isSavedNotice = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simpan Pengaturan Branding")
                        }

                        if (isSavedNotice) {
                            Text(
                                text = "✓ Pengaturan aplikasi berhasil diperbarui!",
                                color = StatusSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Tab 2: Upload Materi & Database Huruf
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Ragam Khat", fontSize = 11.sp, color = SlateTextSecondary)
                            Text("7 Jenis", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                            Text("Naskhi, Tsuluts, dll", fontSize = 9.sp, color = SlateTextSecondary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Database Huruf", fontSize = 11.sp, color = SlateTextSecondary)
                            Text("${hijaiyahList.size} Data", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AmberAccent)
                            Text("28 Huruf × 7 Khat", fontSize = 9.sp, color = SlateTextSecondary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Materi", fontSize = 11.sp, color = SlateTextSecondary)
                            Text("${materiList.size} File", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SkySecondary)
                            Text("PDF, Gambar & Video", fontSize = 9.sp, color = SlateTextSecondary)
                        }
                    }
                }

                // Action Buttons
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Aksi Unggah & Pembaruan Database",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "Unggah berkas modul PDF, lembar gambar kaidah untuk setiap huruf dalam 7 ragam khat, atau modul per nama kaligrafi agar murid dapat mempelajarinya langsung.",
                            fontSize = 11.sp,
                            color = SlateTextSecondary,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddMateriDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unggah Berkas Baru", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo("HIJAIYAH") },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, EmeraldPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.GridView, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Katalog 7 Khat & Huruf", fontSize = 11.sp, color = EmeraldDark)
                            }
                        }
                    }
                }

                // List of Current Materials in Database
                Text(
                    text = "Daftar Materi & Berkas yang Telah Terdaftar (${materiList.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SlateTextPrimary
                )

                materiList.forEach { materi ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                val isPdf = materi.mimeType.contains("pdf") || materi.fileName.endsWith(".pdf")
                                val isImage = materi.mimeType.contains("image") || materi.fileName.endsWith(".png") || materi.fileName.endsWith(".jpg")
                                Icon(
                                    imageVector = if (isPdf) Icons.Default.PictureAsPdf else if (isImage) Icons.Default.Image else Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = if (isPdf) StatusError else if (isImage) EmeraldPrimary else SkySecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = materi.judul,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SlateTextPrimary
                                    )
                                    Row {
                                        Text(
                                            text = materi.khatNama,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldDark
                                        )
                                        if (materi.hurufNama.isNotBlank()) {
                                            Text(" • Huruf ${materi.hurufNama}", fontSize = 10.sp, color = AmberAccent, fontWeight = FontWeight.Bold)
                                        }
                                        Text(" • ${materi.fileName.ifBlank { "Tautan Online" }}", fontSize = 10.sp, color = SlateTextSecondary)
                                    }
                                }
                            }

                            Row {
                                IconButton(onClick = { viewModel.openMateriDetail(materi) }) {
                                    Icon(Icons.Default.Visibility, contentDescription = "Lihat", tint = EmeraldPrimary)
                                }
                                IconButton(onClick = { viewModel.hapusMateri(materi.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = StatusError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onSave = { nama, email, username, role, kelas ->
                viewModel.tambahUser(nama, email, username, role, kelas)
                showAddUserDialog = false
            }
        )
    }

    if (showAddMateriDialog) {
        AddMateriDialog(
            khatOptions = khatList.map { it.id to it.nama },
            letterOptions = hijaiyahList.map { it.id to it.nama }.distinctBy { it.second },
            onDismiss = { showAddMateriDialog = false },
            onSave = { judul, khatId, khatNama, hurufId, hurufNama, desc, sourceType, fileUrl, fileName, mimeType ->
                viewModel.tambahMateri(
                    judul = judul,
                    khatId = khatId,
                    khatNama = khatNama,
                    hurufId = hurufId,
                    hurufNama = hurufNama,
                    deskripsi = desc,
                    tipeSumber = sourceType,
                    fileUrl = fileUrl,
                    fileName = fileName,
                    mimeType = mimeType
                )
                showAddMateriDialog = false
            }
        )
    }
}

@Composable
private fun AddUserDialog(
    onDismiss: () -> Unit,
    onSave: (nama: String, email: String, username: String, role: String, kelas: String) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("SISWA") }
    var kelas by remember { mutableStateOf("IX B") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Pengguna Baru", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Lengkap") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                Text("Pilih Role:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("SISWA", "GURU", "ADMIN").forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = kelas, onValueChange = { kelas = it }, label = { Text("Kelas / Rombel") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isNotBlank() && email.isNotBlank()) {
                        onSave(nama, email, username.ifBlank { email.substringBefore("@") }, role, kelas)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

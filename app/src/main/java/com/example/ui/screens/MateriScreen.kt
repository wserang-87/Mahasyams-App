package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Materi
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateriScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val materiList by viewModel.materiList.collectAsState()
    val khatList by viewModel.khatList.collectAsState()
    val hijaiyahList by viewModel.hijaiyahList.collectAsState()

    var selectedKhatFilter by remember { mutableStateOf("SEMUA") }
    var selectedHurufFilter by remember { mutableStateOf("SEMUA") }
    var selectedFormatFilter by remember { mutableStateOf("SEMUA") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Unique list of letter names for filtering
    val uniqueLetterNames = remember(hijaiyahList) {
        listOf("SEMUA") + hijaiyahList.map { it.nama }.distinct()
    }

    val filteredMateri = remember(materiList, selectedKhatFilter, selectedHurufFilter, selectedFormatFilter, searchQuery) {
        materiList.filter { materi ->
            val matchKhat = selectedKhatFilter == "SEMUA" || materi.khatId.equals(selectedKhatFilter, ignoreCase = true)
            val matchHuruf = selectedHurufFilter == "SEMUA" || materi.hurufNama.equals(selectedHurufFilter, ignoreCase = true)
            val matchFormat = when (selectedFormatFilter) {
                "PDF" -> materi.mimeType.contains("pdf") || materi.fileName.endsWith(".pdf")
                "GAMBAR" -> materi.mimeType.contains("image") || materi.fileName.endsWith(".png") || materi.fileName.endsWith(".jpg")
                "VIDEO" -> materi.mimeType.contains("video") || materi.fileUrl.contains("youtube")
                else -> true
            }
            val matchQuery = searchQuery.isBlank() ||
                materi.judul.contains(searchQuery, ignoreCase = true) ||
                materi.deskripsi.contains(searchQuery, ignoreCase = true) ||
                materi.hurufNama.contains(searchQuery, ignoreCase = true)

            matchKhat && matchHuruf && matchFormat && matchQuery
        }
    }

    Scaffold(
        floatingActionButton = {
            if (currentUser.role in listOf("ADMIN", "GURU")) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.CloudUpload, contentDescription = "Unggah Materi") },
                    text = { Text("Unggah Materi PDF / Gambar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(SlateBackground)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Materi & Lembar Kaidah",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Modul PDF, lembar gambar kaidah huruf & video pembelajaran",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari materi, nama huruf, atau kaidah...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = SlateSurface,
                    unfocusedContainerColor = SlateSurface
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Format Filter: Semua, PDF, Gambar, Video
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFormatFilter == "SEMUA",
                    onClick = { selectedFormatFilter = "SEMUA" },
                    label = { Text("Semua Format", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFormatFilter == "PDF",
                    onClick = { selectedFormatFilter = "PDF" },
                    label = { Text("Dokumen PDF", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StatusError, modifier = Modifier.size(14.dp)) }
                )
                FilterChip(
                    selected = selectedFormatFilter == "GAMBAR",
                    onClick = { selectedFormatFilter = "GAMBAR" },
                    label = { Text("Lembar Gambar", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp)) }
                )
                FilterChip(
                    selected = selectedFormatFilter == "VIDEO",
                    onClick = { selectedFormatFilter = "VIDEO" },
                    label = { Text("Video", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SkySecondary, modifier = Modifier.size(14.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Khat Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedKhatFilter == "SEMUA",
                        onClick = { selectedKhatFilter = "SEMUA" },
                        label = { Text("Semua Khat", fontSize = 11.sp) }
                    )
                }
                items(khatList) { khat ->
                    FilterChip(
                        selected = selectedKhatFilter.equals(khat.id, ignoreCase = true),
                        onClick = { selectedKhatFilter = khat.id },
                        label = { Text(khat.nama.replace("Khat ", ""), fontSize = 11.sp) }
                    )
                }
            }

            // Huruf Filter Chips (if any huruf available)
            if (uniqueLetterNames.size > 1) {
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedHurufFilter == "SEMUA",
                            onClick = { selectedHurufFilter = "SEMUA" },
                            label = { Text("Semua Huruf", fontSize = 11.sp) }
                        )
                    }
                    items(uniqueLetterNames.filter { it != "SEMUA" }) { letterName ->
                        FilterChip(
                            selected = selectedHurufFilter.equals(letterName, ignoreCase = true),
                            onClick = { selectedHurufFilter = letterName },
                            label = { Text("Huruf $letterName", fontSize = 11.sp) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Materials
            if (filteredMateri.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.MenuBook,
                    title = "Belum Ada Materi",
                    subtitle = "Materi pembelajaran atau berkas untuk filter ini belum tersedia."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredMateri, key = { it.id }) { materi ->
                        MateriCard(
                            materi = materi,
                            canDelete = currentUser.role in listOf("ADMIN", "GURU"),
                            onDelete = { viewModel.hapusMateri(materi.id) },
                            onClick = { viewModel.openMateriDetail(materi) }
                        )
                    }
                }
            }
        }
    }

    // Add Materi Dialog with LINK / UPLOAD Mode + Per Huruf & 7 Khat Options
    if (showAddDialog) {
        AddMateriDialog(
            khatOptions = khatList.map { it.id to it.nama },
            letterOptions = hijaiyahList.map { it.id to it.nama }.distinctBy { it.second },
            onDismiss = { showAddDialog = false },
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
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MateriCard(
    materi: Materi,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val isVideo = materi.mimeType.contains("video") || materi.fileUrl.contains("youtube")
    val isImage = materi.mimeType.contains("image") || materi.fileName.endsWith(".png") || materi.fileName.endsWith(".jpg")
    val isPdf = materi.mimeType.contains("pdf") || materi.fileName.endsWith(".pdf")

    val icon = when {
        isVideo -> Icons.Default.PlayCircle
        isImage -> Icons.Default.Image
        else -> Icons.Default.PictureAsPdf
    }
    val iconColor = when {
        isVideo -> OnSkyContainer
        isImage -> EmeraldDark
        else -> StatusError
    }
    val boxColor = when {
        isVideo -> SkyContainer
        isImage -> EmeraldContainer
        else -> Color(0xFFFFEBEE)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(boxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = materi.khatNama,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnEmeraldContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (materi.hurufNama.isNotBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = AmberContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Huruf ${materi.hurufNama}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAmberContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPdf) "PDF" else if (isImage) "Gambar" else "Video",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = materi.judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SlateTextPrimary
                )

                Text(
                    text = materi.deskripsi,
                    fontSize = 12.sp,
                    color = SlateTextSecondary,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }

            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = StatusError)
                }
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun AddMateriDialog(
    khatOptions: List<Pair<String, String>>,
    letterOptions: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onSave: (
        judul: String,
        khatId: String,
        khatNama: String,
        hurufId: String,
        hurufNama: String,
        desc: String,
        sourceType: String,
        fileUrl: String,
        fileName: String,
        mimeType: String
    ) -> Unit
) {
    var scopeType by remember { mutableStateOf("KHAT") } // "KHAT" (umum) atau "HURUF" (khusus huruf)
    var selectedKhat by remember { mutableStateOf(khatOptions.firstOrNull() ?: ("naskhi" to "Khat Naskhi")) }
    var selectedLetter by remember { mutableStateOf(letterOptions.firstOrNull() ?: ("alif" to "Alif")) }

    var fileFormat by remember { mutableStateOf("PDF") } // "PDF", "GAMBAR", "VIDEO"
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var sourceType by remember { mutableStateOf("UPLOAD") } // LINK atau UPLOAD
    var fileUrl by remember { mutableStateOf("") }
    var uploadedFileName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Auto generate suggested file name & title
    LaunchedEffect(scopeType, selectedKhat, selectedLetter, fileFormat) {
        val extension = when (fileFormat) {
            "PDF" -> "pdf"
            "GAMBAR" -> "png"
            else -> "mp4"
        }
        if (scopeType == "HURUF") {
            uploadedFileName = "Lembar_${selectedLetter.second}_${selectedKhat.second.replace(" ", "_")}.$extension"
            if (judul.isBlank() || judul.startsWith("Kaidah ")) {
                judul = "Kaidah Huruf ${selectedLetter.second} - ${selectedKhat.second}"
            }
        } else {
            uploadedFileName = "Modul_Lengkap_${selectedKhat.second.replace(" ", "_")}.$extension"
            if (judul.isBlank() || judul.startsWith("Kaidah ")) {
                judul = "Panduan Lengkap ${selectedKhat.second}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Unggah Materi Baru ke Database", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Unggah berkas PDF, Gambar Lembar Kaidah, atau Video", fontSize = 11.sp, color = SlateTextSecondary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Scope Type: Materi Khat Umum vs Materi Khusus Huruf
                Text("Lingkup Materi Pembelajaran:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = scopeType == "KHAT",
                        onClick = { scopeType = "KHAT" },
                        label = { Text("Per Jenis Khat") }
                    )
                    FilterChip(
                        selected = scopeType == "HURUF",
                        onClick = { scopeType = "HURUF" },
                        label = { Text("Per Huruf Hijaiyah") }
                    )
                }

                // If Per Huruf, show Huruf Selector
                if (scopeType == "HURUF") {
                    Text("Pilih Huruf Hijaiyah:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(letterOptions) { letter ->
                            FilterChip(
                                selected = selectedLetter.second == letter.second,
                                onClick = { selectedLetter = letter },
                                label = { Text(letter.second, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Khat Selector
                Text("Pilih Ragam Khat (7 Jenis Khat):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(khatOptions) { opt ->
                        FilterChip(
                            selected = selectedKhat.first == opt.first,
                            onClick = { selectedKhat = opt },
                            label = { Text(opt.second.replace("Khat ", ""), fontSize = 11.sp) }
                        )
                    }
                }

                // File Format Selector: PDF, Gambar, Video
                Text("Pilih Format Berkas:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = fileFormat == "PDF",
                        onClick = { fileFormat = "PDF" },
                        label = { Text("PDF") },
                        leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StatusError, modifier = Modifier.size(15.dp)) }
                    )
                    FilterChip(
                        selected = fileFormat == "GAMBAR",
                        onClick = { fileFormat = "GAMBAR" },
                        label = { Text("Gambar") },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(15.dp)) }
                    )
                    FilterChip(
                        selected = fileFormat == "VIDEO",
                        onClick = { fileFormat = "VIDEO" },
                        label = { Text("Video") },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SkySecondary, modifier = Modifier.size(15.dp)) }
                    )
                }

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it; errorMessage = null },
                    label = { Text("Judul Materi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Kaidah / Catatan Pembelajaran") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Source Type: UPLOAD (Database / Storage) or LINK (Tautan Online)
                Text("Metode Penyimpanan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = sourceType == "UPLOAD",
                        onClick = { sourceType = "UPLOAD"; errorMessage = null },
                        label = { Text("Unggah Berkas (Database)") },
                        leadingIcon = { Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = sourceType == "LINK",
                        onClick = { sourceType = "LINK"; errorMessage = null },
                        label = { Text("Tautan Eksternal (URL)") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                if (sourceType == "UPLOAD") {
                    OutlinedTextField(
                        value = uploadedFileName,
                        onValueChange = { uploadedFileName = it },
                        label = { Text("Nama Berkas yang Disimpan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SlateSurfaceVariant,
                        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val icon = if (fileFormat == "PDF") Icons.Default.PictureAsPdf else if (fileFormat == "GAMBAR") Icons.Default.Image else Icons.Default.VideoFile
                            Icon(icon, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Berkas: $uploadedFileName",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = EmeraldDark
                            )
                            Text(
                                text = "Tersimpan ke database lokal aplikasi kaligrafi",
                                fontSize = 9.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = fileUrl,
                        onValueChange = { fileUrl = it; errorMessage = null },
                        label = { Text("URL Berkas / Media (https://...)") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = StatusError,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (judul.isBlank()) {
                        errorMessage = "Judul materi wajib diisi."
                        return@Button
                    }
                    if (sourceType == "LINK" && fileUrl.isBlank()) {
                        errorMessage = "URL wajib diisi jika memilih tautan eksternal."
                        return@Button
                    }

                    val computedMimeType = when (fileFormat) {
                        "PDF" -> "application/pdf"
                        "GAMBAR" -> "image/png"
                        else -> "video/mp4"
                    }

                    val resolvedFileUrl = if (sourceType == "LINK") fileUrl else "https://mahasyams.id/database/materi/${uploadedFileName}"
                    val resolvedFileName = if (sourceType == "LINK") "Tautan Eksternal" else uploadedFileName

                    onSave(
                        judul,
                        selectedKhat.first,
                        selectedKhat.second,
                        if (scopeType == "HURUF") selectedLetter.first else "",
                        if (scopeType == "HURUF") selectedLetter.second else "",
                        deskripsi.ifBlank { "Materi pembelajaran kaligrafi ${selectedKhat.second}" },
                        sourceType,
                        resolvedFileUrl,
                        resolvedFileName,
                        computedMimeType
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan ke Database")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

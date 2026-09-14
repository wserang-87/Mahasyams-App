package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HijaiyahLetter
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.components.CalligraphyCanvas
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijaiyahScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val hijaiyahList by viewModel.hijaiyahList.collectAsState()
    val selectedKhat by viewModel.selectedKhatInHijaiyah.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedLetterForDetail by remember { mutableStateOf<HijaiyahLetter?>(null) }
    var showAdminUploadDialogForLetter by remember { mutableStateOf<HijaiyahLetter?>(null) }

    val khatListOptions = listOf(
        Pair("Naskhi", "نسخي"),
        Pair("Tsuluts", "ثلث"),
        Pair("Diwani", "ديواني"),
        Pair("Diwani Jali", "ديواني جلي"),
        Pair("Farisi", "فارسي"),
        Pair("Nasta'liq", "نستعليق"),
        Pair("Kufi", "كوفي")
    )

    // Filter by selected Khat and search query
    val filteredList = remember(hijaiyahList, selectedKhat, searchQuery) {
        hijaiyahList.filter { letter ->
            val matchKhat = letter.khat.equals(selectedKhat, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                letter.nama.contains(searchQuery, ignoreCase = true) ||
                letter.huruf.contains(searchQuery)
            matchKhat && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Huruf Hijaiyah & 7 Khat (الحروف الهجائية)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SlateTextPrimary
                )
                Text(
                    text = "Pilih jenis khat dan huruf hijaiyah untuk mempelajari kaidah, diagram 4 posisi, serta berkas materi PDF & gambar",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7 Khat Horizontal Selector
        Text(
            text = "Pilih Jenis Khat (7 Ragam Klasik):",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SlateTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(khatListOptions) { (khatName, arabLabel) ->
                val isSelected = selectedKhat.equals(khatName, ignoreCase = true)
                Surface(
                    onClick = { viewModel.setKhatInHijaiyah(khatName) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) EmeraldPrimary else SlateSurface,
                    border = BorderStroke(1.dp, if (isSelected) EmeraldPrimary else SlateBorder),
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = khatName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = arabLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) AmberAccent else EmeraldPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar & Filter Summary
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari huruf (Alif, Ba, Jim...)", fontSize = 13.sp) },
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gaya: Khat $selectedKhat • ${filteredList.size} Huruf Tersedia",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = EmeraldDark
            )
            if (currentUser.role in listOf("ADMIN", "GURU")) {
                Surface(
                    color = AmberContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Mode Kelola / Upload Aktif",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnAmberContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid of 28 Letters
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 78.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.id }) { letter ->
                val hasFile = letter.fileUrl.isNotBlank() || letter.fileName.isNotBlank()
                Surface(
                    onClick = { selectedLetterForDetail = letter },
                    shape = RoundedCornerShape(14.dp),
                    color = SlateSurface,
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Letter badge & File indicator
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (hasFile) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (letter.mimeType.contains("pdf")) StatusError.copy(alpha = 0.15f) else EmeraldContainer,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = if (letter.mimeType.contains("pdf")) "PDF" else "Gbr",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (letter.mimeType.contains("pdf")) StatusError else EmeraldDark,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = letter.huruf,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = letter.nama,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog with 4 Positions Diagram & Attached PDF/Image Viewer
    selectedLetterForDetail?.let { letter ->
        HijaiyahDetailDialog(
            letter = letter,
            currentUser = currentUser,
            onDismiss = { selectedLetterForDetail = null },
            onOpenUploadDialog = {
                showAdminUploadDialogForLetter = letter
            }
        )
    }

    // Admin Upload / Update File Dialog for this specific letter and khat
    showAdminUploadDialogForLetter?.let { letter ->
        AdminUploadLetterFileDialog(
            letter = letter,
            onDismiss = { showAdminUploadDialogForLetter = null },
            onSave = { fileUrl, fileName, mimeType, notes ->
                viewModel.updateLetterFile(letter.id, fileUrl, fileName, mimeType, notes)
                // Refresh local state
                selectedLetterForDetail = letter.copy(
                    fileUrl = fileUrl,
                    fileName = fileName,
                    mimeType = mimeType,
                    catatanKaidah = notes
                )
                showAdminUploadDialogForLetter = null
            }
        )
    }
}

@Composable
private fun HijaiyahDetailDialog(
    letter: HijaiyahLetter,
    currentUser: User,
    onDismiss: () -> Unit,
    onOpenUploadDialog: () -> Unit
) {
    var showInteractivePractice by remember { mutableStateOf(false) }
    var showFileViewerPreview by remember { mutableStateOf(false) }
    var previewZoom by remember { mutableStateOf(100) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Huruf ${letter.nama}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDark
                    )
                    Text(
                        text = "Kaidah Gaya Khat ${letter.khat}",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldContainer
                ) {
                    Text(
                        text = letter.huruf,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 4 Positions Grid (Tunggal, Awal, Tengah, Akhir)
                Text(
                    text = "Bentuk 4 Posisi Tulisan (Khat ${letter.khat}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SlateTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PositionCard("TUNGGAL", letter.tunggalTeks, letter.tunggalDesc, Modifier.weight(1f))
                    PositionCard("AWAL", letter.awalTeks, letter.awalDesc, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PositionCard("TENGAH", letter.tengahTeks, letter.tengahDesc, Modifier.weight(1f))
                    PositionCard("AKHIR", letter.akhirTeks, letter.akhirDesc, Modifier.weight(1f))
                }

                // Kaidah Note
                Card(
                    colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Kaidah Ukuran Khat ${letter.khat}:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAmberContainer
                            )
                            Text(
                                text = letter.catatanKaidah,
                                fontSize = 12.sp,
                                color = OnAmberContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // File Attachment (PDF / Gambar) uploaded by Admin
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isPdf = letter.mimeType.contains("pdf") || letter.fileName.endsWith(".pdf")
                                Icon(
                                    imageVector = if (isPdf) Icons.Default.PictureAsPdf else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = if (isPdf) StatusError else EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = letter.fileName.ifBlank { "Lembar_Kaidah_${letter.nama}_${letter.khat}.png" },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateTextPrimary
                                    )
                                    Text(
                                        text = "Berkas Materi Database • ${if (isPdf) "Dokumen PDF" else "Gambar Lembar Kaidah"}",
                                        fontSize = 9.sp,
                                        color = SlateTextSecondary
                                    )
                                }
                            }

                            FilledTonalButton(
                                onClick = { showFileViewerPreview = !showFileViewerPreview },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(if (showFileViewerPreview) "Tutup" else "Lihat", fontSize = 10.sp)
                            }
                        }

                        // In-Dialog Visual File Preview
                        if (showFileViewerPreview) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFFDF8))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldContainer,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(
                                            text = "LEMBAR KAIDAH KHAT ${letter.khat.uppercase()}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnEmeraldContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${letter.huruf}  ـ${letter.huruf}ـ  ${letter.huruf}ـ",
                                        fontSize = (28 * (previewZoom / 100f)).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Skala Titik Nuqthah: ${letter.catatanKaidah.take(60)}...",
                                        fontSize = 10.sp,
                                        color = SlateTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Admin / Guru Upload Action
                if (currentUser.role in listOf("ADMIN", "GURU")) {
                    OutlinedButton(
                        onClick = onOpenUploadDialog,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary),
                        border = BorderStroke(1.dp, EmeraldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unggah / Perbarui Berkas PDF/Gambar Huruf Ini", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Interactive Practice Qalam toggle
                if (!showInteractivePractice) {
                    OutlinedButton(
                        onClick = { showInteractivePractice = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Coba Tulis dengan Qalam Digital", fontSize = 12.sp)
                    }
                } else {
                    CalligraphyCanvas(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun PositionCard(
    positionName: String,
    arabicText: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = EmeraldContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = positionName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnEmeraldContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFFDF8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = arabicText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 9.sp,
                color = SlateTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun AdminUploadLetterFileDialog(
    letter: HijaiyahLetter,
    onDismiss: () -> Unit,
    onSave: (fileUrl: String, fileName: String, mimeType: String, notes: String) -> Unit
) {
    var mimeType by remember { mutableStateOf(letter.mimeType.ifBlank { "application/pdf" }) }
    var fileName by remember { mutableStateOf(letter.fileName.ifBlank { "Kaidah_${letter.nama}_Khat_${letter.khat}.pdf" }) }
    var fileUrl by remember { mutableStateOf(letter.fileUrl.ifBlank { "https://mahasyams.id/database/materi/${letter.id}.${if (mimeType.contains("pdf")) "pdf" else "png"}" }) }
    var notes by remember { mutableStateOf(letter.catatanKaidah) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Unggah Berkas Materi Huruf", fontWeight = FontWeight.Bold)
                Text("Huruf ${letter.nama} • Khat ${letter.khat}", fontSize = 12.sp, color = EmeraldDark)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Pilih Jenis Format Berkas:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mimeType.contains("pdf"),
                        onClick = {
                            mimeType = "application/pdf"
                            fileName = "Kaidah_${letter.nama}_Khat_${letter.khat}.pdf"
                        },
                        label = { Text("Dokumen PDF") },
                        leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StatusError, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = mimeType.contains("image"),
                        onClick = {
                            mimeType = "image/png"
                            fileName = "Lembar_Kaidah_${letter.nama}_${letter.khat}.png"
                        },
                        label = { Text("Gambar Lembar") },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp)) }
                    )
                }

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Nama Berkas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fileUrl,
                    onValueChange = { fileUrl = it },
                    label = { Text("URL Berkas / Path Penyimpanan") },
                    placeholder = { Text("https://... atau internal storage path") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Kaidah Qalam & Ukuran") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SlateSurfaceVariant,
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Berkas akan tersimpan di database lokal untuk huruf & khat ini.",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(fileUrl, fileName, mimeType, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan ke Database")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.data.model.Tugas
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.components.CalligraphyCanvas
import com.example.ui.components.DeadlineBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTugasScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val tugas = viewModel.selectedTugas.collectAsState().value

    if (tugas == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pilih tugas terlebih dahulu.")
        }
        return
    }

    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    var showSubmitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // TopBar
        TopAppBar(
            title = { Text("Detail Tugas Kaligrafi", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo("TUGAS") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = EmeraldContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Khat ${tugas.khatId.uppercase()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnEmeraldContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        DeadlineBadge(deadlineMs = tugas.deadline)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = tugas.judul,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SlateTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Guru Pengajar: ${tugas.guruNama}",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = SlateBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Mulai:", fontSize = 11.sp, color = SlateTextSecondary)
                            Text(sdf.format(Date(tugas.mulai)), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateTextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Batas Akhir:", fontSize = 11.sp, color = SlateTextSecondary)
                            Text(sdf.format(Date(tugas.deadline)), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusError)
                        }
                    }
                }
            }

            // Instructions Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Instruksi Pengerjaan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tugas.deskripsi,
                        fontSize = 13.sp,
                        color = SlateTextPrimary,
                        lineHeight = 20.sp
                    )

                    if (tugas.fileUrl.isNotBlank() || tugas.fileName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SlateSurfaceVariant,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = EmeraldPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (tugas.fileName.isNotBlank()) tugas.fileName else "Berkas_Panduan_Tugas",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "${tugas.sourceType} • Panduan Format & Contoh Khat",
                                            fontSize = 10.sp,
                                            color = SlateTextSecondary
                                        )
                                    }
                                }
                                TextButton(onClick = {}) {
                                    Text("Buka", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Student Action Button
            if (currentUser.role == "SISWA") {
                Button(
                    onClick = { showSubmitDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kerjakan & Kumpulkan Karya Kaligrafi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Submit Karya Dialog with Canvas Drawing + Photo Upload + Preview (Masterplan Section 31, 58, 59)
    if (showSubmitDialog) {
        SubmitKaryaDialog(
            tugas = tugas,
            currentUser = currentUser,
            onDismiss = { showSubmitDialog = false },
            onSubmit = { catatan, drawingData, fileUrl, fileName ->
                viewModel.submitKarya(tugas, catatan, drawingData, fileUrl, fileName)
                showSubmitDialog = false
            }
        )
    }
}

@Composable
fun SubmitKaryaDialog(
    tugas: Tugas,
    currentUser: User,
    onDismiss: () -> Unit,
    onSubmit: (catatan: String, drawingData: String, fileUrl: String, fileName: String) -> Unit
) {
    var submissionMethod by remember { mutableStateOf("CANVAS") } // "CANVAS" atau "UPLOAD"
    var catatanSiswa by remember { mutableStateOf("") }
    var drawingData by remember { mutableStateOf("") }
    var simulatedUploadedFile by remember { mutableStateOf("karya_${currentUser.username}_khat_${tugas.khatId}.jpg") }
    var isPreviewStep by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isPreviewStep) "Preview Karya Sebelum Kirim" else "Kumpulkan Latihan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = tugas.judul,
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isPreviewStep) {
                    // Method Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = submissionMethod == "CANVAS",
                            onClick = { submissionMethod = "CANVAS" },
                            label = { Text("Gores Qalam Digital", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = submissionMethod == "UPLOAD",
                            onClick = { submissionMethod = "UPLOAD" },
                            label = { Text("Upload Foto Kertas", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (submissionMethod == "CANVAS") {
                        CalligraphyCanvas(
                            modifier = Modifier.fillMaxWidth(),
                            onStrokesChanged = { data -> drawingData = data }
                        )
                    } else {
                        // Upload component (Masterplan Section 58)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SlateSurfaceVariant,
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "📁 $simulatedUploadedFile",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "Ukuran: 2.1 MB • Format: JPG, PNG (Maks 8 MB)",
                                    fontSize = 10.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = catatanSiswa,
                        onValueChange = { catatanSiswa = it },
                        label = { Text("Catatan untuk Guru (Opsional)") },
                        placeholder = { Text("Contoh: Mohon koreksi tarikan huruf Ba baris ke-2") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Preview Step (Masterplan Section 59: "Siswa dapat melihat preview sebelum menekan Kirim")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFDF8))
                            .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ Siap dikirim ke Ustadz Ridwan",
                                fontSize = 12.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (submissionMethod == "CANVAS") "Goresan Digital Qalam ($drawingData)" else simulatedUploadedFile,
                                fontSize = 10.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }

                    if (catatanSiswa.isNotBlank()) {
                        Text(
                            text = "Catatan Anda: \"$catatanSiswa\"",
                            fontSize = 12.sp,
                            color = SlateTextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isPreviewStep) {
                Button(
                    onClick = { isPreviewStep = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Lihat Preview")
                }
            } else {
                Button(
                    onClick = {
                        onSubmit(
                            catatanSiswa,
                            if (submissionMethod == "CANVAS") (if (drawingData.isNotBlank()) drawingData else "STROKES:Active-Canvas") else "",
                            if (submissionMethod == "UPLOAD") "storage/latihan/${currentUser.uid}/${tugas.id}/$simulatedUploadedFile" else "",
                            simulatedUploadedFile
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Kirim Karya Sekarang")
                }
            }
        },
        dismissButton = {
            if (isPreviewStep) {
                TextButton(onClick = { isPreviewStep = false }) { Text("Ubah") }
            } else {
                TextButton(onClick = onDismiss) { Text("Batal") }
            }
        }
    )
}

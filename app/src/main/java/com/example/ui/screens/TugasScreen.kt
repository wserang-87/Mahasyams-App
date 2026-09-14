package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.Tugas
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.components.DeadlineBadge
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val tugasList by viewModel.tugasList.collectAsState()
    val khatList by viewModel.khatList.collectAsState()
    val materiList by viewModel.materiList.collectAsState()
    val mySubmissions by viewModel.mySubmissions.collectAsState()

    var showAddTugasDialog by remember { mutableStateOf(false) }
    var selectedKhatFilter by remember { mutableStateOf("SEMUA") }

    val filteredTugas = remember(tugasList, selectedKhatFilter) {
        if (selectedKhatFilter == "SEMUA") tugasList else {
            tugasList.filter { it.khatId.equals(selectedKhatFilter, ignoreCase = true) }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (currentUser.role in listOf("ADMIN", "GURU")) {
                FloatingActionButton(
                    onClick = { showAddTugasDialog = true },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
                }
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
                        text = "Tugas Kaligrafi",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Latihan berkala, pembinaan adab & teknik menulis khat",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Khat
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedKhatFilter == "SEMUA",
                        onClick = { selectedKhatFilter = "SEMUA" },
                        label = { Text("Semua Tugas") }
                    )
                }
                items(khatList) { khat ->
                    FilterChip(
                        selected = selectedKhatFilter.equals(khat.id, ignoreCase = true),
                        onClick = { selectedKhatFilter = khat.id },
                        label = { Text(khat.nama.replace("Khat ", "")) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredTugas.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Assignment,
                    title = "Belum Ada Tugas",
                    subtitle = "Saat ini belum ada tugas kaligrafi yang ditugaskan untuk kategori ini."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTugas, key = { it.id }) { tugas ->
                        val hasSubmitted = mySubmissions.any { it.tugasId == tugas.id }
                        TugasCard(
                            tugas = tugas,
                            hasSubmitted = hasSubmitted,
                            canDelete = currentUser.role in listOf("ADMIN", "GURU"),
                            onDelete = { viewModel.hapusTugas(tugas.id) },
                            onClick = { viewModel.openTugasDetail(tugas) }
                        )
                    }
                }
            }
        }
    }

    // Add Tugas Dialog
    if (showAddTugasDialog) {
        AddTugasDialog(
            khatOptions = khatList.map { it.id to it.nama },
            materiOptions = materiList.map { it.id to it.judul },
            onDismiss = { showAddTugasDialog = false },
            onSave = { judul, desc, materiId, khatId, deadlineDays, sourceType, fileUrl, fileName ->
                viewModel.tambahTugas(judul, desc, materiId, khatId, deadlineDays, sourceType, fileUrl, fileName)
                showAddTugasDialog = false
            }
        )
    }
}

@Composable
private fun TugasCard(
    tugas: Tugas,
    hasSubmitted: Boolean,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = tugas.khatId.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnEmeraldContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (hasSubmitted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = SkyContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Sudah Dikirim",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSkyContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                DeadlineBadge(deadlineMs = tugas.deadline, isCompleted = hasSubmitted)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tugas.judul,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = SlateTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tugas.deskripsi,
                fontSize = 12.sp,
                color = SlateTextSecondary,
                maxLines = 2,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Batas: ${sdf.format(Date(tugas.deadline))}",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = StatusError)
                        }
                    }
                    Text(
                        text = "Buka Tugas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddTugasDialog(
    khatOptions: List<Pair<String, String>>,
    materiOptions: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onSave: (judul: String, desc: String, materiId: String, khatId: String, deadlineDays: Int, sourceType: String, fileUrl: String, fileName: String) -> Unit
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var selectedKhat by remember { mutableStateOf(khatOptions.firstOrNull()?.first ?: "naskhi") }
    var selectedMateriId by remember { mutableStateOf(materiOptions.firstOrNull()?.first ?: "") }
    var deadlineDays by remember { mutableStateOf(5) }
    var sourceType by remember { mutableStateOf("LINK") }
    var fileUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Tugas Kaligrafi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = judul, onValueChange = { judul = it }, label = { Text("Judul Tugas") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, label = { Text("Instruksi Tugas") }, maxLines = 3, modifier = Modifier.fillMaxWidth())

                Text("Jenis Khat:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(khatOptions) { opt ->
                        FilterChip(
                            selected = selectedKhat == opt.first,
                            onClick = { selectedKhat = opt.first },
                            label = { Text(opt.second.replace("Khat ", ""), fontSize = 11.sp) }
                        )
                    }
                }

                Text("Tenggat Waktu:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3 to "3 Hari", 5 to "5 Hari", 7 to "7 Hari", 14 to "14 Hari").forEach { (days, label) ->
                        FilterChip(
                            selected = deadlineDays == days,
                            onClick = { deadlineDays = days },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Sumber Berkas Panduan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = sourceType == "LINK", onClick = { sourceType = "LINK" }, label = { Text("LINK") })
                    FilterChip(selected = sourceType == "UPLOAD", onClick = { sourceType = "UPLOAD" }, label = { Text("UPLOAD") })
                }

                if (sourceType == "LINK") {
                    OutlinedTextField(value = fileUrl, onValueChange = { fileUrl = it }, label = { Text("URL Panduan") }, placeholder = { Text("https://...") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (judul.isNotBlank()) {
                        onSave(judul, deskripsi, selectedMateriId, selectedKhat, deadlineDays, sourceType, fileUrl, "panduan_tugas.pdf")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan Tugas")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.Khat
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IstilahKhatScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val khatList by viewModel.khatList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedKhatForDetail by remember { mutableStateOf<Khat?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredKhat = remember(khatList, searchQuery) {
        if (searchQuery.isBlank()) khatList else {
            khatList.filter {
                it.nama.contains(searchQuery, ignoreCase = true) ||
                it.namaArab.contains(searchQuery, ignoreCase = true) ||
                it.karakteristik.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (currentUser.role in listOf("ADMIN", "GURU")) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Khat")
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
                        text = "Istilah & Ragam Khat",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Mengenal 7 jenis kaligrafi Islam klasik beserta kaidahnya",
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
                placeholder = { Text("Cari jenis khat (Naskhi, Tsuluts, dll)...", fontSize = 13.sp) },
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

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredKhat, key = { it.id }) { khat ->
                    KhatCard(
                        khat = khat,
                        onClick = { selectedKhatForDetail = khat }
                    )
                }
            }
        }
    }

    // Detail Modal Dialog
    selectedKhatForDetail?.let { khat ->
        KhatDetailDialog(
            khat = khat,
            onDismiss = { selectedKhatForDetail = null }
        )
    }

    // Add Khat Dialog for Guru/Admin
    if (showAddDialog) {
        AddKhatDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newKhat ->
                viewModel.selectedKhat.value = newKhat
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun KhatCard(
    khat: Khat,
    onClick: () -> Unit
) {
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
                Column {
                    Text(
                        text = khat.nama,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDark
                    )
                    Text(
                        text = "Tokoh: ${khat.tokoh.split(",").firstOrNull() ?: "-"}",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldContainer
                ) {
                    Text(
                        text = khat.namaArab,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sample Calligraphy Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFBF7))
                    .border(1.dp, AmberAccent.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = khat.contohTeksArab,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = khat.karakteristik,
                fontSize = 12.sp,
                color = SlateTextSecondary,
                maxLines = 2,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pelajari Selengkapnya",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
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

@Composable
private fun KhatDetailDialog(
    khat: Khat,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = khat.nama,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDark
                    )
                    Text(
                        text = khat.namaArab,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberAccent
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFDF8))
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = khat.contohTeksArab,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = khat.contohDeskripsi,
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                }

                item {
                    SectionDetailItem(title = "Sejarah", content = khat.sejarah, icon = Icons.Default.HistoryEdu)
                }
                item {
                    SectionDetailItem(title = "Karakteristik", content = khat.karakteristik, icon = Icons.Default.Brush)
                }
                item {
                    SectionDetailItem(title = "Ciri Khas & Kaidah", content = khat.ciriKhas, icon = Icons.Default.Straighten)
                }
                item {
                    SectionDetailItem(title = "Tokoh & Pelopor", content = khat.tokoh, icon = Icons.Default.Person)
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
private fun SectionDetailItem(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateSurfaceVariant)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EmeraldDark
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 12.sp,
            color = SlateTextPrimary,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun AddKhatDialog(
    onDismiss: () -> Unit,
    onSave: (Khat) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var namaArab by remember { mutableStateOf("") }
    var karakteristik by remember { mutableStateOf("") }
    var ciriKhas by remember { mutableStateOf("") }
    var tokoh by remember { mutableStateOf("") }
    var contohTeks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Istilah Khat", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Khat") }, singleLine = true)
                OutlinedTextField(value = namaArab, onValueChange = { namaArab = it }, label = { Text("Nama Arab") }, singleLine = true)
                OutlinedTextField(value = karakteristik, onValueChange = { karakteristik = it }, label = { Text("Karakteristik") })
                OutlinedTextField(value = ciriKhas, onValueChange = { ciriKhas = it }, label = { Text("Ciri Khas") })
                OutlinedTextField(value = tokoh, onValueChange = { tokoh = it }, label = { Text("Tokoh") }, singleLine = true)
                OutlinedTextField(value = contohTeks, onValueChange = { contohTeks = it }, label = { Text("Contoh Lafadz") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isNotBlank()) {
                        val id = nama.lowercase().replace(" ", "_")
                        onSave(Khat(id, nama, namaArab, "Khat pelengkap", karakteristik, ciriKhas, tokoh, "Contoh", contohTeks))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

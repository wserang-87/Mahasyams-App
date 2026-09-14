package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@Composable
fun KhathathScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val allSiswa by viewModel.allSiswa.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedClassFilter by remember { mutableStateOf("SEMUA") }

    val classes = remember(allSiswa) {
        listOf("SEMUA") + allSiswa.map { it.kelas }.distinct()
    }

    val filteredSiswa = remember(allSiswa, searchQuery, selectedClassFilter) {
        allSiswa.filter { siswa ->
            val matchQuery = searchQuery.isBlank() ||
                siswa.nama.contains(searchQuery, ignoreCase = true) ||
                siswa.email.contains(searchQuery, ignoreCase = true)
            val matchClass = selectedClassFilter == "SEMUA" || siswa.kelas == selectedClassFilter
            matchQuery && matchClass
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Daftar Khathath (Santri & Siswa)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
            Text(
                text = "Direktori murid kaligrafi, informasi kelas dan pembimbing",
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari nama siswa atau email...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
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

        // Class Filter Chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            classes.forEach { cls ->
                FilterChip(
                    selected = selectedClassFilter == cls,
                    onClick = { selectedClassFilter = cls },
                    label = { Text(if (cls == "SEMUA") "Semua Kelas" else "Kelas $cls", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredSiswa, key = { it.uid }) { siswa ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = siswa.nama.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EmeraldDark
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = siswa.nama,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SlateTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = SkyContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = siswa.kelas,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSkyContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = siswa.email,
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                            Text(
                                text = "Pembimbing: ${siswa.guruPembimbing}",
                                fontSize = 10.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = siswa.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnEmeraldContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

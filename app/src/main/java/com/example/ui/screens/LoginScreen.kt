package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: CalligraphyViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()

    var selectedRole by remember { mutableStateOf("SISWA") } // "ADMIN", "GURU", "SISWA"
    var usernameOrEmail by remember { mutableStateOf("ahmad@mahasyams.id") }
    var password by remember { mutableStateOf("123456") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    // Auto update default user field when switching role tab
    LaunchedEffect(selectedRole, allUsers) {
        val defaultUserForRole = when (selectedRole) {
            "ADMIN" -> allUsers.firstOrNull { it.role == "ADMIN" }
            "GURU" -> allUsers.firstOrNull { it.role == "GURU" }
            else -> allUsers.firstOrNull { it.role == "SISWA" }
        }
        if (defaultUserForRole != null) {
            usernameOrEmail = defaultUserForRole.email
        } else {
            usernameOrEmail = when (selectedRole) {
                "ADMIN" -> "admin@mahasyams.id"
                "GURU" -> "ridwan@mahasyams.id"
                else -> "ahmad@mahasyams.id"
            }
        }
        errorMessage = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Logo & Branding
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AmberContainer, EmeraldContainer)
                        )
                    )
                    .border(2.5.dp, AmberAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "Logo Mahasyams",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = appConfig.appName.ifBlank { "Mahasyams Kaligrafi" },
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = EmeraldDark
            )

            Text(
                text = "Pembelajaran Seni Khat Islam & 7 Ragam Kaidah",
                fontSize = 12.sp,
                color = SlateTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Role Selector Tabs (Admin, Guru, Siswa)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pilih Jenis Akun untuk Masuk:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleTabButton(
                            title = "Admin",
                            subtitle = "Pengelola",
                            icon = Icons.Default.AdminPanelSettings,
                            isSelected = selectedRole == "ADMIN",
                            activeColor = AmberAccent,
                            activeContainer = AmberContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = "ADMIN" }
                        )

                        RoleTabButton(
                            title = "Guru",
                            subtitle = "Khathath",
                            icon = Icons.Default.School,
                            isSelected = selectedRole == "GURU",
                            activeColor = SkySecondary,
                            activeContainer = SkyContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = "GURU" }
                        )

                        RoleTabButton(
                            title = "Siswa",
                            subtitle = "Santri",
                            icon = Icons.Default.Person,
                            isSelected = selectedRole == "SISWA",
                            activeColor = EmeraldPrimary,
                            activeContainer = EmeraldContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = "SISWA" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Login Input Form
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Masuk Sebagai $selectedRole",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = when (selectedRole) {
                            "ADMIN" -> AmberAccent
                            "GURU" -> SkySecondary
                            else -> EmeraldDark
                        }
                    )

                    OutlinedTextField(
                        value = usernameOrEmail,
                        onValueChange = {
                            usernameOrEmail = it
                            errorMessage = null
                        },
                        label = { Text("Email atau Username") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Kata Sandi") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Sembunyikan sandi" else "Tampilkan sandi"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                val success = viewModel.login(usernameOrEmail, selectedRole)
                                if (!success) {
                                    errorMessage = "Akun $usernameOrEmail untuk role $selectedRole tidak ditemukan."
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    color = StatusError,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val success = viewModel.login(usernameOrEmail, selectedRole)
                            if (!success) {
                                errorMessage = "Akun tidak ditemukan. Silakan gunakan salah satu akun demo di bawah."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (selectedRole) {
                                "ADMIN" -> AmberAccent
                                "GURU" -> SkySecondary
                                else -> EmeraldPrimary
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Masuk Sebagai $selectedRole",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick One-Click Login Cards for Admin, Guru, Siswa
            Text(
                text = "— ATAU MASUK CEPAT DENGAN SATU KLIK —",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // List of seeded accounts
            val sampleAccounts = remember(allUsers) {
                val admin = allUsers.firstOrNull { it.role == "ADMIN" } ?: User(
                    uid = "admin-01",
                    nama = "Ustadzah Fatimah (Admin)",
                    email = "admin@mahasyams.id",
                    username = "admin",
                    role = "ADMIN",
                    kelas = "Pusat Mahasyams"
                )
                val guru = allUsers.firstOrNull { it.role == "GURU" } ?: User(
                    uid = "guru-01",
                    nama = "Ustadz Ridwan Al-Khathath",
                    email = "ridwan@mahasyams.id",
                    username = "ridwan",
                    role = "GURU",
                    kelas = "Pengajar Utama"
                )
                val siswa = allUsers.firstOrNull { it.role == "SISWA" } ?: User(
                    uid = "siswa-01",
                    nama = "Ahmad Al-Mubarok",
                    email = "ahmad@mahasyams.id",
                    username = "ahmad",
                    role = "SISWA",
                    kelas = "IX A"
                )
                listOf(admin, guru, siswa)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sampleAccounts.forEach { user ->
                    val roleColor = when (user.role) {
                        "ADMIN" -> AmberAccent
                        "GURU" -> SkySecondary
                        else -> EmeraldPrimary
                    }
                    val roleContainer = when (user.role) {
                        "ADMIN" -> AmberContainer
                        "GURU" -> SkyContainer
                        else -> EmeraldContainer
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        border = BorderStroke(1.dp, roleColor.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.loginAsUser(user)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(roleContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (user.role) {
                                            "ADMIN" -> Icons.Default.AdminPanelSettings
                                            "GURU" -> Icons.Default.School
                                            else -> Icons.Default.Person
                                        },
                                        contentDescription = null,
                                        tint = roleColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.nama,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SlateTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = roleContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = user.role,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = roleColor,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${user.email} • ${user.kelas}",
                                        fontSize = 11.sp,
                                        color = SlateTextSecondary
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.loginAsUser(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = roleColor),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Masuk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RoleTabButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    activeContainer: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) activeContainer else SlateSurfaceVariant,
        border = if (isSelected) BorderStroke(1.5.dp, activeColor) else BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) activeColor else SlateTextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isSelected) activeColor else SlateTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = SlateTextSecondary
            )
        }
    }
}

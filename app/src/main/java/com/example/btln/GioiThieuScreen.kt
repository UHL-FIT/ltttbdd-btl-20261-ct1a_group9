package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GioiThieuScreen(isDarkMode: Boolean, onBack: () -> Unit) {
    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val cardColor = if (isDarkMode) Color(0xFF152642) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giới thiệu", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = textColor) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AboutCard(isDarkMode, "Về Ứng Dụng Pokedex", Icons.Default.Info, "Ứng dụng Pokedex của chúng tôi là một công cụ tra cứu toàn diện dành cho những người yêu thích Pokémon. Bạn có thể xem danh sách hơn 1000 Pokémon, tra cứu chi tiết chỉ số, hệ, và kỹ năng của từng loài.", cardColor, textColor)
            AboutCard(isDarkMode, "Pokémon là gì?", Icons.Default.CatchingPokemon, "Pokémon là những sinh vật kỳ lạ sống trong tự nhiên hoặc bên cạnh con người. Các nhà huấn luyện Pokémon bắt và huấn luyện chúng để thi đấu. Mỗi Pokémon có những đặc điểm và hệ riêng biệt như Lửa, Nước, Cỏ...", cardColor, textColor)
            AboutCard(isDarkMode, "Thông tin phiên bản", Icons.Default.Update, "Phiên bản hiện tại: v1.3.0\n\n- Cập nhật kiến trúc MVVM và Navigation chuẩn.\n- Tương khắc hệ chuyên sâu.\n- Chuỗi tiến hóa trực quan.", cardColor, textColor)

            Text("© 2026 Pokedex Project", modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally), color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun AboutCard(isDarkMode: Boolean, title: String, icon: ImageVector, content: String, cardColor: Color, textColor: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(title, color = Color(0xFF4FC3F7), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(content, color = textColor, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
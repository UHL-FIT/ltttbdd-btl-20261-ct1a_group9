package com.example.btln

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuongKhacScreen(isDarkMode: Boolean, onBack: () -> Unit) {
    val types = listOf("normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy")
    var selected by remember { mutableStateOf("fire") }
    var relations by remember { mutableStateOf<DamageRelations?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selected) {
        isLoading = true
        try { relations = RetrofitClient.service.getTypeDetail(selected).damage_relations } catch (e: Exception) {} finally { isLoading = false }
    }

    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val headerColor = if (isDarkMode) Color(0xFF152642) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    // Màu sắc tiêu đề thích ứng theo Mode
    val colorGreen = if (isDarkMode) Color(0xFF81C784) else Color(0xFF2E7D32)
    val colorYellow = if (isDarkMode) Color(0xFFFFF176) else Color(0xFFB8860B) // Đậm hơn ở bản sáng
    val colorRed = if (isDarkMode) Color(0xFFFF8A80) else Color(0xFFD32F2F)
    val colorCyan = if (isDarkMode) Color(0xFF4DD0E1) else Color(0xFF00838F)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tương Khắc Hệ", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = headerColor),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(padding)) {
            Text("Chọn hệ để tra cứu:", color = colorCyan, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold)

            LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(types) { t ->
                    val translated = translateType(t)
                    val typeColor = getTypeColor(translated)
                    Surface(
                        color = if (selected == t) typeColor else (if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.White),
                        shape = RoundedCornerShape(20.dp),
                        border = if (selected != t && !isDarkMode) BorderStroke(1.dp, Color.LightGray) else null,
                        modifier = Modifier.clickable { selected = t }
                    ) {
                        Text(
                            text = translated.uppercase(),
                            color = if (selected == t) Color.Black else textColor,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.Cyan) }
            } else if (relations != null) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(24.dp)) {

                    Text("KHI TẤN CÔNG (Gây sát thương)", color = colorCyan, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)

                    EffectBox(isDarkMode, "SIÊU HIỆU QUẢ (x2)", relations!!.double_damage_to, colorGreen)
                    EffectBox(isDarkMode, "KHÔNG HIỆU QUẢ (x0.5)", relations!!.half_damage_to, colorYellow)
                    EffectBox(isDarkMode, "VÔ DỤNG (x0)", relations!!.no_damage_to, colorRed)

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                    Text("KHI PHÒNG THỦ (Nhận sát thương)", color = colorCyan, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)

                    EffectBox(isDarkMode, "BỊ KHẮC CHẾ (Nhận x2)", relations!!.double_damage_from, colorRed)
                    EffectBox(isDarkMode, "KHÁNG TỐT (Nhận x0.5)", relations!!.half_damage_from, colorCyan)
                    EffectBox(isDarkMode, "MIỄN NHIỄM (Nhận x0)", relations!!.no_damage_from, if(isDarkMode) Color.White else Color.DarkGray)

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EffectBox(isDarkMode: Boolean, title: String, list: List<NamedResource>, color: Color) {
    Column {
        Text(title, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        if (list.isEmpty()) {
            Text("Không có", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        } else {
            FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                list.forEach {
                    val name = translateType(it.name)
                    val typeColor = getTypeColor(name)
                    Surface(
                        color = typeColor.copy(alpha = if (isDarkMode) 0.15f else 0.1f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, typeColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = name,
                            color = if (isDarkMode) typeColor else Color.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
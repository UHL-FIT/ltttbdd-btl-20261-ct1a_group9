package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoiHinhScreen(
    myTeam: List<PokemonResult?>,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onSlotClick: (Int) -> Unit,
    onPokemonClick: (PokemonResult) -> Unit
) {
    // Sử dụng trực tiếp giá trị isDarkMode truyền từ MainActivity để đảm bảo đồng bộ
    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val cardColor = if (isDarkMode) Color(0xFF152642) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đội Hình Chiến Đấu", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(padding)
                .padding(8.dp)
        ) {
            // Duyệt qua đúng 6 ô trong đội hình từ ViewModel truyền xuống
            items(6) { index ->
                val pokemon = myTeam[index]

                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(180.dp)
                        .clickable {
                            if (pokemon != null) onPokemonClick(pokemon) else onSlotClick(index)
                        },
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("SLOT 0${index + 1}", color = Color(0xFF4FC3F7), fontWeight = FontWeight.Bold)

                        if (pokemon != null) {
                            // Lấy ID từ URL để hiện ảnh
                            val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
                                modifier = Modifier.size(90.dp),
                                contentDescription = null
                            )
                            Text(
                                text = pokemon.name.replaceFirstChar { it.uppercase() },
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            // Hiện nút cộng nếu ô trống
                            Surface(
                                color = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
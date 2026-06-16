package com.example.btln

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PokemonDetailScreen(
    pokemon: PokemonResult,
    isDarkMode: Boolean,
    allPokemonList: List<PokemonResult>,
    myTeam: List<PokemonResult?>,
    onBackClick: () -> Unit,
    onPokemonClick: (PokemonResult) -> Unit,
    onAddToTeam: (PokemonResult, Int) -> Unit,
    onRemoveFromTeam: (Int) -> Unit
) {
    var detail by remember(pokemon) { mutableStateOf<PokemonDetailResponse?>(null) }
    var evolutionChain by remember(pokemon) { mutableStateOf<List<PokemonResult>>(emptyList()) }
    var isLoading by remember(pokemon) { mutableStateOf(true) }

    val id = pokemon.url.split("/").filter { it.isNotEmpty() }.lastOrNull() ?: "1"
    val scrollState = rememberScrollState()

    LaunchedEffect(pokemon) {
        isLoading = true
        try {
            val d = RetrofitClient.service.getPokemonDetail(pokemon.name)
            detail = d
            val species = RetrofitClient.service.getPokemonSpecies(d.species.name)
            val evoId = species.evolution_chain.url.split("/").filter { it.isNotEmpty() }.last().toInt()
            val evo = RetrofitClient.service.getEvolutionChain(evoId)

            val suffix = if (pokemon.name.contains("-")) "-" + pokemon.name.split("-").last() else ""
            val list = mutableListOf<PokemonResult>()
            fun traverse(link: ChainLink) {
                val name = link.species.name
                val matched = allPokemonList.find { it.name == "$name$suffix" } ?: allPokemonList.find { it.name == name } ?: PokemonResult(name, link.species.url)
                list.add(matched)
                if (link.evolves_to.isNotEmpty()) traverse(link.evolves_to[0])
            }
            traverse(evo.chain)
            evolutionChain = list
        } catch (e: Exception) {}
        isLoading = false
    }

    val bgColor = if (isDarkMode) Color(0xFF0A1428) else Color(0xFFF5F5F5)
    val cardColor = if (isDarkMode) Color(0xFF152642) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Column(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, null, tint = textColor)
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
                            modifier = Modifier.size(200.dp),
                            contentDescription = null
                        )
                        Text(
                            text = pokemon.name.uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )

                        if (detail != null) {
                            Row(
                                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                InfoBox(isDarkMode, "Hệ", detail!!.types.joinToString { translateType(it.type.name) }.uppercase())
                                InfoBox(isDarkMode, "Cao", "${detail!!.height / 10.0}m")
                                InfoBox(isDarkMode, "Nặng", "${detail!!.weight / 10.0}kg")
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 16.dp))

                            Text(
                                "ĐẶC TÍNH",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4FC3F7),
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                detail!!.abilities.forEach { slot ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(translateAbility(slot.ability.name) + if (slot.is_hidden) " (Ẩn)" else "", color = textColor) }
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 16.dp))

                            Text(
                                "CHỈ SỐ CƠ BẢN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4FC3F7),
                                modifier = Modifier.align(Alignment.Start)
                            )
                            detail!!.stats.forEach { s ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        translateStat(s.stat.name),
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(80.dp)
                                    )
                                    Text(
                                        s.base_stat.toString(),
                                        color = textColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(35.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { s.base_stat / 255f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp),
                                        color = if (s.base_stat < 60) Color.Red else if (s.base_stat < 100) Color.Yellow else Color.Green,
                                        trackColor = Color.Gray.copy(alpha = 0.1f)
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 16.dp))

                            Text(
                                "CHUỖI TIẾN HÓA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4FC3F7),
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                evolutionChain.forEachIndexed { i, p ->
                                    val eid = p.url.split("/").filter { it.isNotEmpty() }.last()
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable { onPokemonClick(p) }
                                    ) {
                                        Box(
                                            modifier = Modifier.size(60.dp)
                                                .background(if (p.name == pokemon.name) Color(0xFF4FC3F7).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(30.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$eid.png",
                                                modifier = Modifier.size(45.dp),
                                                contentDescription = null
                                            )
                                        }
                                        Text(
                                            p.name.replaceFirstChar { it.uppercase() },
                                            color = if (p.name == pokemon.name) Color(0xFF4FC3F7) else textColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                    if (i < evolutionChain.size - 1) Text("→", color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 16.dp))

                            Text(
                                "KỸ NĂNG CÓ THỂ HỌC",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4FC3F7),
                                modifier = Modifier.align(Alignment.Start)
                            )
                            detail!!.moves.take(6).chunked(2).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { m ->
                                        Card(
                                            modifier = Modifier.weight(1f),
                                            colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f))
                                        ) {
                                            Text(
                                                m.move.name.replace("-", " ").uppercase(),
                                                color = textColor,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                val teamIndex = myTeam.indexOfFirst { it?.name == pokemon.name }
                val isTeamFull = myTeam.all { it != null }

                if (teamIndex != -1) {
                    Button(
                        onClick = { onRemoveFromTeam(teamIndex) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Xóa khỏi đội hình", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (isTeamFull) {
                                onAddToTeam(pokemon, -1)
                            } else {
                                val empty = myTeam.indexOfFirst { it == null }
                                if (empty != -1) onAddToTeam(pokemon, empty)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTeamFull) Color.Gray else Color(0xFFFFC107)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            if (isTeamFull) "Đội hình đã đầy (6/6)" else "Thêm vào đội hình",
                            color = if (isTeamFull) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InfoBox(isDarkMode: Boolean, title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = if (isDarkMode) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

fun translateType(type: String): String = when (type.lowercase()) {
    "grass" -> "Cỏ"; "poison" -> "Độc"; "fire" -> "Lửa"; "water" -> "Nước"
    "electric" -> "Điện"; "psychic" -> "Siêu Linh"; "ice" -> "Băng"; "dragon" -> "Rồng"
    "dark" -> "Bóng Tối"; "fairy" -> "Tiên"; "normal" -> "Thường"; "fighting" -> "Giác Đấu"
    "flying" -> "Bay"; "ground" -> "Đất"; "rock" -> "Đá"; "bug" -> "Côn Trùng"
    "ghost" -> "Ma"; "steel" -> "Thép"
    else -> type.uppercase()
}

fun translateStat(stat: String): String = when (stat.lowercase()) {
    "hp" -> "HP"; "attack" -> "Tấn công"; "defense" -> "Phòng thủ"
    "special-attack" -> "ST. Công"; "special-defense" -> "ST. Thủ"; "speed" -> "Tốc độ"
    else -> stat.uppercase()
}

fun translateAbility(ability: String): String = ability.replace("-", " ").uppercase()
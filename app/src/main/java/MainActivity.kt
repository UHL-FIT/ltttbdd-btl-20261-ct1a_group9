package com.example.btln

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: PokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // === Ghi Log đúng chính xác theo ảnh yêu cầu ===
        Log.d("LifecycleCheck", "Hàm onCreate đã chạy")

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val isDarkModeStore by viewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = true)
            val dynamicBgColor = if (isDarkModeStore) Color(0xFF0A1428) else Color(0xFFF5F5F5)
            val dynamicTextColor = if (isDarkModeStore) Color.White else Color.Black

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val allPokemon by viewModel.allPokemon.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        scrimColor = Color.Black.copy(alpha = 0.6f),
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = dynamicBgColor,
                                modifier = Modifier.width(280.dp).fillMaxHeight()
                            ) {
                                Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                                    Text("Pokedex Menu", color = Color(0xFF4FC3F7), fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp))

                                    DrawerItem(Icons.Default.Home, "Trang chủ", dynamicTextColor) {
                                        scope.launch { drawerState.close() }
                                        if (navController.currentDestination?.route != "home") {
                                            navController.popBackStack("home", inclusive = false)
                                        }
                                    }

                                    DrawerItem(Icons.Default.Groups, "Đội hình", dynamicTextColor) {
                                        scope.launch { drawerState.close() }
                                        if (navController.currentDestination?.route != "team") {
                                            if (!navController.popBackStack("team", inclusive = false)) {
                                                navController.navigate("team")
                                            }
                                        }
                                    }

                                    DrawerItem(Icons.Default.BarChart, "Thống kê", dynamicTextColor) {
                                        scope.launch { drawerState.close() }
                                        if (navController.currentDestination?.route != "stats") {
                                            if (!navController.popBackStack("stats", inclusive = false)) {
                                                navController.navigate("stats")
                                            }
                                        }
                                    }

                                    DrawerItem(Icons.Default.Bolt, "Tương khắc", dynamicTextColor) {
                                        scope.launch { drawerState.close() }
                                        if (navController.currentDestination?.route != "types") {
                                            if (!navController.popBackStack("types", inclusive = false)) {
                                                navController.navigate("types")
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    DrawerItem(Icons.Default.Settings, "Cài đặt", dynamicTextColor) {
                                        scope.launch { drawerState.close() }
                                        if (navController.currentDestination?.route != "settings") {
                                            if (!navController.popBackStack("settings", inclusive = false)) {
                                                navController.navigate("settings")
                                            }
                                        }
                                    }

                                    DrawerItem(Icons.Default.Info, "Giới thiệu", dynamicTextColor) {
                                        scope.launch { drawerState.close() }
                                        if (navController.currentDestination?.route != "about") {
                                            if (!navController.popBackStack("about", inclusive = false)) {
                                                navController.navigate("about")
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.fillMaxSize(),
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None },
                            popEnterTransition = { EnterTransition.None },
                            popExitTransition = { ExitTransition.None }
                        ) {
                            composable("home") {
                                TrangChuScreen(
                                    allPokemon = allPokemon,
                                    isLoading = isLoading,
                                    isDarkMode = isDarkModeStore,
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onPokemonClick = { pokemon ->
                                        viewModel.setSelectedPokemon(pokemon)
                                        navController.navigate("detail")
                                    }
                                )
                            }
                            composable("detail") {
                                val selected by viewModel.selectedPokemon.collectAsStateWithLifecycle()
                                selected?.let { p ->
                                    PokemonDetailScreen(
                                        pokemon = p,
                                        isDarkMode = isDarkModeStore,
                                        allPokemonList = allPokemon,
                                        myTeam = viewModel.myTeam,
                                        onBackClick = { navController.popBackStack() },
                                        onPokemonClick = { viewModel.setSelectedPokemon(it) },
                                        onAddToTeam = { poke, slot -> viewModel.addToTeam(poke, slot) },
                                        onRemoveFromTeam = { slot -> viewModel.removeFromTeam(slot) }
                                    )
                                }
                            }
                            composable("team") {
                                DoiHinhScreen(
                                    myTeam = viewModel.myTeam,
                                    isDarkMode = isDarkModeStore,
                                    onBack = { navController.popBackStack() },
                                    onSlotClick = { slot ->
                                        viewModel.setTargetSlot(slot)
                                        navController.navigate("select")
                                    },
                                    onPokemonClick = { p ->
                                        viewModel.setSelectedPokemon(p)
                                        navController.navigate("detail")
                                    }
                                )
                            }
                            composable("select") {
                                PokemonSelectScreen(
                                    pokemonList = allPokemon,
                                    isDarkMode = isDarkModeStore,
                                    onSelect = { p ->
                                        viewModel.addToTeam(p, viewModel.targetSlot)
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable("stats") {
                                ThongKeScreen(
                                    isDarkMode = isDarkModeStore,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("types") {
                                TuongKhacScreen(
                                    isDarkMode = isDarkModeStore,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(
                                    isDarkMode = isDarkModeStore,
                                    onBack = { navController.popBackStack() },
                                    onToggleDark = { viewModel.toggleDarkMode(it) }
                                )
                            }
                            composable("about") {
                                GioiThieuScreen(
                                    isDarkMode = isDarkModeStore,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }

                    errorMessage?.let { message ->
                        Snackbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            action = {
                                TextButton(onClick = { viewModel.clearError() }) {
                                    Text("Đóng", color = Color.White)
                                }
                            },
                            containerColor = if (message.contains("✅") || message.contains("thành công"))
                                Color(0xFF4CAF50) else Color(0xFFF44336)
                        ) {
                            Text(message, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifecycleCheck", "Hàm onStart đã chạy")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifecycleCheck", "Hàm onResume đã chạy")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifecycleCheck", "Hàm onPause đã chạy")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifecycleCheck", "Hàm onStop đã chạy")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifecycleCheck", "Hàm onDestroy đã chạy")
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, null, tint = color) },
        label = { Text(label, color = color, fontSize = 16.sp) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
package com.example.ratonean2_app.navigation.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ratonean2_app.R
import com.example.ratonean2_app.list.presentation.view.ListScreen
import com.example.ratonean2_app.map.presentation.state.SearchUiState
import com.example.ratonean2_app.map.presentation.view.MapScreen
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.navigation.presentation.components.DrawerContent
import com.example.ratonean2_app.navigation.presentation.components.SearchBarHeader
import com.example.ratonean2_app.navigation.presentation.components.TabButton
import com.example.ratonean2_app.product.presentation.components.ProductCard
import com.example.ratonean2_app.product.presentation.viewmodel.ProductViewModel
import com.example.ratonean2_app.profile.ProfileScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("map") }

    val uiState by mapViewModel.state.collectAsStateWithLifecycle()
    var currentIndex by remember { mutableIntStateOf(0) }

    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(selectedTab) {
        val target = if (selectedTab == "map") 0 else 1
        if (pagerState.currentPage != target) {
            pagerState.animateScrollToPage(target)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                onDestinationClicked = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                when (page) {
                    0 -> MapScreen(
                        uiState = uiState,
                        onIntent = { mapViewModel.onIntent(it) }
                    )
                    1 -> ListScreen(
                        uiState = uiState,
                        onIntent = { mapViewModel.onIntent(it) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                SearchBarHeader(
                    query = query,
                    onQueryChange = { query = it },
                    active = active,
                    onActiveChange = { active = it },
                    drawerState = drawerState,
                    uiState = uiState,
                    onClearClick = { query = "" },
                    onIntent = { intent -> mapViewModel.onIntent(intent) },
                )

                if (!active) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TabButton("Mapa", selectedTab == "map") { selectedTab = "map" }
                        TabButton("Lista", selectedTab == "list") { selectedTab = "list" }
                    }
                }
            }

            if (selectedTab == "map" && uiState.searchProducts.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val products = uiState.searchProducts

                    if (products.size > 1) {
                        IconButton(
                            onClick = { currentIndex = if (currentIndex > 0) currentIndex - 1 else products.lastIndex },
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Anterior", tint = Color.Black)
                        }
                    }

                    val currentProduct = products.getOrNull(currentIndex)
                    currentProduct?.let { product ->
                        ProductCard(
                            storeLogoRes = R.drawable.markers_ratonean2,
                            productImageUrl = product.imageUrl ?: "",
                            productName = product.description,
                            productPrice = "$${product.listPrice}",
                            modifier = Modifier.width(280.dp).shadow(8.dp, RoundedCornerShape(16.dp))
                        )
                    }

                    if (products.size > 1) {
                        IconButton(
                            onClick = { currentIndex = if (currentIndex < products.lastIndex) currentIndex + 1 else 0 },
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
package com.example.composemultiplatform.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute

@Composable
fun NavRoot() {
    val navController = rememberNavController()
    DeepLinkListener(navController)
    NavHost(
        navController = navController,
        startDestination = Route.List
    ) {
        composable<Route.List> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ){
                items(100) {
                    Text(
                        text = "Item $it",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable{
                                navController.navigate(Route.Detail(it))
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
        composable<Route.Detail>(
            deepLinks = listOf(
                navDeepLink { this.uriPattern = "https://my-url.com/item/{id}" },
                navDeepLink { this.uriPattern = "poop://my-url.com/item/{id}" },
            )
        ){ backStackEntry ->
            val route = backStackEntry.toRoute<Route.Detail>()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Item ${route.id}")
            }
        }
    }
}
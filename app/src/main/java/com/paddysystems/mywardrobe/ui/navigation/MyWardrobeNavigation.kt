package com.paddysystems.mywardrobe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paddysystems.mywardrobe.WardrobeScreen
import com.paddysystems.mywardrobe.ui.screens.additem.AddItemScreen
import com.paddysystems.mywardrobe.ui.screens.itemdetails.ItemDetailsScreen

private object Routes {
    const val WARDROBE = "wardrobe"
    const val ADD_ITEM = "add-item"
    const val ITEM_DETAILS = "item-details"
}

@Composable
fun MyWardrobeNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WARDROBE,
        modifier = modifier
    ) {
        composable(Routes.WARDROBE) {
            WardrobeScreen(
                onAddItemClick = {
                    navController.navigate(Routes.ADD_ITEM)
                },
                onItemClick = {
                    navController.navigate(Routes.ITEM_DETAILS)
                }
            )
        }

        composable(Routes.ADD_ITEM) {
            AddItemScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ITEM_DETAILS) {
            ItemDetailsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
package com.paddysystems.mywardrobe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paddysystems.mywardrobe.WardrobeScreen
import com.paddysystems.mywardrobe.ui.screens.additem.AddItemScreen
import com.paddysystems.mywardrobe.ui.screens.edititem.EditItemScreen
import com.paddysystems.mywardrobe.ui.screens.itemdetails.ItemDetailsScreen

private object Routes {
    const val WARDROBE =
        "wardrobe"

    const val ADD_ITEM =
        "add-item"

    const val ITEM_DETAILS =
        "item-details/{itemId}"

    const val EDIT_ITEM =
        "edit-item/{itemId}"

    fun itemDetails(
        itemId: String
    ): String {
        return "item-details/$itemId"
    }

    fun editItem(
        itemId: String
    ): String {
        return "edit-item/$itemId"
    }
}

@Composable
fun MyWardrobeNavigation(
    modifier: Modifier = Modifier
) {
    val navController =
        rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WARDROBE,
        modifier = modifier
    ) {

        composable(
            Routes.WARDROBE
        ) {
            WardrobeScreen(
                onAddItemClick = {
                    navController.navigate(
                        Routes.ADD_ITEM
                    )
                },
                onItemClick = { item ->
                    navController.navigate(
                        Routes.itemDetails(
                            item.id
                        )
                    )
                }
            )
        }

        composable(
            Routes.ADD_ITEM
        ) {
            AddItemScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Routes.ITEM_DETAILS
        ) { backStackEntry ->

            val itemId =
                backStackEntry.arguments
                    ?.getString("itemId")
                    ?: return@composable

            ItemDetailsScreen(
                itemId = itemId,
                onEdit = {
                    navController.navigate(
                        Routes.editItem(
                            itemId
                        )
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Routes.EDIT_ITEM
        ) { backStackEntry ->

            val itemId =
                backStackEntry.arguments
                    ?.getString("itemId")
                    ?: return@composable

            EditItemScreen(
                itemId = itemId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
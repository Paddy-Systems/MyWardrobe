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
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import com.paddysystems.mywardrobe.ui.screens.createoutfit.CreateOutfitScreen
import com.paddysystems.mywardrobe.ui.screens.outfits.OutfitsScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue


private object Routes {
    const val WARDROBE =
        "wardrobe"

    const val ADD_ITEM =
        "add-item"

    const val ITEM_DETAILS =
        "item-details/{itemId}"

    const val EDIT_ITEM =
        "edit-item/{itemId}"

    const val CREATE_OUTFIT =
        "create-outfit"

    const val OUTFITS =
        "outfits"

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

    val backStackEntry by
    navController
        .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry
            ?.destination
            ?.route

    val showWardrobeOnRight =
        currentRoute ==
                Routes.OUTFITS

    val showWardrobeOnLeft =
        currentRoute !=
                Routes.WARDROBE &&
                currentRoute !=
                Routes.OUTFITS

    Scaffold(
        modifier = modifier,

        bottomBar = {
            MyWardrobeBottomBar(
                showWardrobeOnLeft =
                    showWardrobeOnLeft,

                showWardrobeOnRight =
                    showWardrobeOnRight,

                onCreateOutfit = {
                    navController.navigate(
                        Routes.CREATE_OUTFIT
                    ) {
                        launchSingleTop = true
                    }
                },

                onAddItem = {
                    navController.navigate(
                        Routes.ADD_ITEM
                    )
                },

                onViewOutfits = {
                    navController.navigate(
                        Routes.OUTFITS
                    ) {
                        launchSingleTop = true
                    }
                },

                onWardrobe = {
                    navController.popBackStack(
                        route = Routes.WARDROBE,
                        inclusive = false
                    )
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController =
                navController,

            startDestination =
                Routes.WARDROBE,

            modifier =
                Modifier.padding(
                    innerPadding
                )
        ) {

            composable(
                Routes.WARDROBE
            ) {
                WardrobeScreen(
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
                Routes.CREATE_OUTFIT
            ) {
                CreateOutfitScreen()
            }

            composable(
                Routes.OUTFITS
            ) {
                OutfitsScreen()
            }

            // Keep your existing
            // ADD_ITEM / ITEM_DETAILS /
            // EDIT_ITEM destinations here.

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
}
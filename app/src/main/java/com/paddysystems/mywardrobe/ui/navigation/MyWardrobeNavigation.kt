package com.paddysystems.mywardrobe.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paddysystems.mywardrobe.WardrobeScreen
import com.paddysystems.mywardrobe.ui.screens.additem.AddItemScreen
import com.paddysystems.mywardrobe.ui.screens.createoutfit.CreateOutfitScreen
import com.paddysystems.mywardrobe.ui.screens.edititem.EditItemScreen
import com.paddysystems.mywardrobe.ui.screens.itemdetails.ItemDetailsScreen
import com.paddysystems.mywardrobe.ui.screens.outfits.OutfitDetailsScreen
import com.paddysystems.mywardrobe.ui.screens.outfits.OutfitsScreen

private object Routes {
    const val WARDROBE = "wardrobe"
    const val ADD_ITEM = "add-item"
    const val ITEM_DETAILS = "item-details/{itemId}"
    const val EDIT_ITEM = "edit-item/{itemId}"
    const val CREATE_OUTFIT = "create-outfit"
    const val OUTFITS = "outfits"
    const val OUTFIT_DETAILS = "outfit-details/{outfitId}"
    const val EDIT_OUTFIT = "edit-outfit/{outfitId}"

    fun itemDetails(itemId: String): String = "item-details/$itemId"
    fun editItem(itemId: String): String = "edit-item/$itemId"
    fun outfitDetails(outfitId: String): String = "outfit-details/$outfitId"
    fun editOutfit(outfitId: String): String = "edit-outfit/$outfitId"
}

@Composable
fun MyWardrobeNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    var outfitsRefreshKey by remember {
        mutableIntStateOf(0)
    }

    var wardrobeRefreshKey by remember {
        mutableIntStateOf(0)
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val selectedDestination = when (currentRoute) {
        Routes.ADD_ITEM -> MyWardrobeDestination.ADD
        Routes.OUTFITS,
        Routes.CREATE_OUTFIT,
        Routes.OUTFIT_DETAILS,
        Routes.EDIT_OUTFIT -> MyWardrobeDestination.OUTFITS
        else -> MyWardrobeDestination.WARDROBE
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            MyWardrobeBottomBar(
                selectedDestination = selectedDestination,
                onAddItem = {
                    navController.navigate(Routes.ADD_ITEM)
                },
                onViewOutfits = {
                    navController.navigate(Routes.OUTFITS) {
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
            navController = navController,
            startDestination = Routes.WARDROBE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.WARDROBE) {
                WardrobeScreen(
                    refreshKey = wardrobeRefreshKey,
                    onWardrobeChanged = {
                        wardrobeRefreshKey++
                        outfitsRefreshKey++
                    },
                    onItemClick = { item ->
                        navController.navigate(Routes.itemDetails(item.id))
                    }
                )
            }

            composable(Routes.CREATE_OUTFIT) {
                CreateOutfitScreen(
                    onSaved = {
                        outfitsRefreshKey++
                        wardrobeRefreshKey++
                        navController.navigate(Routes.OUTFITS) {
                            popUpTo(Routes.CREATE_OUTFIT) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.OUTFITS) {
                OutfitsScreen(
                    refreshKey = outfitsRefreshKey,
                    onCreateOutfit = {
                        navController.navigate(Routes.CREATE_OUTFIT)
                    },
                    onOutfitClick = { outfitId ->
                        navController.navigate(Routes.outfitDetails(outfitId))
                    }
                )
            }

            composable(Routes.OUTFIT_DETAILS) { entry ->
                val outfitId = entry.arguments
                    ?.getString("outfitId")
                    ?: return@composable

                OutfitDetailsScreen(
                    outfitId = outfitId,
                    refreshKey = outfitsRefreshKey,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEdit = {
                        navController.navigate(Routes.editOutfit(outfitId))
                    },
                    onItemClick = { itemId ->
                        navController.navigate(Routes.itemDetails(itemId))
                    },
                    onChanged = {
                        outfitsRefreshKey++
                        wardrobeRefreshKey++
                    },
                    onDeleted = {
                        outfitsRefreshKey++
                        wardrobeRefreshKey++
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.EDIT_OUTFIT) { entry ->
                val outfitId = entry.arguments
                    ?.getString("outfitId")
                    ?: return@composable

                CreateOutfitScreen(
                    outfitId = outfitId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        outfitsRefreshKey++
                        wardrobeRefreshKey++
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.ADD_ITEM) {
                AddItemScreen(
                    onBack = {
                        // Successful Add Item currently exits through this callback,
                        // so refreshing here makes the new item visible immediately.
                        wardrobeRefreshKey++
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.ITEM_DETAILS) { entry ->
                val itemId = entry.arguments
                    ?.getString("itemId")
                    ?: return@composable

                ItemDetailsScreen(
                    itemId = itemId,
                    refreshKey = wardrobeRefreshKey,
                    onEdit = {
                        navController.navigate(Routes.editItem(itemId))
                    },
                    onOutfitClick = { outfitId ->
                        navController.navigate(Routes.outfitDetails(outfitId))
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.EDIT_ITEM) { entry ->
                val itemId = entry.arguments
                    ?.getString("itemId")
                    ?: return@composable

                EditItemScreen(
                    itemId = itemId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        wardrobeRefreshKey++
                        outfitsRefreshKey++
                        navController.popBackStack()
                    },
                    onDeleted = {
                        wardrobeRefreshKey++
                        outfitsRefreshKey++
                        navController.popBackStack(
                            route = Routes.WARDROBE,
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}

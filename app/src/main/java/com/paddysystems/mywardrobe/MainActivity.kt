package com.paddysystems.mywardrobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paddysystems.mywardrobe.ui.theme.MyWardrobeTheme
import com.paddysystems.mywardrobe.ui.navigation.MyWardrobeNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWardrobeTheme {
                MyWardrobeNavigation()
            }
        }
    }
}

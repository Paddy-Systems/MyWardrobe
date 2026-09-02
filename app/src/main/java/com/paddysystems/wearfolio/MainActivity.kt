package com.paddysystems.wearfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.paddysystems.wearfolio.ui.theme.WearfolioTheme
import com.paddysystems.wearfolio.ui.WearfolioApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WearfolioTheme {
                WearfolioApp()
            }
        }
    }
}

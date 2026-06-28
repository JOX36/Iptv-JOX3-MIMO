package com.jox3.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jox3.tv.ui.navigation.Jox3NavHost
import com.jox3.tv.ui.theme.Jox3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jox3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Jox3NavHost()
                }
            }
        }
    }
}

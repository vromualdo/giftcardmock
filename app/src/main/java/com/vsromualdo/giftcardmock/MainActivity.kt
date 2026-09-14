package com.vsromualdo.giftcardmock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vsromualdo.giftcardmock.navigation.GiftCardNavHost
import com.vsromualdo.giftcardmock.ui.theme.GiftcardMockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GiftcardMockApplication

        setContent {
            GiftcardMockTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GiftCardNavHost(repository = app.repository, authRepository = app.authRepository)
                }
            }
        }
    }
}

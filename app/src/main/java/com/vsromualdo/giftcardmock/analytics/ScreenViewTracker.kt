package com.vsromualdo.giftcardmock.analytics

import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

private val screenNamesByRoute = mapOf(
    "login" to "Login",
    "catalog" to "Catalog",
    "detail/{giftCardId}" to "Detail",
    "wallet" to "Wallet",
)

fun trackScreenView(route: String?) {
    val screenName = screenNamesByRoute[route] ?: return
    runCatching {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }
}

fun NavController.addScreenViewTracking(): NavController.OnDestinationChangedListener {
    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        trackScreenView(destination.route)
    }
    addOnDestinationChangedListener(listener)
    return listener
}

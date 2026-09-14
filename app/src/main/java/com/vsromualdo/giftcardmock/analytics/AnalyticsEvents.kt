package com.vsromualdo.giftcardmock.analytics

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

fun trackLoginFailed() {
    runCatching { Firebase.analytics.logEvent("login_failed") {} }
}

fun trackLoginSuccess() {
    runCatching { Firebase.analytics.logEvent("login_success") {} }
}

fun trackGiftCardPurchase(brand: String, amount: Int) {
    runCatching {
        Firebase.analytics.logEvent("giftcard_purchase") {
            param("brand", brand)
            param("amount", amount.toLong())
        }
    }
}

fun trackGiftCardRedeem(brand: String, amount: Int) {
    runCatching {
        Firebase.analytics.logEvent("giftcard_redeem") {
            param("brand", brand)
            param("amount", amount.toLong())
        }
    }
}

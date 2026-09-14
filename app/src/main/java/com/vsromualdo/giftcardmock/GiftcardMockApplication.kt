package com.vsromualdo.giftcardmock

import android.app.Application
import com.vsromualdo.giftcardmock.data.repository.AuthRepository
import com.vsromualdo.giftcardmock.data.repository.GiftCardRepository

class GiftcardMockApplication : Application() {
    val repository: GiftCardRepository by lazy { GiftCardRepository() }
    val authRepository: AuthRepository by lazy { AuthRepository() }
}

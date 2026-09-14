package com.vsromualdo.giftcardmock.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vsromualdo.giftcardmock.analytics.aws.sendAwsEvent
import com.vsromualdo.giftcardmock.analytics.trackGiftCardRedeem
import com.vsromualdo.giftcardmock.data.model.GiftCardStatus
import com.vsromualdo.giftcardmock.data.model.PurchasedGiftCard
import com.vsromualdo.giftcardmock.data.repository.GiftCardRepository
import kotlinx.coroutines.flow.StateFlow

class WalletViewModel(private val repository: GiftCardRepository) : ViewModel() {

    val wallet: StateFlow<List<PurchasedGiftCard>> = repository.wallet

    fun redeem(purchasedId: String) {
        val item = wallet.value.firstOrNull { it.id == purchasedId }
        repository.redeem(purchasedId)
        if (item != null && item.status == GiftCardStatus.ACTIVE) {
            trackGiftCardRedeem(item.giftCard.brand, item.amount)
            sendAwsEvent(
                eventCategory = "TODO_CATEGORY_GIFTCARD",
                eventAction = "TODO_ACTION_REDEEM",
                eventLabel = "Resgate de giftcard: ${item.giftCard.brand} - R$ ${item.amount}",
            )
        }
    }

    companion object {
        fun factory(repository: GiftCardRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { WalletViewModel(repository) }
        }
    }
}

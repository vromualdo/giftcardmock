package com.vsromualdo.giftcardmock.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vsromualdo.giftcardmock.analytics.aws.sendAwsEvent
import com.vsromualdo.giftcardmock.analytics.trackGiftCardPurchase
import com.vsromualdo.giftcardmock.data.model.GiftCard
import com.vsromualdo.giftcardmock.data.model.PurchasedGiftCard
import com.vsromualdo.giftcardmock.data.repository.GiftCardRepository

class DetailViewModel(
    private val repository: GiftCardRepository,
    private val giftCardId: String,
) : ViewModel() {

    val giftCard: GiftCard? = repository.findById(giftCardId)

    var selectedDenomination by mutableStateOf(giftCard?.denominations?.firstOrNull())
        private set

    fun selectDenomination(value: Int) {
        selectedDenomination = value
    }

    fun purchase(): PurchasedGiftCard? {
        val amount = selectedDenomination ?: return null
        val purchased = repository.purchase(giftCardId, amount)
        if (purchased != null) {
            trackGiftCardPurchase(purchased.giftCard.brand, purchased.amount)
            sendAwsEvent(
                eventCategory = "TODO_CATEGORY_GIFTCARD",
                eventAction = "TODO_ACTION_PURCHASE",
                eventLabel = "Compra de giftcard: ${purchased.giftCard.brand} - R$ ${purchased.amount}",
            )
        }
        return purchased
    }

    companion object {
        fun factory(repository: GiftCardRepository, giftCardId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { DetailViewModel(repository, giftCardId) }
            }
    }
}

package com.vsromualdo.giftcardmock.data.model

enum class GiftCardStatus { ACTIVE, REDEEMED }

data class PurchasedGiftCard(
    val id: String,
    val giftCard: GiftCard,
    val amount: Int,
    val code: String,
    val status: GiftCardStatus,
)

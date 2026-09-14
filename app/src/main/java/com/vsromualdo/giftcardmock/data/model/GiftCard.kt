package com.vsromualdo.giftcardmock.data.model

data class GiftCard(
    val id: String,
    val brand: String,
    val colorHex: String,
    val description: String,
    val denominations: List<Int>,
)

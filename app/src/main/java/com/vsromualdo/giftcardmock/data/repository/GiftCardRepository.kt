package com.vsromualdo.giftcardmock.data.repository

import com.vsromualdo.giftcardmock.data.model.GiftCard
import com.vsromualdo.giftcardmock.data.model.GiftCardStatus
import com.vsromualdo.giftcardmock.data.model.PurchasedGiftCard
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GiftCardRepository {

    private val mockCatalog = listOf(
        GiftCard(
            id = "netshop",
            brand = "NetShop",
            colorHex = "#E53935",
            description = "Compre produtos eletrônicos e de casa em uma das maiores lojas online.",
            denominations = listOf(25, 50, 100, 200),
        ),
        GiftCard(
            id = "playzone",
            brand = "PlayZone",
            colorHex = "#1E88E5",
            description = "Créditos para jogos, DLCs e assinaturas na maior loja de games.",
            denominations = listOf(20, 50, 100),
        ),
        GiftCard(
            id = "streamplus",
            brand = "StreamPlus",
            colorHex = "#8E24AA",
            description = "Assine ou renove seu plano de streaming favorito.",
            denominations = listOf(30, 60, 90),
        ),
        GiftCard(
            id = "foodie",
            brand = "Foodie Express",
            colorHex = "#FB8C00",
            description = "Peça comida dos melhores restaurantes da sua cidade.",
            denominations = listOf(25, 50, 75, 150),
        ),
        GiftCard(
            id = "bookhaven",
            brand = "Book Haven",
            colorHex = "#00897B",
            description = "Livros físicos e digitais para todos os gostos.",
            denominations = listOf(20, 40, 80),
        ),
        GiftCard(
            id = "fitgo",
            brand = "FitGo",
            colorHex = "#43A047",
            description = "Equipamentos e roupas esportivas para o seu treino.",
            denominations = listOf(30, 60, 120),
        ),
    )

    private val _catalog = MutableStateFlow(mockCatalog)
    val catalog: StateFlow<List<GiftCard>> = _catalog.asStateFlow()

    private val _wallet = MutableStateFlow<List<PurchasedGiftCard>>(emptyList())
    val wallet: StateFlow<List<PurchasedGiftCard>> = _wallet.asStateFlow()

    fun findById(giftCardId: String): GiftCard? =
        _catalog.value.firstOrNull { it.id == giftCardId }

    fun purchase(giftCardId: String, amount: Int): PurchasedGiftCard? {
        val giftCard = findById(giftCardId) ?: return null
        val purchased = PurchasedGiftCard(
            id = UUID.randomUUID().toString(),
            giftCard = giftCard,
            amount = amount,
            code = generateMockCode(),
            status = GiftCardStatus.ACTIVE,
        )
        _wallet.value = _wallet.value + purchased
        return purchased
    }

    fun redeem(purchasedId: String) {
        _wallet.value = _wallet.value.map { purchased ->
            if (purchased.id == purchasedId && purchased.status == GiftCardStatus.ACTIVE) {
                purchased.copy(status = GiftCardStatus.REDEEMED)
            } else {
                purchased
            }
        }
    }

    private fun generateMockCode(): String =
        UUID.randomUUID().toString().replace("-", "").take(12).uppercase().chunked(4).joinToString("-")
}

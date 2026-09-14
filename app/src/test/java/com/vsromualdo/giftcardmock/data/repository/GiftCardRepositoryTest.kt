package com.vsromualdo.giftcardmock.data.repository

import com.vsromualdo.giftcardmock.data.model.GiftCardStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GiftCardRepositoryTest {

    @Test
    fun `purchase adds an active card to the wallet`() {
        val repository = GiftCardRepository()
        val giftCard = repository.catalog.value.first()

        val purchased = repository.purchase(giftCard.id, giftCard.denominations.first())

        assertNotNull(purchased)
        assertEquals(GiftCardStatus.ACTIVE, purchased!!.status)
        assertTrue(repository.wallet.value.any { it.id == purchased.id })
    }

    @Test
    fun `redeem marks the card as redeemed and is idempotent`() {
        val repository = GiftCardRepository()
        val giftCard = repository.catalog.value.first()
        val purchased = repository.purchase(giftCard.id, giftCard.denominations.first())!!

        repository.redeem(purchased.id)
        val afterFirstRedeem = repository.wallet.value.first { it.id == purchased.id }
        assertEquals(GiftCardStatus.REDEEMED, afterFirstRedeem.status)

        repository.redeem(purchased.id)
        val afterSecondRedeem = repository.wallet.value.first { it.id == purchased.id }
        assertEquals(GiftCardStatus.REDEEMED, afterSecondRedeem.status)
        assertEquals(1, repository.wallet.value.count { it.id == purchased.id })
    }

    @Test
    fun `purchase with unknown giftCardId returns null and does not touch wallet`() {
        val repository = GiftCardRepository()

        val purchased = repository.purchase("unknown-id", 50)

        assertEquals(null, purchased)
        assertTrue(repository.wallet.value.isEmpty())
    }
}

package com.vsromualdo.giftcardmock.ui.catalog

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.vsromualdo.giftcardmock.data.repository.GiftCardRepository
import org.junit.Rule
import org.junit.Test

class CatalogScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun catalogScreen_rendersMockedBrands() {
        val repository = GiftCardRepository()
        val viewModel = CatalogViewModel(repository)

        composeTestRule.setContent {
            CatalogScreen(
                viewModel = viewModel,
                onGiftCardClick = {},
                onWalletClick = {},
            )
        }

        val firstBrand = repository.catalog.value.first().brand
        composeTestRule.onNodeWithText(firstBrand).assertExists()
    }
}

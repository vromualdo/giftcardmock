package com.vsromualdo.giftcardmock.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vsromualdo.giftcardmock.data.model.GiftCard
import com.vsromualdo.giftcardmock.data.repository.GiftCardRepository
import kotlinx.coroutines.flow.StateFlow

class CatalogViewModel(repository: GiftCardRepository) : ViewModel() {

    val catalog: StateFlow<List<GiftCard>> = repository.catalog

    companion object {
        fun factory(repository: GiftCardRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { CatalogViewModel(repository) }
        }
    }
}

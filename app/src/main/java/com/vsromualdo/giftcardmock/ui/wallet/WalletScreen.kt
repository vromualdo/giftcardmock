package com.vsromualdo.giftcardmock.ui.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsromualdo.giftcardmock.data.model.GiftCardStatus
import com.vsromualdo.giftcardmock.data.model.PurchasedGiftCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onBackClick: () -> Unit,
) {
    val wallet by viewModel.wallet.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Carteira") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        if (wallet.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            ) {
                Text("Você ainda não comprou nenhum giftcard.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(wallet, key = { it.id }) { purchased ->
                PurchasedGiftCardItem(
                    purchased = purchased,
                    onRedeemClick = { viewModel.redeem(purchased.id) },
                )
            }
        }
    }
}

@Composable
private fun PurchasedGiftCardItem(
    purchased: PurchasedGiftCard,
    onRedeemClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = purchased.giftCard.brand, style = MaterialTheme.typography.titleMedium)
            Text(text = "Valor: R$ ${purchased.amount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Código: ${purchased.code}", style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val statusLabel = if (purchased.status == GiftCardStatus.ACTIVE) "Ativo" else "Resgatado"
                Text(text = "Status: $statusLabel", style = MaterialTheme.typography.bodyMedium)

                TextButton(
                    onClick = onRedeemClick,
                    enabled = purchased.status == GiftCardStatus.ACTIVE,
                ) {
                    Text("Resgatar")
                }
            }
        }
    }
}

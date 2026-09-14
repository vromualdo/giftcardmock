package com.vsromualdo.giftcardmock.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    onPurchased: () -> Unit,
) {
    val giftCard = viewModel.giftCard

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(giftCard?.brand ?: "Giftcard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        if (giftCard == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            ) {
                Text("Giftcard não encontrado.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = giftCard.description, style = MaterialTheme.typography.bodyLarge)

            Text(text = "Escolha um valor", style = MaterialTheme.typography.titleMedium)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            ) {
                items(giftCard.denominations) { denomination ->
                    FilterChip(
                        selected = viewModel.selectedDenomination == denomination,
                        onClick = { viewModel.selectDenomination(denomination) },
                        label = { Text("R$ $denomination") },
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.purchase()
                    onPurchased()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Comprar")
            }
        }
    }
}

package com.vsromualdo.giftcardmock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vsromualdo.giftcardmock.analytics.addScreenViewTracking
import com.vsromualdo.giftcardmock.data.repository.AuthRepository
import com.vsromualdo.giftcardmock.data.repository.GiftCardRepository
import com.vsromualdo.giftcardmock.ui.catalog.CatalogScreen
import com.vsromualdo.giftcardmock.ui.catalog.CatalogViewModel
import com.vsromualdo.giftcardmock.ui.detail.DetailScreen
import com.vsromualdo.giftcardmock.ui.detail.DetailViewModel
import com.vsromualdo.giftcardmock.ui.login.LoginScreen
import com.vsromualdo.giftcardmock.ui.login.LoginViewModel
import com.vsromualdo.giftcardmock.ui.wallet.WalletScreen
import com.vsromualdo.giftcardmock.ui.wallet.WalletViewModel

private const val ROUTE_LOGIN = "login"
private const val ROUTE_CATALOG = "catalog"
private const val ROUTE_WALLET = "wallet"
private const val ROUTE_DETAIL = "detail/{giftCardId}"
private const val ARG_GIFT_CARD_ID = "giftCardId"

@Composable
fun GiftCardNavHost(
    repository: GiftCardRepository,
    authRepository: AuthRepository,
    navController: NavHostController = rememberNavController(),
) {
    DisposableEffect(navController) {
        val listener = navController.addScreenViewTracking()
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    NavHost(navController = navController, startDestination = ROUTE_LOGIN) {
        composable(ROUTE_LOGIN) {
            val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(authRepository))
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(ROUTE_CATALOG) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(ROUTE_CATALOG) {
            val viewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.factory(repository))
            CatalogScreen(
                viewModel = viewModel,
                onGiftCardClick = { giftCardId -> navController.navigate("detail/$giftCardId") },
                onWalletClick = { navController.navigate(ROUTE_WALLET) },
            )
        }
        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument(ARG_GIFT_CARD_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val giftCardId = backStackEntry.arguments?.getString(ARG_GIFT_CARD_ID).orEmpty()
            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.factory(repository, giftCardId),
            )
            DetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPurchased = {
                    navController.navigate(ROUTE_WALLET) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(ROUTE_WALLET) {
            val viewModel: WalletViewModel = viewModel(factory = WalletViewModel.factory(repository))
            WalletScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}

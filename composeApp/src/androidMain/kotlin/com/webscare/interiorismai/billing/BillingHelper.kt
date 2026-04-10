package com.webscare.interiorismai.billing

import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.*
import com.webscare.interiorismai.utils.AppContext

actual class BillingHelper actual constructor(
    private val onProductsLoaded: (List<PurchaseProduct>) -> Unit,
    private val onPurchaseComplete: (productId: String, credits: Int) -> Unit,
    private val onPurchaseCancelled: () -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var productDetailsList = listOf<ProductDetails>()

    private val billingClient = BillingClient.newBuilder(AppContext.get())
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .setListener { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { handlePurchase(it) }
            }else {
                mainHandler.post { onPurchaseCancelled() }
            }
        }
        .build()

    actual fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }
        })
    }

    private fun queryProducts() {
        val products = listOf("credits_basic", "credits_standard", "credits_pro").map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(products).build()
        ) { result, detailsResult ->
            println("🔴 BILLING: responseCode=${result.responseCode}")
            println("🔴 BILLING: message=${result.debugMessage}")
            println("🔴 BILLING: productsCount=${detailsResult.productDetailsList?.size}")

            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList = detailsResult.productDetailsList ?: emptyList()
                val mapped = productDetailsList.map { p ->
                    PurchaseProduct(
                        productId = p.productId,
                        name = p.name,
                        price = p.oneTimePurchaseOfferDetails?.formattedPrice ?: "",
                        credits = creditsFor(p.productId)
                    )
                }
                println("🔴 BILLING: mapped=${mapped.size} products")

                mainHandler.post { onProductsLoaded(mapped) }
            }
        }
    }

    actual fun launchPurchase(productId: String): Boolean {
        println("🔴 LAUNCH 1: productId=$productId")
        println("🔴 LAUNCH 2: productDetailsList size=${productDetailsList.size}")
        println("🔴 LAUNCH 3: productDetailsList ids=${productDetailsList.map { it.productId }}")

        val product = productDetailsList.find { it.productId == productId } ?: run {
            println("❌ LAUNCH FAIL: Product not found: $productId")
            return false  // ✅ product nahi mila
        }

        val activity = AppContext.getActivity() ?: run {
            println("❌ LAUNCH FAIL: Activity is NULL!")
            return false  // ✅ activity nahi mili
        }
        println("🔴 LAUNCH 4: activity found = $activity")

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product).build())
            ).build()
        billingClient.launchBillingFlow(activity, params)
        println("🔴 LAUNCH 5: launchBillingFlow called!")

        return true  // ✅ successfully launched
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val productId = purchase.products.firstOrNull() ?: return
            val credits = creditsFor(productId)
            mainHandler.post { onPurchaseComplete(productId, credits) }
            if (!purchase.isAcknowledged) {
                billingClient.consumeAsync(
                    ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken).build()
                ) { _, _ -> }
            }
        }
    }

    actual fun disconnect() {
        if (billingClient.isReady) billingClient.endConnection()
    }

    private fun creditsFor(productId: String) = when (productId) {
        "credits_basic" -> 200
        "credits_standard" -> 500
        "credits_pro" -> 900
        else -> 0
    }
}
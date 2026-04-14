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
    private var pendingProductId: String? = null

    // ✅ val → var
    private var billingClient = createBillingClient()

    // ✅ Naya client factory
    private fun createBillingClient(): BillingClient {
        return BillingClient.newBuilder(AppContext.get())
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .setListener { result, purchases ->
                println("🟣 BILLING LISTENER: code=${result.responseCode}")
                if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    purchases.forEach { handlePurchase(it) }
                } else {
                    mainHandler.post { onPurchaseCancelled() }
                }
            }
            .build()
    }

    actual fun startConnection() {
        // ✅ CLOSED hai to naya client banao
        if (billingClient.connectionState == BillingClient.ConnectionState.CLOSED) {
            println("⚠️ BILLING: Client CLOSED — creating fresh instance")
            billingClient = createBillingClient()
            productDetailsList = listOf()
        }

        if (billingClient.isReady) {
            println("✅ BILLING: Already ready")
            pendingProductId?.let { productId ->
                pendingProductId = null
                launchPurchase(productId)
            }
            return
        }

        println("⚠️ BILLING: Starting connection... state=${billingClient.connectionState}")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                println("⚠️ BILLING: Disconnected — retrying in 2s")
                mainHandler.postDelayed({ startConnection() }, 2000)
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                println("✅ BILLING: SetupFinished code=${result.responseCode}")
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                    pendingProductId?.let { productId ->
                        println("✅ BILLING: Launching pending: $productId")
                        mainHandler.postDelayed({
                            pendingProductId = null
                            launchPurchase(productId)
                        }, 1000)
                    }
                } else {
                    println("❌ BILLING: Setup failed code=${result.responseCode} — retrying in 3s")
                    mainHandler.postDelayed({ startConnection() }, 3000)
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
            println("🔴 BILLING: responseCode=${result.responseCode} count=${detailsResult.productDetailsList?.size}")
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
        println("🔴 LAUNCH 1: isReady=${billingClient.isReady} state=${billingClient.connectionState} products=${productDetailsList.size}")

        if (!billingClient.isReady) {
            println("⚠️ LAUNCH: Not ready — saving pending and reconnecting")
            pendingProductId = productId
            startConnection()
            return false
        }

        if (productDetailsList.isEmpty()) {
            println("⚠️ LAUNCH: Products empty — reloading")
            pendingProductId = productId
            queryProducts()
            mainHandler.postDelayed({
                pendingProductId?.let {
                    pendingProductId = null
                    launchPurchase(it)
                }
            }, 1000)
            return false
        }

        pendingProductId = null

        val product = productDetailsList.find { it.productId == productId } ?: run {
            println("❌ LAUNCH: Product not found: $productId")
            return false
        }

        val activity = AppContext.getActivity() ?: run {
            println("❌ LAUNCH: Activity NULL")
            return false
        }

        if (activity.isFinishing || activity.isDestroyed) {
            println("❌ LAUNCH: Activity finishing/destroyed")
            return false
        }

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product).build())
            ).build()

        val result = billingClient.launchBillingFlow(activity, params)
        println("🔴 LAUNCH RESULT: code=${result.responseCode} msg=${result.debugMessage}")
        return result.responseCode == BillingClient.BillingResponseCode.OK
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
                ) { consumeResult, _ ->
                    println("🟢 CONSUME: code=${consumeResult.responseCode}")
                }
            }
        }
    }

    actual fun disconnect() {
        println("⚠️ BILLING: disconnect called")
        pendingProductId = null
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    private fun creditsFor(productId: String) = when (productId) {
        "credits_basic" -> 200
        "credits_standard" -> 500
        "credits_pro" -> 900
        else -> 0
    }
}
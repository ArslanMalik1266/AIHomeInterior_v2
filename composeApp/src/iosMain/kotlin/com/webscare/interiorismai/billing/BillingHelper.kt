package com.webscare.interiorismai.billing

actual class BillingHelper actual constructor(
    private val onProductsLoaded: (List<PurchaseProduct>) -> Unit,
    private val onPurchaseComplete: (productId: String, credits: Int) -> Unit,
    private val onPurchaseCancelled: () -> Unit
) {
    actual fun startConnection() {}
    actual fun launchPurchase(productId: String): Boolean {
        return false  // ✅
    }
    actual fun disconnect() {}
}
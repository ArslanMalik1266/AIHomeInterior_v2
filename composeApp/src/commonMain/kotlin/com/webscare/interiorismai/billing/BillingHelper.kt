package com.webscare.interiorismai.billing

data class PurchaseProduct(
    val productId: String,
    val name: String,
    val price: String,
    val credits: Int
)

expect class BillingHelper(
    onProductsLoaded: (List<PurchaseProduct>) -> Unit,
    onPurchaseComplete: (productId: String, credits: Int) -> Unit,
    onPurchaseCancelled: () -> Unit
) {
    fun startConnection()
    fun launchPurchase(productId: String): Boolean  // ✅ Boolean
    fun disconnect()
}
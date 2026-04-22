package com.webscare.interiorismai.utils


import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import coil3.asDrawable
import coil3.toBitmap

actual fun coil3.Image.toByteArray(): ByteArray {
    val bitmap = this.toBitmap()
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}
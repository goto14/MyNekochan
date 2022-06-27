package org.miura.mynekochan

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray {
    val str = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, str)
    return str.toByteArray()
}
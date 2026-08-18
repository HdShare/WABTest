package me.hd.wabtest.util

import android.util.Base64
import java.nio.charset.Charset

object Base64Util {
    private const val DEFAULT_CHARSET = "UTF-8"

    fun decode(text: String): String {
        return try {
            val bytes = Base64.decode(text, Base64.DEFAULT)
            String(bytes, Charset.forName(DEFAULT_CHARSET))
        } catch (_: Exception) {
            ""
        }
    }

    fun encode(text: String): String {
        return try {
            val bytes = text.toByteArray(Charset.forName(DEFAULT_CHARSET))
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (_: Exception) {
            ""
        }
    }
}

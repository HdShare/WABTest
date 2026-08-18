package me.hd.wabtest.hook.wrapper

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import me.hd.wabtest.hook.HostData.toHostClazz

class MMKVWrapper private constructor(private val mmkv: Any) {

    companion object {
        private fun mmkvWithID(mmapID: String): Any {
            return "com.tencent.mmkv.MMKV".toHostClazz().resolve().firstMethod {
                name = "mmkvWithID"
                parameters(String::class)
            }.invoke(mmapID)!!
        }

        fun get(name: String): MMKVWrapper {
            return MMKVWrapper(mmkvWithID(name))
        }
    }

    fun containsKey(key: String): Boolean {
        return mmkv.asResolver().firstMethod {
            name = "containsKey"
            parameters(String::class)
        }.invoke<Boolean>(key)!!
    }

    fun putInt(key: String, value: Int) {
        mmkv.asResolver().firstMethod {
            name = "putInt"
            parameters(String::class, Int::class)
        }.invoke(key, value)
    }

    fun getInt(key: String, defValue: Int): Int {
        return mmkv.asResolver().firstMethod {
            name = "getInt"
            parameters(String::class, Int::class)
        }.invoke<Int>(key, defValue)!!
    }

    fun putString(key: String, value: String) {
        mmkv.asResolver().firstMethod {
            name = "putString"
            parameters(String::class, String::class)
        }.invoke(key, value)
    }

    fun getString(key: String, defValue: String): String {
        return mmkv.asResolver().firstMethod {
            name = "getString"
            parameters(String::class, String::class)
        }.invoke<String>(key, defValue)!!
    }

    fun putBytes(key: String, value: ByteArray) {
        mmkv.asResolver().firstMethod {
            name = "putBytes"
            parameters(String::class, ByteArray::class)
        }.invoke(key, value)
    }

    fun getBytes(key: String, defValue: ByteArray): ByteArray {
        return mmkv.asResolver().firstMethod {
            name = "getBytes"
            parameters(String::class, ByteArray::class)
        }.invoke<ByteArray>(key, defValue)!!
    }
}

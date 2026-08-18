package me.hd.wabtest.core.repaier

import me.hd.wabtest.hook.wrapper.MMKVWrapper

class RepairerManager {
    private val repairerMmkv by lazy { MMKVWrapper.get("Repairer") }
}

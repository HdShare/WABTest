package io.github.wabtest.core.repaier

import io.github.wabtest.hook.wrapper.MMKVWrapper

class RepairerManager {
    private val repairerMmkv by lazy { MMKVWrapper.get("Repairer") }
}

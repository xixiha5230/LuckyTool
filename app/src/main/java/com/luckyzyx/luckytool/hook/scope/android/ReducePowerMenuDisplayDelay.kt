package com.luckyzyx.luckytool.hook.scope.android

import android.view.KeyEvent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs

object ReducePowerMenuDisplayDelay : YukiBaseHooker() {
    override fun onHook() {
        var isEnable = prefs(ModulePrefs).getBoolean("reduce_power_menu_display_delay", false)
        dataChannel.wait<Boolean>("reduce_power_menu_display_delay") { isEnable = it }

        //Source PhoneWindowManager -> PowerKeyRule -> super getVeryLongPressTimeoutMs
        findClass("com.android.server.policy.SingleKeyGestureDetectorExtImpl").hook {
            injectMember {
                method { name = "modifyPressTimeout" }
                afterHook {
                    if (!isEnable) return@afterHook
                    val pressType = args().first().cast<Int>() ?: return@afterHook
                    val event = args().last().cast<KeyEvent>() ?: return@afterHook
                    if (pressType == 1 && event.keyCode == 26) result = 800L
                }
            }
        }
    }
}
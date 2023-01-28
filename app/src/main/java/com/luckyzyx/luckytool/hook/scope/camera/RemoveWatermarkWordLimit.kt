package com.luckyzyx.luckytool.hook.scope.camera

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.utils.tools.XposedPrefs
import java.util.*

object RemoveWatermarkWordLimit : YukiBaseHooker() {
    override fun onHook() {
        val appSet = prefs(XposedPrefs).getStringSet(packageName, ArraySet()).toTypedArray().apply {
            Arrays.sort(this)
            forEach {
                this[this.indexOf(it)] = it.substring(2)
            }
        }
        val clazz = when (appSet[2]) {
            "8d5b992", "38e5b1a" -> "$7"
            "c7732c4" -> "$5"
            "22-10-20 15:45", "23-01-05 16:32", "22-06-02 23:45", "22-12-13 11:33", "22-11-18 16:19" -> "$5"
            else -> ""
        }
        // Source CameraSubSettingFragment
        // Log camera_namelength_outofrange
        searchClass {
            from("com.oplus.camera.setting", "com.oplus.camera.ui.menu.setting").absolute()
            method {
                name = "onCreate"
            }.count(1)
            method {
                name = "onDestroy"
            }.count(1)
            method {
                name = "onDestroyView"
            }.count(1)
            method {
                name = "onPause"
            }.count(1)
            method {
                name = "onPreferenceChange"
            }.count(1)
            method {
                name = "onPreferenceClick"
            }.count(1)
            method {
                name = "onResume"
            }.count(1)
            method {
                name = "onViewCreated"
            }.count(1)
            method {
                param { it[0] == CharSequenceClass && it[1] == IntType && it[2] == IntType }
                paramCount = 6
                returnType = CharSequenceClass
            }.count(0..1)
        }.get()?.apply {
            findClass(canonicalName!! + clazz).hook {
                injectMember {
                    method {
                        name = "filter"
                        returnType = CharSequenceClass
                    }
                    intercept()
                }
            }
        }
    }
}




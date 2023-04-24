package com.luckyzyx.luckytool.hook.scope.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.luckyzyx.luckytool.utils.ModulePrefs

object MultiApp : YukiBaseHooker() {
    override fun onHook() {
        var isEnable = prefs(ModulePrefs).getBoolean("multi_app_enable", false)
        dataChannel.wait<Boolean>("multi_app_enable") { isEnable = it }
        var enabledMulti = prefs(ModulePrefs).getStringSet("multi_app_custom_list", ArraySet())
        dataChannel.wait<Set<String>>("multi_app_custom_list") { enabledMulti = it }
        //Source OplusMultiAppConfig
        findClass("com.oplus.multiapp.OplusMultiAppConfig").hook {
            injectMember {
                method {
                    name = "getAllowedPkgList"
                    returnType = ListClass
                }
                beforeHook {
                    if (!isEnable || enabledMulti.isEmpty()) return@beforeHook
                    result = java.util.ArrayList(enabledMulti)
//                    field {
//                        name = "mAllowedPkgList"
//                        type = ListClass
//                    }.get(instance).set(java.util.ArrayList(enabledMulti))
                }
            }
        }
    }
}
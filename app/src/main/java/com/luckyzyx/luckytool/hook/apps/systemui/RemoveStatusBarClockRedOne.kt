package com.luckyzyx.luckytool.hook.apps.systemui

import android.os.Build.VERSION.SDK_INT
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.luckytool.utils.tools.SDK
import com.luckyzyx.luckytool.utils.tools.getColorOSVersion

class RemoveStatusBarClockRedOne : YukiBaseHooker() {
    override fun onHook() {
        if (SDK_INT == 30 && getColorOSVersion == "V12"){
            findClass("com.android.systemui.statusbar.policy.Clock").hook {
                injectMember {
                    method {
                        name = "setShouldShowOpStyle"
                        param(BooleanType)
                    }
                    beforeHook {
                        args(0).setFalse()
                    }
                }
            }
        }
        if (SDK >= 31){
            findClass("com.oplusos.systemui.ext.BaseClockExt").hook {
                injectMember {
                    method {
                        name = "setTextWithRedOneStyle"
                        paramCount = 2
                    }
                    beforeHook {
                        args(0).cast<TextView>()?.text = args(1).cast<CharSequence>().toString()
                        resultFalse()
                    }
                }.onNoSuchMemberFailure {
                    loggerE(msg = "MethodNotFound -> setTextWithRedOneStyle")
                }
            }
        }
    }
}
package com.luckyzyx.luckytool.hook.scope.systemui

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs

object StatusBarPower : YukiBaseHooker() {
    override fun onHook() {
        val removePercent = prefs(ModulePrefs).getBoolean("remove_statusbar_battery_percent", false)
        val userTypeface = prefs(ModulePrefs).getBoolean("statusbar_power_user_typeface", false)
        val customFontSize = prefs(ModulePrefs).getInt("statusbar_power_font_size", 0)

        //Source StatBatteryMeterView
        val clazz = "com.oplusos.systemui.statusbar.widget.StatBatteryMeterView"
        if (clazz.toClassOrNull() == null) return
        findClass(clazz).hook {
            injectMember {
                method { name = "onConfigChanged" }
                afterHook { method { name = "updatePercentText" }.get(instance).call() }
            }
            injectMember {
                method { name = "updatePercentText" }
                afterHook {
                    field { name = "batteryPercentText" }.get(instance).cast<TextView>()?.apply {
                        if (removePercent) text = text.toString().replace("%", "")
                        if (userTypeface) {
                            typeface = Typeface.DEFAULT_BOLD
                            setTextSize(
                                TypedValue.COMPLEX_UNIT_DIP,
                                if (customFontSize == 0) 12F else customFontSize.toFloat() * 2
                            )
                        }
                    }
                }
            }
        }
    }
}
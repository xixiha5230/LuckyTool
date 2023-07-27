package com.luckyzyx.luckytool.hook.scope.systemui

import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.luckyzyx.luckytool.utils.ModulePrefs

object LockScreenChargingComponent : YukiBaseHooker() {
    override fun onHook() {
        var userTypeface =
            prefs(ModulePrefs).getBoolean("lock_screen_charging_use_user_typeface", false)
        dataChannel.wait<Boolean>("lock_screen_charging_use_user_typeface") { userTypeface = it }
        var textLogo = prefs(ModulePrefs).getString("set_lock_screen_charging_text_logo_style", "0")
        dataChannel.wait<String>("set_lock_screen_charging_text_logo_style") { textLogo = it }
        var showWattage = prefs(ModulePrefs).getBoolean("force_lock_screen_charging_show_wattage", false)
        dataChannel.wait<Boolean>("force_lock_screen_charging_show_wattage") { showWattage = it }

        //Source ChargingLevelAndLogoView
        findClass("com.oplusos.systemui.keyguard.charginganim.siphonanim.ChargingLevelAndLogoView").hook {
            injectMember {
                method { name = "updatePowerFormat" }
                afterHook {
                    if (!userTypeface) return@afterHook
                    instance<LinearLayout>().allViews.forEach {
                        if (it.javaClass == TextViewClass) {
                            (it as TextView).typeface = Typeface.DEFAULT_BOLD
                        }
                    }
                }
            }
            injectMember {
                method { name = "showTextLogo" }
                beforeHook {
                    when (textLogo) {
                        "1" -> resultTrue()
                        "2" -> resultFalse()
                        else -> return@beforeHook
                    }
                }
            }
        }

        //Source ChargingAnimationImpl
        findClass("com.oplusos.systemui.keyguard.charginganim.ChargingAnimationImpl").hook {
            injectMember {
                method { name = "isMaxWattageMatchs" }
                beforeHook {
                    val mChargerWattage = field { name = "mChargerWattage" }.get(instance).int()
                    if (showWattage && (mChargerWattage != 0)) resultTrue()
                }
            }
        }

        val clazz =
            "com.oplusos.systemui.keyguard.charginganim.siphonanim.flavorone.ChargingLevelAndLogoViewForFlavorOneVfx"
        if (clazz.toClassOrNull() == null) return
        //Source ChargingLevelAndLogoViewForFlavorOneVfx
        findClass(clazz).hook {
            injectMember {
                method { name = "setTypeface" }
                afterHook {
                    if (!userTypeface) return@afterHook
                    instance<LinearLayout>().allViews.forEach {
                        if (it.javaClass == TextViewClass) {
                            (it as TextView).typeface = Typeface.DEFAULT_BOLD
                        }
                    }
                }
            }
            injectMember {
                method { name = "showTextLogo" }
                beforeHook {
                    when (textLogo) {
                        "1" -> resultTrue()
                        "2" -> resultFalse()
                        else -> return@beforeHook
                    }
                }
            }
        }
    }
}
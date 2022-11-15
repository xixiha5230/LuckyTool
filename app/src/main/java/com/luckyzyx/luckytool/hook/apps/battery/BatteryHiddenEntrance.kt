package com.luckyzyx.luckytool.hook.apps.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.luckyzyx.luckytool.utils.tools.XposedPrefs

class BatteryHiddenEntrance : YukiBaseHooker() {
    override fun onHook() {
        val openScreenPowerSave = prefs(XposedPrefs).getBoolean("open_screen_power_save", false)
        val openBatteryHealth = prefs(XposedPrefs).getBoolean("open_battery_health", false)
        val openBatteryOptimize = false
        //Source AppFeatureProviderUtils
        searchClass {
            from("k4").absolute()
            field().none()
            method {
                param(ContentResolverClass, StringType)
                returnType = BooleanType
            }.count(1)
            method {
                param(ContentResolverClass, StringType, IntType)
                returnType = IntType
            }.count(1)
            method {
                param(ContentResolverClass, StringType, BooleanType)
                returnType = BooleanType
            }.count(1)
            method {
                param(ContentResolverClass, StringType)
                returnType = ListClass
            }.count(1)
        }.get()?.hook {
            injectMember {
                method {
                    param(ContentResolverClass, StringType)
                    returnType = BooleanType
                }
                beforeHook {
                    when (args(1).cast<String>()) {
                        //屏幕省电
                        "com.oplus.battery.cabc_level_dynamic_enable" -> if (openScreenPowerSave) resultTrue()
                        //电池健康
                        "os.charge.settings.batterysettings.batteryhealth" -> if (openBatteryHealth) resultTrue()
                        //电池引擎优化
                        "com.oplus.battery.life.mode.notificate" -> if (openBatteryOptimize) resultTrue()
                    }
                }
            }
            injectMember {
                method {
                    param(ContentResolverClass, StringType, IntType)
                    returnType = IntType
                }
                beforeHook {
                    val array = arrayOf(args(1).cast<String>(), args(2).cast<Int>())
                    //电池引擎优化
                    if (array[0] == "com.oplus.battery.life.mode.notificate" && array[1] == 0) {
                        if (openBatteryOptimize) result = 1
                    }
                }
            }
        } ?: loggerD(msg = "$packageName\nError -> BatteryHiddenEntrance")

        //res/xml/battery_health_preference.xml
        //BatteryHealthDataPreference
    }
}
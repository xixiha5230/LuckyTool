package com.luckyzyx.luckytool.hook.scope.alarmclock

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.StringClass

object RemoveAlarmClockWidgetRedOne : YukiBaseHooker() {
    override fun onHook() {
        //Source OnePlusWidget
        val clazz = "com.coloros.widget.smallweather.OnePlusWidget".toClassOrNull() ?: return
        val isHas = clazz.javaClass.hasMethod {
            param(StringClass, StringClass)
            returnType = CharSequenceClass
        }
        if (isHas) {
            clazz.field { type(CharSequenceClass).index().first() }.get().set("")
            return
        }
        //OnePlusWidget setTextViewText -> local_hour_txt -> SpannableStringBuilder -> CharSequence
        searchClass {
            from("m0", "j0").absolute()
            field { type = CharSequenceClass }.count(1)
            field { type = HandlerClass }.count(1)
            field { type = BooleanType }.count(3)
            method {
                param(ContextClass, StringClass, StringClass)
                returnType = CharSequenceClass
            }.count(1)
            method {
                param(ContextClass, StringClass)
                returnType = CharSequenceClass
            }.count(1)
        }.get()?.field {
            type(CharSequenceClass).index().first()
        }?.get()?.set("") ?: loggerD(msg = "$packageName\nError -> RemoveAlarmClockWidgetRedOne")
    }
}
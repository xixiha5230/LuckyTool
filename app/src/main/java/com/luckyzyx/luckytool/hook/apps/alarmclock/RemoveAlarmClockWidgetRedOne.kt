package com.luckyzyx.luckytool.hook.apps.alarmclock

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceType

class RemoveAlarmClockWidgetRedOne : YukiBaseHooker() {
    override fun onHook() {
        //Source OnePlusWidget
        //Search CharSequence Field
        "com.coloros.widget.smallweather.OnePlusWidget".toClass().field {
            type(CharSequenceType).index().first()
        }.get().set("")

        //Source update one plus clock +4 -> setTextViewText
        searchClass {
            from("m0").absolute()
//            field {
//                type = CharSequenceType
//            }.count(1)
            field {
                type = HandlerClass
            }.count(1)
            field {
                type = BooleanType
            }.count(3)
//            method {
//                param(ContextClass, StringType, StringType)
//                returnType = CharSequenceType
//            }.count(1)
//            method {
//                param(ContextClass, StringType)
//                returnType = CharSequenceType
//            }.count(1)
        }.get()?.takeIf {
            it.hasField { type(CharSequenceType) }
        }?.field {
            type(CharSequenceType).index().first()
        }?.get()?.set("")
    }
}







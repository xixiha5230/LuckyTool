package com.luckyzyx.luckytool.hook.hooker

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.packageinstaller.AllowReplaceInstall
import com.luckyzyx.luckytool.hook.scope.packageinstaller.AutoClickInstallButton
import com.luckyzyx.luckytool.hook.scope.packageinstaller.AutoClickUnInstallButton
import com.luckyzyx.luckytool.hook.scope.packageinstaller.HookFeatureOption
import com.luckyzyx.luckytool.hook.scope.packageinstaller.RemoveInstallAds
import com.luckyzyx.luckytool.hook.scope.packageinstaller.ShowPackageNameAndVersionCode
import com.luckyzyx.luckytool.hook.scope.packageinstaller.SkipApkScan
import com.luckyzyx.luckytool.utils.ModulePrefs
import java.util.Arrays

object HookPackageInstaller : YukiBaseHooker() {
    override fun onHook() {
        val appSet =
            prefs(ModulePrefs).getStringSet(packageName, ArraySet()).toTypedArray().apply {
                Arrays.sort(this)
                forEach {
                    this[this.indexOf(it)] = it.substring(2)
                }
            }
        //非ColorOS官方安装器直接返回
        if (appSet[2] == "null") return
        //HookFeatureOption
        loadHooker(HookFeatureOption)
        //跳过安装扫描
        if (prefs(ModulePrefs).getBoolean("skip_apk_scan", false)) {
            loadHooker(SkipApkScan(appSet[2]))
        }
        //低/相同版本警告
        if (prefs(ModulePrefs).getBoolean("allow_downgrade_install", false)) {
            loadHooker(AllowReplaceInstall)
        }
        //移除安装完成广告
        if (prefs(ModulePrefs).getBoolean("remove_install_ads", false)) {
            loadHooker(RemoveInstallAds)
        }
        //自动点击安装按钮
        if (prefs(ModulePrefs).getBoolean("auto_click_install_button", false)) {
            loadHooker(AutoClickInstallButton)
        }
        //自动点击卸载按钮
        if (prefs(ModulePrefs).getBoolean("auto_click_uninstall_button", false)) {
            loadHooker(AutoClickUnInstallButton)
        }
        //显示包名与旧版本号
        if (prefs(ModulePrefs).getBoolean("show_packagename_and_versioncode", false)) {
            loadHooker(ShowPackageNameAndVersionCode)
        }
    }
}
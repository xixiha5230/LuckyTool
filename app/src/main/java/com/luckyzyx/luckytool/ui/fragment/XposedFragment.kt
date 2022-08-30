package com.luckyzyx.luckytool.ui.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentXposedBinding
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.tools.SDK
import com.luckyzyx.luckytool.utils.tools.XposedPrefs
import com.luckyzyx.luckytool.utils.tools.dp2px
import com.luckyzyx.luckytool.utils.tools.getAppVersion
import rikka.core.util.ResourceUtils

class XposedFragment : Fragment() {
    private lateinit var binding: FragmentXposedBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentXposedBinding.inflate(inflater)
        initToolbarTabs()
        return binding.root
    }

    private fun initToolbarTabs() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.xposed_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        setHasOptionsMenu(true)
        val tabs = binding.tablayout
        tabs.apply {
            addTab(tabs.newTab().setText(requireActivity().getString(R.string.system_app)),0,true)
            addTab(tabs.newTab().setText(requireActivity().getString(R.string.other_app)),1,false)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position){
                        0 -> {
                            navController.popBackStack()
                            navController.navigate(R.id.systemApp)
                        }
                        1 -> {
                            navController.popBackStack()
                            navController.navigate(R.id.otherApp)
                        }
                    }
                }
                //取消选中
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                //再次选中
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.menu_reboot)).setIcon(R.drawable.ic_baseline_refresh_24).setShowAsActionFlags(
            MenuItem.SHOW_AS_ACTION_IF_ROOM
        ).apply {
            if (ResourceUtils.isNightMode(resources.configuration)){
                this.iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.menu_versioninfo)).setIcon(R.drawable.ic_baseline_info_24).setShowAsActionFlags(
            MenuItem.SHOW_AS_ACTION_IF_ROOM
        ).apply {
            if (ResourceUtils.isNightMode(resources.configuration)){
                this.iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) (activity as MainActivity).restartScope(requireActivity())
        if (item.itemId == 2) bottomSheet()
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    fun bottomSheet(){
        val xposedScope = resources.getStringArray(R.array.xposed_scope)
        var str = getString(R.string.scope_version_info)+"\n"
        for (scope in xposedScope) {
            val arrayList = requireActivity().getAppVersion(scope)
            if (arrayList.isEmpty()) continue
            str += "\n$scope\n${getString(R.string.version_first)} ${arrayList[0]}(${arrayList[1]})[${arrayList[2]}]\n"
        }
        val nestedScrollView = NestedScrollView(requireActivity()).apply {
            setPadding(requireActivity().dp2px(16F).toInt())
            addView(
                TextView(requireActivity()).apply {
                    textSize = 16F
                    text = str
                }
            )
        }
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(nestedScrollView)
        bottomSheetDialog.show()
    }
}

class SystemApp : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Android)
                    summary = getString(R.string.corepatch)
                    key = "Android"
                    setIcon(R.mipmap.android_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_system)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.StatusBar)
                    summary = getString(R.string.StatusBarNotice)+","+getString(R.string.StatusBarIcon)+","+getString(R.string.StatusBarClock)
                    key = "StatusBar"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_statusBar)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Desktop)
                    summary = getString(R.string.launcher_layout_row_colume)+","+getString(R.string.remove_appicon_dot)
                    key = "Desktop"
                    setIcon(R.mipmap.launcher_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_desktop)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.LockScreen)
                    summary = getString(R.string.remove_lock_screen_redone)+","+getString(R.string.remove_lock_screen_camera)
                    key = "LockScreen"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_lockScreen)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Screenshot)
                    summary = getString(R.string.remove_system_screenshot_delay)+","+getString(R.string.remove_screenshot_privacy_limit)
                    key = "Screenshot"
                    setIcon(R.mipmap.screenshot_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_screenshot)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Application)
                    summary = getString(R.string.skip_apk_scan)+","+getString(R.string.unlock_startup_limit)
                    key = "Application"
                    setIcon(R.mipmap.packageinstaller_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_application)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Miscellaneous)
                    summary = getString(R.string.Miscellaneous_summary)
                    key = "Miscellaneous"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_miscellaneous)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Camera)
                    summary = getString(R.string.remove_watermark_word_limit)
                    key = "Camera"
                    setIcon(R.mipmap.camera_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_camera)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.OplusGames)
                    summary = getString(R.string.remove_root_check)
                    key = "OplusGames"
                    setIcon(R.mipmap.oplusgames_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_oplusGames)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.ThemeStore)
                    summary = getString(R.string.unlock_themestore_vip)
                    key = "ThemeStore"
                    setIcon(R.mipmap.themestore_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_themeStore)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.CloudService)
                    summary = getString(R.string.remove_network_limit)
                    key = "CloudService"
                    setIcon(R.mipmap.cloudservice_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_systemApp_to_cloudService)
                        true
                    }
                }
            )
        }
    }
}

class OtherApp : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.Everyimage)
                    summary = getString(R.string.skip_startup_page)+","+getString(R.string.vip_download)
                    key = "com.east2d.everyimage"
                    setIcon(R.mipmap.everyimage_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_otherApp_to_everyimage)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.AlphaBackupPro)
                    summary = getString(R.string.remove_pro_license)
                    key = "com.ruet_cse_1503050.ragib.appbackup.pro"
                    setIcon(R.mipmap.alphabackup_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_otherApp_to_alphaBackupPro)
                        true
                    }
                }
            )
        }
    }
}

class System : ModulePreferenceFragment(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                PreferenceCategory(requireActivity()).apply {
                    setTitle(R.string.corepatch)
                    setSummary(R.string.corepatch_summary)
                    key = "CorePatch"
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    setTitle(R.string.downgr)
                    setSummary(R.string.downgr_summary)
                    key = "downgrade"
                    setDefaultValue(true)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    setTitle(R.string.authcreak)
                    setSummary(R.string.authcreak_summary)
                    key = "authcreak"
                    setDefaultValue(true)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    setTitle(R.string.digestCreak)
                    setSummary(R.string.digestCreak_summary)
                    key = "digestCreak"
                    setDefaultValue(true)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    setTitle(R.string.UsePreSig)
                    setSummary(R.string.UsePreSig_summary)
                    key = "UsePreSig"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    setTitle(R.string.enhancedMode)
                    setSummary(R.string.enhancedMode_summary)
                    key = "enhancedMode"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        super.onSharedPreferenceChanged(sharedPreferences, key)
        if (key == "UsePreSig" && sharedPreferences!!.getBoolean(key, false)) {
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage(R.string.usepresig_warn)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
}

class StatusBar : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.StatusBarClock)
                    summary = getString(R.string.statusbar_clock_show_second)+","+getString(R.string.statusbar_clock_show_doublerow)+","+getString(R.string.statusbar_clock_doublerow_fontsize)
                    key = "StatusBarClock"
                    setIcon(R.mipmap.alarmclock_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_statusBar_to_statusBarClock)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.StatusBarDate)
                    summary = getString(R.string.remove_statusbar_date_comma)
                    key = "StatusBarDate"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_statusBar_to_statusBarDate)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.StatusBarNotice)
                    summary = getString(R.string.remove_statusbar_top_notification)+","+getString(R.string.remove_charging_completed)
                    key = "StatusBarNotice"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_statusBar_to_statusBarNotice)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.StatusBarIcon)
                    summary = getString(R.string.remove_statusbar_battery_percent)+","+getString(R.string.remove_statusbar_user_switcher)
                    key = "StatusBarIcon"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_statusBar_to_statusBarIcon)
                        true
                    }
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.StatusBarTiles)
                    summary = getString(R.string.tile_unexpanded_columns_vertical)+","+getString(R.string.tile_expanded_columns_vertical)
                    key = "StatusBarTiles"
                    setIcon(R.mipmap.systemui_icon)
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_statusBar_to_statusBarTile)
                        true
                    }
                }
            )
        }
    }
}

class StatusBarClock : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_enable)
                    key = "statusbar_clock_enable"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_year)
                    key = "statusbar_clock_show_year"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_month)
                    key = "statusbar_clock_show_month"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_day)
                    key = "statusbar_clock_show_day"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_week)
                    key = "statusbar_clock_show_week"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_period)
                    key = "statusbar_clock_show_period"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_second)
                    key = "statusbar_clock_show_second"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_show_doublerow)
                    key = "statusbar_clock_show_doublerow"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_singlerow_fontsize)
                    summary = getString(R.string.statusbar_clock_fontsize_summary)
                    key = "statusbar_clock_singlerow_fontsize"
                    setDefaultValue(0)
                    max = 18
                    min = 0
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_clock_doublerow_fontsize)
                    summary = getString(R.string.statusbar_clock_fontsize_summary)
                    key = "statusbar_clock_doublerow_fontsize"
                    setDefaultValue(0)
                    max = 10
                    min = 0
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_clock_redone)
                    key = "remove_statusbar_clock_redone"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_year")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_month")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_day")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_week")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_period")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_second")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SwitchPreference>("statusbar_clock_show_doublerow")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SeekBarPreference>("statusbar_clock_singlerow_fontsize")?.dependency = "statusbar_clock_enable"
        preferenceScreen.findPreference<SeekBarPreference>("statusbar_clock_doublerow_fontsize")?.dependency = "statusbar_clock_show_doublerow"
    }
}

class StatusBarDate : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_date_comma)
                    summary = getString(R.string.remove_statusbar_date_comma_summary)
                    key = "remove_statusbar_date_comma"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class StatusBarNotice : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_top_notification)
                    summary = getString(R.string.remove_statusbar_top_notification_summary)
                    key = "remove_statusbar_top_notification"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_vpn_active_notification)
                    summary = getString(R.string.remove_vpn_active_notification_summary)
                    key = "remove_vpn_active_notification"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_devmode)
                    key = "remove_statusbar_devmode"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_charging_completed)
                    key = "remove_charging_completed"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_bottom_networkwarn)
                    summary = getString(R.string.remove_statusbar_bottom_networkwarn_summary)
                    key = "remove_statusbar_bottom_networkwarn"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class StatusBarIcon : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_battery_percent)
                    key = "remove_statusbar_battery_percent"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.set_network_speed)
                    key = "set_network_speed"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_securepayment_icon)
                    key = "remove_statusbar_securepayment_icon"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_statusbar_user_switcher)
                    key = "remove_statusbar_user_switcher"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class StatusBarTile : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.statusbar_tile_enable)
                    key = "statusbar_tile_enable"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_unexpanded_columns_vertical)
                    key = "tile_unexpanded_columns_vertical"
                    setDefaultValue(6)
                    max = 6
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK < 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_unexpanded_columns_horizontal)
                    key = "tile_unexpanded_columns_horizontal"
                    setDefaultValue(6)
                    max = 8
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK < 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_expanded_columns_vertical)
                    key = "tile_expanded_columns_vertical"
                    setDefaultValue(4)
                    max = 7
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK < 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_expanded_columns_horizontal)
                    key = "tile_expanded_columns_horizontal"
                    setDefaultValue(6)
                    max = 9
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK < 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_unexpanded_columns_vertical)
                    key = "tile_unexpanded_columns_vertical_c13"
                    setDefaultValue(5)
                    max = 6
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK >= 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_expanded_rows_vertical)
                    key = "tile_expanded_rows_vertical_c13"
                    setDefaultValue(3)
                    max = 4
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK >= 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_expanded_columns_vertical)
                    key = "tile_expanded_columns_vertical_c13"
                    setDefaultValue(4)
                    max = 7
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK >= 33
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.tile_columns_horizontal_c13)
                    key = "tile_columns_horizontal_c13"
                    setDefaultValue(5)
                    max = 6
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                    isVisible = SDK >= 33
                }
            )
        }
        preferenceScreen.findPreference<SeekBarPreference>("tile_unexpanded_columns_vertical")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_unexpanded_columns_horizontal")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_expanded_columns_vertical")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_expanded_columns_horizontal")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_unexpanded_columns_vertical_c13")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_expanded_rows_vertical_c13")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_expanded_columns_vertical_c13")?.dependency = "statusbar_tile_enable"
        preferenceScreen.findPreference<SeekBarPreference>("tile_columns_horizontal_c13")?.dependency = "statusbar_tile_enable"
    }
}

class Desktop : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_alarmclock_widget_redone)
                    key = "remove_alarmclock_widget_redone"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_appicon_dot)
                    key = "remove_appicon_dot"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                PreferenceCategory(requireActivity()).apply {
                    title = getString(R.string.launcher_layout_related)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.launcher_layout_enable)
                    summary = getString(R.string.launcher_layout_row_colume)
                    key = "launcher_layout_enable"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.launcher_layout_max_rows)
                    key = "launcher_layout_max_rows"
                    setDefaultValue(6)
                    max = 8
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SeekBarPreference(requireActivity()).apply {
                    title = getString(R.string.launcher_layout_max_columns)
                    key = "launcher_layout_max_columns"
                    setDefaultValue(4)
                    max = 6
                    min = 1
                    seekBarIncrement = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                }
            )
        }
        preferenceScreen.findPreference<SeekBarPreference>("launcher_layout_max_rows")?.dependency = "launcher_layout_enable"
        preferenceScreen.findPreference<SeekBarPreference>("launcher_layout_max_columns")?.dependency = "launcher_layout_enable"
    }
}

class LockScreen : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_lock_screen_redone)
                    key = "remove_lock_screen_redone"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_lock_screen_camera)
                    key = "remove_lock_screen_camera"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.set_lock_screen_centered)
                    summary = getString(R.string.set_lock_screen_centered_summary)
                    key = "set_lock_screen_centered"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class Screenshot : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_system_screenshot_delay)
                    summary = getString(R.string.remove_system_screenshot_delay_summary)
                    key = "remove_system_screenshot_delay"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_screenshot_privacy_limit)
                    summary = getString(R.string.remove_screenshot_privacy_limit_summary)
                    key = "remove_screenshot_privacy_limit"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.disable_flag_secure)
                    summary = getString(R.string.disable_flag_secure_summary)
                    key = "disable_flag_secure"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class Application : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                PreferenceCategory(requireActivity()).apply {
                    title = getString(R.string.MultiApp)
                    key = "MultiApp"
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.multi_app_enable)
                    summary = getString(R.string.multi_app_enable_summary)
                    key = "multi_app_enable"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.multi_app_list)
                    summary = getString(R.string.multi_app_list_summary)
                    key = "multi_app_list"
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_application_to_multiFragment)
                        true
                    }
                }
            )
            addPreference(
                PreferenceCategory(requireActivity()).apply {
                    title = getString(R.string.AppInstallationRelated)
                    key = "PackageInstaller"
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.skip_apk_scan)
                    summary = getString(R.string.skip_apk_scan_summary)
                    key = "skip_apk_scan"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.allow_downgrade_install)
                    summary = getString(R.string.allow_downgrade_install_summary)
                    key = "allow_downgrade_install"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_install_ads)
                    summary = getString(R.string.remove_install_ads_summary)
                    key = "remove_install_ads"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.replase_aosp_installer)
                    summary = getString(R.string.replase_aosp_installer_summary)
                    key = "replase_aosp_installer"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_adb_install_confirm)
                    summary = getString(R.string.remove_adb_install_confirm_summary)
                    key = "remove_adb_install_confirm"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                PreferenceCategory(requireActivity()).apply {
                    title = getString(R.string.ApplyOtherRestrictions)
                    key = "ApplyOtherRestrictions"
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.unlock_startup_limit)
                    summary = getString(R.string.unlock_startup_limit_summary)
                    key = "unlock_startup_limit"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.unlock_task_locks)
                    key = "unlock_task_locks"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
        preferenceScreen.findPreference<Preference>("multi_app_list")?.dependency = "multi_app_enable"

    }
}

class Miscellaneous : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                Preference(requireActivity()).apply {
                    title = getString(R.string.PowerMenu)
                    summary = getString(R.string.PowerMenu_summary)
                    key = "PowerMenu"
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        requireActivity().findNavController(R.id.xposed_fragment_container).navigate(R.id.action_miscellaneous_to_powerMenu)
                        true
                    }
                    isVisible = SDK_INT in 31..32
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.show_charging_ripple)
                    summary = getString(R.string.show_charging_ripple_summary)
                    key = "show_charging_ripple"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    isVisible = SDK_INT >= 31
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.disable_duplicate_floating_window)
                    summary = getString(R.string.disable_duplicate_floating_window_summary)
                    key = "disable_duplicate_floating_window"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    isVisible = SDK_INT >= 33
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.disable_headphone_high_volume_warning)
                    summary = getString(R.string.disable_headphone_high_volume_warning_summary)
                    key = "disable_headphone_high_volume_warning"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class PowerMenu : ModulePreferenceFragment(){
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.power_menu_enable)
                    summary = getString(R.string.power_menu_enable_summary)
                    key = "power_menu_enable"
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.power_menu_sos_button)
                    summary = getString(R.string.power_menu_button_summary)
                    key = "power_menu_sos_button"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.power_menu_lock_button)
                    summary = getString(R.string.power_menu_button_summary)
                    key = "power_menu_lock_button"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.power_menu_simple_layout)
                    summary = getString(R.string.power_menu_simple_layout_summary)
                    key = "power_menu_simple_layout"
                    setDefaultValue(true)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.power_menu_remove_add_controls)
                    summary = getString(R.string.power_menu_remove_add_controls_summary)
                    key = "power_menu_remove_add_controls"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
        preferenceScreen.findPreference<SwitchPreference>("power_menu_sos_button")?.dependency = "power_menu_enable"
        preferenceScreen.findPreference<SwitchPreference>("power_menu_lock_button")?.dependency = "power_menu_enable"
        preferenceScreen.findPreference<SwitchPreference>("power_menu_simple_layout")?.dependency = "power_menu_enable"
        preferenceScreen.findPreference<SwitchPreference>("power_menu_remove_add_controls")?.dependency = "power_menu_enable"
    }
}

class Camera : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_watermark_word_limit)
                    key = "remove_watermark_word_limit"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class OplusGames : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_root_check)
                    summary = getString(R.string.remove_root_check_summary)
                    key = "remove_root_check"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class CloudService : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_network_limit)
                    summary = getString(R.string.remove_network_limit_summary)
                    key = "remove_network_limit"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class ThemeStore : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.unlock_themestore_vip)
                    summary = getString(R.string.unlock_themestore_vip_summary)
                    key = "unlock_themestore_vip"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class Everyimage : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.skip_startup_page)
                    key = "skip_startup_page"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.vip_download)
                    key = "vip_download"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}

class AlphaBackupPro : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = XposedPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(
                SwitchPreference(requireActivity()).apply {
                    title = getString(R.string.remove_pro_license)
                    key = "remove_check_license"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                }
            )
        }
    }
}
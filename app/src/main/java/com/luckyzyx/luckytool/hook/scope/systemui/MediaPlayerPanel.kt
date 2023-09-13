package com.luckyzyx.luckytool.hook.scope.systemui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.luckyzyx.luckytool.hook.utils.sysui.DependencyUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.safeOfNull

object MediaPlayerPanel : YukiBaseHooker() {
    override fun onHook() {
        //媒体播放器显示模式
        if (getOSVersionCode == 29) loadHooker(MediaPlayerDisplayModeC132)
        else loadHooker(MediaPlayerDisplayMode)
        //强制开启媒体切换按钮
        if (prefs(ModulePrefs).getBoolean("force_enable_media_toggle_button", false)) {
            loadHooker(ForceEnableMediaToggleButton)
        }
    }

    object MediaPlayerDisplayMode : YukiBaseHooker() {
        override fun onHook() {
            var mode = prefs(ModulePrefs).getString("set_media_player_display_mode", "0")
            dataChannel.wait<String>("set_media_player_display_mode") { mode = it }

            //Source OplusQsMediaCarouselController
            findClass("com.oplus.systemui.qs.media.OplusQsMediaCarouselController").hook {
                injectMember {
                    method { name = "setCurrentMediaData" }
                    afterHook {
                        val status = when (mode) {
                            "1" -> true
                            "2" -> false
                            else -> return@afterHook
                        }
                        val mediaModeChangeListener = field { name = "mediaModeChangeListener" }
                            .get(instance).any() ?: return@afterHook
                        mediaModeChangeListener.current().method {
                            name = "onChanged"
                        }.call(status)
                    }
                }
                injectMember {
                    method { name = "setMediaModeChangeListener" }
                    afterHook {
                        val status = when (mode) {
                            "1" -> true
                            "2" -> false
                            else -> return@afterHook
                        }
                        val mediaModeChangeListener = args().first().any() ?: return@afterHook
                        mediaModeChangeListener.current().method {
                            name = "onChanged"
                        }.call(status)
                    }
                }
            }
        }
    }

    object MediaPlayerDisplayModeC132 : YukiBaseHooker() {
        @SuppressLint("DiscouragedApi")
        override fun onHook() {
            var mode = prefs(ModulePrefs).getString("set_media_player_display_mode", "0")
            dataChannel.wait<String>("set_media_player_display_mode") { mode = it }

            //Source updateQsMediaPanelView
            findClass("com.oplusos.systemui.qs.OplusQSTileMediaContainer").hook {
                injectMember {
                    method { name = "updateQsMediaPanelView" }
                    beforeHook {
                        val status = when (mode) {
                            "1" -> 0
                            "2" -> 8
                            else -> return@beforeHook
                        }
                        val res = args().first().cast<Resources>() ?: return@beforeHook
                        val bool = args().last().cast<Boolean>() ?: return@beforeHook
                        val linear = field { name = "mQsMediaPanelContainer" }.get(instance)
                            .cast<LinearLayout>() ?: return@beforeHook
                        val mTmpConstraintSet = field { name = "mTmpConstraintSet" }
                            .get(instance).any() ?: return@beforeHook
                        val smallHeight = res.getIdentifier(
                            "oplus_qs_media_panel_height_smallspace",
                            "dimen", packageName
                        ).takeIf { it != 0 } ?: return@beforeHook
                        val height = res.getIdentifier(
                            "oplus_qs_media_panel_height",
                            "dimen", packageName
                        ).takeIf { it != 0 } ?: return@beforeHook
                        val heightSize = safeOfNull {
                            res.getDimensionPixelSize(if (bool) smallHeight else height)
                        } ?: return@beforeHook

                        mTmpConstraintSet.setVisibilitySet(linear.id, status)
                        if (status == 0) mTmpConstraintSet.constrainHeightSet(
                            linear.id, heightSize
                        )
                        resultNull()
                    }
                }
                injectMember {
                    method { name = "updateQsSecondTileContainer" }
                    beforeHook {
                        val status = when (mode) {
                            "1" -> true
                            "2" -> false
                            else -> return@beforeHook
                        }
                        val res = args().first().cast<Resources>() ?: return@beforeHook
                        val bool = args().last().cast<Boolean>() ?: return@beforeHook
                        val linear = field { name = "mSecondTileContainer" }.get(instance)
                            .cast<LinearLayout>() ?: return@beforeHook
                        val mTmpConstraintSet = field { name = "mTmpConstraintSet" }
                            .get(instance).any() ?: return@beforeHook
                        val smallSideMargin = res.getIdentifier(
                            "qs_footer_hl_tile_side_margin_smallspace",
                            "dimen", packageName
                        ).takeIf { it != 0 } ?: return@beforeHook
                        val sideMargin = res.getIdentifier(
                            "qs_footer_hl_tile_side_margin",
                            "dimen", packageName
                        ).takeIf { it != 0 } ?: return@beforeHook
                        val sideSize = safeOfNull {
                            res.getDimensionPixelSize(if (bool) smallSideMargin else sideMargin)
                        } ?: return@beforeHook
                        val guideLine = res.getIdentifier(
                            "guide_line", "id", packageName
                        ).takeIf { it != 0 } ?: return@beforeHook
                        if (status) {
                            val firstTile = field { name = "mFirstTileContainer" }.get(instance)
                                .cast<LinearLayout>() ?: return@beforeHook
                            val smallContainerMargin = res.getIdentifier(
                                "qs_footer_hl_tile_two_container_margin_top_smallspace",
                                "dimen", packageName
                            ).takeIf { it != 0 } ?: return@beforeHook
                            val containerMargin = res.getIdentifier(
                                "qs_footer_hl_tile_two_container_margin_top",
                                "dimen", packageName
                            ).takeIf { it != 0 } ?: return@beforeHook
                            val containerSize = safeOfNull {
                                res.getDimensionPixelSize(if (bool) smallContainerMargin else containerMargin)
                            } ?: return@beforeHook
                            mTmpConstraintSet.connectSet(linear.id, 6, 0, 6, 0)
                            mTmpConstraintSet.connectSet(linear.id, 7, guideLine, 6, sideSize)
                            mTmpConstraintSet.connectSet(
                                linear.id, 3, firstTile.id, 4, containerSize
                            )
                            resultNull()
                            return@beforeHook
                        }
                        mTmpConstraintSet.connectSet(linear.id, 6, guideLine, 7, sideSize)
                        mTmpConstraintSet.connectSet(linear.id, 7, 0, 7, 0)
                        mTmpConstraintSet.connectSet(linear.id, 3, 0, 3, 0)
                        resultNull()
                    }
                }
            }
        }
    }

    fun Any.connectSet(
        startId: Int, startSide: Int, endId: Int, endSide: Int, margin: Int
    ) {
        this.current().method {
            name = "connect"
            paramCount = 5
        }.call(startId, startSide, endId, endSide, margin)
    }

    fun Any.constrainHeightSet(viewId: Int, height: Int) {
        this.current().method {
            name = "constrainHeight"
            paramCount = 2
        }.call(viewId, height)
    }

    fun Any.setVisibilitySet(viewId: Int, visibility: Int) {
        this.current().method {
            name = "setVisibility"
            paramCount = 2
        }.call(viewId, visibility)
    }

    object ForceEnableMediaToggleButton : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusQsMediaPanelView
            findClass("com.oplus.systemui.qs.media.OplusQsMediaPanelView").hook {
                injectMember {
                    method { name = "bindMediaData" }
                    afterHook {
                        args().first().any() ?: field { name = "mMediaOutputBtn" }.get(instance)
                            .cast<ImageButton>()?.setMediaOutputBtn()
                    }
                }
            }
            //Source OplusQsMediaOutputDialog
            findClass("com.oplus.systemui.qs.media.OplusQsMediaOutputDialog").hook {
                injectMember {
                    method { name = "bindMediaView" }
                    afterHook {
                        args().first().any() ?: field { name = "mMediaOutputBtn" }.get(instance)
                            .cast<ImageButton>()?.setMediaOutputBtn()
                    }
                }
            }
        }
    }

    private fun ImageButton.setMediaOutputBtn() {
        isVisible = true
        isEnabled = true
        setOnClickListener {
            val clazz = "com.android.systemui.media.dialog.MediaOutputDialogFactory".toClass()
            val mMediaOutputDialogFactory = DependencyUtils(appClassLoader).get(clazz)
            mMediaOutputDialogFactory?.current()?.method { name = "create";paramCount = 3 }
                ?.call("", true, null)
        }
    }
}
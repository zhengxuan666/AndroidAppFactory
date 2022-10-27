package com.bihe0832.android.common.debug.module

import android.content.Intent
import android.provider.Settings
import android.view.View
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.item.DebugTipsData
import com.bihe0832.android.common.debug.log.DebugLogActivity
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.privacy.AgreementPrivacy
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.file.select.FileSelectTools
import com.bihe0832.android.lib.lifecycle.*
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.intent.IntentUtils
import com.bihe0832.android.lib.utils.os.BuildUtils
import com.bihe0832.android.lib.utils.os.ManufacturerUtil
import com.bihe0832.android.lib.utils.time.DateUtil
import java.io.File

open class DebugCommonFragment : DebugEnvFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugTipsData("APPFactory的通用组件和工具"))
            add(DebugItemData("查看应用版本及环境", View.OnClickListener { showAPPInfo() }))
            add(DebugItemData("查看使用情况", View.OnClickListener { showUsedInfo() }))
            add(DebugItemData("查看设备信息", View.OnClickListener { showMobileInfo() }))
            add(DebugItemData("查看第三方应用信息", View.OnClickListener { showOtherAPPInfo() }))
            add(DebugItemData("<font color ='#3AC8EF'><b>日志管理</b></font>", View.OnClickListener {
                showLog()
            }))
            add(DebugItemData("打开开发者模式") {
                IntentUtils.startSettings(context, Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            })
            add(DebugItemData("打开应用设置") { IntentUtils.startAppDetailSettings(context) })
            add(DebugItemData("清除缓存") {
                FileUtils.deleteDirectory(File(ZixieContext.getZixieFolder()))
                ZixieContext.restartApp()
            })
            add(DebugItemData("清除用户信息授权") {
                AgreementPrivacy.resetPrivacy()
                ZixieContext.restartApp()
            })
        }
    }

    protected fun startActivity(activityName: String) {
        try {
            startActivityWithException(activityName)
        } catch (e: Exception) {
            e.printStackTrace()
            ZixieContext.showToast("请确认当前运行的测试模块是否包含该应用")
        }
    }

    protected open fun showLog() {
        startActivityWithException(DebugLogActivity::class.java)
    }

    protected fun showMobileInfo() {
        mutableListOf<String>().apply {
            add("应用包名: ${context!!.packageName}")
            add("设备ID: ${ZixieContext.deviceId}")
            add("厂商型号: ${ManufacturerUtil.MANUFACTURER}, ${ManufacturerUtil.MODEL}, ${ManufacturerUtil.BRAND}")
            var sdkVersion = "系统版本: Android ${BuildUtils.RELEASE}, API  ${BuildUtils.SDK_INT}" + if (ManufacturerUtil.isHarmonyOs()) {
                ", Harmony(${ManufacturerUtil.getHarmonyVersion()})"
            } else {
                ""
            }
            add("<font color ='#3AC8EF'>$sdkVersion</font>")

            add("系统指纹: ${ManufacturerUtil.FINGERPRINT}")

        }.let {
            showInfoWithHTML("设备信息", it)
        }
    }

    protected fun showAPPInfo() {
        mutableListOf<String>().apply {
            var version = if (ZixieContext.isDebug()) {
                "内测版"
            } else {
                if (ZixieContext.isOfficial()) {
                    "外发版"
                } else {
                    "预发布版"
                }
            }
            add("应用名称: ${APKUtils.getAppName(context)}")
            add("应用包名: ${ZixieContext.applicationContext!!.packageName}")
            add("安装时间: ${DateUtil.getDateEN(LifecycleHelper.getVersionInstalledTime())}")
            add("版本类型: $version")
            add("<font color ='#3AC8EF'><b>应用版本: ${ZixieContext.getVersionName()}.${ZixieContext.getVersionCode()}</b></font>")
            add("版本标识: ${ZixieContext.getVersionTag()}")
            add("签名MD5: ${APKUtils.getSigMd5ByPkgName(ZixieContext.applicationContext, ZixieContext.applicationContext?.packageName)}")
        }.let {
            showInfoWithHTML("应用信息", it)
        }
    }

    protected fun showUsedInfo() {
        mutableListOf<String>().apply {
            add("应用安装时间: ${DateUtil.getDateEN(LifecycleHelper.getAPPInstalledTime())}")
            add("应用安装时间: ${DateUtil.getDateEN(LifecycleHelper.getAPPInstalledTime())}")
            add("当前版本安装时间: ${DateUtil.getDateEN(LifecycleHelper.getVersionInstalledTime())}")

            add("上次启动版本号: ${LifecycleHelper.getAPPLastVersion()}")
            add("上次启动时间: ${DateUtil.getDateEN(LifecycleHelper.getAPPLastStartTime())}")

            add("本次启动类型: ${
                LifecycleHelper.isFirstStart.let {
                    when (it) {
                        INSTALL_TYPE_NOT_FIRST -> "非首次启动"
                        INSTALL_TYPE_VERSION_FIRST -> "版本首次启动"
                        INSTALL_TYPE_APP_FIRST -> "应用首次启动"
                        else -> "类型错误（$it）"
                    }
                }
            }")
            add("本次启动时间: ${DateUtil.getDateEN(ApplicationObserver.getAPPStartTime())}")
            add("累积使用天数: ${LifecycleHelper.getAPPUsedDays()}")
            add("累积使用次数: ${LifecycleHelper.getAPPUsedTimes()}")
            add("当前版本使用次数: ${LifecycleHelper.getCurrentVersionUsedTimes()}")

            add("最后一次退后台: ${DateUtil.getDateEN(ApplicationObserver.getLastPauseTime())}")
            add("最后一次回前台: ${DateUtil.getDateEN(ApplicationObserver.getLastResumedTime())}")

            add("当前页面: ${ActivityObserver.getCurrentActivity()?.javaClass?.name}")
        }.let {
            showInfo("应用使用情况", it)
        }
    }

    protected fun showOtherAPPInfo() {
        val builder = StringBuilder()
        builder.append("第三方应用信息:\n\n")
        addPackageInfo("com.tencent.mobileqq", builder)
        addPackageInfo("com.tencent.mm", builder)
        addPackageInfo("com.tencent.qqlite", builder)
        addPackageInfo("com.tencent.mobileqqi", builder)
        addPackageInfo("com.tencent.tim", builder)
        showInfo("第三方应用信息", builder.toString())
    }

    protected fun addPackageInfo(packageName: String, builder: StringBuilder) {
        val info = APKUtils.getInstalledPackage(context, packageName)
        builder.append("\n$packageName: ")
        if (null == info) {
            builder.append("未安装")
        } else {
            builder.append("\n\tname: ${APKUtils.getAppName(context, packageName)}\n")
            builder.append("	versionName: ${info.versionName}\n")
            builder.append("	versionCode: ${info.versionCode}\n")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FileSelectTools.FILE_CHOOSER && resultCode == RESULT_OK) {
            data?.extras?.getString(FileSelectTools.INTENT_EXTRA_KEY_WEB_URL, "")?.let { filePath ->
                FileUtils.sendFile(activity!!, filePath, "*/*").let {
                    if (!it) {
                        ZixieContext.showToast("分享文件:$filePath 失败")
                    }
                }
            }
        }
    }
}
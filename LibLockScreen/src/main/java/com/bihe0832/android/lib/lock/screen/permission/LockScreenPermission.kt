package com.bihe0832.android.lib.lock.screen.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import com.bihe0832.android.lib.lock.screen.service.LockScreenService
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.permission.ui.PermissionDialog
import com.bihe0832.android.lib.permission.wrapper.openFloatSettings
import com.bihe0832.android.lib.ui.dialog.callback.OnDialogListener

object LockScreenPermission {

    val mLockScreenPermission = mutableListOf(Manifest.permission.WAKE_LOCK, Manifest.permission.DISABLE_KEYGUARD, Manifest.permission.SYSTEM_ALERT_WINDOW)

    const val SCENE = "lock"

    init {
        PermissionManager.addPermissionGroup(SCENE, Manifest.permission.WAKE_LOCK, mLockScreenPermission)
        PermissionManager.addPermissionGroupDesc(SCENE, Manifest.permission.WAKE_LOCK, "显示在其他应用上层")
        PermissionManager.addPermissionGroupScene(SCENE, Manifest.permission.WAKE_LOCK, "自定义锁屏界面")
    }

    fun startLockService(context: Context, cls: Class<out LockScreenService?>) {
        ZLog.e(LockScreenService.TAG, "startLockService ${cls.name}")
        val intent = Intent(context, cls)
        intent.setPackage(context.packageName)
        context.startService(intent)
    }

    fun startLockServiceWithPermission(context: Context, cls: Class<out LockScreenService?>) {
        if (PermissionManager.isAllPermissionOK(context, mLockScreenPermission)) {
            startLockService(context, cls)
        } else {
            PermissionDialog(context).apply {
                negative = "直接开启"
                positive = "授权并开启"
                needSpecial = true
            }.let {
                it.show(SCENE, mLockScreenPermission, true, object :
                    OnDialogListener {
                    override fun onPositiveClick() {
                        startLockService(context, cls)
                        openFloatSettings(context)
                        it.dismiss()
                    }

                    override fun onNegativeClick() {
                        startLockService(context, cls)
                        it.dismiss()
                    }

                    override fun onCancel() {
                        startLockService(context, cls)
                        it.dismiss()
                    }
                })
            }
        }
    }

}
package com.bihe0832.android.base.debug.permission

import android.Manifest
import android.app.Activity
import android.view.View
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.module.DebugEnvFragment
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.permission.ui.PermissionDialog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener

class DebugPermissionFragment : DebugEnvFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugItemData("自定义内容权限弹框", View.OnClickListener { testCustomPermission(activity) }))
            add(DebugItemData("通用权限弹框", View.OnClickListener { testCommonPermission(activity) }))
            add(DebugItemData("通用权限弹框V2", View.OnClickListener { testCommonPermissionV2(activity) }))
        }
    }

    private fun testCustomPermission(activity: Activity?) {
        PermissionManager.addPermissionGroupContent(HashMap<String, String>().apply {
            put(Manifest.permission.RECORD_AUDIO, "M3U8下载助手需要将<font color ='#38ADFF'><b>下载数据存储在SD卡</b></font>才能访问，当前手机尚未开启悬浮窗权限，请点击「点击开启」前往设置！")
        })
        activity?.let { it ->
            DebugPermissionDialog(it).let { permissionDialog ->
                permissionDialog.show("", Manifest.permission.RECORD_AUDIO, true, object : OnDialogListener {
                    override fun onPositiveClick() {
//                    openFloatPermissionSettings(context)
                        permissionDialog.dismiss()
                    }

                    override fun onNegativeClick() {
                        permissionDialog.dismiss()

                    }

                    override fun onCancel() {
                        permissionDialog.dismiss()
                    }
                })

                PermissionManager.addPermissionGroupContent(HashMap<String, String>().apply {
                    put(Manifest.permission.RECORD_AUDIO, "")
                })
            }
        }
    }

    private fun testCommonPermission(activity: Activity?) {
        PermissionManager.addPermissionGroupScene(HashMap<String, String>().apply {
            put(Manifest.permission.RECORD_AUDIO, "数据存储")
        })
        PermissionManager.addPermissionGroupDesc(HashMap<String, String>().apply {
            put(Manifest.permission.RECORD_AUDIO, "SD卡权限")
        })
        activity?.let { it ->
            PermissionDialog(it).let { permissionDialog ->
                permissionDialog.show("", Manifest.permission.RECORD_AUDIO, true, object : OnDialogListener {
                    override fun onPositiveClick() {
//                    openFloatPermissionSettings(context)
                        permissionDialog.dismiss()
                    }

                    override fun onNegativeClick() {
                        permissionDialog.dismiss()

                    }

                    override fun onCancel() {
                        permissionDialog.dismiss()
                    }
                })
            }
        }
    }

    private fun testCommonPermissionV2(activity: Activity?) {
        PermissionManager.checkPermission(activity, mutableListOf<String>().apply {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        })
    }
}
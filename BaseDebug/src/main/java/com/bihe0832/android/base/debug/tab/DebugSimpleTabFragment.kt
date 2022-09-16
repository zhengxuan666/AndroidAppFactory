package com.bihe0832.android.base.debug.tab

import android.view.View
import com.bihe0832.android.base.debug.R
import com.bihe0832.android.base.debug.dialog.DebugDialogFragment
import com.bihe0832.android.base.debug.download.DebugDownloadFragment
import com.bihe0832.android.base.debug.file.DebugFileFragment
import com.bihe0832.android.base.debug.permission.DebugPermissionFragment
import com.bihe0832.android.common.ui.bottom.bar.CommonMainFragment
import com.bihe0832.android.common.ui.bottom.bar.SimpleBottomBarTab
import com.bihe0832.android.framework.ui.BaseFragment
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.bottom.bar.BaseBottomBarTab

class DebugSimpleTabFragment : CommonMainFragment() {

    override fun getDefaultTabID(): Int {
        return 1
    }

    override fun getFragments(): ArrayList<BaseFragment> {
        return ArrayList<BaseFragment>().apply {
            add(DebugDialogFragment())
            add(DebugDownloadFragment())
            add(DebugPermissionFragment())
            add(DebugFileFragment())
        }
    }

    override fun getBottomBarTabs(): ArrayList<BaseBottomBarTab> {
        ArrayList<BaseBottomBarTab>().apply {
            add(SimpleBottomBarTab(context, R.mipmap.icon_camera, "弹框"))
            add(SimpleBottomBarTab(context, R.mipmap.icon_author, "下载"))
            add(SimpleBottomBarTab(context, R.mipmap.icon_feedback, "权限"))
            add(SimpleBottomBarTab(context, R.mipmap.icon_cloud, "文件"))
        }.let {
            return it
        }
    }

    override fun onBottomBarTabSelected(position: Int, prePosition: Int) {
        super.onBottomBarTabSelected(position, prePosition)
        getBottomBar().getItem(0).setUnreadDot(true)
        getBottomBar().getItem(1).setUnreadCount(2)
        getBottomBar().getItem(2).setUnreadCount(22)
        getBottomBar().getItem(3).setUnreadCount(200)
    }
}
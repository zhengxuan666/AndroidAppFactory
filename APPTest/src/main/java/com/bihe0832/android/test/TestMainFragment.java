package com.bihe0832.android.test;

import androidx.fragment.app.Fragment;

import com.bihe0832.android.base.debug.AAFDebugTempFragment;
import com.bihe0832.android.base.debug.dialog.DebugDialogFragment;
import com.bihe0832.android.base.debug.download.DebugDownloadFragment;
import com.bihe0832.android.base.debug.file.DebugFileFragment;
import com.bihe0832.android.common.debug.DebugMainFragment;
import com.bihe0832.android.common.debug.module.DebugCommonFragment;
import com.bihe0832.android.test.module.AAFDebugCommonFragment;
import com.bihe0832.android.test.module.AAFDebugRouterFragment;


/**
 * Created by zixie on 16/6/30.
 */
public class TestMainFragment extends DebugMainFragment {

    private static final String TAB_FOR_DEV_COMMON = "通用调试";
    private static final String TAB_FOR_DEV_TEMP = "临时调试";
    private static final String TAB_FOR_ROUTER = "路由测试";
    private static final String TAB_FOR_DEV = "开发测试";

    public TestMainFragment() {
        super(new String[]{
                TAB_FOR_DEV_COMMON, TAB_FOR_DEV_TEMP, TAB_FOR_ROUTER, TAB_FOR_DEV
        });
    }

    // 当前是否是开发模式
    private final boolean isDev = false;

    protected Fragment getFragmentByIndex(String title) {
        if (title.equals(TAB_FOR_DEV)) {
            return new DebugDialogFragment();
        } else if (title.equals(TAB_FOR_DEV_TEMP)) {
            return new AAFDebugTempFragment();
        } else if (title.equals(TAB_FOR_DEV_COMMON)) {
            return new AAFDebugCommonFragment();
        } else if (title.equals(TAB_FOR_ROUTER)) {
            return new AAFDebugRouterFragment();
        } else {
            return new DebugCommonFragment();
        }
    }

    @Override
    protected int getDefaultTabIndex() {
        if (isDev) {
            return mTabString.length - 1;
        } else {
            return 1;
        }
    }
}

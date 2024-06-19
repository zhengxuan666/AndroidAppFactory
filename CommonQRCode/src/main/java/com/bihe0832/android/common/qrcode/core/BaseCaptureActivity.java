/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bihe0832.android.common.qrcode.core;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import com.bihe0832.android.common.qrcode.view.ViewfinderView;
import com.bihe0832.android.framework.ZixieContext;
import com.bihe0832.android.framework.ui.BaseActivity;
import com.bihe0832.android.lib.camera.scan.CameraScan;
import com.bihe0832.android.lib.camera.scan.CameraScan.OnScanResultCallback;
import com.bihe0832.android.lib.camera.scan.DefaultCameraScan;
import com.bihe0832.android.lib.camera.scan.analyze.Analyzer;
import com.bihe0832.android.lib.camera.scan.analyze.MultiFormatAnalyzer;
import com.bihe0832.android.lib.camera.scan.analyze.QRCodeAnalyzer;
import com.bihe0832.android.lib.camera.scan.config.DecodeConfig;
import com.bihe0832.android.lib.media.image.CheckedEnableImageView;
import com.bihe0832.android.lib.qrcode.DecodeFormatManager;

public abstract class BaseCaptureActivity extends BaseActivity {

    protected PreviewView previewView;
    protected ViewfinderView viewfinderView;
    protected CheckedEnableImageView flashlightView;
    private CameraScan mCameraScan;

    public abstract int getLayoutId();

    public abstract ViewfinderView getViewfinderView();

    public abstract PreviewView getPreviewView();

    public abstract CheckedEnableImageView getFlashlightView();

    public abstract OnScanResultCallback getOnScanResultCallback();

    protected CameraScan createCameraScan() {
        return new DefaultCameraScan(this, previewView);
    }

    public CameraScan getCameraScan() {
        return mCameraScan;
    }

    protected Analyzer createAnalyzer() {
        //初始化解码配置
        DecodeConfig decodeConfig = createDecodeConfig();
        return new MultiFormatAnalyzer(decodeConfig);
    }

    protected DecodeConfig createDecodeConfig() {
        //初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        //如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
        decodeConfig.setHints(DecodeFormatManager.ALL_HINTS);
        //设置是否全区域识别，默认false
        decodeConfig.setFullAreaScan(true);
        //设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
        decodeConfig.setAreaRectRatio(0.8f);
        //设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
        decodeConfig.setAreaRectVerticalOffset(0);
        //设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数
        decodeConfig.setAreaRectHorizontalOffset(0);
        return decodeConfig;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            initUI();
        }
    }

    /**
     * 初始化
     */
    public void initUI() {
        previewView = getPreviewView();
        viewfinderView = getViewfinderView();
        flashlightView = getFlashlightView();
        if (flashlightView != null) {
            flashlightView.setOnClickListener(v -> onClickFlashlight());
        }
        initCameraScan();
        startCamera();
    }

    /**
     * 初始化CameraScan
     */
    public void initCameraScan() {
        mCameraScan = createCameraScan();
        //设置分析器,如果内置实现的一些分析器不满足您的需求，你也可以自定义去实现
        mCameraScan.setAnalyzer(createAnalyzer());
        mCameraScan.setOnScanResultCallback(getOnScanResultCallback());
    }

    /**
     * 点击手电筒
     */
    protected void onClickFlashlight() {
        toggleTorchState();
    }

    /**
     * 启动相机预览
     */
    public void startCamera() {
        if (mCameraScan != null) {
            mCameraScan.startCamera();
        }
    }

    public void stopCamera() {
        if (mCameraScan != null) {
            mCameraScan.stopCamera();
        }
    }

    private void releaseCamera() {
        if (mCameraScan != null) {
            mCameraScan.release();
        }
    }

    /**
     * 切换闪光灯状态（开启/关闭）
     */
    protected void toggleTorchState() {
        if (mCameraScan != null) {
            try {
                boolean isTorch = mCameraScan.isTorchEnabled();
                if (!isTorch) {
                    ZixieContext.INSTANCE.showToast("暂时无法开启闪光灯");
                    return;
                }
                mCameraScan.enableTorch(!isTorch);
                if (flashlightView != null) {
                    flashlightView.setChecked(!isTorch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }

}
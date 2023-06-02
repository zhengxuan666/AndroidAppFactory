package com.bihe0832.android.common.webview.nativeimpl

import android.content.Context
import com.bihe0832.android.common.webview.core.WebviewLoggerFile
import com.bihe0832.android.lib.jsbridge.BaseJsBridge

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2023/6/2.
 * Description: Description
 */
open class NativeJsBridge(context: Context?) : BaseJsBridge(context) {

    var mWebView: NativeWebView? = null

    constructor(context: Context?, webView: NativeWebView) : this(context) {
        this.mWebView = webView
    }

    override fun loadUrl(s: String) {
        mWebView?.loadUrl(s)
    }

    override fun callback(function: String, result: String, type: ResponseType) {
        WebviewLoggerFile.logCallback(function, result, type)
        super.callback(function, result, type)
    }
}
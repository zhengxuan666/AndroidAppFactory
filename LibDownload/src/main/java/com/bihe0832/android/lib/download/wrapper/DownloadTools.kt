package com.bihe0832.android.lib.download.wrapper

import android.content.Context
import android.text.TextUtils
import com.bihe0832.android.lib.download.DownloadErrorCode
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.DownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.file.mimetype.FileMimeTypes
import com.bihe0832.android.lib.request.URLUtils
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 主要用于二层封装，所有的下载会优先走到预处理的listener，处理结束以后再对外回调最终结果，仅能回调通过：
 *
 * DownLoadAPK，DownloadConfig，DownloadFile 触发的下载
 */

object DownloadTools {

    private val UNIQUE_KEY = "DownloadTools"

    // 外部注册的全局回调，仅回调，不包含任何逻辑，如果一个URL在多个地方下载，下载状态也只会每个Listener触发一次
    private var mGlobalDownloadListenerList = CopyOnWriteArrayList<DownloadListener>()
    fun addGlobalDownloadListener(downloadListener: DownloadListener?) {
        mGlobalDownloadListenerList.add(downloadListener)
    }

    fun removeGlobalDownloadListener(downloadListener: DownloadListener?) {
        if (mGlobalDownloadListenerList.contains(downloadListener)) {
            mGlobalDownloadListenerList.remove(downloadListener)
        }
    }

    // 下载URL与回调的对应关系，基本上一个URL对应一个回调，下载时在这里转换，不进一步到底层，同样的，底层的回调，在这里做进一步的分发
    private var mDownloadKeyListenerList = ConcurrentHashMap<Long, KeyListener>()

    private class KeyListener {
        private var nameListener = ConcurrentHashMap<String, CopyOnWriteArrayList<DownloadListener?>>()
        private val mListener = object : DownloadListener {
            override fun onWait(item: DownloadItem) {
                nameListener.values.forEach { list ->
                    list.forEach {
                        it?.onWait(item)
                    }
                }

                mGlobalDownloadListenerList.forEach {
                    it.onWait(item)
                }
            }

            override fun onStart(item: DownloadItem) {
                nameListener.values.forEach { list ->
                    list.forEach {
                        it?.onStart(item)
                    }
                }

                mGlobalDownloadListenerList.forEach {
                    it.onStart(item)
                }
            }

            override fun onProgress(item: DownloadItem) {
                nameListener.values.forEach { list ->
                    list.forEach {
                        it?.onProgress(item)
                    }
                }

                mGlobalDownloadListenerList.forEach {
                    it.onProgress(item)
                }
            }

            override fun onPause(item: DownloadItem) {
                nameListener.values.forEach { list ->
                    list.forEach {
                        it?.onPause(item)
                    }
                }

                mGlobalDownloadListenerList.forEach {
                    it.onPause(item)
                }
            }

            @Synchronized
            override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                nameListener.values.forEach { list ->
                    list.forEach {
                        it?.onFail(errorCode, msg, item)
                    }
                }
                nameListener.clear()

                mGlobalDownloadListenerList.forEach {
                    it.onFail(errorCode, msg, item)
                }
            }

            @Synchronized
            fun notifySuccess(downloadPath: String, item: DownloadItem): String {
                var path = downloadPath
                nameListener[UNIQUE_KEY]?.forEach {
                    it?.let {
                        path = it.onComplete(path, item)
                    }
                }
                nameListener.remove(UNIQUE_KEY)
                val iterator = nameListener.entries.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    val filePath = next.key
                    val listenerList = next.value
                    if (filePath == path) {
                        listenerList.forEach {
                            it?.let {
                                path = it.onComplete(path, item)
                            }
                        }
                    } else {
                        try {
                            FileUtils.copyFile(File(path), File(filePath), true).let { result ->
                                if (result) {
                                    path = filePath
                                    listenerList.forEach {
                                        it?.let {
                                            path = it.onComplete(path, item)
                                        }
                                    }
                                } else {
                                    listenerList.forEach {
                                        it?.onFail(
                                            DownloadErrorCode.ERR_FILE_RENAME_FAILED,
                                            "download success and rename failed",
                                            item,
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            listenerList.forEach {
                                it?.onFail(
                                    DownloadErrorCode.ERR_FILE_RENAME_FAILED,
                                    "download success and rename throw Exception:$e",
                                    item,
                                )
                            }
                        }
                    }
                    iterator.remove()
                    nameListener.remove(filePath)
                }
                mGlobalDownloadListenerList.forEach {
                    path = it.onComplete(path, item)
                }
                return path
            }

            override fun onComplete(downloadPath: String, item: DownloadItem): String {
                return notifySuccess(downloadPath, item)
            }

            @Synchronized
            override fun onDelete(item: DownloadItem) {
                nameListener.values.forEach { list ->
                    list.forEach {
                        it?.onDelete(item)
                    }
                }
                nameListener.clear()

                mGlobalDownloadListenerList.forEach {
                    it.onDelete(item)
                }
            }
        }

        fun getDownloadListener(): DownloadListener {
            return mListener
        }

        private fun getKeyDownloadListenerList(finalPath: String): CopyOnWriteArrayList<DownloadListener?> {
            var file = finalPath
            if (TextUtils.isEmpty(file)) {
                file = UNIQUE_KEY
            }
            var list = nameListener[file] ?: CopyOnWriteArrayList<DownloadListener?>()
            nameListener[file] = list
            return list
        }

        fun addNameListener(downloadListener: DownloadListener?) {
            addNameListener("", downloadListener)
        }

        fun addNameListener(finalPath: String, downloadListener: DownloadListener?) {
            getKeyDownloadListenerList(finalPath).apply {
                downloadListener?.let {
                    add(downloadListener)
                }
            }
        }
    }

    private fun addNewKeyListener(
        downloadID: Long,
        finalPath: String,
        isFile: Boolean,
        downloadListener: DownloadListener?,
    ) {
        if (!mDownloadKeyListenerList.containsKey(downloadID) || null == mDownloadKeyListenerList[downloadID]) {
            mDownloadKeyListenerList[downloadID] = KeyListener()
        }
        var keyListener = mDownloadKeyListenerList[downloadID] ?: KeyListener()
        mDownloadKeyListenerList[downloadID] = keyListener

        if (isFile) {
            keyListener.addNameListener(finalPath, downloadListener)
        } else {
            keyListener.addNameListener(downloadListener)
        }
    }

    @Synchronized
    fun startDownload(
        context: Context,
        title: String,
        msg: String,
        url: String,
        path: String,
        isFilePath: Boolean,
        md5: String,
        sha256: String,
        forceDownloadNew: Boolean,
        useMobile: Boolean,
        actionKey: String,
        forceDownload: Boolean,
        needRecord: Boolean,
        downloadListener: DownloadListener?,
    ) {
        DownloadItem().apply {
            if (FileMimeTypes.isApkFile(URLUtils.getFileName(url))) {
                setNotificationVisibility(true)
            } else {
                setNotificationVisibility(false)
            }
            this.downloadURL = url
            this.downloadTitle = title
            this.downloadDesc = msg
            if (!TextUtils.isEmpty(path)) {
                this.fileFolder = if (isFilePath) {
                    File(path).parent
                } else {
                    path
                }
            }
            this.fileMD5 = md5
            this.fileSHA256 = sha256
            this.isForceDownloadNew = forceDownloadNew
            this.actionKey = actionKey
            this.isDownloadWhenUseMobile = useMobile
            this.isNeedRecord = needRecord
        }.let {
            // 文件已经存在，直接回调
            if (isFilePath && (!TextUtils.isEmpty(md5) || !TextUtils.isEmpty(sha256))
                    && FileUtils.checkFileExist(path, 0, md5, sha256, false)
            ) {
                downloadListener?.onComplete(path, it)
                return
            }

            addNewKeyListener(it.downloadID, path, isFilePath, downloadListener)
            it.downloadListener = mDownloadKeyListenerList[it.downloadID]?.getDownloadListener()
            DownloadUtils.startDownload(context, it, forceDownload)
        }
    }
}

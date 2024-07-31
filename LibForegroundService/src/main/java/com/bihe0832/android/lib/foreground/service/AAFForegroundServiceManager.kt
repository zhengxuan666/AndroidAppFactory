package com.bihe0832.android.lib.foreground.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.bihe0832.android.lib.foreground.service.R
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.notification.NotifyManager
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.os.BuildUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * Summary
 * @author code@bihe0832.com
 * Created on 2024/7/31.
 * Description:
 *
 */
object AAFForegroundServiceManager {

    interface ForegroundServiceAction {
        fun getScene(): String
        fun getNotifyContent(): String
        fun onStartCommand(context: Context, intent: Intent, flags: Int, startId: Int)
    }

    const val TAG = "AAFForegroundService"
    const val ACTION_UPADTE = "AAFForegroundServiceManager.update"
    const val ACTION_STOP = "AAFForegroundServiceManager.stop"

    private const val NOTICE_ID = 99998
    private const val NOTICE_CHANNEL_ID = "ForegroundService"


    private var mNoticeID = NOTICE_ID
    private var mChannelID = NOTICE_CHANNEL_ID
    private var mChannelName = ""
    private var mResID = R.mipmap.icon
    private val actionList = ConcurrentHashMap<String, ForegroundServiceAction>()

    private fun startByAction(action: ForegroundServiceAction) {
        actionList[action.getScene()] = action
    }

    private fun startService(context: Context, action: String, intent: Intent): Boolean {
        try {
            intent.setClass(context, AAFForegroundService::class.java)
            intent.setAction(action)
            context.startService(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun stopService(context: Context, action: String) {
        if (actionList.containsKey(action)) {
            actionList.remove(action)
        }
        if (actionList.isEmpty()) {
            startService(context, ACTION_STOP, Intent())
        } else {
            startService(context, ACTION_UPADTE, Intent())
        }
    }


    private fun getChannelName(context: Context): String {
        return if (TextUtils.isEmpty(mChannelName)) {
            APKUtils.getAppName(context)
        } else {
            mChannelName
        }

    }

    private fun getNotifyContent(context: Context): String {
        var string = APKUtils.getAppName(context) + "的"
        string += actionList.map { it.value.getNotifyContent() }.joinToString("、")
        return string + "服务正在运行中..."
    }

    internal fun getNoticeID(): Int {
        return mNoticeID
    }

    internal fun getChannelID(): String {
        return mChannelID
    }

    internal fun getCurrentNotification(context: Context): Notification {
        val channelName = getChannelName(context)
        NotifyManager.createNotificationChannel(context, channelName, mChannelID)
        // 如果API大于18，需要弹出一个可见通知
        return if (BuildUtils.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationCompat.Builder(context, mChannelID).setOngoing(true).setSmallIcon(mResID)
                    .setContentText(getNotifyContent(context)).setContentTitle(channelName).build()
        } else {
            Notification()
        }
    }

    internal fun doAction(context: Context, intent: Intent, flags: Int, startId: Int) {
        intent.action?.let { action ->
            actionList[action]?.onStartCommand(context, intent, flags, startId)
        }
    }

    fun setNoticeID(id: Int) {
        mNoticeID = id
    }

    fun setChannelID(id: String) {
        if (!TextUtils.isEmpty(id)) {
            mChannelID = id
        }
    }

    fun setChannelName(name: String) {
        mChannelName = name
    }

    fun setResID(id: Int) {
        mResID = id
    }

    fun sendToForegroundService(context: Context, intent: Intent, action: ForegroundServiceAction): Boolean {
        val actionName = action.getScene()
        if (ACTION_UPADTE.equals(actionName) || ACTION_STOP.equals(actionName)) {
            ZLog.e(TAG, "sendToForegroundService bad action name:$actionName")
            return false
        }
        return if (startService(context, actionName, intent)) {
            startByAction(action)
            ZLog.e(TAG, "sendToForegroundService success")
            true
        } else {
            ZLog.e(TAG, "sendToForegroundService startService failed :$actionName")
            false
        }
    }

    fun deleteFromForegroundService(context: Context, scene: String) {
        stopService(context, scene)
    }
}
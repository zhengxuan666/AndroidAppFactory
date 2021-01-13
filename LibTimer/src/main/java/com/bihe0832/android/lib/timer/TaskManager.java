package com.bihe0832.android.lib.timer;


import android.text.TextUtils;
import com.bihe0832.android.lib.log.ZLog;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.jvm.Synchronized;

public class TaskManager {

    //定时任务检查的时间粒度
    private static final String LOG_TAG = "TaskManager";
    protected static final int PERIOD = 500;
    private Timer timer = null;
    private boolean started = false;
    private ConcurrentHashMap<String, BaseTask> mTaskList = new ConcurrentHashMap<String, BaseTask>();

    private static volatile TaskManager instance;

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    private TaskManager() {
        super();
    }

    private void startTimer() {
        if (started == true) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TaskDispatcher(), 0, PERIOD);
        started = true;
    }

    private void stopTimer() {
        if (timer != null && started == true) {
            timer.cancel();
            timer = null;
            started = false;
        }
    }

    private class TaskDispatcher extends TimerTask {

        @Override
        @Synchronized
        public void run() {
            try {
                if (mTaskList.isEmpty()) {
                    ZLog.d(LOG_TAG, "TaskDispatcher stopTimer");
                    stopTimer();
                } else {
                    ZLog.d(LOG_TAG, "TaskDispatcher :" + mTaskList.size());
                    Iterator<Entry<String, BaseTask>> iter = mTaskList.entrySet().iterator();
                    while (iter.hasNext()) {
                        Entry<String, BaseTask> entry = iter.next();
                        final BaseTask task = (BaseTask) entry.getValue();
                        ZLog.d(LOG_TAG, "TaskDispatcher :" + task.getTaskName());
                        if (task.isDeleted()) {
                            mTaskList.remove(task);
                        } else {
                            if (task.getNotifiedTimes() > task.getMyInterval() - 1) {
                                task.resetNotifiedTimes();
                                task.run();
                            }
                            task.increaseNotifiedTimes();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Synchronized
    public int addTask(BaseTask task) {
        ZLog.d(LOG_TAG, "add task:" + task.getTaskName());
        if (TextUtils.isEmpty(task.getTaskName())) {
            ZLog.e(LOG_TAG, "add task: bad task name" + task.getTaskName());
            return -3;
        }
        if (task.getMyInterval() < 1) {
            ZLog.e(LOG_TAG, "add task: bad Interval" + task.getTaskName());
            return -1;
        }
        int result = -1;
        if (mTaskList.containsKey(task.getTaskName())) {
            if (mTaskList.get(task.getTaskName()).isDeleted()) {
                mTaskList.get(task.getTaskName()).setDeleted(false);
                return 1;
            } else {
                return -2;
            }
        } else {
            synchronized (mTaskList) {
                mTaskList.put(task.getTaskName(), task);
            }
            result = 0;
        }

        if (!started) {
            startTimer();
        }
        return result;
    }

    /**
     * 根据定时任务名称获取对应的定时任务
     *
     * @param taskName 任务名称
     * @return 返回对应的定时任务
     */
    public BaseTask getTaskByName(String taskName) {
        return mTaskList.get(taskName);
    }

    /**
     * 根据定时任务名称删除对应的任务
     *
     * @param taskName 任务名称
     */
    public void removeTask(String taskName) {
        ZLog.d(LOG_TAG, "remove task:" + taskName);
        if (!TextUtils.isEmpty(taskName)) {
            if (mTaskList.containsKey(taskName)) {
                mTaskList.get(taskName).setDeleted(true);
                mTaskList.remove(taskName);
            }
        }
    }

    public void letTaskRunEarly(String taskName) {
        ZLog.d(LOG_TAG, "letTaskRunEarly task:" + taskName);
        BaseTask task = getTaskByName(taskName);
        if (null != task) {
            task.letTaskRunEarly();
        }
    }
}

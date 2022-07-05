/*
 * *
 *  * Created by zixie <code@bihe0832.com> on 2022/6/16 下午3:08
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 2022/6/16 下午2:15
 *
 */

package com.bihe0832.android.lib.file.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.bihe0832.android.lib.file.FileUtils;
import com.bihe0832.android.lib.file.R;
import com.bihe0832.android.lib.log.ZLog;
import com.bihe0832.android.lib.utils.os.BuildUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zixie code@bihe0832.com
 * Created on 2020-02-30.
 * ZixieFileProvider 提供的Provider时，可以选择自定路径或者使用库默认路径
 * <p>
 * 如果是选择默认的路径时你需要获取库提供的provider 可以使用接口 {@link ZixieFileProvider#getZixieFileProvider(Context, File)}
 * <p>
 * 如果是选择自定义的，
 * 需要保证文件保存地址为 {@link ZixieFileProvider#getZixieFilePath(Context)} 获取，或者
 * 使用context.getExternalFilesDir(context.getString(R.string.lib_bihe0832_file_folder))得到
 * <p>
 * 并同步做如下操作，否则会造成获取provider失败等问题：
 * <p>
 * 添加以下String值定义：
 * <p>
 * lib_bihe0832_file_folder：自定义文件目录
 * 在res/xml添加文件：file_paths.xml，内容为：
 * <p>
 * <?xml version="1.0" encoding="utf-8"?>
 * <paths>
 * <external-files-path name="download" path="你自定义的文件目录"/>
 * </paths>
 */
public class ZixieFileProvider extends FileProvider {

    @NotNull
    public static final Uri getZixieFileProvider(@NotNull Context context, @NotNull File file) {
        Uri uri = null;
        try {
            uri = FileProvider.getUriForFile(context, getZixieFileProviderName(context), file);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                uri = FileProvider.getUriForFile(context, getAAFInnerFileProviderName(context), file);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        return uri;
    }

    public static final String getZixieFileProviderName(@NotNull Context context) {
        return context.getPackageName() + ".bihe0832";
    }

    static final String getAAFInnerFileProviderName(@NotNull Context context) {
        return context.getPackageName() + ".aaf.inner";
    }

    public static void setFileUriForIntent(Context context, Intent intent, File file, String mine_type) {
        if (BuildUtils.INSTANCE.getSDK_INT() < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), mine_type);
        } else {
            Uri fileProvider = ZixieFileProvider.getZixieFileProvider(context, file);
            intent.setDataAndType(ZixieFileProvider.getZixieFileProvider(context, file), mine_type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, fileProvider);
        }
    }

    @NotNull
    public static final String getZixieFilePath(@NotNull Context context) {
        return getZixieFilePath(context, context.getResources().getString(R.string.lib_bihe0832_file_folder));
    }

    @NotNull
    public static final String getZixieFilePath(@NotNull Context context, String filePath) {
        String subPath = filePath;
        if (TextUtils.isEmpty(subPath)) {
            subPath = context.getResources().getString(R.string.lib_bihe0832_file_folder);
        }

        String absoluteFilePath = "";
        File tempFile = context.getExternalFilesDir(subPath);
        if (tempFile == null) {
            absoluteFilePath = context.getFilesDir().getAbsolutePath() + File.separator + subPath;
        } else {
            absoluteFilePath = tempFile.getAbsolutePath();
        }

        FileUtils.INSTANCE.checkAndCreateFolder(absoluteFilePath);
        if (!absoluteFilePath.endsWith(File.separator)) {
            return absoluteFilePath + File.separator;
        } else {
            return absoluteFilePath;
        }
    }


    /**
     * 将uri转换为file
     * uri类型为file的直接转换出路径
     * uri类型为content的将对应的文件复制到沙盒内的cache目录下进行操作
     *
     * @param context 上下文
     * @param uri     uri
     * @return file
     */
    public static File uriToFile(Context context, Uri uri) {
        if (uri == null) {
            ZLog.e("uri is null");
            return null;
        }
        File file = null;
        if (uri.getScheme() != null) {
            ZLog.e("uri.getScheme()：" + uri.getScheme());
            if (uri.getScheme().equals(ContentResolver.SCHEME_FILE) && uri.getPath() != null) {
                //此uri为文件，并且path不为空(保存在沙盒内的文件可以随意访问，外部文件path则为空)
                file = new File(uri.getPath());
            } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                //此uri为content类型，将该文件复制到沙盒内
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    try {
                        InputStream inputStream = resolver.openInputStream(uri);
                        if (context.getExternalCacheDir() != null) {
                            //该文件放入cache缓存文件夹中
                            File cache = new File(context.getExternalCacheDir(), fileName);
                            FileOutputStream fileOutputStream = new FileOutputStream(cache);
                            if (inputStream != null) {
//                                    FileUtils.copy(inputStream, fileOutputStream);
                                //上面的copy方法在低版本的手机中会报java.lang.NoSuchMethodError错误，使用原始的读写流操作进行复制
                                byte[] len = new byte[Math.min(inputStream.available(), 1024 * 1024)];
                                int read;
                                while ((read = inputStream.read(len)) != -1) {
                                    fileOutputStream.write(len, 0, read);
                                }
                                file = cache;
                                fileOutputStream.close();
                                inputStream.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return file;
    }
}
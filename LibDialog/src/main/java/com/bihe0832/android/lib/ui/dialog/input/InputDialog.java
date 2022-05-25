/*
 * *
 *  * Created by zixie < code@bihe0832.com > on 2022/5/25 上午10:45
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 2022/5/25 上午10:45
 *
 */

package com.bihe0832.android.lib.ui.dialog.input;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.bihe0832.android.lib.ui.dialog.CommonDialog;
import com.bihe0832.android.lib.ui.dialog.OnDialogListener;
import com.bihe0832.android.lib.ui.dialog.R;
import com.bihe0832.android.lib.utils.os.DisplayUtil;

/**
 * @author zixie code@bihe0832.com
 * Created on 2022/5/25.
 * Description: Description
 */
public class InputDialog {
    public static void showInputDialog(final Context context, String titleName, String msg, String positive,
                                       String negtive, Boolean canCanceledOnTouchOutside, int inputType, String defaultValue, String hint,
                                       final InputDialogCallback listener) {
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(titleName);
        dialog.setHtmlContent(msg);
        dialog.setPositive(positive);
        dialog.setNegative(negtive);
        dialog.setCanceledOnTouchOutside(canCanceledOnTouchOutside);
        final EditText editText = new EditText(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(params);
        editText.setSingleLine();
        editText.setTextSize(9);
        editText.setInputType(inputType);
        editText.setPadding(
                DisplayUtil.dip2px(context, 4f),
                DisplayUtil.dip2px(context, 8f),
                DisplayUtil.dip2px(context, 4f),
                DisplayUtil.dip2px(context, 8f)
        );
        editText.setBackgroundColor(context.getResources().getColor(R.color.com_bihe0832_dialog_hint));
        editText.setTextColor(context.getResources().getColor(R.color.com_bihe0832_dialog_bg));
        editText.setHint(hint);
        if (!TextUtils.isEmpty(defaultValue)) {
            editText.requestFocus();
            editText.setText(defaultValue);
            editText.selectAll();
        }

        dialog.addViewToContent(editText);
        dialog.setOnClickBottomListener(new OnDialogListener() {
            @Override
            public void onPositiveClick() {
                try {
                    listener.onPositiveClick(editText.getText().toString());
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNegativeClick() {
                try {
                    listener.onNegativeClick(editText.getText().toString());
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                try {
                    listener.onCancel(editText.getText().toString());
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    public static void showInputDialog(final Context context, String titleName, String msg, String defaultValue,
                                       final InputDialogCompletedCallback listener) {
        showInputDialog(
                context, titleName, msg,
                context.getString(R.string.dialog_button_ok), "",
                true, EditorInfo.TYPE_CLASS_TEXT, defaultValue, context.getString(R.string.dialog_input_hint),
                new InputDialogCallback() {
                    @Override
                    public void onPositiveClick(String result) {
                        listener.onInputCompleted(result);
                    }

                    @Override
                    public void onNegativeClick(String result) {

                    }

                    @Override
                    public void onCancel(String result) {

                    }
                });
    }
}
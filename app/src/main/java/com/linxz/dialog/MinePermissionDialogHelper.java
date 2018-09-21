package com.linxz.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.linxz.permission.R;
import com.linxz.permissionlib.interfaces.MineDialogHandler;
import com.linxz.permissionlib.listener.MineDialogListener;

/**
 * <p>
 * Function： TODO
 * <p>
 * ver     date      		author
 * ──────────────────────────────────
 * V1.0   2018年09月21日10:51  lin_xiao_zhang@163.com
 * <p>
 * Copyright (c) 2018,  All Rights Reserved.
 *
 * @author linxz
 */
public class MinePermissionDialogHelper {

    public static Dialog showBusinessDialog(Context context, String title,String content,final MineDialogListener mineDialogListener){
        final Dialog dialog = new Dialog(context);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_permission_notice, null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        TextView tvTitle=view.findViewById(R.id.tvTitle);
        TextView tvContent=view.findViewById(R.id.tvContent);
        tvTitle.setText(title);
        tvContent.setText(content);
        dialog.setCancelable(false);
        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mineDialogListener!=null){
                    mineDialogListener.onNegativeClick();
                }
            }
        });
        view.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mineDialogListener!=null){
                    mineDialogListener.onPositiveClick();
                }
            }
        });
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.show();
        return dialog;
    }

}

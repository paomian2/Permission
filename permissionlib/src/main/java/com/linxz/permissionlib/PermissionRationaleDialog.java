
package com.linxz.permissionlib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class PermissionRationaleDialog extends Dialog {

    private String mTitle;
    private String mMsg;
    private String positiveBtnText;
    private OnClickListener positiveButtonClickListener;
    private String negativeBtnText;
    private OnClickListener negativeButtonClickListener;

    private boolean mIsFailedPermission;
    private String[] mPermissions;

    public PermissionRationaleDialog(@NonNull Context context) {
        super(context, R.style.dialog_custom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_dialog_layout);
        // title
        boolean showTitle = !TextUtils.isEmpty(mTitle);
        TextView tv_title = (TextView) findViewById(R.id.dialog_title);
        if (showTitle) {
            tv_title.setText(mTitle);
        }
        tv_title.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        // msg
        TextView tv_msg = (TextView) findViewById(R.id.dialog_text);
        tv_msg.setText(mMsg);

        // 权限信息
        boolean showPermissionDesc = mPermissions != null && mPermissions.length != 0;
        ViewGroup perLayout = (ViewGroup) findViewById(R.id.ll_permission_desc_layout);
        if (showPermissionDesc) {
            TextView desc = (TextView) findViewById(R.id.tv_permission_desc);
            desc.setText(mIsFailedPermission ? "请求失败的权限：" : "需要请求的权限：");
            Drawable point = ContextCompat.getDrawable(getContext(), R.drawable.icon_permission);
            point.setBounds(0, 0, 10, 10);
            for (String permission : mPermissions) {
                TextView textView = new TextView(getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setTextColor(Color.parseColor("#2c3e50"));
                textView.setCompoundDrawables(point, null, null, null);
                textView.setText(" "+permission);
                perLayout.addView(textView, -1, -2);
                ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            }
        }
        perLayout.setVisibility(showPermissionDesc ? View.VISIBLE : View.GONE);

        // 确认
        TextView btn_sure = (TextView) findViewById(R.id.dialog_sure);
        if (!TextUtils.isEmpty(positiveBtnText)) {
            btn_sure.setText(positiveBtnText);
        }
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonClickListener != null) {
                    positiveButtonClickListener.onClick(PermissionRationaleDialog.this, v.getId());
                }
                dismiss();
            }
        });

        // 取消
        TextView btn_cancle = (TextView) findViewById(R.id.dialog_cancel);
        if (!TextUtils.isEmpty(negativeBtnText)) {
            btn_cancle.setText(negativeBtnText);
        }
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (negativeButtonClickListener != null) {
                    negativeButtonClickListener.onClick(PermissionRationaleDialog.this, v.getId());
                }
            }
        });

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        // 宽度全屏
        WindowManager windowManager = scanForActivity(getContext()).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
        lp.y = -display.getHeight() * 1 / 18; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        // 点击Dialog外部消失
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public PermissionRationaleDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public PermissionRationaleDialog setMessage(String msg) {
        mMsg = msg;
        return this;
    }

    public PermissionRationaleDialog setPermission(boolean isFailedPermission, String... permissions) {
        mPermissions = permissions;
        mIsFailedPermission = isFailedPermission;
        this.mIsFailedPermission = isFailedPermission;
        return this;
    }


    public PermissionRationaleDialog setPositiveButton(String btnText, OnClickListener clickListener) {
        positiveBtnText = btnText;
        positiveButtonClickListener = clickListener;
        return this;
    }

    public PermissionRationaleDialog setNegativeButton(String btnText, OnClickListener clickListener) {
        negativeBtnText = btnText;
        negativeButtonClickListener = clickListener;
        return this;
    }

    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

}

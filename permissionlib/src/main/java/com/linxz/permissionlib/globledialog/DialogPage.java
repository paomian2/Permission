
package com.linxz.permissionlib.globledialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseArray;

import com.linxz.permissionlib.PermissionRationaleDialog;

public class DialogPage implements DialogInterface {

    static SparseArray<DialogPage> dialogPageMap = new SparseArray<>();

    private Context context;

    private String mTitle;
    private String mMsg;

    private String positiveBtnText;
    private OnClickListener positiveButtonClickListener;
    private String negativeBtnText;
    private OnClickListener negativeButtonClickListener;


    private OnActivityForResultListener resultListener;

    public DialogPage(Context cx) {
        this.context = cx;
    }

    private Activity activity;
    private Dialog mDialog;

    ActivityLifecycleInterface mLifecycleInterface = new ActivityLifecycleInterface() {
        @Override
        public void onCreate(Activity activity) {
            DialogPage.this.activity = activity;
            mDialog = getDialog(activity);
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            onPageResult(requestCode, resultCode, data);
        }

        @Override
        public void onDestroy() {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    };

    private Dialog getDialog(Activity activity) {
        return new PermissionRationaleDialog(activity)
                .setTitle(mTitle)
                .setMessage(mMsg)
                .setPositiveButton(positiveBtnText, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (positiveButtonClickListener != null) {
                            positiveButtonClickListener.onClick(DialogPage.this, which);
                        }
                    }
                })
                .setNegativeButton(negativeBtnText, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (negativeButtonClickListener != null) {
                            negativeButtonClickListener.onClick(DialogPage.this, which);
                        }
                    }
                });
    }

    /**
     * 如有页面跳转返回，此方法回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onPageResult(int requestCode, int resultCode, Intent data) {
        dismiss();
        if (resultListener != null) {
            resultListener.onActivityForResult(requestCode, resultCode, data);
        }
    }

    public DialogPage setTitle(String title) {
        mTitle = title;
        return this;
    }

    public DialogPage setMsg(String msg) {
        mMsg = msg;
        return this;
    }

    public DialogPage setPositiveButton(String btnText, OnClickListener clickListener) {
        positiveBtnText = btnText;
        positiveButtonClickListener = clickListener;
        return this;
    }

    public DialogPage setNegativeButton(String btnText, OnClickListener clickListener) {
        negativeBtnText = btnText;
        negativeButtonClickListener = clickListener;
        return this;
    }


    @Override
    public void cancel() {
        dismiss();
    }

    @Override
    public void dismiss() {
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    public void showPage() {
        dialogPageMap.put(hashCode(), this);
        Intent intent = new Intent(context, DialogContentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DialogContentActivity.class.getCanonicalName(), hashCode());
        context.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int requestCode, OnActivityForResultListener resultListener) {
        this.resultListener = resultListener;
        activity.startActivityForResult(intent, requestCode);
    }

    public interface OnActivityForResultListener {
        void onActivityForResult(int requestCode, int resultCode, Intent data);
    }
}

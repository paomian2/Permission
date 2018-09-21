
package com.linxz.permissionlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.linxz.permissionlib.interfaces.DialogHandler;
import com.linxz.permissionlib.interfaces.MineDialogHandler;
import com.linxz.permissionlib.listener.MineDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class PermissionActivity extends Activity {

    private static final String PACKAGE_URL_SCHEME = "package";

    private static final int REQUEST_CODE_PERMISSION_SETTING = 300;
    private static final int REQUEST_CODE_PERMISSION_SYSTEM_ALERT_WINDOW = 301;
    private static final int REQUEST_CODE_PERMISSION_WRITE_SETTINGS = 302;


    private PermissionRequest mPermissionRequest;

    private List<String> mReqPermissionList = new ArrayList<>();

    private boolean hasRefused;

    private DialogHandler settingHandler = new PermissionDialogHandler() {

        @Override
        public void resume() {
            //去系统设置中设置权限
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(PACKAGE_URL_SCHEME, getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_CODE_PERMISSION_SETTING);
        }

        @Override
        public void showDefaultDialog(String title, String msg) {
            List<String> deniedPermissions = LinxzPermissionUtils.getDeniedPermissions(getContext(), mReqPermissionList);
            Set<String> groupLabels = LinxzPermissionUtils.getPermissionGroupLabels(getContext(), deniedPermissions);
            new PermissionRationaleDialog(getContext())
                    .setTitle(title)
                    .setMessage(msg)
                    .setPermission(true, groupLabels.toArray(new String[groupLabels.size()]))
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resume();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancel();
                        }
                    })
                    .show();
        }

        @Override
        public void showMineDialog(MineDialogHandler mineDialogHandler) {
            List<String> deniedPermissions = LinxzPermissionUtils.getDeniedPermissions(getContext(), mReqPermissionList);
            Set<String> groupLabels = LinxzPermissionUtils.getPermissionGroupLabels(getContext(), deniedPermissions);
            if (mineDialogHandler!=null){
                mineDialogHandler.onDialogShow(new MineDialogListener(){
                    @Override
                    public void onNegativeClick() {
                        cancel();
                    }

                    @Override
                    public void onPositiveClick() {
                        resume();
                    }
                },PermissionActivity.this,true,groupLabels.toArray(new String[groupLabels.size()]));
            }
        }

    };

    private DialogHandler rationaleHandler = new PermissionDialogHandler() {

        @Override
        public void resume() {
            // 执行权限请求
            if (!mReqPermissionList.isEmpty()) {
                if (!requestsSpecialPermission(mReqPermissionList)) {
                    requestPermissions(mReqPermissionList.toArray(new String[mReqPermissionList.size()]), 1);
                }
            } else {
                cancel();
            }
        }

        @Override
        public void showDefaultDialog(String title, String msg) {
            Set<String> groupLabels = LinxzPermissionUtils.getPermissionGroupLabels(getContext(), mReqPermissionList);
            resume();
//            new PermissionRationaleDialog(getContext())
//                    .setTitle(title)
//                    .setMessage(msg)
//                    .setPermission(false, groupLabels.toArray(new String[groupLabels.size()]))
//                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            resume();
//                        }
//                    })
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            cancel();
//                        }
//                    })
//                    .show();
        }

        @Override
        public void showMineDialog(MineDialogHandler mineDialogHandler) {
            Set<String> groupLabels = LinxzPermissionUtils.getPermissionGroupLabels(getContext(), mReqPermissionList);
            resume();
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int code = intent.getIntExtra(PermissionRequest.PERMISSION_REQUEST_CODE, -1);
        mPermissionRequest = PermissionRequest.mRequests.get(code);
        if (mPermissionRequest != null) {
            // 执行权限请求
            mReqPermissionList.addAll(mPermissionRequest.getDeniedPermissions());
            hasRefused = shouldShowRationalePermissions(mReqPermissionList);// true 代表之前有被拒绝过
            // 不展示预提示框的条件：（第一次请求权限且默认一次不展示提示）或者没有设置展示提示的逻辑
            if ((!hasRefused && !mPermissionRequest.mShowTipAtFirst) || !mPermissionRequest.onRationale(rationaleHandler, PermissionRequest.REQUEST_PERMISSION_RATIONALE)) {
                rationaleHandler.resume();
            }
            return;
        }
        finish();
    }

    /**
     * 危险权限请求结果返回
     *
     * @param requestCode  请求码
     * @param permissions  被请求的权限
     * @param grantResults 对应的请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_DENIED == grantResults[i]
                    || !LinxzPermissionUtils.hasPermission(this, permissions[i])) { // 权限申请失败

                boolean showRationalePermissions = shouldShowRationalePermissions(Arrays.asList(permissions));
                if (!showRationalePermissions && !hasRefused){
                    mPermissionRequest.onRationale(settingHandler, PermissionRequest.SETTING_PERMISSION_RATIONALE);
                }else{
                    rationaleHandler.cancel();
                }
//                if (mPermissionRequest.mShowTipAtFirst || hasRefused || !showRationalePermissions) {// 第二次或者多次申请被拒绝或者是小米的坑货情况
//                    mPermissionRequest.onRationale(settingHandler, JBTPermissionRequest.SETTING_PERMISSION_RATIONALE);
//                } else {
//                    rationaleHandler.cancel();
//                }
                return;
            }
        }
        rationaleHandler.cancel();
    }

    /**
     * 权限设置面板，弹窗设置面板，"系统设置"权限获取面版结果返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PERMISSION_SETTING) {
            settingHandler.cancel();
        } else if (requestCode == REQUEST_CODE_PERMISSION_SYSTEM_ALERT_WINDOW) {
            String permission = Manifest.permission.SYSTEM_ALERT_WINDOW;
            mReqPermissionList.remove(permission);
            if (hasSpecialPermission(permission)) {
                mPermissionRequest.onSpecialPermissionOk(permission);
            }
            rationaleHandler.resume();
        } else if (requestCode == REQUEST_CODE_PERMISSION_SETTING) {
            String permission = Manifest.permission.WRITE_SETTINGS;
            mReqPermissionList.remove(permission);
            if (hasSpecialPermission(permission)) {
                mPermissionRequest.onSpecialPermissionOk(permission);
            }
            rationaleHandler.resume();
        }
    }

    /**
     * 判断第一次权限请求是否被拒绝了
     *
     * @param permissions
     * @return
     */
    public boolean shouldShowRationalePermissions(@NonNull List<String> permissions) {
        for (String permission : permissions) {
            boolean rationale = shouldShowRequestPermissionRationale(permission);
            if (rationale) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求特殊权限
     *
     * @param permissions
     * @return 是否进行了请求
     */
    private boolean requestsSpecialPermission(List<String> permissions) {

        String permission = Manifest.permission.SYSTEM_ALERT_WINDOW;
        if (permissions.contains(permission)) {
            if (!hasSpecialPermission(permission)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_PERMISSION_SYSTEM_ALERT_WINDOW);
                return true;
            } else {
                mPermissionRequest.onSpecialPermissionOk(permission);
            }

        }

        permission = Manifest.permission.WRITE_SETTINGS;
        if (permissions.contains(permission)) {
            if (!hasSpecialPermission(permission)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_PERMISSION_WRITE_SETTINGS);
                return true;
            } else {
                mPermissionRequest.onSpecialPermissionOk(permission);
            }

        }
        return false;
    }

    /**
     * 是否有特殊权限
     *
     * @param permission
     * @return
     */
    private boolean hasSpecialPermission(String permission) {
        if (Manifest.permission.SYSTEM_ALERT_WINDOW.equalsIgnoreCase(permission)) {
            if (Settings.canDrawOverlays(this)) {
                return true;
            }
        } else if (Manifest.permission.WRITE_SETTINGS.equalsIgnoreCase(permission)) {
            if (Settings.System.canWrite(this)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissionRequest != null) {
            mPermissionRequest.callBackResult();
        }
    }
    
    abstract class PermissionDialogHandler implements DialogHandler {
        @Override
        public Context getContext() {
            return PermissionActivity.this;
        }

        @Override
        public void cancel() {
            finish();
        }

    }
}

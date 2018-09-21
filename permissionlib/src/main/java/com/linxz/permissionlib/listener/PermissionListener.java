package com.linxz.permissionlib.listener;

import android.support.annotation.NonNull;


public interface PermissionListener {

    /**
     * 申请权限全部成功时回调
     *
     * @param requestCode      请求码
     * @param grantPermissions 请求的权限
     */
    void onSucceed(int requestCode, @NonNull String[] grantPermissions);

    /**
     * 申请权限失败回调
     *
     * @param requestCode       请求码
     * @param deniedPermissions 请求失败的权限
     */
    void onFailed(int requestCode, @NonNull String[] deniedPermissions);

}
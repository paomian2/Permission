package com.linxz.permissionlib.interfaces;

import android.content.Context;

import com.linxz.permissionlib.listener.MineDialogListener;

/**
 * <p>
 * Function： TODO
 * <p>
 * ver     date      		author
 * ──────────────────────────────────
 * V1.0   2018年09月21日10:07  lin_xiao_zhang@163.com
 * <p>
 * Copyright (c) 2018,  All Rights Reserved.
 *
 * @author linxz
 */
public interface MineDialogHandler {
    void onDialogShow(MineDialogListener mineDialogListener, Context cx,boolean isFailedPermission, String... permissions);
}

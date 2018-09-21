package com.linxz.permissionlib;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.linxz.permissionlib.interfaces.DialogHandler;
import com.linxz.permissionlib.interfaces.MineDialogHandler;
import com.linxz.permissionlib.listener.PermissionListener;
import com.linxz.permissionlib.listener.RationaleListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author linxz
 */
public class LinxzPermissionUtils {

    private static Set<String> permissions;
    
    public static void checkPermission(Context cx, String[] permission, PermissionListener listener){
        LinxzPermissionUtils
                .with(cx)
                .permission(permission)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, DialogHandler requestRationaleHandler) {
                        requestRationaleHandler.showDefaultDialog("正在获取权限", "该功能需获取新的权限，否则无法正常使用");
                    }

                    @Override
                    public void showSettingPermissionRationale(int requestCode, DialogHandler settingRationaleHandler) {
                        settingRationaleHandler.showDefaultDialog("权限获取失败", "权限请求失败，无法正常使用该功能，是否去“设置“中开启权限？");
                    }
                })
                .callback(listener)
                .request();
    }


    public static void checkPermission(Context cx, String[] permission, PermissionListener listener, final MineDialogHandler dialogHandler){
        LinxzPermissionUtils
                .with(cx)
                .permission(permission)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, DialogHandler requestRationaleHandler) {
                        requestRationaleHandler.showMineDialog(dialogHandler);
                    }

                    @Override
                    public void showSettingPermissionRationale(int requestCode, DialogHandler settingRationaleHandler) {
                        settingRationaleHandler.showMineDialog(dialogHandler);
                    }
                })
                .callback(listener)
                .request();

    }







    /**
     * 获取权限请求对象
     *
     * @param cx
     * @return
     */
    public static PermissionRequest with(@NonNull Context cx) {
        return with(cx, false);
    }


    /**
     * 获取权限请求对象
     *
     * @param cx
     * @param showTipAtFirst 第一次是否展示提示
     * @return
     */
    public static PermissionRequest with(@NonNull Context cx, boolean showTipAtFirst) {
        if (!(cx instanceof Application)) {
            cx = cx.getApplicationContext();
        }
        return new PermissionRequest(cx, showTipAtFirst);
    }

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context     {@link Context}.
     * @param permissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context     {@link Context}.
     * @param permissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) {
                continue;
            }
            int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result == AppOpsManagerCompat.MODE_IGNORED) {
                return false;
            }
            result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取权限的名字
     *
     * @param context
     * @param permission
     * @return
     */
    public static String getPermissionLabelStr(Context context, String permission) {
        String labelStr = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            return permissionInfo.loadDescription(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return labelStr;
    }

    /**
     * 获取权限组的名字
     *
     * @param context
     * @param permission
     * @return
     */
    public static String getPermissionGroupLabelStr(Context context, String permission) {
        String labelStr = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
            return permissionGroupInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return labelStr;
    }

    /**
     * 获取权限组的名字
     *
     * @param context
     * @param permissions
     * @return
     */
    public static Set<String> getPermissionGroupLabels(Context context, List<String> permissions) {
        HashSet<String> groupLabel = new HashSet<>();
        for (String permission : permissions) {
            String labelStr = getPermissionGroupLabelStr(context, permission);
            if (!TextUtils.isEmpty(labelStr)) {
                groupLabel.add(labelStr);
            }
        }
        return groupLabel;
    }

    /**
     * 获取还未获取成功的权限
     *
     * @param context
     * @param permissions
     * @return
     */
    static List<String> getDeniedPermissions(Context context, @NonNull List<String> permissions) {
        List<String> deniedList = new ArrayList<>(1);
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedList.add(permission);
            }
        }
        return deniedList;
    }

    /**
     * 检测清单文件中是否注册了该权限
     *
     * @param context
     * @param permissions
     * @return
     */
    static boolean isPermissionInManifest(@NonNull Context context, @NonNull String[] permissions) {
        boolean result = true;
        for (String permission : permissions) {
            result &= getAllPermission(context).contains(permission);
        }
        return result;
    }

    /**
     * 获取所有在清单文件上注册的权限
     *
     * @param cx
     * @return
     */
    private static Set<String> getAllPermission(Context cx) {
        if (permissions == null) {
            permissions = new HashSet<>();
            PackageInfo packageInfo = null;
            try {
                PackageManager packageManager = cx.getPackageManager();
                packageInfo = packageManager.getPackageInfo(cx.getPackageName(), PackageManager.GET_PERMISSIONS);
                if (packageInfo.requestedPermissions != null) {
                    permissions.addAll(Arrays.asList(packageInfo.requestedPermissions));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return permissions;
    }


}

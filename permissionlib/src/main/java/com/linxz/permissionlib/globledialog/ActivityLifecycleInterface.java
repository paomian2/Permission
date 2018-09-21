
package com.linxz.permissionlib.globledialog;

import android.app.Activity;
import android.content.Intent;

interface ActivityLifecycleInterface {
    void onCreate(Activity activity);
    void onDestroy();
    void onActivityResult(int requestCode, int resultCode, Intent data);

}

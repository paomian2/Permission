package com.linxz.permission

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.linxz.dialog.MinePermissionDialogHelper
import com.linxz.permissionlib.LinxzPermissionUtils
import com.linxz.permissionlib.interfaces.MineDialogHandler
import com.linxz.permissionlib.listener.MineDialogListener
import com.linxz.permissionlib.listener.PermissionListener
import java.util.HashMap


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDef.setOnClickListener({
            defAuthRequest()
        })
        btnMine.setOnClickListener({
            mineAuthRequest()
        })


    }

    /**默认样式权限申请*/
    private fun defAuthRequest(){
        val permission=arrayOf(android.Manifest.permission.CALL_PHONE)
        LinxzPermissionUtils.checkPermission(this,permission,object :PermissionListener{
            override fun onFailed(requestCode: Int, deniedPermissions: Array<out String>) {
                Toast.makeText(this@MainActivity,"没有授权无法使用该功能",Toast.LENGTH_LONG).show()
            }

            override fun onSucceed(requestCode: Int, grantPermissions: Array<out String>) {
                callPhone("18778048490")
            }
        })
    }

    /**电话样式权限申请*/
    private fun mineAuthRequest(){
        val permission=arrayOf(android.Manifest.permission.CALL_PHONE)
        LinxzPermissionUtils.checkPermission(this,permission,object :PermissionListener{
            override fun onFailed(requestCode: Int, deniedPermissions: Array<out String>) {
                Toast.makeText(this@MainActivity,"没有授权无法使用该功能",Toast.LENGTH_LONG).show()
            }

            override fun onSucceed(requestCode: Int, grantPermissions: Array<out String>) {
                callPhone("18778048490")
            }
        },object:MineDialogHandler{
            override fun onDialogShow(mineDialogListener: MineDialogListener?, cx:Context,isFailedPermission: Boolean, vararg permissions: String?) {
                var permissionDesc=""
                for (temp in permissions) {
                    permissionDesc = " " + temp+"\n"
                }
                MinePermissionDialogHelper.showBusinessDialog(cx,"自定义Dialog",permissionDesc,mineDialogListener)
            }
        })
    }

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_CALL)
        val data = Uri.parse("tel:" + phoneNum)
        intent.data = data
        startActivity(intent)
    }
}

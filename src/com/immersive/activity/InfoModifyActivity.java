package com.immersive.activity;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.User;
import com.immersive.net.NetStatus;
import com.immersive.net.SneakerApi;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.widget.LoadingDialog;

public class InfoModifyActivity extends BaseActivity {

	public static final String TAG = "InfoModifyActivity";
	private ImageView topbar_back, topbar_syc = null;
	private TextView topbar_title = null;
	private ImageView gender_female, gender_male = null;
	private RelativeLayout select_female, select_male = null;
	private EditText et_email, et_password, et_name, et_birthday, et_height, et_weight = null;
	private Button btn_reset, btn_register = null;
	private View.OnClickListener mOnClickListener = null;
	
	private String email, password, name, birthday, height, weight ="";
	private int gender = -1;
	private LoadingDialog mLoadingDialog = null;
	private User user = null;
	
	private GreenDaoUtils mDBUtils = null;
	

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 setContentView(R.layout.page_register);
		 mDBUtils = GreenDaoUtils.getInstance(this);
		 initListener();
		 initWidget();
		 
	}
	
	private void initWidget() {
		gender_female = (ImageView) findViewById(R.id.gender_female);
		gender_female.setOnClickListener(mOnClickListener);
		gender_male = (ImageView) findViewById(R.id.gender_male);
		gender_male.setOnClickListener(mOnClickListener);
		select_female = (RelativeLayout) findViewById(R.id.female_select);
		select_male = (RelativeLayout) findViewById(R.id.male_select);
		
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);
		et_name = (EditText) findViewById(R.id.et_name);
		
		et_birthday = (EditText) findViewById(R.id.et_birthday);
		et_birthday.setOnClickListener(mOnClickListener);
		et_height = (EditText) findViewById(R.id.et_height);
		et_weight = (EditText) findViewById(R.id.et_weight);
		
		
		topbar_title = (TextView) findViewById(R.id.topbar_title);
		topbar_title.setText(getString(R.string.register));
		topbar_back = (ImageView) findViewById(R.id.topbar_opv);
		topbar_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
		topbar_back.setOnClickListener(mOnClickListener);
		topbar_syc = (ImageView) findViewById(R.id.topbar_syc);
		topbar_syc.setVisibility(View.INVISIBLE);
		
		btn_reset = (Button) findViewById(R.id.opv_reset);
		btn_reset.setOnClickListener(mOnClickListener);
		btn_register = (Button) findViewById(R.id.opv_register);
		btn_register.setOnClickListener(mOnClickListener);
	}
	
	private void initListener() {
		mOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.gender_female:
					select_female.setBackground(getResources().getDrawable(R.drawable.ic_select));
					select_male.setBackground(null);
					gender = 0;
					break;
				case R.id.gender_male:
					select_male.setBackground(getResources().getDrawable(R.drawable.ic_select));
					select_female.setBackground(null);
					gender = 1;
					break;
				case R.id.et_birthday:
					showTimePickerDailog();
					break;
				case R.id.topbar_opv:
					finish();
					break;
				case R.id.opv_register:
					register();
					break;
				case R.id.opv_reset:
					reset();
					break;
				}
				
			}
		};
	}
	
	private void showTimePickerDailog() {
		Calendar c = Calendar.getInstance();
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				et_birthday.setText(year + " - " + monthOfYear + " - " + dayOfMonth);
			}
		}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
	}
	
	private void reset() {
		et_email.setText("");
		et_password.setText("");
		et_name.setText("");
		et_birthday.setText("");
		et_height.setText("");
		et_weight.setText("");
	}
	
	private void register() {
		email = et_email.getText().toString().trim();
		password = et_password.getText().toString().trim();
		name = et_name.getText().toString().trim();
		birthday = et_birthday.getText().toString().trim();
		height = et_height.getText().toString().trim();
		weight = et_weight.getText().toString().trim();
		
		if (email == "" || password == "" || name =="" || birthday == "" || height == "" || weight == "") {
			Toast.makeText(this, "请输入完整的注册信息", Toast.LENGTH_SHORT).show();
			return;
		}
		
		user = new User();
		user.setEmail(email);
		user.setPassword(password);
		SneakerApi.userRegister(user);
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.show();
		
	}
	
	private void modifyUserInfo() {
		user.setGender(gender);
		user.setName(name);
		user.setHeight(height);
		user.setWeight(weight);
		user.setBirthday(birthday);
		
		SneakerApi.userInfo_set(user);
	}
	
	private void saveUserInfo() {
		mDBUtils.addToUserTable(user);
		AppContext.user_id = user.getId().intValue();
		AppContext.user = user;
		this.setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void handler(Message msg) {
		switch (msg.what) {
		case NetStatus.REGISTER_SUC:
			Log.d(TAG, "register Success");
			modifyUserInfo();
			break;
			
		case NetStatus.REGISTER_SUC>>2:
			mLoadingDialog.dismiss();
			break;
		
		case NetStatus.USER_HAS_EXIST:
			mLoadingDialog.dismiss();
			break;
			
		case NetStatus.USERINFO_SET_SUC:
			Log.d(TAG, "info set Success");
			saveUserInfo();
			mLoadingDialog.dismiss();
			break;
			
		case NetStatus.USERINFO_SET_SUC>>2:
			mLoadingDialog.dismiss();
			break;
		}
	}
}

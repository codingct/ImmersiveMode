package com.immersive.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.User;
import com.immersive.net.NetStatus;
import com.immersive.net.SneakerApi;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.widget.LoadingDialog;

public class LoginActivity extends BaseActivity {
	public static final String TAG = "LoginActivity";

	private LinearLayout btn_skip = null;
	private Button btn_login, btn_register = null;
	private EditText email, password = null;
	private View.OnClickListener mOnClickListener = null;
	
	private GreenDaoUtils mDBUtils = null;
	private User user = null;
	
	private LoadingDialog mLoadingDialog = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 setContentView(R.layout.page_login);
		 
		 mDBUtils = GreenDaoUtils.getInstance(this);
		 initListener();
		 initWidget();
		 loadData();
	}
	
	private void initListener() {
		mOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.login_skip:
					showDialog();
					break;
				case R.id.opv_login:
					login();
					break;
				case R.id.opv_register:
					register();
					break;
				}
			}
		};
	}
	
	private void initWidget() {
		btn_skip = (LinearLayout) findViewById(R.id.login_skip);
		btn_skip.setOnClickListener(mOnClickListener);
		btn_login = (Button) findViewById(R.id.opv_login);
		btn_login.setOnClickListener(mOnClickListener);
		btn_register = (Button) findViewById(R.id.opv_register);
		btn_register.setOnClickListener(mOnClickListener);
		
		email = (EditText) findViewById(R.id.et_email);
		password = (EditText) findViewById(R.id.et_password);
	}
	
	private void showDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.dialog_skip));
		builder.setTitle(getString(R.string.dialog_skip_title));
		
		builder.setPositiveButton(getString(R.string.dialog_skip_konw), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				skipLogin();
				finish();
				}
			});
		builder.setNegativeButton(getString(R.string.dialog_skip_login), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				}
			});
		builder.create().show();
	}
	
	private void loadData() {
		List<User> mUser = mDBUtils.getAllUser();
		if (mUser.size() == 0) {
			Log.e(TAG, "User table no record");
			return;
		}
		user = mDBUtils.getAllUser().get(0);
	}
	
	private void login() {
		user = new User();
		user.setEmail(email.getText().toString().trim());
		user.setPassword(password.getText().toString().trim());
		SneakerApi.userLogin(user);
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.show();
	}
	
	private void saveAccountInfo() {
		Log.d(TAG, "user_id:" + user.getId());
		Log.d(TAG, "AccessToken:" + AppContext.tmpAccessToken);
		mDBUtils.deleteAllUser();
		mDBUtils.addToUserTable(user);
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void skipLogin() {
		AppContext.user_id = -1;
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void register() {
		Intent intent = new Intent(this, InfoModifyActivity.class);
		startActivityForResult(intent, 1);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
    }
	
	@Override
	public void handler(Message msg) {
		switch (msg.what) {
		case NetStatus.LOGIN_SUC:
			saveAccountInfo();
			mLoadingDialog.dismiss();
			break;
			
		case NetStatus.LOGIN_SUC>>2:
			mLoadingDialog.dismiss();
			Toast.makeText(LoginActivity.this, "网络错误，请检查手机网络", Toast.LENGTH_SHORT).show();
			break;
			
		case NetStatus.USER_NOT_EXIST:
			mLoadingDialog.dismiss();
			Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
			break;
			
		case NetStatus.PASSWORD_INVALID:
			mLoadingDialog.dismiss();
			Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}

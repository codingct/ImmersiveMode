package com.immersive.net;

public class NetStatus {
	public static final int PTR_REFRESH = 1;
	public static final int PTR_LOAD = 2;
	
	

	public static final int ACCESS_TOKEN_INVALID = 100;		// Access token 错误
	public static final int ACCESS_TOKEN_NOT_EXIST = 101;	// Access token 不存在
	public static final int PARAM_POST_INVALID = 102; 		// Post 参数错误
	public static final int SERVER_REQUEST_ERROR = 103; 	// 服务器无响应
	public static final int USER_NOT_EXIST = 104;			// 用户不存在
	public static final int PASSWORD_INVALID = 105;			// 密码错误
	public static final int USER_HAS_EXIST = 106;			// 注册时用户已存在
	public static final int FILE_UPLOAD_ERROR = 107;		// 文件上传错误
	public static final int AVATAR_FORMAT_INVALID = 108;	// 头像图片格式错误
	public static final int DATABASE_NODATA = 109; 			// 数据库无数据

	//最后一位为0为了方便位移运算
	public static final int LOGIN_SUC = 10010;
	public static final int REGISTER_SUC = 10020;
	public static final int LOGOUT_SUC = 10030;
	public static final int USERINFO_GET_SUC = 10040;
	public static final int USERINFO_SET_SUC = 10050;
	public static final int RECORD_DAILY_GET_SUC = 10060;
	public static final int RECORD_DAILY_SET_SUC = 10070;
	
	
	
	
	private int statusCode;
	private String statusMessage = "error";
	
	public NetStatus(int errCode) {
		this(errCode, null);
	}
	
	public NetStatus(int statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}
}

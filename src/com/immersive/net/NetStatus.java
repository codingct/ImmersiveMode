package com.immersive.net;

public class NetStatus {
	public static final int PTR_REFRESH = 1;
	public static final int PTR_LOAD = 2;
	
	public static final int DATABAS = 100;					// 数据库错误
	public static final int ACTION_DOES_NOT_EXIST = 101;	// 行为不存在
	public static final int ACCESS_TOKEN_INVALID = 102;		// Access token 无效
	public static final int NO_DEVICE_ID = 103;				// 未提供 Device id

	//最后一位为0为了方便位移运算
	public static final int LOGIN_SUC = 10010;
	public static final int REGISTER_SUC = 10020;
	public static final int LOGOUT_SUC = 10030;
	public static final int USERINFO_GET_SUC = 10040;
	public static final int ONLINES_GET_SUC = 10050;
	public static final int FAVOUS_GET_SUC = 10060;
	public static final int ONES_FAVOUS_SUC = 10070;
	public static final int FAVOUR_CREATE_SUC = 10080;
	public static final int UPLOAD_AVATAR_SUC = 10090;
	public static final int REGIONS_SUC = 10100;
	public static final int FAVOUR_DELETE_SUC = 10110;
	public static final int USERINFO_EDIT_SUC = 10120;
	public static final int GIFT_SEND_SUC = 10130;
	public static final int GIFT_INFO_SUC = 10140;
	public static final int VISIT_SUC = 10150;
	public static final int VISIT_INFO_SUC = 10160;
	public static final int FEEDBACK_SUC = 10170;
	public static final int PHOTO_GETALL_SUC = 10180;
	public static final int UPDATE_ONLINE_STATUS_SUC = 10190;
	public static final int VOICE_GET_SUC = 10200;
	public static final int PHOTO_UPLOAD_SUC = 10210;
	public static final int PHOTO_COMPRESS_SUC = 10220;
	
	public static final int PHOTO_SELECTED = 10230;
	public static final int PHOTO_DELETE_SUC = 10240;
	public static final int VOICE_UPLOAD_SUC = 10250;
	public static final int VOICE_RECORD_SUC = 10260;
	public static final int ACCUSE_SUC = 10270;
	public static final int CREDIT_GET_SUC = 10280;
	public static final int PHOTO_SET_PRICE_SUC = 10290;
	public static final int PHOTO_BUY_SUC = 10300;
	public static final int CREDIT_RECORD_GET_SUC = 10310;
	public static final int CREDIT_WITHDRAW_SUC = 10320;
	public static final int XMPP_PHOTO_COMPRESS_SUC = 10330;
	public static final int XMPP_PHOTO_UPLOAD_SUC = 10340;
	public static final int XMPP_VOICE_UPLOAD_SUC = 10350;
	public static final int XMPP_VOICE_RECORD_SUC = 10360;
	public static final int XMPP_VOICE_GET_SUC = 10370;
	public static final int XMPP_CREDIT_GET_SUC = 10380;
	public static final int XMPP_VOICE_CHARGE_SUC = 10390;
	
	// Users
	public static final int NO_ACCEPTED_ID = 200;			// 创建用户时未指定有效第三方ID
	public static final int INVALID_ID_TYPE = 201;			// 查询用户信息时未指定有效的ID类型
	public static final int USER_DOES_NOT_EXIST = 202;		// 查询用户信息时指定ID用户不存在
	public static final int NO_WID = 203;					// 用户操作时未指定wid
	public static final int NO_USER_AFFECTED = 204;			// 更新用户资料时数据操作未影响任何行
	public static final int NO_INFO_PROVIDED = 205;			// 更新用户资料时未提供任何资料
	public static final int NO_COUNT_SPECIFIED = 206;		// 获取在线用户列表时未指定数量

	// File upload
	public static final int FILE_CORRUPTED = 300;			// 上传的文件无效
	public static final int FILE_UPLOAD_ATTACK = 301;		// 潜在的文件上传攻击行为

	// Photos
	public static final int NO_PHOTO_ID = 400;				// 删除照片时未指定照片ID
	public static final int PHOTO_DOES_NOT_EXIST = 401;		// 要访问的照片不存在
	public static final int PHOTO_NO_ACCESS = 402;			// 越权访问照片
	public static final int PHOTO_SIZE_INVALID = 403;		// 访问图片时尺寸无效
	public static final int NO_PHOTO_GROUP_ID = 404;		// 未指定图片组ID
	public static final int PHOTO_GROUP_DOES_NOT_EXIST = 405; // 要获取的图片组不存在
	public static final int NO_PRICE = 406;					// 设置图片组价格时未指定价格
	public static final int PHOTO_BOUGHT_BY_OTHERS = 407;	//图片已被他人购买
	
	// Orders
	public static final int BUYER_CREDITS_NOT_ENOUGH = 500;	// 购买照片时买家积分余额不足
	public static final int BUYER_INVALID = 501;			// 购买照片时买家无效   
	public static final int BUYER_ALREADY_BOUGHT = 502;		// 买家重复购买相同照片
	
	
	public static final int BUYER_CREDITS_NOT_ENOUGH_GIFT = 1101;	// 购买照片时买家积分余额不足
	
	
	
	private int statusCode;
	private String statusMessage;
	
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

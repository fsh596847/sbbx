package com.zhaolong.android.sbbx.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.zhaolong.android.sbbx.MyApplication;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.wxApi.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq req) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		switch(resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
//				Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
//				Toast.makeText(this, "被拒绝", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		MyApplication.wxApi.handleIntent(intent, this);
		finish();
	}
}

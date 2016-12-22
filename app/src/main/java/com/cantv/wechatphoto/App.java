package com.cantv.wechatphoto;


import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public class App extends TinkerApplication {
	public App() {
		super(ShareConstants.TINKER_ENABLE_ALL, "com.cantv.wechatphoto.SampleApplicationLike",
				"com.tencent.tinker.loader.TinkerLoader", false);
	}
}

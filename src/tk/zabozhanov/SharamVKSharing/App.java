package tk.zabozhanov.SharamVKSharing;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.vk.sdk.*;
import com.vk.sdk.api.VKError;

/**
 * Created by zabozhanov on 28/02/14.
 */
public class App extends Application {

	protected Context mContext;

	public interface AuthorizeListener {
		public void doSearch();
	}

	public static String sTokenKey = "VK_ACCESS_TOKEN";
	public static String[] sMyScope = new String[]{VKScope.AUDIO};

	public AuthorizeListener mListener;

	private static volatile App _singleton;

	public static App getInstance() {
		if (_singleton == null) {
			_singleton = new App();
			_singleton.onCreate();
		}
		return _singleton;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		initializeVK();
		_singleton = this;
	}

	public void initializeVK() {
		VKSdk.initialize(sdkListener, "4216320", VKAccessToken.tokenFromSharedPreferences(mContext, sTokenKey));
	}

	public void logout() {
		if (mContext != null) {

		}
		VKSdk.authorize(sMyScope, true, true);
	}

	private VKSdkListener sdkListener = new VKSdkListener() {
		@Override
		public void onCaptchaError(VKError captchaError) {
			new VKCaptchaDialog(captchaError).show();
		}

		@Override
		public void onTokenExpired(VKAccessToken expiredToken) {
			VKSdk.authorize(App.sMyScope);
		}

		@Override
		public void onAccessDenied(VKError authorizationError) {
			new AlertDialog.Builder(App.this)
					.setMessage(authorizationError.errorMessage)
					.show();
		}

		@Override
		public void onReceiveNewToken(VKAccessToken newToken) {
			newToken.saveTokenToSharedPreferences(getApplicationContext(), sTokenKey);
			if (mListener != null) {
				mListener.doSearch();
			}
		}

		@Override
		public void onAcceptUserToken(VKAccessToken token) {
			if (mListener != null) {
				mListener.doSearch();
			}
		}
	};
}

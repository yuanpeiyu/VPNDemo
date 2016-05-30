package com.example.yuanpeiyu.vpntest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 在焦点改变并没获取到焦点时，保存文字
 * @author lyx
 *
 */
public class RememberEditText extends EditText {
	public static final String TAG = RememberEditText.class.getSimpleName();

	private static final String PREF = TAG;
	private static final String KeyPrefix = "";

	private Context mContext = null;
	private String mKey = null;

	public RememberEditText(Context context) {
		super(context);
		mContext = context;
	}

	public RememberEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public RememberEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	// help function
	public static RememberEditText bind(Activity activity, int id, final String key) {
		RememberEditText redt = (RememberEditText) activity.findViewById(id);
		redt.setKey(key);
		redt.loadValue();
		return redt;
	}

	private void setKey(final String key) {
		mKey = KeyPrefix + key;
	}

	private void loadValue() {
		if (mKey == null) {
			return;
		}

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF,
				Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(mKey, "");
		setText(value);
	}

	private void saveValue() {
		if (mKey == null) {
			return;
		}

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(mKey, getText().toString());
		editor.commit();
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (!focused) {
			saveValue();
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

}

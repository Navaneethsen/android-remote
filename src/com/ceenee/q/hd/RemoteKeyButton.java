package com.ceenee.q.hd;

import com.ceenee.q.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * TODO: document your custom view class.
 */
public class RemoteKeyButton extends ImageButton {
	protected String keyName;

	public String getKeyName() {
		return keyName;
	}

	public RemoteKeyButton(Context context) {
		super(context);		
	}

	public RemoteKeyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initRemoteButton(attrs);
	}

	public RemoteKeyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initRemoteButton(attrs);
	}
	
	
	private void initRemoteButton(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.remote_button);
		this.keyName = a.getString(R.styleable.remote_button_remote_key_name);
		a.recycle();
	}
}

package com.axcoto.shinjuku.sushi;

import android.widget.ImageButton;

/**
 * TODO: document your custom view class.
 */
public class RemoteKeyButton extends ImageButton {

	 public RemoteKeyButton(Context context) {
	  super(context);
	  // TODO Auto-generated constructor stub
	 }

	 public RemoteKeyButton(Context context, AttributeSet attrs) {
	  super(context, attrs);
	  // TODO Auto-generated constructor stub
	  initStyleButton(attrs);
	 }

	 public RemoteKeyButton(Context context, AttributeSet attrs, int defStyle) {
	  super(context, attrs, defStyle);
	  // TODO Auto-generated constructor stub
	  initStyleButton(attrs);
	 }
	 
	 private void initStyleButton(AttributeSet attrs){
	  TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.style_Button);
	  String Text1 = a.getString(R.styleable.style_Button_myText_1);
	  String Text2 = a.getString(R.styleable.style_Button_myText_2);
	  setText(Text1 + "\n" + Text2);
	  a.recycle();
	 }   
}

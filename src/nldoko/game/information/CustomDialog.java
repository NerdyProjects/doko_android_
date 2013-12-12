package nldoko.game.information;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public abstract class CustomDialog extends Dialog implements android.view.View.OnClickListener {
	
	public Context mContext;
	public LayoutInflater mInflater;

	public Activity mActivity;
	public Dialog mDialog;
	public Button mOkBtn;
	public TextView mDialogTitle;
	
	public CustomDialog(Context context) {
		super(context);
		this.mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setUpContent();
	}

	public abstract void setUpContent();
	public abstract void onClick(View v);
}

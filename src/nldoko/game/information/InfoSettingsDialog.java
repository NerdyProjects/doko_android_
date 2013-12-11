package nldoko.game.information;

import java.util.zip.Inflater;

import nldoko.game.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class InfoSettingsDialog extends Dialog implements android.view.View.OnClickListener {
	
	private Context mContext;
	private LinearLayout mContent;
	private LayoutInflater mInflater;
	private String[][] mCntEntries;
	

	public Activity mActivity;
	public Dialog mDialog;
	public Button mOkBtn;
	public TextView mDialogTitle;
	
	public InfoSettingsDialog(Context context) {
		super(context);
		this.mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resources res = mContext.getResources();
		
		TypedArray ta = res.obtainTypedArray(R.array.infoSettingsDialogEntries);
		int n = ta.length();
		mCntEntries = new String[n][];
		for (int i = 0; i < n; ++i) {
		    int id = ta.getResourceId(i, 0);
		    if (id > 0) {
		    	mCntEntries[i] = res.getStringArray(id);
		    } 
		}
		ta.recycle();
	}
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.info_settings_dialog);
	    
	    mContent = (LinearLayout)findViewById(R.id.info_settings_dialog_container);
	    if (mContent != null && mInflater != null) {
	    	View v;
	    	TextView tV;
	    	
	    	v = mInflater.inflate(R.layout.info_settings_dialog_entry_header, null);
	    	tV = (TextView)v.findViewById(R.id.info_settings_dialog_entry_header);
	    	tV.setText(mContext.getResources().getString(R.string.str_info_cnt_cnt_variants));
	    	mContent.addView(v);
	    	
	    	for(String[] a : mCntEntries) {
				v = mInflater.inflate(R.layout.info_settings_dialog_entry, null);
				if (v != null && a.length == 2) {
					// set title
					tV = (TextView)v.findViewById(R.id.info_settings_dialog_entry_title);
					if (tV != null) {
						tV.setText(a[0]+":");
					}
					// set text
					tV = (TextView)v.findViewById(R.id.info_settings_dialog_entry_text);
					if (tV != null) {
						tV.setText(a[1]);
					}
				}
	    		mContent.addView(v);
	    	}
	    	
	    	v = mInflater.inflate(R.layout.info_settings_dialog_entry_header, null);
	    	tV = (TextView)v.findViewById(R.id.info_settings_dialog_entry_header);
	    	tV.setText(mContext.getResources().getString(R.string.str_bockrounds));
	    	mContent.addView(v);
	    	
	    	v = mInflater.inflate(R.layout.info_settings_dialog_entry, null);
	    	tV = (TextView)v.findViewById(R.id.info_settings_dialog_entry_title);
	    	tV.setVisibility(View.GONE);
	    	tV = (TextView)v.findViewById(R.id.info_settings_dialog_entry_text);
	    	tV.setText(mContext.getResources().getString(R.string.str_info_bock_cnt_info));
	    	mContent.addView(v);

	    }
	    mDialogTitle = (TextView)findViewById(R.id.info_settings_dialog_header);
	    if (mDialogTitle != null) mDialogTitle.setText(mContext.getResources().getString(R.string.str_info));
	    mOkBtn = (Button)findViewById(R.id.info_settings_dialog_btn);
	    mOkBtn.setText(mContext.getResources().getString(R.string.str_close));
	    mOkBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    	case R.id.info_settings_dialog_btn:
	    		dismiss();
	    		break;
	    	default:
	    		break;
	    }
	    dismiss();
	}
}
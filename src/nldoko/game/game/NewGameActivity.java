package nldoko.game.game;

import java.util.ArrayList;

import nldoko.game.R;
import nldoko.game.XML.DokoXMLClass;
import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.GAME_CNT_VARIANT;
import nldoko.game.information.AboutActivity;
import nldoko.game.information.InfoSettingsDialog;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewGameActivity extends Activity {
	private Context mContext;
	
	private String TAG = "NewGame";
	
	private ActionBar mActionBar;
	private LinearLayout mLayout;
	private LinearLayout mMarkSuspendedLayout;
	private Boolean isMarkSuspendedPlayerSelected = false;
	private ImageView mIv;
	private TextView mTv;
	private TextView mTvPlayerCnt;
	private LayoutInflater inflater;
	private int mPlayerCnt = DokoData.MIN_PLAYER;
	private AutoCompleteTextView myAutoComplete;
	private Spinner mSpActivePlayer;
	private Spinner mSpBockLimit;
	private Spinner mSpGameCntVariant;
	private CheckBox mCbSuspendMark;
	
	private Button mBtnStart;
	
	private ArrayList<Integer> mActivePlayerArrayList = new ArrayList<Integer>();
	private ArrayList<Integer> mBockLimitArrayList = new ArrayList<Integer>();
	private ArrayAdapter<Integer> mSPActivePlayerArrayAdapter;
	private ArrayAdapter<Integer> mSPBockLimitArrayAdapter;
	private ArrayAdapter<String> mSPGameCntVaraintArrayAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgame);
        mContext = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mActionBar = getActionBar();
        mActionBar.show();
        mActionBar.setTitle(getResources().getString(R.string.str_new_game));
        
        
        
        setUI();
        
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }


    private void setUI() {
    	mLayout = (LinearLayout)findViewById(R.id.new_game_main_layout);
    	mLayout.requestFocus();
    	
    	mLayout = (LinearLayout)findViewById(R.id.categorie_player_header);
    	
    	mIv = (ImageView)mLayout.findViewById(R.id.icon);
    	mIv.setImageDrawable(getResources().getDrawable(R.drawable.social_group));
    	
    	mTv = (TextView)mLayout.findViewById(R.id.fragment_game_round_str_prim);
    	mTv.setText(getResources().getString(R.string.str_player_create));
    	   	
    	
    	
    	mTvPlayerCnt = (TextView)mLayout.findViewById(R.id.fragment_game_round_str_extra);
    	mTvPlayerCnt.setText(String.valueOf(mPlayerCnt));
    	
    	mLayout = (LinearLayout)findViewById(R.id.player_entry_add_btn); 
    	if(mLayout != null)mLayout.setOnClickListener(new addPlayerClickListener());
    	
    	mSpActivePlayer	 	= (Spinner)findViewById(R.id.sp_act_player_cnt);
    	mSpBockLimit 		= (Spinner)findViewById(R.id.sp_bock_cnt);
    	mSpGameCntVariant	= (Spinner)findViewById(R.id.sp_game_cnt_variant);
    	mCbSuspendMark		= (CheckBox)findViewById(R.id.cb_suspend);
    	
    	mLayout = (LinearLayout)findViewById(R.id.categorie_settings_header);
    	mTv = (TextView)mLayout.findViewById(R.id.fragment_game_round_str_prim);
    	mTv.setText(getResources().getString(R.string.str_game_settings));
    	mIv = (ImageView)mLayout.findViewById(R.id.icon);
    	mIv.setImageDrawable(getResources().getDrawable(R.drawable.action_settings));
    	mIv = (ImageView)mLayout.findViewById(R.id.icon_back);
    	mIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_about));
    	
        Animation inOutInfinit = AnimationUtils.loadAnimation(this, R.anim.about_icon_infinit_fade_in_out);
        mIv.startAnimation(inOutInfinit);
        if(mIv != null)mIv.setOnClickListener(new settingInfoClickListener());
        
        mMarkSuspendedLayout = (LinearLayout)findViewById(R.id.fragment_game_set_mark_suspend_entry);
        if (mMarkSuspendedLayout != null) {
        	mMarkSuspendedLayout.setVisibility(View.GONE);
        }
        
        if (mCbSuspendMark != null) {
        	mCbSuspendMark.setOnCheckedChangeListener(new markSuspendChangeListener());
        }
    	
    	mBtnStart = (Button)findViewById(R.id.btn_change_game_settings);
    	mBtnStart.setOnClickListener(new startBtnClickListener());
    	
    	setSpinnerValues();
    	setAutoCompleteNames();
	}
    
    private void setSpinnerValues(){
    	int mSelction;
    	
    	mSelction = mSpActivePlayer.getSelectedItemPosition();
    	mActivePlayerArrayList.clear();
    	for(int k=DokoData.MIN_PLAYER; k <= mPlayerCnt && k <= DokoData.MAX_ACTIVE_PLAYER;k++) mActivePlayerArrayList.add(k);
    	mSPActivePlayerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item,mActivePlayerArrayList);
   	    mSpActivePlayer.setAdapter(mSPActivePlayerArrayAdapter);
   	    mSpActivePlayer.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int arg2, long arg3) {
				updateMarkPlayerOption();				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
   	    });
   	    		
   	    if(mSpActivePlayer.getAdapter().getCount() > mSelction) mSpActivePlayer.setSelection(mSelction);

   	    int i = 0;
   		String[] mGameCntVariantArr  = new String[DokoData.GAME_CNT_VARAINT_ARRAY.length];
   		for (Integer[] entry : DokoData.GAME_CNT_VARAINT_ARRAY) {
   			if (entry != null && entry.length == 2) {
   				String cntName = getResources().getString(entry[0]);
   				mGameCntVariantArr[i] = cntName;
   			}
   			i++;
   		}

   		mSPGameCntVaraintArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,mGameCntVariantArr);
   	    mSpGameCntVariant.setAdapter(mSPGameCntVaraintArrayAdapter);
   	    
   	 	mSelction = mSpBockLimit.getSelectedItemPosition();
    	mBockLimitArrayList.clear();
    	for(int k=0;k<=mPlayerCnt;k++) mBockLimitArrayList.add(k);
    	mSPBockLimitArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item,mBockLimitArrayList);
   	    mSpBockLimit.setAdapter(mSPBockLimitArrayAdapter);
   	    
   	    if(mSpBockLimit.getAdapter().getCount() > mSelction) mSpBockLimit.setSelection(mSelction);
    }
    
    private void setAutoCompleteNames(){
    	View v;
    	mLayout = (LinearLayout)findViewById(R.id.player_view_holder);
    	loadPlayerNames();
    	for(int i=0;i<mLayout.getChildCount();i++){
    	    v = mLayout.getChildAt(i);
    	    if (v.getId() == R.id.player_entry){
    	    	v = (AutoCompleteTextView)v.findViewById(R.id.player_entry_auto_complete);
    	    	   ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,DokoData.PLAYER_NAMES);
    	    	((AutoCompleteTextView) v).setAdapter(adapter);
    	    	((AutoCompleteTextView) v).setOnTouchListener(new View.OnTouchListener(){
    	    		   @Override
    	    		   public boolean onTouch(View v, MotionEvent event){
    	    			   ((AutoCompleteTextView) v).showDropDown();    return false;
    	    		   }
    	    		});

    	    }  
    	}
    }
    
    private void loadPlayerNames() {
		if(!DokoXMLClass.isAppDirOK(mContext)) return;
		DokoXMLClass.isXMLPresent(mContext,DokoData.PLAYER_NAMES_XML,true);
		DokoXMLClass.getPlayerNamesFromXML(mContext,DokoData.PLAYER_NAMES_XML,DokoData.PLAYER_NAMES);
		
	}


	private void updatePlayerCnt(){
    	View v;
    	mLayout = (LinearLayout)findViewById(R.id.player_view_holder);
    	mPlayerCnt = 0;
    	for(int i=0;i<mLayout.getChildCount();i++){
    	    v = mLayout.getChildAt(i);
    	    if (v.getId() == R.id.player_entry) mPlayerCnt++;
    	}
    	mTvPlayerCnt.setText(String.valueOf(mPlayerCnt));

    	updateMarkPlayerOption();
    	setSpinnerValues();
    }
	
	private void updateMarkPlayerOption() {
		if (mMarkSuspendedLayout == null) {
			return;
		}
		CheckBox cb = (CheckBox)mMarkSuspendedLayout.findViewById(R.id.cb_suspend);
        if (cb != null) {
        	if (mSpActivePlayer.getSelectedItemPosition()+4 < mPlayerCnt) {
        		mMarkSuspendedLayout.setVisibility(View.VISIBLE);
        	} else {
        		mMarkSuspendedLayout.setVisibility(View.GONE);
        		cb.setSelected(false);
        		isMarkSuspendedPlayerSelected = false;
        	}
        }
	}
	
	private class startBtnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isPlayerNameSet(true)){
				Toast.makeText(v.getContext(), R.string.str_error_player_name, Toast.LENGTH_SHORT).show();
				return;
			}
			ArrayList<String> mPlayerNames = getPlayerNames();
			Intent i = new Intent(mContext,GameActivity.class);
			for(int k=0;k<mPlayerCnt && k<mPlayerNames.size();k++){
				i.putExtra(DokoData.PLAYERS_KEY[k], mPlayerNames.get(k).toString());
			}
			i.putExtra(DokoData.PLAYER_CNT_KEY, mPlayerCnt);
			i.putExtra(DokoData.MARK_SUSPEND_OPTION_KEY, isMarkSuspendedPlayerSelected);
			i.putExtra(DokoData.BOCKLIMIT_KEY, mSpBockLimit.getSelectedItemPosition());
			i.putExtra(DokoData.ACTIVE_PLAYER_KEY, mSpActivePlayer.getSelectedItemPosition()+4);
			i.putExtra(DokoData.GAME_CNT_VARIANT_KEY, GAME_CNT_VARIANT.values()[mSpGameCntVariant.getSelectedItemPosition()]);

			startActivity(i);
		}
	}
	

	private boolean isPlayerNameSet(boolean saveToXML) {
		ArrayList<String> mPlayerNames = getPlayerNames();
		//Log.d(TAG,mPlayerNames.size()+"+"+ mPlayerCnt);
		if(mPlayerNames.size() < mPlayerCnt) return false;
		
		for(int i=0;i<mPlayerNames.size();i++){
			if(mPlayerNames.get(i).isEmpty()) return false;
		}
		
    	for(int i=0;i<DokoData.PLAYER_NAMES.size();i++){ 
    		if(!mPlayerNames.contains(DokoData.PLAYER_NAMES.get(i)) )
    			mPlayerNames.add(DokoData.PLAYER_NAMES.get(i));
    	}
		DokoData.PLAYER_NAMES = mPlayerNames;
		DokoXMLClass.savePlayerNamesToXML(mContext, DokoData.PLAYER_NAMES_XML,mPlayerNames);
		return true;
	}
	
	private ArrayList<String> getPlayerNames(){
		ArrayList<String> mPlayerNames = new ArrayList<String>();
		View v;
		AutoCompleteTextView ac;
		mLayout = (LinearLayout)findViewById(R.id.player_view_holder);

    	for(int i=0;i<mLayout.getChildCount();i++){
    	    v = mLayout.getChildAt(i);
    	    if (v.getId() == R.id.player_entry){
    	    	ac = (AutoCompleteTextView)v.findViewById(R.id.player_entry_auto_complete);
    	    	if(!mPlayerNames.contains(ac.getText().toString().trim())){
    	    		//Log.d(TAG,ac.getText().toString());
    	    		mPlayerNames.add(ac.getText().toString().trim());
    	    	}
    	    }
    	}

    	return mPlayerNames;
	}
    
    private class addPlayerClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(mPlayerCnt == DokoData.MAX_PLAYER) return;
			mLayout = (LinearLayout)findViewById(R.id.player_view_holder);
			v = inflater.inflate(R.layout.player_entry, null);
			
			mIv = (ImageView)v.findViewById(R.id.player_entry_remove);
			mIv.setOnClickListener(new removePlayerClickListener());
			mIv.setVisibility(View.VISIBLE);
			mIv.setId(mPlayerCnt+1);
			mLayout.addView(v);
			
			updatePlayerCnt();
	        
			setAutoCompleteNames();
		}
    	
    }
    
    private class removePlayerClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mLayout = (LinearLayout)findViewById(R.id.player_view_holder);	
			mLayout.removeView((View) v.getParent().getParent().getParent());
			updatePlayerCnt();	        
		}
    	
    }
    
    public class settingInfoClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			InfoSettingsDialog infoDialog = new InfoSettingsDialog(mContext);
			infoDialog.show();
		}
    }
    
    public class markSuspendChangeListener implements OnCheckedChangeListener{
		public void onCheckedChanged(CompoundButton arg0, boolean selected) {
			isMarkSuspendedPlayerSelected = selected;
		}
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    
    
    @Override
  	public boolean onOptionsItemSelected(MenuItem item){
    	// same as using a normal menu
    	Intent i;
    	switch(item.getItemId()) {
    	case R.id.action_about:
    		i = new Intent(this, AboutActivity.class);
    		startActivity(i);
    		break;
    	}
    	return true;
    }
    
    
}

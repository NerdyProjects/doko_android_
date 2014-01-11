package nldoko.game.game;

import java.util.ArrayList;

import nldoko.game.R;
import nldoko.game.XML.DokoXMLClass;
import nldoko.game.classes.GameClass;
import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.GAME_CNT_VARIANT;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeGameSettingActivity extends Activity {
	private Context mContext;
	
	private String TAG = "NewGame";
	
	private ActionBar mActionBar;
	private LinearLayout mLayout;
	private ImageView mIv;
	private TextView mTv;
	private TextView mTvPlayerCnt;
	private LayoutInflater inflater;
	private int mPlayerCnt = DokoData.MIN_PLAYER;
	private Spinner mSpActivePlayer;
	private Spinner mSpBockLimit;
	
	private Button mBtnChangeGameSettings;
	
	private ArrayList<Integer> mActivePlayerArrayList = new ArrayList<Integer>();
	private ArrayList<Integer> mBockLimitArrayList = new ArrayList<Integer>();
	private ArrayAdapter<Integer> mSPActivePlayerArrayAdapter;
	private ArrayAdapter<Integer> mSPBockLimitArrayAdapter;
	private bockLimitChangeSpListener mSpBockLimitChangeListener;
	
	private LinearLayout mPlayerHolder;
	private ArrayList<View> mPlayerViewList = new ArrayList<View>();
	
	private GameClass mGameHolder =  null;
	
	private boolean mGameSettingsChanged = false;
	
	private boolean mDeleteBockRounds = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_game_settings);
        mContext = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mActionBar = getActionBar();
        mActionBar.show();
        mActionBar.setTitle(getResources().getString(R.string.str_change_game_settings));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
        mGameHolder =  loadGameState();

        if(mGameHolder == null){
        	Toast.makeText(this, getResources().getString(R.string.str_error_load_game_for_change_game_settings), Toast.LENGTH_LONG).show();
        	finish();
        }
        
        mPlayerCnt = mGameHolder.getPlayerCount();
        setUI();
        
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }


    private GameClass loadGameState() {
    	GameClass mGame = null;
    	Intent intent = getIntent();
    	Bundle extras = intent.getExtras();
    	int mActivePlayers,mBockLimit,mPlayerCnt;
    	GAME_CNT_VARIANT mGameCntVaraint;
    	String mTmp = "";


    	if(extras != null){
        	mPlayerCnt = extras.getInt(DokoData.PLAYER_CNT_KEY,0);
        	mActivePlayers =  extras.getInt(DokoData.ACTIVE_PLAYER_KEY,0);
        	mBockLimit = extras.getInt(DokoData.BOCKLIMIT_KEY,0);
        	boolean mMarkSuspendedPlayers = extras.getBoolean(DokoData.MARK_SUSPEND_OPTION_KEY,false);
        	mGameCntVaraint = (GAME_CNT_VARIANT)intent.getSerializableExtra(DokoData.GAME_CNT_VARIANT_KEY);
        	
        	if(mPlayerCnt < DokoData.MIN_PLAYER || mPlayerCnt > DokoData.MAX_PLAYER 
        			|| mActivePlayers > mPlayerCnt || mActivePlayers < DokoData.MIN_PLAYER || 
        			(mPlayerCnt == 0 || mActivePlayers == 0))
        		return null;
        	
        	mGame = new GameClass(mPlayerCnt, mActivePlayers, mBockLimit,mGameCntVaraint,mMarkSuspendedPlayers);
        	for(int k=0;k<mPlayerCnt;k++){
        		Log.d(TAG,mTmp+"k:"+k);
        		mTmp = extras.getString(DokoData.PLAYERS_KEY[k],"");
        		if(mTmp == null || mTmp.length() == 0) return null;
        		Log.d(TAG,mTmp);
        		mGame.getPlayer(k).setName(mTmp);
        	}
        }
		return mGame;
	}


	private void setUI() {
    	mPlayerHolder = (LinearLayout)findViewById(R.id.player_view_holder);
    	View v;
    	for(int i=0;i<mGameHolder.getPlayerCount();i++){
    		v = inflater.inflate(R.layout.player_entry, null);
    		mPlayerHolder.addView(v);
    		mPlayerViewList.add(v);
    	}
    	
    	mLayout = (LinearLayout)findViewById(R.id.categorie_player_header);
    	
    	mIv = (ImageView)mLayout.findViewById(R.id.icon);
    	mIv.setImageDrawable(getResources().getDrawable(R.drawable.social_group));
    	
    	mTv = (TextView)mLayout.findViewById(R.id.fragment_game_round_str_prim);
    	mTv.setText(getResources().getString(R.string.str_player_create));
    	   	
    	
    	
    	mTvPlayerCnt = (TextView)mLayout.findViewById(R.id.fragment_game_round_str_extra);
    	mTvPlayerCnt.setText(String.valueOf(mGameHolder.getPlayerCount()));
    	
    	mLayout = (LinearLayout)findViewById(R.id.player_entry_add_btn); 
    	if(mLayout != null)mLayout.setOnClickListener(new addPlayerClickListener());
    	
    	mSpActivePlayer = (Spinner)findViewById(R.id.sp_act_player_cnt);
    	mSpBockLimit = (Spinner)findViewById(R.id.sp_bock_cnt);
    	
    	mLayout = (LinearLayout)findViewById(R.id.categorie_settings_header);
    	mTv = (TextView)mLayout.findViewById(R.id.fragment_game_round_str_prim);
    	mTv.setText(getResources().getString(R.string.str_game_settings));
    	mIv = (ImageView)mLayout.findViewById(R.id.icon);
    	mIv.setImageDrawable(getResources().getDrawable(R.drawable.action_settings));
    	
    	mBtnChangeGameSettings = (Button)findViewById(R.id.btn_change_game_settings);
    	mBtnChangeGameSettings.setOnClickListener(new changeGameSettingsBtnClickListener());
    	
    	mSpBockLimitChangeListener = new bockLimitChangeSpListener();
    	mSpBockLimit.setOnItemSelectedListener(mSpBockLimitChangeListener);
    	
    	setSpinnerValues();
    	setAutoCompleteNames();
    	
    	mLayout = (LinearLayout)findViewById(R.id.game_settings_layout);
    	mLayout.requestFocus();
		
	}
    
    private void setSpinnerValues(){
    	int mSelction;
    	
    	mSelction = mSpActivePlayer.getSelectedItemPosition();
    	mActivePlayerArrayList.clear();
    	for(int k=DokoData.MIN_PLAYER; k <= mPlayerCnt && k <= DokoData.MAX_ACTIVE_PLAYER;k++) mActivePlayerArrayList.add(k);
    	mSPActivePlayerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item,mActivePlayerArrayList);
   	    mSpActivePlayer.setAdapter(mSPActivePlayerArrayAdapter);
   	    
   	    if(mSpActivePlayer.getAdapter().getCount() > mSelction) mSpActivePlayer.setSelection(mGameHolder.getActivePlayerCount()-4);
   	    
   	 	mSelction = mSpBockLimit.getSelectedItemPosition();
    	mBockLimitArrayList.clear();
    	//disable temp 
    	//TODO
    	mSpBockLimit.setEnabled(false);
    	mSpBockLimit.setClickable(false);
    	
    	for(int k=0;k<=mPlayerCnt;k++) mBockLimitArrayList.add(k);
    	mSPBockLimitArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item,mBockLimitArrayList);
   	    mSpBockLimit.setAdapter(mSPBockLimitArrayAdapter);
   	    
   	    if(mSpBockLimit.getAdapter().getCount() > mSelction) mSpBockLimit.setSelection(mGameHolder.getBockRoundLimit());
    }
    
    private void setAutoCompleteNames(){
    	View v;
    	ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,DokoData.PLAYER_NAMES);
    	mLayout = (LinearLayout)findViewById(R.id.player_view_holder);
    	loadPlayerNames();
    	for(int i=0;i<DokoData.MAX_PLAYER && i<mPlayerViewList.size();i++){
    	    v = mPlayerViewList.get(i);
    	    if (v.getId() == R.id.player_entry){
    	    	v = (AutoCompleteTextView)v.findViewById(R.id.player_entry_auto_complete);
    	    	if(i < mGameHolder.getPlayerCount()){
    	    		((AutoCompleteTextView) v).setText(mGameHolder.getPlayer(i).getName());
    	    		((AutoCompleteTextView) v).setSelection(0); 
    	    		((AutoCompleteTextView) v).setAdapter((ArrayAdapter<String>)null);
    	    		((AutoCompleteTextView) v).setEnabled(false);
    	    	}
    	    	else{
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
    	mPlayerViewList.clear();
    	for(int i=0;i<mLayout.getChildCount();i++){
    	    v = mLayout.getChildAt(i);
    	    if (v.getId() == R.id.player_entry){
    	    	mPlayerCnt++;
    	    	mPlayerViewList.add(v);
    	    }
    	}
    	mTvPlayerCnt.setText(String.valueOf(mPlayerCnt));
    	
    	setSpinnerValues();
    	
    }
	
	private class bockLimitChangeSpListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v,	int pos, long id) {
			if(pos < mGameHolder.getBockRoundLimit()) showBockDeleteDialog();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	}
	
	private class changeGameSettingsBtnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isPlayerNameSet(true)){
				Toast.makeText(v.getContext(), R.string.str_error_player_name, Toast.LENGTH_SHORT).show();
				return;
			}
			sendGameSettingsAndExit();
		}
	}
	

	private boolean isPlayerNameSet(boolean saveToXML) {
		ArrayList<String> mPlayerNames = getPlayerNames();
		Log.d(TAG,mPlayerNames.size()+"+"+ mPlayerCnt);
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

    	for(int i=0;i<mPlayerViewList.size();i++){
    	    v = mPlayerViewList.get(i);
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
			mPlayerViewList.add(v);
			
			updatePlayerCnt();
			
			setAutoCompleteNames();
		}
    	
    }
    
    private class removePlayerClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mLayout = (LinearLayout)findViewById(R.id.player_view_holder);	
			mLayout.removeView((View) v.getParent().getParent().getParent());
			
			for(int i=0;i<mPlayerViewList.size();i++){
				Log.d(TAG,mLayout.getId()+"+"+mPlayerViewList.get(i).getId());
			}
			updatePlayerCnt();
			
		}
    	
    }
    
	private void showExitDialog(){
		Builder back = new AlertDialog.Builder(this);
		back.setTitle(R.string.str_change_game_settings_save);
		back.setMessage(R.string.str_change_game_settings_save_q);
		back.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				sendGameSettingsAndExit();
			}
		});

		back.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		back.show();
	}
	
	private void showBockDeleteDialog(){
		Builder back = new AlertDialog.Builder(this);
		back.setTitle(R.string.str_change_game_settings_delete_bockrounds);
		back.setMessage(R.string.str_change_game_settings_delete_bockrounds_q);
		back.setPositiveButton(R.string.str_accept, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mDeleteBockRounds = true;
				sendGameSettingsAndExit();
			}
		});

		back.setNegativeButton(R.string.str_abort, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mSpBockLimit.setSelection(mGameHolder.getBockRoundLimit());
			}
		});
		back.show();
	}
  
    
    protected void sendGameSettingsAndExit() {
    	Intent i = getIntent();
    	
    	i.putExtra(DokoData.CHANGE_GAME_SETTINGS_KEY, true);
    	
		i.putExtra(DokoData.PLAYER_CNT_KEY, mPlayerCnt);
		i.putExtra(DokoData.BOCKLIMIT_KEY, mSpBockLimit.getSelectedItemPosition());
		i.putExtra(DokoData.ACTIVE_PLAYER_KEY, mSpActivePlayer.getSelectedItemPosition()+4);
		
		ArrayList<String> mPlayerNames = getPlayerNames();
		for(int k=0;k<mPlayerCnt && k<mPlayerNames.size();k++){
			i.putExtra(DokoData.PLAYERS_KEY[k], mPlayerNames.get(k).toString());
		}

		setResult(RESULT_OK, i);
    	finish();	
	}
    


    @Override
    public void onBackPressed(){
    	if(mGameSettingsChanged) showExitDialog();
    	else finish();
    	return;
    }

	@Override
  	public boolean onOptionsItemSelected(MenuItem item){
    	// same as using a normal menu
    	switch(item.getItemId()) {
    	case android.R.id.home:
        	if(mGameSettingsChanged) showExitDialog();
        	else finish();
    	}
    	return true;
    }
    
}

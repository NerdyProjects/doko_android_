package nldoko.game.game;

import java.util.ArrayList;
import nldoko.android.Functions;
import nldoko.game.R;
import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.PLAYER_ROUND_RESULT_STATE;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EditRoundActivity extends Activity {
	private Context mContext;
	
	private static Intent intent;
	
	private static String TAG = "EditRound";
	
	private ActionBar mActionBar;
	private static LinearLayout mLayout;
	private static LayoutInflater mInflater;
	private static TextView mTvAddRoundBockPoints;
	private static RadioGroup mNewRoundBockRadioGroup; 
	private static Button mBtnEditRound;
	private static Button mBtnAbort;
	private static EditText mEtNewRoundPoints;
	

	
	private static ArrayList<TextView> mRoundPlayer = new ArrayList<TextView>();
	
    private static RadioButton mRNewRoundBockYes;
    private static RadioButton mRNewRoundBockNo;
    
    private static PlayernameClickListener mPlayernameClickListener;
    private static PlayernameLongClickListener mPlayernameLongClickListener;
    private static btnEditRoundClickListener mBtnEditRoundClickListener;

	private static ArrayList<String> mPlayerNames;
	private static ArrayList<PLAYER_ROUND_RESULT_STATE> mPlayerStates;
	
    private static int mWinnerList[];
    private static int mSuspendList[];
	
	
	private static int mActivePlayers;
	private static int mPlayerCnt;
	private static int mRoundPoints = 0;
	private static int mBockRound = 0;
	private static PLAYER_ROUND_RESULT_STATE mPlayerState = PLAYER_ROUND_RESULT_STATE.LOSE_STATE;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editround);
        mContext = this;
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mActionBar = getActionBar();
        mActionBar.show();
        mActionBar.setTitle(getResources().getString(R.string.str_edit_round));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
    	intent = getIntent();
    	Bundle extras = intent.getExtras();
    	
    	String mName = "";
    	
    	mPlayerNames =  	new ArrayList<String>();
    	mPlayerStates = 	new ArrayList<PLAYER_ROUND_RESULT_STATE>();
    	mWinnerList = 		new int[DokoData.MAX_PLAYER];
    	mSuspendList = 		new int[DokoData.MAX_PLAYER];


    	if(extras != null){
        	mPlayerCnt = extras.getInt(DokoData.PLAYER_CNT_KEY,0);
        	mActivePlayers =  extras.getInt(DokoData.ACTIVE_PLAYER_KEY,0);
        	mBockRound = extras.getInt(DokoData.BOCKROUND_KEY,0);
        	mRoundPoints = extras.getInt(DokoData.ROUND_POINTS_KEY,0);
        	
        	if(mPlayerCnt < DokoData.MIN_PLAYER || mPlayerCnt > DokoData.MAX_PLAYER 
        			|| mActivePlayers > mPlayerCnt || mActivePlayers < DokoData.MIN_PLAYER || 
        			(mPlayerCnt == 0 || mActivePlayers == 0))
        		return;
        	
        	for(int k=0;k<mPlayerCnt;k++){
        		mName = extras.getString(DokoData.PLAYERS_KEY[k],"");
        		mPlayerState = (PLAYER_ROUND_RESULT_STATE)intent.getSerializableExtra(DokoData.PLAYERS_KEY[k]+"_STATE");
        		if(mName == null || mName.length() == 0) return;
        		mPlayerNames.add(mName);
        		mPlayerStates.add(mPlayerState);

        	}
        }
    	
    	mLayout = (LinearLayout)findViewById(R.id.game_edit_round_main_layout);
        if(mLayout != null){
            mPlayernameLongClickListener = new PlayernameLongClickListener();
            mPlayernameClickListener = new PlayernameClickListener();
            mBtnEditRoundClickListener = new btnEditRoundClickListener();
        	setUIEditNewRound(mLayout);
        }
        
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }


	private static void setUIEditNewRound(View rootView) {
		GridView mGv;
		ImageView mIv;
		TextView mTv;
		String mStr;

		loadUINewRoundPlayerSection(rootView);
	
		mEtNewRoundPoints = (EditText)rootView.findViewById(R.id.game_add_round_points_entry);
		mEtNewRoundPoints.setText(""+mRoundPoints);
		mEtNewRoundPoints.clearFocus();
		
		mBtnEditRound = (Button)rootView.findViewById(R.id.btn_game_edit_round);
		mBtnEditRound.setOnClickListener(mBtnEditRoundClickListener);
		
		mBtnAbort = (Button)rootView.findViewById(R.id.btn_game_edit_round_abort);
		mBtnAbort.setOnClickListener(mBtnEditRoundClickListener);
		
		mTvAddRoundBockPoints = (TextView)rootView.findViewById(R.id.game_add_round_bock_points);
		if(mBockRound > 0){
			mStr = rootView.getResources().getString(R.string.str_bockround)+" ";
			mStr += Functions.getBockCountAsRom(mBockRound); 
			mTvAddRoundBockPoints.setText(mStr);
			mTvAddRoundBockPoints.setVisibility(View.VISIBLE);
		}
		
		mNewRoundBockRadioGroup = (RadioGroup)rootView.findViewById(R.id.game_add_round_bock_radio);
		mRNewRoundBockYes = (RadioButton)rootView.findViewById(R.id.game_add_round_bock_radio_yes);
		mRNewRoundBockNo = (RadioButton)rootView.findViewById(R.id.game_add_round_bock_radio_no);
		mNewRoundBockRadioGroup.setEnabled(false);
		mRNewRoundBockYes.setEnabled(false);
		mRNewRoundBockNo.setEnabled(false);
		
		mGv = (GridView)rootView.findViewById(R.id.game_add_round_point_grid);
		mGv.setAdapter(new GameAddNewRoundPointGridAdapter(rootView.getContext(),mEtNewRoundPoints));
		
		mIv = (ImageView)rootView.findViewById(R.id.icon);
		mIv.setImageDrawable(rootView.getResources().getDrawable(R.drawable.social_cc_bcc));
		
		mTv = (TextView)rootView.findViewById(R.id.fragment_game_round_str_prim);
		if(mActivePlayers < mPlayerCnt)
			mTv.setText(rootView.getResources().getString(R.string.str_game_points_choose_winner_and_suspend));
		else
			mTv.setText(rootView.getResources().getString(R.string.str_game_points_choose_winner));
	
		mLayout = (LinearLayout)rootView.findViewById(R.id.game_add_round_bock_container);
		if(mBockRound == 0) mLayout.setVisibility(View.INVISIBLE);
		else mLayout.setVisibility(View.VISIBLE);
		
		
		rootView.findViewById(R.id.game_edit_round_main_layout).requestFocus();
	}

    
    private static void loadUINewRoundPlayerSection(View rootView) {
    	LinearLayout mLl;
    	TextView mTv;
    	int mTmp;
    	
    	mRoundPlayer.clear();
    	
		mLayout = (LinearLayout)rootView.findViewById(R.id.game_add_round_playersection);
		
		mTmp = (int) ((double)mPlayerCnt/2 + 0.5d);
		for(int i=0;i<(DokoData.MAX_PLAYER/2) && i<mTmp ;i++){
			mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_add_round_2_player_row, null);
			mLayout.addView(mLl);
	
			mTv = (TextView)mLl.findViewById(R.id.game_add_round_playername_left);
			
			mTv.setText(mPlayerNames.get(i*2));
			mTv.setOnClickListener(mPlayernameClickListener);
			if(mPlayerCnt-mActivePlayers > 0) mTv.setOnLongClickListener(mPlayernameLongClickListener);
			mRoundPlayer.add(mTv);
			if(mPlayerStates.get(i*2) == PLAYER_ROUND_RESULT_STATE.WIN_STATE)
				mTv.performClick();	
			else if(mPlayerStates.get(i*2) == PLAYER_ROUND_RESULT_STATE.SUSPEND_STATE)
				mTv.performLongClick();
			
			mTv = (TextView)mLl.findViewById(R.id.game_add_round_playername_right);
			
			if(mPlayerCnt == 5 && i == 2){
				mLl.removeView(mTv);
			} else if(mPlayerCnt == 7 && i == 3){
				mLl.removeView(mTv);
			}
			else{
				mTv.setText(mPlayerNames.get(i*2+1));
				mTv.setOnClickListener(mPlayernameClickListener);
				if(mPlayerCnt-mActivePlayers > 0) mTv.setOnLongClickListener(mPlayernameLongClickListener);
				mRoundPlayer.add(mTv);
				if(mPlayerStates.get(i*2+1) == PLAYER_ROUND_RESULT_STATE.WIN_STATE) {
					Log.d(TAG,"win state click");
					mTv.performClick();	
				} else if(mPlayerStates.get(i*2+1) == PLAYER_ROUND_RESULT_STATE.SUSPEND_STATE) {
					Log.d(TAG,"lose state long click");
					mTv.performLongClick();	
				}
			}
		}
	}
    

    
	private void showExitDialog(){
		Builder back = new AlertDialog.Builder(this);
		back.setTitle(R.string.str_abort);
		back.setMessage(R.string.str_edit_round_abort_q);
		back.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});

		back.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		back.show();
	}
	private boolean isNewRoundDataOK() {
		if(getNewRoundPoints() == -1) return false;
		if(!isWinnerCntOK() || !isSuspendCntOK() ) return false;
		return true;
	}
	
	private boolean isSuspendCntOK(){
		if(mPlayerCnt-mActivePlayers == 0) return true;
		if(getSuspendCnt() == (mPlayerCnt-mActivePlayers)) return true;
		return false;
	}
	
	private boolean isWinnerCntOK(){
		int mWinnerCnt = getWinnerCnt();
		if(mWinnerCnt >= mActivePlayers || mWinnerCnt == 0) return false;
		return true;
	}
		
	private int getSuspendCnt(){
		int m = 0;
		for(int i=0;i<mSuspendList.length;i++){
			if(mSuspendList[i] == 1) m++;
		}
		return m;
	}
	
	private int getWinnerCnt(){
		int m = 0;
		for(int i=0;i<mWinnerList.length;i++){
			if(mWinnerList[i] == 1) m++;
		}
		return m;
	}
	
	public class PlayernameClickListener implements OnClickListener{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if(mRoundPlayer.size() > mSuspendList.length)
				Log.e(TAG,"error Array"+mRoundPlayer.size()+"#"+mSuspendList.length);
			for(int i=0;i<mRoundPlayer.size();i++){
				if(v == mRoundPlayer.get(i) && mSuspendList[i]==0){
					if(mWinnerList[i] == 0 && getWinnerCnt() < mActivePlayers-1){
						((TextView) v).setBackgroundDrawable(v.getResources().getDrawable(R.drawable.select_green));
						mWinnerList[i] = 1;
					}
					else{
						mWinnerList[i] = 0;
						v.setBackgroundColor(v.getResources().getColor(R.color.white));
					}
				}
			}
			return;
		}
    }
	
	public class PlayernameLongClickListener implements OnLongClickListener{
		@SuppressWarnings("deprecation")
		@Override
		public boolean onLongClick(View v) {
			for(int i=0;i<mRoundPlayer.size();i++){
				if(v == mRoundPlayer.get(i) && mWinnerList[i]==0){
					if(mSuspendList[i] == 0 && getSuspendCnt() < mPlayerCnt-mActivePlayers){
						v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.select_gray));
						mSuspendList[i] = 1;
					}
					else{
						mSuspendList[i] = 0;
						v.setBackgroundColor(v.getResources().getColor(R.color.white));
					}
				}
			}
			return true;	
		}
    }
	
	public class btnEditRoundClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_game_edit_round:
				if(!isNewRoundDataOK()){
					Toast.makeText(v.getContext(), R.string.str_error_game_new_round_data, Toast.LENGTH_SHORT).show();
					return;
				}
				
		    	Intent i = intent;
		    	i.putExtra(DokoData.CHANGE_ROUND_KEY, true);
		    	i.putExtra(DokoData.ROUND_POINTS_KEY, getNewRoundPoints() * (mBockRound + 1));
		    	
				PLAYER_ROUND_RESULT_STATE mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.WIN_STATE;
				for(int k=0; k < mPlayerCnt; k++){
					if (mSuspendList[k] == 1) {
						mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.SUSPEND_STATE;
					} else if (mWinnerList[k] == 1) {
						mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.WIN_STATE;
					} else  {
						mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.LOSE_STATE;
					}
					i.putExtra(DokoData.PLAYERS_KEY[k]+"_STATE", mPlayerRoundState.ordinal());
				}
	
				setResult(RESULT_OK, i);
		    	finish();	
				break;
				
			default:
				finish();
				break;
			}
		}
	}



    @Override
    public void onBackPressed(){
    	showExitDialog();
    	return;
    }

	@Override
  	public boolean onOptionsItemSelected(MenuItem item){
    	// same as using a normal menu
    	switch(item.getItemId()) {
    	case android.R.id.home:
    		showExitDialog(); break;
    	}
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
	
	private int getNewRoundPoints(){
		int mPoints;
		try{
			mPoints = Integer.valueOf(mEtNewRoundPoints.getText().toString());
			return mPoints;
		}
		catch(Exception e){
			return -1;
		}
	}
	
	
    
}

package nldoko.game.game;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import nldoko.android.Functions;
import nldoko.game.R;
import nldoko.game.XML.DokoXMLClass;
import nldoko.game.classes.GameClass;
import nldoko.game.classes.RoundClass;
import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.GAME_CNT_VARIANT;
import nldoko.game.data.DokoData.GAME_VIEW_TYPE;
import nldoko.game.data.DokoData.PLAYER_ROUND_RESULT_STATE;
import nldoko.game.information.AboutActivity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment.SavedState;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends FragmentActivity  {
	private Context mContext;
	
	private String TAG = "Game";
		
	private static ListView mLvRounds;
	private static GameMainListAdapter mLvRoundAdapter;
	
	private static ActionBar mActionBar;
	private static LinearLayout mLayout;
	private static LinearLayout mGameRoundsInfoSwipe;
	private static LinearLayout mBottomInfos;
	private static TextView mBottomInfoBockRoundCount;
	private static TextView mBottomInfoBockRoundPreview;
	
	private static boolean mBockPreviewOnOff = true;
	
	private TextView mTvPlayerCnt;
	private static TextView mTvAddRoundBockPoints;
	private static LayoutInflater mInflater;
	private int mPlayerCnt;
	private Spinner mSpActivePlayer;
	private Spinner mSpBockLimit;
	
	private static RadioGroup mNewRoundBockRadioGroup; 
	private static Button mBtnAddRound;
	private static EditText mEtNewRoundPoints;
	
	private ArrayList<Integer> mActivePlayerArrayList = new ArrayList<Integer>();
	private ArrayList<Integer> mBockLimitArrayList = new ArrayList<Integer>();
	private ArrayAdapter<Integer> mSPActivePlayerArrayAdapter;
	private ArrayAdapter<Integer> mSPBockLimitArrayAdapter;
	
	private static ArrayList<TextView> mGameAddRoundPlayer = new ArrayList<TextView>();
	
	private SwipePagerAdapter mDemoCollectionPagerAdapter;
    private ViewPager mViewPager;
    private int mFocusedPage = 0;
	private static final int mIndexGameMain 		= 0;
	private static final int mIndexGameNewRound 	= 1;
	protected static GameClass mGame;
    private static int mWinnerList[] = new int[DokoData.MAX_PLAYER];
    private static int mSuspendList[] = new int[DokoData.MAX_PLAYER];
    
    private static RadioButton mRNewRoundBockYes;
    private static RadioButton mRNewRoundBockNo;

    private static GameAddRoundPlayernameClickListener mAddRoundPlayernameClickListener;
    private static GameAddRoundPlayernameLongClickListener mAddRoundPlayernameLongClickListener;
    private static btnAddRoundClickListener mBtnAddRoundClickListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mContext = this;
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mActionBar = getActionBar();
        mActionBar.show();
        mActionBar.setTitle(getResources().getString(R.string.str_game));

        mGame = getGame(savedInstanceState);
        
        if(mGame == null){
        	Toast.makeText(this, getResources().getString(R.string.str_error_game_start), Toast.LENGTH_LONG).show();
        	finish();
        }
            
        mPlayerCnt = mGame.getPlayerCount();
       
        loadSwipeViews();
 
        mAddRoundPlayernameClickListener = new GameAddRoundPlayernameClickListener();
        mAddRoundPlayernameLongClickListener = new GameAddRoundPlayernameLongClickListener();
        mBtnAddRoundClickListener = new btnAddRoundClickListener();
        
        findViewById(R.id.game_main_layout).requestFocus();
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }
    
    private void loadSwipeViews(){
    	  mDemoCollectionPagerAdapter =  new SwipePagerAdapter(getSupportFragmentManager());
          mViewPager = (ViewPager) findViewById(R.id.game_pager);
          mViewPager.setAdapter(mDemoCollectionPagerAdapter);
          mViewPager.setOnPageChangeListener(new GamePageChangeListener());
    }
    
    private void reloadSwipeViews(){
    	loadSwipeViews();
    }
    
    
    private class GamePageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            mFocusedPage = position;
            String mStr = null;
            switch (mFocusedPage) {
			case mIndexGameMain:
				mActionBar.setTitle(getResources().getString(R.string.str_game)); return;
			case mIndexGameNewRound:
	  			mActionBar.setTitle(getResources().getString(R.string.str_game_new_round)); 
	  			if(mGame != null && mGame.getPreRoundList().size() > 0 && mGame.getPreRoundList().get(0).getBockCount() > 0){
	  				mStr = getResources().getString(R.string.str_bockround)+" ";
	  				mStr += Functions.getBockCountAsRom(mGame.getPreRoundList().get(0).getBockCount()); 
	  				mTvAddRoundBockPoints.setText(mStr);
	  			}
	  			else mTvAddRoundBockPoints.setText(mStr);
	  			
	  			return;
			default:
				mActionBar.setTitle(getResources().getString(R.string.str_game));
				break;
			}
        }
    }
    
    private GameClass getGame(Bundle savedInstanceState){
    	GameClass mGame = null;
    	Intent intent = getIntent();
    	Bundle extras = intent.getExtras();
    	int mActivePlayers,mBockLimit,mPlayerCnt;
    	GAME_CNT_VARIANT mGameCntVaraint;
    	String mTmp = "";
    	
    	//Log.d(TAG,"getgame");
        //Check if a game exists else try to create one 
        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
        	mGame =  loadStateData(savedInstanceState);
        }
        else if(extras != null && extras.getBoolean("RestoreGameFromXML", false)){
        	String file = extras.getString("filename");
        	Log.d(TAG,"Game from XML file:"+file);
        	mGame =  DokoXMLClass.restoreGameStateFromXML(this,file);
        	if (mGame != null) {
        		// if success delete old file
                DokoXMLClass.saveGameStateToXML(mContext, mGame);
        	}
        }
        else if(extras != null){
        	mPlayerCnt 		= extras.getInt(DokoData.PLAYER_CNT_KEY,0);
        	mActivePlayers 	= extras.getInt(DokoData.ACTIVE_PLAYER_KEY,0);
        	mBockLimit		= extras.getInt(DokoData.BOCKLIMIT_KEY,0);
        	mGameCntVaraint = (GAME_CNT_VARIANT)intent.getSerializableExtra(DokoData.GAME_CNT_VARIANT_KEY);
        	
        	if(mPlayerCnt < DokoData.MIN_PLAYER || mPlayerCnt > DokoData.MAX_PLAYER 
        			|| mActivePlayers > mPlayerCnt || mActivePlayers < DokoData.MIN_PLAYER
        			|| (mPlayerCnt == 0 || mActivePlayers == 0))
        		return null;
        	
        	mGame = new GameClass(mPlayerCnt, mActivePlayers, mBockLimit,mGameCntVaraint);
        	Log.d(TAG,"bl:"+mBockLimit+" - prercnt"+mGame.getPreRoundList().size());
        	for(int k=0;k<mPlayerCnt;k++){
        		//Log.d(TAG,mTmp+"k:"+k);
        		mTmp = extras.getString(DokoData.PLAYERS_KEY[k],"");
        		if(mTmp == null || mTmp.length() == 0) return null;
        		//Log.d(TAG,mTmp);
        		mGame.getPlayer(k).setName(mTmp);
        	}
        }
        else{
        	mGame = new GameClass(5, 4, 1, GAME_CNT_VARIANT.CNT_VARIANT_NORMAL);
	    	
        	mGame.getPlayer(0).setName("Johannes");
        	mGame.getPlayer(1).setName("Christoph");
        	mGame.getPlayer(2).setName("P3");
	    	mGame.getPlayer(3).setName("P4");
	    	mGame.getPlayer(4).setName("P5");
	    	mGame.getPlayer(5).setName("P6");
	    	mGame.getPlayer(6).setName("P7");
	    	mGame.getPlayer(7).setName("P8");
        }
        return mGame;
    }
    

    
    
	 // Since this is an object collection, use a FragmentStatePagerAdapter,
	 // and NOT a FragmentPagerAdapter.
	 public class SwipePagerAdapter extends FragmentPagerAdapter {
	    private static final int mIndexCnt	= 2;  
	    

	    
	    public SwipePagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	
	    @Override
	    public Fragment getItem(int i) {
	        Fragment fragment = new DemoObjectFragment();
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt(DemoObjectFragment.mPageIndex, i);
	        fragment.setArguments(args);
	        return fragment;
	    }
	
	    @Override
	    public int getCount() {
	        return mIndexCnt;
	    }
	
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	Log.d(TAG,"frag getPageTitle");
	        return "OBJECT " + (position);
	    }
	}

	// Instances of this class are fragments representing a single
	//object in our collection.
	public static class DemoObjectFragment extends Fragment {
	  public static final String mPageIndex = "pageIndex";

	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      // The last two arguments ensure LayoutParams are inflated
	      // properly.
		  Bundle args = getArguments();
		  View rootView;
		  switch(args.getInt(mPageIndex)){
		  	case mIndexGameMain:
		  		rootView  = inflater.inflate(R.layout.fragment_game_main, container, false);
		  		setUIMain(rootView);
		  		break;
		  	case mIndexGameNewRound:
		  		rootView  = inflater.inflate(R.layout.fragment_game_add_round, container, false);
		  		setUINewRound(rootView);
		  		break;
		  	default:
		  		rootView  = inflater.inflate(R.layout.spacer_gray, container, false);
		  }
		  return rootView;
	  }
	}
	
	private static void setUIMain(View rootView) {
		mLvRounds = (ListView)rootView.findViewById(R.id.fragment_game_round_list);
		
		mLvRounds.setDivider(rootView.getResources().getDrawable(R.color.gray_dark));
		mLvRounds.setDividerHeight(1);
		
		mLvRoundAdapter = new GameMainListAdapter(rootView.getContext(), mGame.getRoundList(),mGame);
		mLvRounds.setAdapter(mLvRoundAdapter);
		mLvRoundAdapter.changeRoundListViewMode(GAME_VIEW_TYPE.ROUND_VIEW_TABLE);
		mGameRoundsInfoSwipe = (LinearLayout)rootView.findViewById(R.id.fragment_game_rounds_infos);
		if(mGame != null && mGame.getRoundCount() > 0 && mLvRoundAdapter.getRoundListViewMode() == GAME_VIEW_TYPE.ROUND_VIEW_DETAIL)
			mGameRoundsInfoSwipe.removeAllViews();
		
		if(mGame != null && mGame.getRoundCount() > 0 && mLvRoundAdapter.getRoundListViewMode() == GAME_VIEW_TYPE.ROUND_VIEW_TABLE){
			mGameRoundsInfoSwipe.removeAllViews();
			createTableHeader();
		}
		
		mBottomInfos = (LinearLayout)rootView.findViewById(R.id.fragment_game_bottom_infos);
		mBottomInfoBockRoundCount = (TextView)rootView.findViewById(R.id.fragment_game_bottom_infos_content_bock_count);
		mBottomInfoBockRoundPreview = (TextView)rootView.findViewById(R.id.fragment_game_bottom_infos_content_bock_count_preview);
		
		if(mGame != null){
			if(mGame.getBockRoundLimit() == 0 )
				mBottomInfos.setVisibility(View.GONE);
			else setBottomInfo();
		}
		
	}

	
	private static void setBottomInfo() {
		int mBockRoundCnt = 0, mTmp = 0;
		String mBockPreviewStr = "";
		if(mGame != null && mGame.getBockRoundLimit() > 0){
			for(int i=0;i<mGame.getPreRoundList().size();i++){
				mTmp = mGame.getPreRoundList().get(i).getBockCount();
				if(mTmp > 0){
					mBockRoundCnt++;
					mBockPreviewStr += Functions.getBockCountAsRom(mTmp)+", ";
				}
			}
		}
		if(mBockPreviewStr.length() > 0)
			mBockPreviewStr.substring(0, mBockPreviewStr.length()-1);
		mBottomInfoBockRoundCount.setText(String.valueOf(mBockRoundCnt));
		mBottomInfoBockRoundPreview.setText(mBockPreviewStr);
	}


	private static void setUINewRound(View rootView) {
		GridView mGv;
		ImageView mIv;
		TextView mTv;

		loadUINewRoundPlayerSection(rootView);
	
		mEtNewRoundPoints = (EditText)rootView.findViewById(R.id.game_add_round_points_entry);
		mEtNewRoundPoints.clearFocus();
		
		mBtnAddRound = (Button)rootView.findViewById(R.id.btn_game_add_new_round);
		mBtnAddRound.setOnClickListener(mBtnAddRoundClickListener);
		
		mTvAddRoundBockPoints = (TextView)rootView.findViewById(R.id.game_add_round_bock_points);

		
		mNewRoundBockRadioGroup = (RadioGroup)rootView.findViewById(R.id.game_add_round_bock_radio);
		mRNewRoundBockYes = (RadioButton)rootView.findViewById(R.id.game_add_round_bock_radio_yes);
		mRNewRoundBockNo = (RadioButton)rootView.findViewById(R.id.game_add_round_bock_radio_no);
		
		mGv = (GridView)rootView.findViewById(R.id.game_add_round_point_grid);
		mGv.setAdapter(new GameAddNewRoundPointGridAdapter(rootView.getContext(),mEtNewRoundPoints));
		
		mIv = (ImageView)rootView.findViewById(R.id.icon);
		mIv.setImageDrawable(rootView.getResources().getDrawable(R.drawable.social_cc_bcc));
		
		mTv = (TextView)rootView.findViewById(R.id.fragment_game_round_str_prim);
		if(mGame.getActivePlayerCount() < mGame.getPlayerCount())
			mTv.setText(rootView.getResources().getString(R.string.str_game_points_choose_winner_and_suspend));
		else
			mTv.setText(rootView.getResources().getString(R.string.str_game_points_choose_winner));
	
		mLayout = (LinearLayout)rootView.findViewById(R.id.game_add_round_bock_container);
		if(mGame.getBockRoundLimit() == 0) mLayout.setVisibility(View.INVISIBLE);
		else mLayout.setVisibility(View.VISIBLE);
		
		
		rootView.findViewById(R.id.game_add_round_main_layout).requestFocus();
	}
	
	
    private static void loadUINewRoundPlayerSection(View rootView) {
    	LinearLayout mLl;
    	TextView mTv;
    	int mTmp;
    	
    	mGameAddRoundPlayer.clear();
    	
		mLayout = (LinearLayout)rootView.findViewById(R.id.game_add_round_playersection);
		
		mTmp = (int) ((double)mGame.getPlayerCount()/2 + 0.5d);
		for(int i=0;i<(DokoData.MAX_PLAYER/2) && i<mTmp ;i++){
			mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_add_round_2_player_row, null);
			mLayout.addView(mLl);
	
			mTv = (TextView)mLl.findViewById(R.id.game_add_round_playername_left);
			
			mTv.setText(mGame.getPlayer(i*2).getName());
			mTv.setOnClickListener(mAddRoundPlayernameClickListener);
			if(mGame.getPlayerCount()-mGame.getActivePlayerCount() > 0) mTv.setOnLongClickListener(mAddRoundPlayernameLongClickListener);
			mGameAddRoundPlayer.add(mTv);
			
			mTv = (TextView)mLl.findViewById(R.id.game_add_round_playername_right);
			
			if(mGame.getPlayerCount() == 5 && i == 2){
				mLl.removeView(mTv);
			} else if(mGame.getPlayerCount() == 7 && i == 3){
					mLl.removeView(mTv);
			} else{
				mTv.setText(mGame.getPlayer(i*2+1).getName());
				mTv.setOnClickListener(mAddRoundPlayernameClickListener);
				mGameAddRoundPlayer.add(mTv);
				if(mGame.getPlayerCount()-mGame.getActivePlayerCount() > 0) mTv.setOnLongClickListener(mAddRoundPlayernameLongClickListener);
			}
		}
	}
    

    
    private void setSpinnerValues(){
    	int mSelction;
    	
    	mSelction = mSpActivePlayer.getSelectedItemPosition();
    	mActivePlayerArrayList.clear();
    	for(int k=DokoData.MIN_PLAYER;k<=mPlayerCnt;k++) mActivePlayerArrayList.add(k);
    	mSPActivePlayerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item,mActivePlayerArrayList);
   	    mSpActivePlayer.setAdapter(mSPActivePlayerArrayAdapter);
   	    
   	    if(mSpActivePlayer.getAdapter().getCount() > mSelction) mSpActivePlayer.setSelection(mSelction);
   	    
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
    	
    	setSpinnerValues();
    }
	
	private class btnStartClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isPlayerNameSet(true)){
				Toast.makeText(v.getContext(), R.string.str_error_player_name, Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}
	
	public class btnAddRoundClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(mGame.getRoundCount() == 0){
				if(mLvRoundAdapter.getRoundListViewMode() == GAME_VIEW_TYPE.ROUND_VIEW_TABLE){
					mGameRoundsInfoSwipe.removeAllViews();
					createTableHeader();
				}
				else if(mLvRoundAdapter.getRoundListViewMode() == GAME_VIEW_TYPE.ROUND_VIEW_DETAIL){ 
					mGameRoundsInfoSwipe.removeAllViews();
				}
			}
			
			
			if(!isNewRoundDataOK()){
				Toast.makeText(v.getContext(), R.string.str_error_game_new_round_data, Toast.LENGTH_SHORT).show();
				return;
			}
			
			for(int i=0;i<mWinnerList.length;i++){
				Log.d(TAG,"i:"+i+" win: "+mWinnerList[i]+"~ suspend: "+mSuspendList[i]);
			}
			
			Log.d(TAG,mGame.toString());
			mGame.addNewRound(getNewRoundPoints(),isNewBockRoundSet(),mWinnerList,mSuspendList);
			Log.d(TAG,mGame.toString());		
			notifyDataSetChanged();
			 
			
			mViewPager.setCurrentItem(0,true);
			mLvRounds.requestFocus();
			
			if(mLvRounds.getCount() >= 1)
				mLvRounds.setSelection(mLvRounds.getCount()-1);
			
			resetNewRoundFields();
			
			DokoXMLClass.saveGameStateToXML(mContext, mGame);
			
			setBottomInfo();
		}


	}
	
	private void  notifyDataSetChanged() {
		if(mLvRounds.getAdapter() instanceof ArrayAdapter<?>)((ArrayAdapter<?>) mLvRoundAdapter).notifyDataSetChanged();
	}
	
	
	private static void createTableHeader() {
		LinearLayout mLl = null;
		TextView mTv = null;
		switch(mGame.getPlayerCount()){
			case 4: mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_round_view_table_4_player_header, null); break;
			case 5: mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_round_view_table_5_player_header, null); break;
			case 6: mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_round_view_table_6_player_header, null); break;
			case 7: mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_round_view_table_7_player_header, null); break;
			case 8: mLl = (LinearLayout) mInflater.inflate(R.layout.fragment_game_round_view_table_8_player_header, null); break;
		}
		if(mLl ==  null || DokoData.mTvTablePlayerName.length < mGame.getPlayerCount()) return;
		
		for(int i=0;i<mGame.getPlayerCount();i++){
			mTv = (TextView)mLl.findViewById(DokoData.mTvTablePlayerName[i]);
			mTv.setText(mGame.getPlayer(i).getName());
		}
		mGameRoundsInfoSwipe.removeAllViews();
		mGameRoundsInfoSwipe.addView(mLl);
		mGameRoundsInfoSwipe.setGravity(Gravity.LEFT);
	}
	
	public class GameAddRoundPlayernameClickListener implements OnClickListener{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if(mGameAddRoundPlayer.size() > mSuspendList.length)
				Log.e(TAG,"error Array"+mGameAddRoundPlayer.size()+"#"+mSuspendList.length);
			for(int i=0;i<mGameAddRoundPlayer.size();i++){
				if(v == mGameAddRoundPlayer.get(i) && mSuspendList[i]==0){
					if(mWinnerList[i] == 0 && getWinnerCnt() < mGame.getActivePlayerCount()-1){
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
	
	public class GameAddRoundPlayernameLongClickListener implements OnLongClickListener{
		@SuppressWarnings("deprecation")
		@Override
		public boolean onLongClick(View v) {
			for(int i=0;i<mGameAddRoundPlayer.size();i++){
				if(v == mGameAddRoundPlayer.get(i) && mWinnerList[i]==0){
					if(mSuspendList[i] == 0 && getSuspendCnt() < mGame.getPlayerCount()-mGame.getActivePlayerCount()){
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
	
	private boolean isNewRoundDataOK() {
		if(getNewRoundPoints() == -1) return false;
		if(getNewRoundPoints() != 0 && (!isWinnerCntOK() || !isSuspendCntOK())) return false;
		return true;
	}
	
	private void resetNewRoundFields() {
		TextView mTv;
		mEtNewRoundPoints.setText("");
		for(int i=0;i<mGame.getMAXPlayerCount();i++){
			mSuspendList[i] = 0;
			mWinnerList[i] = 0;
		}
		
		for(int i=0;i<mGameAddRoundPlayer.size();i++){
			mTv = mGameAddRoundPlayer.get(i);
			mTv.setBackgroundColor(mTv.getResources().getColor(R.color.white));
		}
		
		mRNewRoundBockYes.setChecked(false);
		mRNewRoundBockNo.setChecked(true);
		
	}


	private boolean isNewBockRoundSet(){
		switch (mNewRoundBockRadioGroup.getCheckedRadioButtonId()){
			case R.id.game_add_round_bock_radio_yes: return true;	
			case R.id.game_add_round_bock_radio_no: return false;
		}
		return false;
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
	

	
	private boolean isSuspendCntOK(){
		if(mGame.getPlayerCount()-mGame.getActivePlayerCount() == 0) return true;
		if(getSuspendCnt() == (mGame.getPlayerCount()-mGame.getActivePlayerCount())) return true;
		return false;
	}
	
	private boolean isWinnerCntOK(){
		int mWinnerCnt = getWinnerCnt();
		if(mWinnerCnt >= mGame.getActivePlayerCount() || mWinnerCnt == 0) return false;
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
    

    
    private void saveStateData(Bundle outState){
    	if(mGame != null){
	    	ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
	    	try {
	    		ObjectOutput out1 = new ObjectOutputStream(bos1);
				out1.writeObject(mGame);
		    	out1.flush();
		    	out1.close();
		    	outState.putByteArray("GAME_KEY", bos1.toByteArray());
			} 
	    	catch (IOException e) {    	
				e.printStackTrace();
			}	
    	}
    }
    

	private GameClass loadStateData(Bundle savedState){
		GameClass mGame = null;
    	if(savedState != null){
			if(savedState.containsKey("GAME_KEY")){
				ObjectInputStream objectIn;
				try {
					objectIn = new ObjectInputStream(new ByteArrayInputStream(savedState.getByteArray("GAME_KEY")));
					Object obj = objectIn.readObject();
					mGame = (GameClass) obj;

					objectIn.close();
				} 
				catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
    	return mGame;
    }

	private void showExitDialog(){
		Builder back = new AlertDialog.Builder(this);
		back.setTitle(R.string.str_exit_game);
		back.setMessage(R.string.str_exit_game_q);
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
	

	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }
    
    
    @Override
  	public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()) {
    		case R.id.menu_switch_game_list_view:
    			GAME_VIEW_TYPE mRoundListViewMode = mLvRoundAdapter.getRoundListViewMode();
	    		if(mRoundListViewMode == GAME_VIEW_TYPE.ROUND_VIEW_DETAIL){
	    			createTableHeader();
	    			mLvRoundAdapter.changeRoundListViewMode(GAME_VIEW_TYPE.ROUND_VIEW_TABLE);
	    		}
	    		else{
	    			mGameRoundsInfoSwipe.removeAllViews();
	    			mLvRoundAdapter.changeRoundListViewMode(GAME_VIEW_TYPE.ROUND_VIEW_DETAIL);
	    		}
	    		
	    		notifyDataSetChanged();
				mLvRounds.requestFocus();
				
				if(mLvRounds.getCount() >= 1)
					mLvRounds.setSelection(mLvRounds.getCount()-1);
    		break;
    		
    		case R.id.menu_change_game_settings:
    			Intent i = new Intent(this, ChangeGameSettingActivity.class);
    			for(int k=0;k<mGame.getPlayerCount();k++){
    				i.putExtra(DokoData.PLAYERS_KEY[k], mGame.getPlayer(k).getName());
    			}
    			i.putExtra(DokoData.PLAYER_CNT_KEY, mGame.getPlayerCount());
    			i.putExtra(DokoData.BOCKLIMIT_KEY, mGame.getBockRoundLimit());
    			i.putExtra(DokoData.ACTIVE_PLAYER_KEY, mGame.getActivePlayerCount());
    			startActivityForResult(i,DokoData.CHANGE_GAME_SETTINGS_ACTIVITY_CODE);
    		break;
    		
    		case R.id.menu_bock_preview_on_off:
    			if(mBockPreviewOnOff){
    				mBockPreviewOnOff = false;
    				mBottomInfos.setVisibility(View.GONE);
    			}
    			else{
    				if(mGame != null && mGame.getBockRoundLimit() == 0){
    					Toast.makeText(mContext, getResources().getString(R.string.str_bock_preview_not_posible), Toast.LENGTH_LONG).show();
    					return true;
    				}
    				mBockPreviewOnOff = true;
    				mBottomInfos.setVisibility(View.VISIBLE);
    			}
    		break;
    		
    		case R.id.menu_edit_round:
    			Toast.makeText(mContext, getResources().getString(R.string.str_edit_round_info), Toast.LENGTH_LONG).show();
    		break;
    		
    		case R.id.menu_exit_game:
    			showExitDialog();
    		break;
    	}
    	return true;
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

    	switch (requestCode) {
			case DokoData.CHANGE_GAME_SETTINGS_ACTIVITY_CODE:
				handleChangeGameSettingsFinish(requestCode, resultCode, data);
				break;

			case DokoData.EDIT_ROUND_ACTIVITY_CODE:
				handleEditRoundFinish(requestCode, resultCode, data);
			default:
				break;
		}
    }
    
    private void handleEditRoundFinish(int requestCode, int resultCode, Intent data) {
    	Bundle extras = null;
    	int mNewRoundPoints;
        int mTmpWinnerList[] = new int[DokoData.MAX_PLAYER];
        int mTmpSuspendList[] = new int[DokoData.MAX_PLAYER];
		PLAYER_ROUND_RESULT_STATE mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.WIN_STATE;
		
    	if(data != null) extras = data.getExtras();
    	if(extras != null && extras.getBoolean(DokoData.CHANGE_ROUND_KEY,false)){
    		mNewRoundPoints = extras.getInt(DokoData.ROUND_POINTS_KEY,0);
    		//Log.d("GA before",mGame.toString() + " new points:"+mNewRoundPoints);
    		int mTmpState;
        	for(int k=0; k<mPlayerCnt; k++){
        		mTmpState = extras.getInt(DokoData.PLAYERS_KEY[k]+"_STATE",-1);
        		if (mTmpState == -1 || PLAYER_ROUND_RESULT_STATE.valueOf(mTmpState) == null) {
        			Toast.makeText(mContext, getResources().getString(R.string.str_edit_round_error), Toast.LENGTH_LONG).show();
        			return;
        		} else {
        			mTmpWinnerList[k] = 0; // lose default
        			mTmpSuspendList[k] = 0; // not suspending  default
        			mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.valueOf(mTmpState);
        			//Log.d("GA EDIT:","player:"+mGame.getPlayer(k).getName()+" with state: "+mPlayerRoundState+", plcnt:"+mPlayerCnt);
        			switch (mPlayerRoundState) {
						case WIN_STATE: mTmpWinnerList[k] = 1;	break;
						case SUSPEND_STATE: mTmpSuspendList[k] = 1;	break;
						default:
							break;
					}
        		}
        	}

        	mGame.editLastRound(mNewRoundPoints, false, mTmpWinnerList, mTmpSuspendList);
        	reloadSwipeViews(); 
        	//Log.d("GA after",mGame.toString());
        	DokoXMLClass.saveGameStateToXML(mContext, mGame);
        	
        	Toast.makeText(mContext, getResources().getString(R.string.str_edit_round_finish), Toast.LENGTH_LONG).show();
        }
    }
    
    private void handleChangeGameSettingsFinish(int requestCode, int resultCode, Intent data) {
    	Bundle extras = null;
    	int mActivePlayers,mBockLimit,mPlayerCnt,mOldPlayerCnt;
    	String mName = "";
    	    	   	
    	if(data != null) extras = data.getExtras();
    	if(extras != null && extras.getBoolean(DokoData.CHANGE_GAME_SETTINGS_KEY,false)){
    		mPlayerCnt = extras.getInt(DokoData.PLAYER_CNT_KEY,0);
        	mActivePlayers =  extras.getInt(DokoData.ACTIVE_PLAYER_KEY,0);
        	mBockLimit = extras.getInt(DokoData.BOCKLIMIT_KEY,0);
        	
        	if(mPlayerCnt < DokoData.MIN_PLAYER || mPlayerCnt > DokoData.MAX_PLAYER 
        			|| mActivePlayers > mPlayerCnt || mActivePlayers < DokoData.MIN_PLAYER
        			|| (mPlayerCnt == 0 || mActivePlayers == 0))
        		return;
        	
        	//set new game settings
        	mOldPlayerCnt = mGame.getPlayerCount(); 
        	mGame.setPlayerCount(mPlayerCnt);
        	mGame.setActivePlayerCount(mActivePlayers);
        	mGame.setBockRoundLimit(mBockLimit);
        	
        	for(int k=mOldPlayerCnt;k<mPlayerCnt;k++){
        		mName = extras.getString(DokoData.PLAYERS_KEY[k],"");
        		if(mName == null || mName.length() == 0) return;
        		mGame.getPlayer(k).setName(mName);
        	}

        	reloadSwipeViews(); 
        	DokoXMLClass.saveGameStateToXML(mContext, mGame);
        	
        	Toast.makeText(mContext, getResources().getString(R.string.str_change_game_settings_finish), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onBackPressed(){
    	showExitDialog();
    	return;
    }
    
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
		saveStateData(outState);
	    super.onSaveInstanceState(outState);
    }
    
    
    
    
}

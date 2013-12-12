package nldoko.game.game;

import java.util.ArrayList;

import nldoko.android.Functions;
import nldoko.game.R;
import nldoko.game.classes.GameClass;
import nldoko.game.classes.RoundClass;
import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.GAME_RESULT_TYPE;
import nldoko.game.data.DokoData.GAME_VIEW_TYPE;
import nldoko.game.data.DokoData.PLAYER_ROUND_RESULT_STATE;
import nldoko.game.game.NewGameActivity.settingInfoClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameMainListAdapter extends ArrayAdapter<RoundClass> {
	 

	private Context mContext;
    private ArrayList<RoundClass> mRounds;
    private GameClass mGame;
    private LayoutInflater mInflater;
    private RoundNumberLongClickListerner mRoundNrLongListerner;
    private GAME_VIEW_TYPE mRoundListViewMode = GAME_VIEW_TYPE.ROUND_VIEW_DETAIL;
   
    public GameMainListAdapter(Context context,ArrayList<RoundClass> roundArrayList, GameClass game) {
        super(context,0, roundArrayList);
        this.mContext = context;
        this.mRounds = roundArrayList;
        this.mGame = game;
        
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoundNrLongListerner = new RoundNumberLongClickListerner();
    }
 
    public void changeRoundListViewMode(GAME_VIEW_TYPE roundListViewMode){
    	this.mRoundListViewMode = roundListViewMode;
    }
    
    public GAME_VIEW_TYPE getRoundListViewMode(){
    	return this.mRoundListViewMode;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	switch(mRoundListViewMode){
    		case ROUND_VIEW_DETAIL: return getRoundViewDetail(position,convertView,parent);
    		case ROUND_VIEW_TABLE: return getRoundViewTable(position,convertView,parent);
    		default: return getRoundViewDetail(position,convertView,parent);
    	}
  
    }
    
    private View getRoundViewTable(int position, View convertView, ViewGroup parent) {
		View v = convertView;
        LinearLayout mPlayerRow = null;
        TextView mRoundNumber,mRoundPoints,mRoundPointsSolo,mPlayerPoints,mBockCountInfo;
        String mStr = "";
        
        int mPlayerPerRow = 2, mRoundType, mTmp;
        float mPoints,mPointsDiff;
        
        final RoundClass mRound = mRounds.get(position);

        if(mRound == null) return v;
    	if(v == null){
    		if(mGame.getPlayerCount() == 4) v = mInflater.inflate(R.layout.fragment_game_round_view_table_4_player, parent,false);
    		else if(mGame.getPlayerCount() == 5) v = mInflater.inflate(R.layout.fragment_game_round_view_table_5_player, parent,false);
    		else if(mGame.getPlayerCount() == 6) v = mInflater.inflate(R.layout.fragment_game_round_view_table_6_player, parent,false);
    		else if(mGame.getPlayerCount() == 7) v = mInflater.inflate(R.layout.fragment_game_round_view_table_7_player, parent,false);
    		else if(mGame.getPlayerCount() == 8) v = mInflater.inflate(R.layout.fragment_game_round_view_table_8_player, parent,false);
    	}
    	else{
    		if(mGame.getPlayerCount() == 4 && v.getId() != R.id.fragment_game_round_view_table_4_player)
    			v = mInflater.inflate(R.layout.fragment_game_round_view_table_4_player, parent,false);
    		else if(mGame.getPlayerCount() == 5 && v.getId() != R.id.fragment_game_round_view_table_5_player)
    			v = mInflater.inflate(R.layout.fragment_game_round_view_table_5_player, parent,false);
    		else if(mGame.getPlayerCount() == 6 && v.getId() != R.id.fragment_game_round_view_table_6_player)
    			v = mInflater.inflate(R.layout.fragment_game_round_view_table_6_player, parent,false);
    		else if(mGame.getPlayerCount() == 7 && v.getId() != R.id.fragment_game_round_view_table_7_player)
    			v = mInflater.inflate(R.layout.fragment_game_round_view_table_7_player, parent,false);
    		else if(mGame.getPlayerCount() == 8 && v.getId() != R.id.fragment_game_round_view_table_8_player)
    			v = mInflater.inflate(R.layout.fragment_game_round_view_table_8_player, parent,false);
    	}
    	
		if(v ==  null || DokoData.mTvTablePlayerName.length < mGame.getPlayerCount()) return v;
		
		if(mRound.getID() % 2 == 1) v.setBackgroundColor(v.getResources().getColor(R.color.gray));
		else v.setBackgroundColor(v.getResources().getColor(R.color.white));
	
		mRoundNumber = (TextView)v.findViewById(R.id.fragment_game_round_view_table_round_nr);
		mRoundPoints = (TextView)v.findViewById(R.id.fragment_game_round_view_table_round_points);
		mRoundPointsSolo = (TextView)v.findViewById(R.id.fragment_game_round_view_table_round_points_solo);
		mBockCountInfo = (TextView)v.findViewById(R.id.fragment_game_round_view_table_round_bock_info);
		
		mRoundNumber.setText(String.valueOf(mRound.getID()+1));
		if(mGame.getRoundList().size() == mRound.getID()+1)
			mRoundNumber.setBackgroundColor(v.getResources().getColor(R.color.orange_dark));
		else if(mRound.getID() % 2 == 1)
			mRoundNumber.setBackgroundColor(v.getResources().getColor(R.color.gray));
		else 
			mRoundNumber.setBackgroundColor(v.getResources().getColor(R.color.white));
		
		if(mRound.getPoints() == 0) mRoundPoints.setText("-");
		else mRoundPoints.setText(String.valueOf(mRound.getPoints()));
		
		if(mRound.getRoundType() == GAME_RESULT_TYPE.LOSE_SOLO || mRound.getRoundType() == GAME_RESULT_TYPE.WIN_SOLO){
			mRoundPointsSolo.setVisibility(View.VISIBLE);
			mRoundPointsSolo.setText(String.valueOf(mRound.getPoints()*(mGame.getActivePlayerCount()-1)));
		}
		else{
			mRoundPointsSolo.setVisibility(View.GONE);
		}
		
		if(mRound.getBockCount() > 0){
			mBockCountInfo.setVisibility(View.VISIBLE);
			mBockCountInfo.setText("B: "+Functions.getBockCountAsRom(mRound.getBockCount()));
		}
		else mBockCountInfo.setVisibility(View.GONE);
		
		for(int i=0;i<mGame.getPlayerCount();i++){
			mPlayerPoints = (TextView)v.findViewById(DokoData.mTvTablePlayerName[i]);
    		mPoints = mGame.getPlayer(i).getPointHistory(mRound.getID());
    		mPlayerPoints.setText(Functions.getFloatAsString(mPoints));
    		if(mPoints < 0) mPlayerPoints.setTextColor(parent.getResources().getColor(R.color.red));
    		else mPlayerPoints.setTextColor(parent.getResources().getColor(R.color.green_dark));
    		
		}
		v.setOnLongClickListener(mRoundNrLongListerner);
    	return v;
    }

	private View getRoundViewDetail(int position, View convertView, ViewGroup parent) {
		View v = convertView;
        LinearLayout mPlayerRow = null;
        TextView mRoundNumber,mStrHeader,mBockCnt,mRoundPoints,mRoundPointsSolo;
        TextView mPlayerLeftName,mPlayerRightName,mPlayerLeftPoints,mPlayerRightPoints;
        ImageView mPlayerLeftRoundState,mPlayerRightRoundState;
        String mStr = "";
        
        int mPlayerPerRow = 2, mRoundType, mTmp;
        float mPoints,mPointsDiff;
        
        final RoundClass mRound = mRounds.get(position);

        if(mRound == null) return v;
    	if(v == null){
    		if(mGame.getPlayerCount() == 4) v = mInflater.inflate(R.layout.fragment_game_round_view_detail_4_player, parent,false);
    		else v = mInflater.inflate(R.layout.fragment_game_round_view_detail, parent,false);
    	}
    	else{
    		 if(mGame.getPlayerCount() == 4 && v.getId() != R.id.fragment_game_round_view_detail_4_player)  
    			 v = mInflater.inflate(R.layout.fragment_game_round_view_detail_4_player, parent,false);
    		 else if(mGame.getPlayerCount() != 4 && v.getId() != R.id.fragment_game_round_view_detail)
    			 v = mInflater.inflate(R.layout.fragment_game_round_view_detail, parent,false);
    	}
    	   	
    	
		//Set Header
		mRoundNumber = (TextView)v.findViewById(R.id.fragment_game_round_number);
		mStrHeader = (TextView)v.findViewById(R.id.fragment_game_round_str_prim);
		mBockCnt = (TextView)v.findViewById(R.id.fragment_game_round_bock_cnt);
		mRoundPoints = (TextView)v.findViewById(R.id.fragment_game_round_points);
		mRoundPointsSolo = (TextView)v.findViewById(R.id.fragment_game_round_points_solo);
		
		
		if(mRound.getPoints() > 0)mStrHeader.setText(mRound.getRoundTypeAsAtring());
		else mStrHeader.setText(null);
		
		mRoundNumber.setText("#"+String.valueOf(mRound.getID()+1));
		
		if(mGame.getRoundList().size() == mRound.getID()+1)
			mRoundNumber.setBackgroundColor(v.getResources().getColor(R.color.orange_dark));
		else
			mRoundNumber.setBackgroundColor(v.getResources().getColor(R.color.gray));
		
		if(mRound.getPoints() == 0){
			mRoundPoints.setText("-");
			mRoundNumber.setText("#"+String.valueOf(mRound.getID()+1));
		}
		else{
			mRoundPoints.setText(String.valueOf(mRound.getPoints()));
			mRoundNumber.setText("#"+String.valueOf(mRound.getID()+1));
		}
		
		if(mRound.getRoundType() == GAME_RESULT_TYPE.LOSE_SOLO || mRound.getRoundType() == GAME_RESULT_TYPE.WIN_SOLO)
			mRoundPointsSolo.setText(String.valueOf((mRound.getPoints()*(mGame.getActivePlayerCount()-1))));
		else mRoundPointsSolo.setText("");
		
		if(mRound.getBockCount()>0){
			mStr = parent.getResources().getString(R.string.str_bock)+" ";
			mStr += Functions.getBockCountAsRom(mRound.getBockCount());
			mBockCnt.setText(mStr);
		}
		else mBockCnt.setText(null);
		//Set player (name,points,state,colors) 
		mTmp = (int) ((double)mGame.getPlayerCount()/2 + 0.5d);
		
    	for(int i=0; i< (mGame.getMAXPlayerCount()/mPlayerPerRow)  && i<mTmp; i++){
    		switch(i){
    			case 0: mPlayerRow = (LinearLayout)v.findViewById(R.id.fragment_game_round_2_player_row0); break;
    			case 1: mPlayerRow = (LinearLayout)v.findViewById(R.id.fragment_game_round_2_player_row1); break;
    			case 2: mPlayerRow = (LinearLayout)v.findViewById(R.id.fragment_game_round_2_player_row2); break;
    			case 3: mPlayerRow = (LinearLayout)v.findViewById(R.id.fragment_game_round_2_player_row3); break;
    		}

    		if(mPlayerRow == null) return v;
    		
    		//Left
    		mPlayerLeftName = (TextView)mPlayerRow.findViewById(R.id.fragment_game_round_2_player_row_player_left_name);
    		mPlayerLeftPoints = (TextView)mPlayerRow.findViewById(R.id.fragment_game_round_2_player_row_player_left_points);
    		mPlayerLeftRoundState = (ImageView)mPlayerRow.findViewById(R.id.fragment_game_round_2_player_row_player_left_state);
    		
    		mPlayerLeftName.setText(mGame.getPlayer(i*mPlayerPerRow).getName());
    		mPoints = mGame.getPlayer(i*mPlayerPerRow).getPointHistory(mRound.getID());
    		mPlayerLeftPoints.setText(Functions.getFloatAsString(mPoints));
    		if(mPoints < 0) mPlayerLeftPoints.setTextColor(parent.getResources().getColor(R.color.red));
    		else mPlayerLeftPoints.setTextColor(parent.getResources().getColor(R.color.green));
    		
    		if(mRound.getID()>0) mPointsDiff = mPoints - mGame.getPlayer(i*mPlayerPerRow).getPointHistory(mRound.getID()-1);
    		else mPointsDiff = mPoints;
    		
    		if(mPointsDiff == 0 || mRound.getPoints() == 0) mPlayerLeftRoundState.setImageDrawable(parent.getResources().getDrawable(R.drawable.navigation_cancel));
    		else if(mPointsDiff > 0)mPlayerLeftRoundState.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_arrow_up_green));
    		else mPlayerLeftRoundState.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_arrow_up_red));
    		
    		//Right
    		mPlayerRightName = (TextView)mPlayerRow.findViewById(R.id.fragment_game_round_2_player_row_player_right_name);
    		mPlayerRightPoints = (TextView)mPlayerRow.findViewById(R.id.fragment_game_round_2_player_row_player_right_points);
    		mPlayerRightRoundState = (ImageView)mPlayerRow.findViewById(R.id.fragment_game_round_2_player_row_player_right_state);
    	
			if(mGame.getPlayerCount() == 5 && i == 2){
				mPlayerRightName.setText("");
				mPlayerRightPoints.setText("");
				mPlayerRightRoundState.setImageDrawable(null);
			} else if(mGame.getPlayerCount() == 7 && i == 3){
				mPlayerRightName.setText("");
				mPlayerRightPoints.setText("");
				mPlayerRightRoundState.setImageDrawable(null);
			}
			else{
	    		mPlayerRightName.setText(mGame.getPlayer(i*mPlayerPerRow+1).getName());
	    		mPoints = mGame.getPlayer(i*mPlayerPerRow+1).getPointHistory(mRound.getID());
	    		mPlayerRightPoints.setText(Functions.getFloatAsString(mPoints));
	    		if(mPoints < 0) mPlayerRightPoints.setTextColor(parent.getResources().getColor(R.color.red));
	    		else mPlayerRightPoints.setTextColor(parent.getResources().getColor(R.color.green));
	    		
	    		if(mRound.getID()>0) mPointsDiff = mPoints - mGame.getPlayer(i*mPlayerPerRow+1).getPointHistory(mRound.getID()-1);
	    		else mPointsDiff = mPoints;
	    		
	    		if(mPointsDiff == 0 || mRound.getPoints() == 0) mPlayerRightRoundState.setImageDrawable(parent.getResources().getDrawable(R.drawable.navigation_cancel));
	    		else if(mPointsDiff > 0)mPlayerRightRoundState.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_arrow_up_green));
	    		else mPlayerRightRoundState.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_arrow_up_red));
			}
    	
    	}
    	 
    	v.setOnLongClickListener(mRoundNrLongListerner);
        return v;
	}
	
	private void showEditRoundDialog(final int roundNumber, final View v){
		String mStr = "";
		Builder back = new AlertDialog.Builder(mContext);

		if(mGame.getRoundList().get(roundNumber-1).getPoints() == 0 || mGame.getRoundList().size() > roundNumber){
			Toast.makeText(mContext, R.string.str_edit_round_not_possible, Toast.LENGTH_SHORT).show();
			return;
		}
		
        Animation inOutInfinit = AnimationUtils.loadAnimation(mContext, R.anim.infinit_fade_in_out);
        v.startAnimation(inOutInfinit);
        
		mStr = mContext.getResources().getString(R.string.str_game_round)+" "+roundNumber+" "+mContext.getResources().getString(R.string.str_edit)+"?";
		
		back.setTitle(R.string.str_edit_round);
		back.setMessage(mStr);
		back.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.d("ef",mGame.toString());
				Log.d("roundnr edit yes","round nr:"+roundNumber);
				Intent i = new Intent(mContext, EditRoundActivity.class);
				PLAYER_ROUND_RESULT_STATE mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.WIN_STATE;
				for(int k=0;k<mGame.getPlayerCount();k++){
					mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.WIN_STATE;
					i.putExtra(DokoData.PLAYERS_KEY[k], mGame.getPlayer(k).getName());
					Log.d("roundnr edit yes","p:"+mGame.getPlayer(k).getPointHistoryPerRound(roundNumber - 1)+" lenght:"+mGame.getPlayer(k).getPointHistoryPerRoundLength());
					if(mGame.getPlayer(k).getPointHistoryPerRound(roundNumber - 1) < 0) {
						mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.LOSE_STATE;
						Log.d("roundnr edit yes","lose"+mGame.getPlayer(k).getPointHistoryPerRound(roundNumber - 1));
					} else if(mGame.getPlayer(k).getPointHistoryPerRound(roundNumber - 1) == 0) {
						mPlayerRoundState = PLAYER_ROUND_RESULT_STATE.SUSPEND_STATE;
					}
					
					i.putExtra(DokoData.PLAYERS_KEY[k]+"_STATE", mPlayerRoundState);
				}
				i.putExtra(DokoData.BOCKROUND_KEY, mGame.getRoundList().get(roundNumber-1).getBockCount());
				i.putExtra(DokoData.PLAYER_CNT_KEY, mGame.getPlayerCount());
				i.putExtra(DokoData.ACTIVE_PLAYER_KEY, mGame.getActivePlayerCount());
				i.putExtra(DokoData.ROUND_POINTS_KEY, mGame.getRoundList().get(roundNumber-1).getPointsWithoutBock());
				((Activity) mContext).startActivityForResult(i,DokoData.EDIT_ROUND_ACTIVITY_CODE);
				v.clearAnimation();
			}
		});

		back.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				v.clearAnimation();
			}
		});
		back.show();
	}
    
	
	private class RoundNumberLongClickListerner implements OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			int mRoundNr = 0;
			String mTmp = "";
			TextView mTvRoundNr = null;
	    	switch(mRoundListViewMode){
	    		case ROUND_VIEW_DETAIL: mTvRoundNr = (TextView)v.findViewById(R.id.fragment_game_round_number); break;
	    		case ROUND_VIEW_TABLE: mTvRoundNr = (TextView)v.findViewById(R.id.fragment_game_round_view_table_round_nr); break;
	    		default: return true;
	    	}
			
	    	if(mTvRoundNr == null) return false; //Exit if view failure
	    	
	    	mTmp = mTvRoundNr.getText().toString();
	    	mTmp = mTmp.replace("#", ""); //detail view round number has # @ pos0
	    	mRoundNr = Integer.valueOf(mTmp);
	    		    
	        
	    	showEditRoundDialog(mRoundNr,v);
			return false;
		}
		
	}
 
}
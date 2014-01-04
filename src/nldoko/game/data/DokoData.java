package nldoko.game.data;

import java.util.ArrayList;

import nldoko.game.R;
import nldoko.game.data.DokoData.PLAYER_ROUND_RESULT_STATE;

public class DokoData {
	
	public static final int MAX_PLAYER = 8;
	public static final int MAX_ACTIVE_PLAYER = 5;
	public static final int MIN_PLAYER = 4;
	
	public static ArrayList<String> PLAYER_NAMES = new ArrayList<String>();
	public static final String PLAYER_NAMES_XML = "player_names.xml";
	public static final String SAVED_GAME_FILE_POSTFIX =  "_dokoSavedGame.xml";
	
	
	public static enum GAME_RESULT_TYPE {
		NORMAL, 
		WIN_SOLO, 
		LOSE_SOLO, 
		FIVEPLAYER_3WIN, 
		FIVEPLAYER_2WIN
	}
	
	public static enum PLAYER_ROUND_RESULT_STATE {
		LOSE_STATE, 
		WIN_STATE, 
		SUSPEND_STATE;

		public static PLAYER_ROUND_RESULT_STATE valueOf(int state) {
			switch (state) {
				case 0:	return LOSE_STATE;
				case 1:	return WIN_STATE;
				case 2:	return SUSPEND_STATE;
				default:
					break;
			}
			return null;
		}
	}
	
	public static enum GAME_CNT_VARIANT {
		CNT_VARIANT_NORMAL,
		CNT_VARIANT_LOSE,
		CNT_VARIANT_WIN
	}

	public static enum GAME_VIEW_TYPE {
		ROUND_VIEW_DETAIL,
		ROUND_VIEW_TABLE
	}
	
	public static final Integer[] mPointSuggestions = {1,2,3,4,5,6,7,8,9,10,11,12,14,16,18,20};	
	
	public static final Integer[] mTvTablePlayerName = {
			R.id.fragment_game_round_view_table_player_1,
			R.id.fragment_game_round_view_table_player_2,
			R.id.fragment_game_round_view_table_player_3,
			R.id.fragment_game_round_view_table_player_4,
			R.id.fragment_game_round_view_table_player_5,
			R.id.fragment_game_round_view_table_player_6,
			R.id.fragment_game_round_view_table_player_7,
			R.id.fragment_game_round_view_table_player_8,
	};	
	
	// name - description
	public static final Integer[][] GAME_CNT_VARAINT_ARRAY  = {
		{R.string.str_info_cnt_cnt_variant_std_name, R.string.str_info_cnt_cnt_variant_standard},
		{R.string.str_info_cnt_cnt_variant_win_name, R.string.str_info_cnt_cnt_variant_win},
		{R.string.str_info_cnt_cnt_variant_lose_name, R.string.str_info_cnt_cnt_variant_lose} };
	
	
	public static final String CHANGE_GAME_SETTINGS_KEY = "CHANGE_GAME_SETTINGS";
	public static final String CHANGE_ROUND_KEY			= "CHANGE_ROUND";
	public static final String PLAYER_CNT_KEY 			= "PLAYER_CNT";
	public static final String BOCKLIMIT_KEY 			= "BOCKLIMIT";
	public static final String BOCKROUND_KEY 			= "BOCKROUND";
	public static final String ACTIVE_PLAYER_KEY 		= "ACTIVE_PLAYER";
	public static final String GAME_CNT_VARIANT_KEY 	= "GAME_CNT_VARIANT";
	public static final String ROUND_POINTS_KEY 		= "ROUND_POINTS";
	
	public static final String[] PLAYERS_KEY  = {"PLAYER_1","PLAYER_2","PLAYER_3","PLAYER_4",
												 "PLAYER_5","PLAYER_6","PLAYER_7","PLAYER_8"};
	
	public static final int CHANGE_GAME_SETTINGS_ACTIVITY_CODE = 1122;
	public static final int EDIT_ROUND_ACTIVITY_CODE = 1133;
	


}

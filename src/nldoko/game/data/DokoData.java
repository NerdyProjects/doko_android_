package nldoko.game.data;

import java.util.ArrayList;

import nldoko.game.R;

public class DokoData {
	
	public static final int MAX_PLAYER = 6;
	public static final int MAX_ACTIVE_PLAYER = 6;
	public static final int MIN_PLAYER = 4;
	
	public static ArrayList<String> PLAYER_NAMES = new ArrayList<String>();
	public static final String PLAYER_NAMES_XML = "player_names.xml";
	
	public static final int NORMAL = 0;
	public static final int WIN_SOLO = 1;
	public static final int LOSE_SOLO = 2;
	public static final int FIVEPLAYER_3WIN = 3;
	public static final int FIVEPLAYER_2WIN = 4;
	
	public static final int LOSE_STATE = 0;
	public static final int WIN_STATE = 1;
	public static final int SUSPEND_STATE = 2;

	
	public static final Integer[] mPointSuggestions = {1,2,3,4,5,6,7,8,9,10,11,12,14,16,18,20};	
	
	public static final Integer[] mTvTablePlayerName = {
			R.id.fragment_game_round_view_table_player_1,
			R.id.fragment_game_round_view_table_player_2,
			R.id.fragment_game_round_view_table_player_3,
			R.id.fragment_game_round_view_table_player_4,
			R.id.fragment_game_round_view_table_player_5,
			R.id.fragment_game_round_view_table_player_6,	
	};	
	
	public static final String str_round_type_win_solo = "Solo gewonnen";
	public static final String str_round_type_lose_solo = "Solo verloren";
	public static final String str_round_type_solo = "Solo";
	public static final String str_round_type_2vs2 = "2vs2";
	public static final String str_round_type_3vs2 = "3vs2";
	
	public static final String[] GAME_CNT_VARAINT_ARRAY  = {"Standard","Verlierer","Gewinner"};
	
	public static final int ROUND_VIEW_DETAIL = 1;
	public static final int ROUND_VIEW_TABLE  = 2;
	
	public static final String CHANGE_GAME_SETTINGS_KEY = "CHANGE_GAME_SETTINGS";
	public static final String PLAYER_CNT_KEY 			= "PLAYER_CNT";
	public static final String BOCKLIMIT_KEY 			= "BOCKLIMIT";
	public static final String BOCKROUND_KEY 			= "BOCKROUND";
	public static final String ACTIVE_PLAYER_KEY 		= "ACTIVE_PLAYER";
	public static final String GAME_CNT_VARIANT_KEY 	= "GAME_CNT_VARIANT";
	public static final String ROUND_POINTS_KEY 		= "ROUND_POINTS";
	
	public static final String[] PLAYERS_KEY  = {"PLAYER_1","PLAYER_2","PLAYER_3","PLAYER_4","PLAYER_5","PLAYER_6"};
	
	public static final int CNT_VARIANT_NORMAL	= 0;
	public static final int CNT_VARIANT_LOSE 	= 1;		
	public static final int CNT_VARIANT_WIN 	= 2;	

}

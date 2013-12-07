package nldoko.game.classes;

import java.io.Serializable;

import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.GAME_RESULT_TYPE;


public class RoundClass implements Serializable  {


	private static final long serialVersionUID = -7650567591631089724L;
	private int mID;
	private int mPoints;
	private int mBockCount;
	private GAME_RESULT_TYPE mRoundType;
	String mResultText;
	
	
	public RoundClass(int id,int points,int bockCount){
		this.mID = id;
		this.mBockCount 	= bockCount;
		this.mPoints	= points;
	}

	
	public int getID(){
		return this.mID;
	}
	
	public void setID(int id){
		this.mID =  id;
	}
	
	public void setResultText(String rT){
		this.mResultText = rT;
	}
	
	public String getResultText(){
		return this.mResultText;
	}
	
	public int getPoints(){
		return this.mPoints*(this.mBockCount!=0 ? this.mBockCount*2 : 1 );
	}
	
	public int getPointsWithoutBock(){
		return this.mPoints;
	}
	
	public void setPoints(int p){
		this.mPoints = p;
	}
	
	
	public int getBockCount(){
		return mBockCount;
	}
	
	public void setBockCount(int bc){
		this.mBockCount = bc;
	}
	
	public GAME_RESULT_TYPE getRoundType(){
		return mRoundType;
	}
	
	public void setRoundType(int winner_count, int active_player){
		if(winner_count == 1){
			//Win solo
			this.mRoundType = GAME_RESULT_TYPE.WIN_SOLO;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-1)*this.mPoints) ));
		}
		else if(winner_count == 3 && active_player == 4){
			//Lose solo
			this.mRoundType = GAME_RESULT_TYPE.LOSE_SOLO;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-1)*this.mPoints) ));
		}
		else if(winner_count == 4 && active_player == 5){
			//Lose solo
			this.mRoundType = GAME_RESULT_TYPE.LOSE_SOLO;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-1)*this.mPoints) ));
		}
		else if(winner_count == 3 && active_player == 5){
			//3 win vs. 2 lose
			this.mRoundType = GAME_RESULT_TYPE.FIVEPLAYER_3WIN;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-2)*this.mPoints/2) ));
		}
		else if(winner_count == 2 && active_player == 5){
			//2 win vs. 3 lose
			this.mRoundType = GAME_RESULT_TYPE.FIVEPLAYER_2WIN;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-2)*this.mPoints/2) ));
		}
		else{
			this.mRoundType = GAME_RESULT_TYPE.NORMAL;
			this.setResultText(String.valueOf(this.mPoints));
		}
	}
		
	public String getRoundTypeAsAtring(){
		switch(mRoundType){
			case LOSE_SOLO:
			case WIN_SOLO:
				return DokoData.str_round_type_solo;
			case FIVEPLAYER_2WIN:
			case FIVEPLAYER_3WIN:
				return DokoData.str_round_type_3vs2;
			default:
				return  DokoData.str_round_type_2vs2;
		}
	}
}

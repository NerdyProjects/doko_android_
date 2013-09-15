package nldoko.game.classes;

import java.io.Serializable;

import nldoko.game.R;
import nldoko.game.data.DokoData;


public class RoundClass implements Serializable  {


	private static final long serialVersionUID = -7650567591631089724L;
	private int mID;
	private int mPoints;
	private int mBockCount;
	private int mRoundType;
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
	
	public int getRoundType(){
		return mRoundType;
	}
	
	public void setRoundType(int winner_count, int active_player){
		if(winner_count == 1){
			//Win solo
			this.mRoundType = DokoData.WIN_SOLO;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-1)*this.mPoints) ));
		}
		else if(winner_count == 3 && active_player == 4){
			//Lose solo
			this.mRoundType = DokoData.LOSE_SOLO;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-1)*this.mPoints) ));
		}
		else if(winner_count == 4 && active_player == 5){
			//Lose solo
			this.mRoundType = DokoData.LOSE_SOLO;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-1)*this.mPoints) ));
		}
		else if(winner_count == 3 && active_player == 5){
			//3 win vs. 2 lose
			this.mRoundType = DokoData.FIVEPLAYER_3WIN;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-2)*this.mPoints/2) ));
		}
		else if(winner_count == 2 && active_player == 5){
			//2 win vs. 3 lose
			this.mRoundType = DokoData.FIVEPLAYER_2WIN;
			this.setResultText(String.valueOf(this.mPoints+"/"+((active_player-2)*this.mPoints/2) ));
		}
		else{
			this.mRoundType = DokoData.NORMAL;
			this.setResultText(String.valueOf(this.mPoints));
		}
	}
		
	public String getRoundTypeAsAtring(){
		switch(mRoundType){
			case DokoData.LOSE_SOLO:
			case DokoData.WIN_SOLO:
				return DokoData.str_round_type_solo;
			case DokoData.FIVEPLAYER_2WIN:
			case DokoData.FIVEPLAYER_3WIN:
				return DokoData.str_round_type_3vs2;
			default:
				return  DokoData.str_round_type_2vs2;
		}
	}
}

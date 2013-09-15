package nldoko.game.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerClass implements Serializable  {

	private static final long serialVersionUID = 3904343739194363784L;
	private String mName;
	private int mID;
	private float mCurrentPoints;
	private boolean mIsActive = false;
	private ArrayList<Float> mPointHistroy = new ArrayList<Float>();
	
	
	public PlayerClass(){
		this.mCurrentPoints	= 0;
		this.mName = "";
	}
	
	
	
	public PlayerClass(int id){
		this.mID 	= id;
		this.mCurrentPoints	= 0;
		this.mName = "";
	}
	
	public PlayerClass(int id, String name, float points){
		this.mID 	= id;
		this.mCurrentPoints	= points;
		this.mName = name;
	}
	
	

	public boolean isActive(){
		return this.mIsActive;
	}
	
	public void activate(){
		this.mIsActive = true;
	}
	
	public void deactivate(){
		this.mIsActive = false;
	}
	
	public int getID(){
		return this.mID;
	}
	
	public String getName(){
		return this.mName;
	}
		
	public void setName(String n){
		this.mName = n;
	}
	
	public void updatePoints(Float p){
		Float i = mCurrentPoints +p;
		this.mCurrentPoints = i;
		this.mPointHistroy.add(i);
	}
	
	public float getPoints(){
		return this.mCurrentPoints;
	}
	
	public float getPointHistory(int pos){
		return this.mPointHistroy.get(pos);
	}
	
	public int getPointHistoryLength(){
		return this.mPointHistroy.size();
	}
	
}

package nldoko.game.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nldoko.game.R;
import nldoko.game.classes.GameClass;
import nldoko.game.classes.PlayerClass;
import nldoko.game.classes.RoundClass;
import nldoko.game.data.DokoData;
import nldoko.game.data.DokoData.GAME_CNT_VARIANT;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;


public class DokoXMLClass {
	
	private static final String TAG = "DokoXMLClass";
	
	
	public static boolean saveGameStateToXML(Context c, GameClass game){
		if(game != null && DokoXMLClass.isAppDirOK(c)){
			String oldFilename = game.currentFilename();
			String newFilename = game.generateNewFilename();
			XmlSerializer serializer = Xml.newSerializer();
		    StringWriter writer = new StringWriter();

    	    try {
    	        serializer.setOutput(writer);
    	        serializer.startDocument("UTF-8", false);
    	        serializer.text("\n");
    	        serializer.startTag("", "Game");
    	        
    	        serializer.text("\n\t");
    	        serializer.startTag("", "PlayerCnt");
    	        serializer.text(Integer.toString(game.getPlayerCount()));
    	        serializer.endTag("", "PlayerCnt");
    	        
    	        
    	        serializer.text("\n\t");
    	        serializer.startTag("", "ActivePlayers");
    	        serializer.text(Integer.toString(game.getActivePlayerCount()));
    	        serializer.endTag("", "ActivePlayers");
    	        
    	        serializer.text("\n\t");
    	        serializer.startTag("", "BockRoundLimit");
    	        serializer.text(Integer.toString(game.getBockRoundLimit()));
    	        serializer.endTag("", "BockRoundLimit");
    	        
    	        serializer.text("\n\t");
    	        serializer.startTag("", "GameCntVariant");
    	        serializer.text(game.getGameCntVariant().toString());
    	        serializer.endTag("", "GameCntVariant");
    	        
  
    	        serializer.text("\n\t");
    	        serializer.startTag("", "Players");
    	        for(int i=0;i<game.getMAXPlayerCount();i++){
    	        	
        	        serializer.text("\n\t\t");
        	        serializer.startTag("", "Player");

        	        serializer.text("\n\t\t\t");
    	            serializer.startTag("", "name");
    	            serializer.text(game.getPlayer(i).getName());
    	            serializer.endTag("", "name");
    	            
    	            serializer.text("\n\t\t\t");
    	            serializer.startTag("", "points");
    	            serializer.text(Float.toString(game.getPlayer(i).getPoints()));
    	            serializer.endTag("", "points");
    	            
    	            serializer.text("\n\t\t");
    	            serializer.endTag("", "Player");
    	        }
    	        serializer.text("\n\t");
	            serializer.endTag("", "Players");
    	        
	            
    	        serializer.text("\n\t");
    	        serializer.startTag("", "PreRounds");
	            for (int t=0;t<game.getPreRoundList().size();t++){
	            	serializer.text("\n\t\t");
		            serializer.startTag("", "PreRound");
		            
		            serializer.text("\n\t\t\t");
		            serializer.startTag("", "bockCount");
		            serializer.text(Integer.toString(game.getPreRoundList().get(t).getBockCount()));
		            serializer.endTag("", "bockCount");
		            
		            serializer.text("\n\t\t");
		            serializer.endTag("", "PreRound");
		        }
    	        serializer.text("\n\t");
	            serializer.endTag("", "PreRounds");
	            
    	        serializer.text("\n");
    	        serializer.endTag("", "Game");
    	        serializer.endDocument();
    	        
    	        //Write to file
    	        try{
    	        	//Log.d(TAG,writer.toString());
					FileOutputStream fos = c.openFileOutput(newFilename,Context.MODE_PRIVATE);
					OutputStreamWriter osw = new OutputStreamWriter(fos); 
					
				    osw.write(writer.toString());
				    osw.flush();
	    		    fos.flush();
	    		    osw.close();
	    		    fos.close();
	    		    if (oldFilename != null) {
	    		    	c.deleteFile(oldFilename);
	    		    }
	    		    return true;
    	         }	
    	         catch(Exception e){
    	        	 Log.d(TAG,e.toString());
    	         }
    	    } catch (Exception e) {
    	    	Log.d(TAG,e.toString());
    	    } 
    	}
    	return false;
    }
	
	public static GameClass restoreGameStateFromXML(Context c,String filePath) {
		int mPID = 0;
		int mPreID = 1; // 0 = show state
		int mPlayerCnt = 0, mPreRoundCnt = 0, mBockCount = -1, mActivePlayers = 0, mBockRoundLimit = 0;
		GAME_CNT_VARIANT mGameCntVariant = GAME_CNT_VARIANT.CNT_VARIANT_NORMAL;
		Float mPoints = (float) 0.0;
		String mName = "";
		
		GameClass mGame = new GameClass(filePath);
		
		ArrayList<PlayerClass> mPlayers = new ArrayList<PlayerClass>();
		ArrayList<RoundClass> mPreRounds = new ArrayList<RoundClass>();
		
		Node mNode;
		NodeList mNodes,mNodesTmp,mNodesTmp2;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		
		try{
			FileInputStream in = c.openFileInput(filePath);
			
			db = dbf.newDocumentBuilder();
			doc = db.parse(in);
			doc.getDocumentElement().normalize();
			
			NodeList nodeList = doc.getElementsByTagName("Game");
			mNode = nodeList.item(0);
			
			if(mNode == null){
				Log.d(TAG,"XML Parse Error 1");
				return null;
			}
			
			mNodes = mNode.getChildNodes();

			for (int i = 0; i < mNodes.getLength(); i++) {
				mNode = mNodes.item(i);
				if(mNode.getNodeType() != Node.ELEMENT_NODE) continue;
				
				//Log.d(TAG,i+"#"+mNode.getTextContent());
				
				if(mNode.getNodeName().equalsIgnoreCase("PlayerCnt")) mPlayerCnt = Integer.valueOf(mNode.getTextContent());
				else if(mNode.getNodeName().equalsIgnoreCase("ActivePlayers")) mActivePlayers = Integer.valueOf(mNode.getTextContent());
				else if(mNode.getNodeName().equalsIgnoreCase("BockRoundLimit")) mBockRoundLimit = Integer.valueOf(mNode.getTextContent());
				else if(mNode.getNodeName().equalsIgnoreCase("GameCntVariant")) mGameCntVariant = GAME_CNT_VARIANT.valueOf(mNode.getTextContent());
				else if(mNode.getNodeName().equalsIgnoreCase("Players")){
					mNodesTmp = mNode.getChildNodes();
					for(int t=0; t<mNodesTmp.getLength();t++) {
						mNode = mNodesTmp.item(t);
						if(mNode.getNodeType() != Node.ELEMENT_NODE) continue;
						if(mNode.getNodeName().equalsIgnoreCase("Player")){
							mNodesTmp2 =  mNode.getChildNodes();
							for(int k=0;k<mNodesTmp2.getLength();k++){
								mNode = mNodesTmp2.item(k);
								if(mNode.getNodeType() != Node.ELEMENT_NODE) continue;
								if(mNode.getNodeName().equalsIgnoreCase("name")) mName = mNode.getTextContent();
								else if(mNode.getNodeName().equalsIgnoreCase("points")) mPoints = Float.valueOf(mNode.getTextContent());
														
							}

							if(mName.length() > 0) mPlayers.add(new PlayerClass(mPID++,mName,mPoints));
						}
					}
				} 
				else if(mNode.getNodeName().equalsIgnoreCase("PreRounds")){
					mNodesTmp = mNode.getChildNodes();
					for(int t=0; t<mNodesTmp.getLength();t++) {
						mNode = mNodesTmp.item(t);
						if(mNode.getNodeType() != Node.ELEMENT_NODE) continue;
						if(mNode.getNodeName().equalsIgnoreCase("PreRound")){
							mNodesTmp2 =  mNode.getChildNodes();
							for(int k=0;k<mNodesTmp2.getLength();k++){
								mNode = mNodesTmp2.item(k);
								if(mNode.getNodeType() != Node.ELEMENT_NODE) continue;
								if(mNode.getNodeName().equalsIgnoreCase("bockCount")) mBockCount = Integer.valueOf(mNode.getTextContent());
														
							}
							if(mBockCount != -1) mPreRounds.add(new RoundClass(mPreID++, 0, mBockCount));
						}
					}
				} 
			}
		}
		catch(Exception e){
			Log.d(TAG,e.toString());
			return null;
		}
		//fill inactive players
		for(int i=mPlayers.size();i<DokoData.MAX_PLAYER;i++) mPlayers.add(new PlayerClass(i,"",0));
		
		//For Debug
		//Log.d(TAG,mPlayers.size()+"-"+mActivePlayers+"-"+mBockRoundLimit+"-"+mPreRounds.size());
		/*
		for(int k=0;k<mPlayers.size();k++){
			Log.d(TAG,"Player: "+mPlayers.get(k).getName()+" - points:"+mPlayers.get(k).getPoints());
		}
		
		for(int k=0;k<mPreRounds.size();k++){
			Log.d(TAG,"preRound: "+mPreRounds.get(k).getID()+" - bock:"+mPreRounds.get(k).getBockCount());
		}*/
		
		//Log.d(TAG,mPreRounds.size()+"-"+mPlayers.size());*/
		
		if(mPlayers.size() != DokoData.MAX_PLAYER)  Log.d(TAG,"File incorecct (XML)");
		
		//Log.d(TAG,"XML OK");
		
		mGame.getRoundList().add(new RoundClass(0,0,0));
		for(int i=0;i<mPlayers.size();i++){
			mPlayers.get(i).updatePoints(0,(float)0);
		}
		
		mGame.setActivePlayerCount(mActivePlayers);
		mGame.setPlayerCount(mPlayerCnt);
		mGame.setBockRoundLimit(mBockRoundLimit);
		mGame.setGameDataFromRestore(mPlayers, mPreRounds);
		mGame.setGameCntVariant(mGameCntVariant);
		
		return mGame;
	}

   
	public static String getAppDir(Context c){
		return c.getApplicationInfo().dataDir+File.separatorChar;
	}
	
	public static boolean isAppDirOK(Context c){
		File file = new File(getAppDir(c));
		if(!file.isDirectory() || (!file.canWrite() || !file.canRead()) ){
			return false;
		}
		return true;
	}
	
	public static boolean isXMLPresent(Context c,String f,boolean copyXML){
		try {
			c.openFileInput(f);
			return true;
		} catch (FileNotFoundException e) {
			Log.d(TAG,e.toString());
			if(copyXML) return copyXML(c,f);
		}
		return false;
	}
	
	public static boolean copyXML(Context c, String f){
		InputStream ins = c.getResources().openRawResource(R.raw.player_names);
		
		byte[] buffer;
		try {
			buffer = new byte[ins.available()];
			ins.read(buffer);
			ins.close();
			
			FileOutputStream fos = c.openFileOutput(f,Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();
			return true;
		} catch (IOException e) {
			Log.d(TAG,e.toString());
			return false;
		}

	}
	
	public static boolean isExternalStoragePresent() {

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if (!((mExternalStorageAvailable) && (mExternalStorageWriteable))) {
            //Toast.makeText(this, "SD card not present", Toast.LENGTH_LONG).show();

        }
        return (mExternalStorageAvailable) && (mExternalStorageWriteable);
    }
	
   
	public static boolean getPlayerNamesFromXML(Context c,String f, ArrayList<String> playerNames){
		Log.d(TAG,"loadFromXML");
	
		Node node;
		NodeList names;

		//InputStream in = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
	
		//http://stackoverflow.com/questions/3448145/xml-file-parsing-in-android

		try {
			//in = new FileInputStream(file);
			
			FileInputStream in = c.openFileInput(f);
			
			db = dbf.newDocumentBuilder();
			doc = db.parse(in);
			doc.getDocumentElement().normalize();
			//Log.d(TAG, ""+configs.getLength() );
			//get MRF node
			NodeList nodeList = doc.getElementsByTagName("Names");
			node = nodeList.item(0);
			
			if(node == null){
				Log.d(TAG,"XML Parse Error 1");
				return false;
			}
			names = node.getChildNodes();
			playerNames.clear();
			for (int i = 0; i < names.getLength(); i++) {	
				if(names.item(i).getNodeType() != Node.ELEMENT_NODE && !names.item(i).getNodeName().equalsIgnoreCase("name")){
					continue;
				}
				if(!names.item(i).getTextContent().isEmpty())
					playerNames.add(names.item(i).getTextContent());

			}
			Collections.sort(playerNames);
		} catch (SAXException e) {
			Log.d(TAG, e.toString());
		} catch (IOException e) {
			Log.d(TAG, e.toString());
		} catch (ParserConfigurationException e) {
			Log.d(TAG, e.toString());
		} catch (Exception e){
			Log.d(TAG, e.toString());
		}

		return false;
	}
	
	public static boolean savePlayerNamesToXML(Context c,String file, ArrayList<String> playerNames){
		if(playerNames != null && DokoXMLClass.isAppDirOK(c)){
			XmlSerializer serializer = Xml.newSerializer();
		    StringWriter writer = new StringWriter();
		    try {
		        serializer.setOutput(writer);
		        serializer.startDocument("UTF-8", false);
		        serializer.text("\n");
		        serializer.startTag("", "Names");
		        for (String name: playerNames){
		        	serializer.text("\n");
		        	serializer.text("\t");
		            serializer.startTag("", "name");
		            serializer.text(name);
		            serializer.endTag("", "name");
		        }
		        serializer.text("\n");
		        serializer.endTag("", "Names");
		        serializer.endDocument();
		        try{
	
					/*FileOutputStream fos = c.openFileOutput(f,Context.MODE_PRIVATE);
					fos.write(buffer);
					fos.close();*/
		        	//File f = new File(getAppDir(c)+file);
		        	//f.delete();
	
					FileOutputStream fos = c.openFileOutput(file,Context.MODE_PRIVATE);
					OutputStreamWriter osw = new OutputStreamWriter(fos); 
					
				    osw.write(writer.toString());
				    osw.flush();
	    		    fos.flush();
	    		    osw.close();
	    		    fos.close();
		            return true;
		         }	
		         catch(Exception e){
		        	 Log.d(TAG,e.toString());
		         }
		    } catch (Exception e) {
		    	Log.d(TAG,e.toString());
		    } 
		}
		return false;
	}
	

}

package nldoko.game.game;

import java.util.Arrays;
import java.util.Collections;

import nldoko.game.R;
import nldoko.game.XML.DokoXMLClass;
import nldoko.game.classes.GameClass;
import nldoko.game.classes.PlayerClass;
import nldoko.game.data.DokoData;
import nldoko.game.information.AboutActivity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SavedGameListActivity extends Activity {
	private Context mContext;
	
	private String TAG = "SavedGameList";
	
	private ActionBar mActionBar;
	private LinearLayout mEntriesLayout;
	private LayoutInflater inflater;
	
	private static String[] fileList;

	private static int SAVED_GAME_TAG  = 2611;
	private static int SAVED_GAME_TAG_DELETE  = 4211;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);
        mContext = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mActionBar = getActionBar();
        mActionBar.show();
        mActionBar.setTitle(getResources().getString(R.string.str_saved_game));
        mActionBar.setDisplayHomeAsUpEnabled(true);       
        fileList = mContext.fileList();
        setUI();
        
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }

    private void reload() {
    	fileList = mContext.fileList();
    	setUI();
    }

    private void setUI() {
    	int tagCnt = 0;
    	mEntriesLayout = (LinearLayout)findViewById(R.id.saved_games_entries);
    	mEntriesLayout.removeAllViewsInLayout();

    	FileClickListerner mFileClickListerner = new FileClickListerner();
    	FileDeleteClickListener mFileDeleteClickListerner = new FileDeleteClickListener();
    	
    	Arrays.sort(fileList,Collections.reverseOrder());
    	if (fileList != null){
    		View v;
    		TextView mTv;
    		ImageView mIv;
    	    for (String savedGameFile : fileList){
    	    	Log.d("e",savedGameFile);
    	    	if (!savedGameFile.endsWith(DokoData.SAVED_GAME_FILE_POSTFIX)) {
    	    		tagCnt++;
    	    		continue;
    	    	}
    			v = inflater.inflate(R.layout.saved_game_entry, null);
    			    			
    			LinearLayout l = (LinearLayout)v.findViewById(R.id.saved_game_entry_layout_start_game);
    			l.setOnClickListener(mFileClickListerner);
    			l.setTag(SAVED_GAME_TAG + tagCnt);
    			
    			String[] arr = savedGameFile.split("_");
    			 
    			mTv = (TextView)v.findViewById(R.id.saved_game_entry_filename);
    			if (arr.length > 1) {
    				mTv.setText(arr[0]+" - "+arr[1]);
    			} else {
    				mTv.setText(savedGameFile);
    			}
    			
            	GameClass mGame =  DokoXMLClass.restoreGameStateFromXML(this,savedGameFile);
            	if (mGame != null) {
            		// if success delete old file
            		String gameStats = "";
            		for(PlayerClass p : mGame.getPlayers()) {
            			if (p.getName().length() > 0) {
            				gameStats += p.getName()+" ("+p.getPoints()+"), ";
            			}
            		}	
                    mTv = (TextView)v.findViewById(R.id.saved_game_entry_text);
        			mTv.setText(gameStats);
            	}
            	
            	mIv = (ImageView)v.findViewById(R.id.saved_game_entry_delete);
            	mIv.setTag(SAVED_GAME_TAG_DELETE + tagCnt);
            	mIv.setOnClickListener(mFileDeleteClickListerner);
            	
            	Log.d(TAG,"set tagcnt:"+tagCnt);
    			mEntriesLayout.addView(v);  
    			tagCnt++;
    	    }
    	}    	
	} 
    
    private class FileDeleteClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int tagCnt = (Integer)v.getTag();
			tagCnt -= SAVED_GAME_TAG_DELETE;
			if (fileList.length > tagCnt) {
				showDeleteSavedGamesDialog(fileList[tagCnt]);
			}
		}
	};
            	
    private class FileClickListerner implements OnClickListener{
		@Override
		public void onClick(View v) {
			int tagCnt = (Integer)v.getTag();
			tagCnt -= SAVED_GAME_TAG;
			Log.d(TAG,"get tag cnt:"+tagCnt);
			if (fileList.length > tagCnt) {
				String filename = fileList[tagCnt];
				if (filename.length() == 0) return;
	    		Intent i = new Intent(mContext, GameActivity.class);
	    		i.putExtra("RestoreGameFromXML", true);
	    		i.putExtra("filename", filename);
	    		startActivity(i);
	    		finish();
			}		
		}
    }
    
	private void showDeleteAllSavedGamesDialog(){
		Builder back = new AlertDialog.Builder(this);
		back.setTitle(R.string.str_delete_files);
		back.setMessage(R.string.str_saved_game_delete_all_q);
		back.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				for(String filename : fileList) {
					mContext.deleteFile(filename);
				}
				reload();
			}
		});

		back.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		back.show();
	}
	
	private void showDeleteSavedGamesDialog(String file){
		final String filename = file;
		Builder back = new AlertDialog.Builder(this);
		back.setTitle(R.string.str_delete_file);
		back.setMessage(R.string.str_saved_game_delete_q);
		back.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mContext.deleteFile(filename);
				reload();
			}
		});

		back.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		back.show();
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
            
    	case R.id.action_delete_all_saved_games_menu:
    		showDeleteAllSavedGamesDialog();
    		return true;
    		
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_game, menu);
        return true;
    }
    
    
}

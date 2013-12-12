package nldoko.game.game;

import java.util.Arrays;
import java.util.Collections;

import nldoko.game.R;
import nldoko.game.data.DokoData;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SavedGameListActivity extends Activity {
	private Context mContext;
	
	private String TAG = "SavedGameList";
	
	private ActionBar mActionBar;
	private LinearLayout mEntriesLayout;
	private LayoutInflater inflater;

	
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
        
        setUI();
        
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }


    private void setUI() {
    	mEntriesLayout = (LinearLayout)findViewById(R.id.saved_games_entries);
    	
    	FileClickListerner mFileClickListerner = new FileClickListerner();
    	
    	String[] fileList = mContext.fileList();
    	Arrays.sort(fileList,Collections.reverseOrder());
    	if (fileList != null){
    		View v;
    		TextView mTv;
    	    for (String savedGameFile : fileList){
    	    	Log.d("e",savedGameFile);
    	    	if (!savedGameFile.endsWith(DokoData.SAVED_GAME_FILE_POSTFIX)) continue;
    			v = inflater.inflate(R.layout.saved_game_entry, null);
    			v.setOnClickListener(mFileClickListerner);
    			mTv = (TextView)v.findViewById(R.id.saved_game_entry_filename);
    			mTv.setText(savedGameFile);
 
    			mEntriesLayout.addView(v);  
    	    }
    	}    	
	} 
    
    private class FileClickListerner implements OnClickListener{
		@Override
		public void onClick(View v) {
			TextView mTv = (TextView)v.findViewById(R.id.saved_game_entry_filename);	
			if (mTv != null) {
				String filename = mTv.getText().toString();
				if (filename.length() == 0) return;
	    		Intent i = new Intent(mContext, GameActivity.class);
	    		i.putExtra("RestoreGameFromXML", true);
	    		i.putExtra("filename", filename);
	    		startActivity(i);
	    		finish();
			}		
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
}

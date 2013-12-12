package nldoko.game.start;

import nldoko.game.R;
import nldoko.game.classes.TypeWriterClass;
import nldoko.game.game.GameActivity;
import nldoko.game.game.NewGameActivity;
import nldoko.game.game.SavedGameListActivity;
import nldoko.game.information.AboutActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StartActivity extends Activity {
	
	private static String TAG = "StartActivity";
	private ActionBar mActionBar;
	private Button mBtnNewGame;
	private LinearLayout mSavedGameBtn;
	private Button mBtnGame;
	
	private Handler mHandler;
	
	private long mDelayChar;
	
	private static TextView mSavedGameText;
	private static CharSequence mSavedGameTextCharSequence;
	private int mIndex;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
        mActionBar = getActionBar();
        mActionBar.show();
        
        mHandler = new Handler();
        mDelayChar = 50; // ms
        mSavedGameTextCharSequence =  this.getResources().getString(R.string.str_saved_game);
        
        mBtnNewGame = (Button)findViewById(R.id.btn_new_game);
        mBtnNewGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(arg0.getContext(),NewGameActivity.class);
				startActivity(i);
			}
		});
        
        mSavedGameBtn = (LinearLayout)findViewById(R.id.saved_game_btn);
        mSavedGameText = (TextView)findViewById(R.id.saved_game_btn_text);
   
        
        mSavedGameBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(arg0.getContext(),SavedGameListActivity.class);
				startActivity(i);
			}
		});
               
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    
    private Runnable characterAdder = new Runnable() {
    	@Override
    	public void run() {
        	
    		mSavedGameText.setText(mSavedGameTextCharSequence.subSequence(0, mIndex++));
    	    if(mIndex <= mSavedGameTextCharSequence.length()) {
    	    	Log.d(TAG,"run "+mIndex+mSavedGameTextCharSequence);
    	        mHandler.postDelayed(characterAdder, mDelayChar);
    	    }
    	}
    };

    public void animateText() {
    	mIndex = 0;
       	mSavedGameText.setText("");
    	mHandler.removeCallbacks(characterAdder);
    	mHandler.postDelayed(characterAdder, mDelayChar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    
    
    @Override
  	public boolean onOptionsItemSelected(MenuItem item){
    	// same as using a normal menu
    	Intent i;
    	switch(item.getItemId()) {
	    	case R.id.action_about:
	    		i = new Intent(this, AboutActivity.class);
	    		startActivity(i);
	    		break;

    	}
    	return true;
    }
    
}

package nldoko.game.start;

import nldoko.game.R;
import nldoko.game.game.GameActivity;
import nldoko.game.game.NewGameActivity;
import nldoko.game.information.AboutActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity {
	ActionBar mActionBar;
	Button mBtnNewGame;
	Button mBtnGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
        mActionBar = getActionBar();
        mActionBar.show();
        
        mBtnNewGame = (Button)findViewById(R.id.btn_new_game);
        mBtnNewGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(arg0.getContext(),NewGameActivity.class);
				startActivity(i);
			}
		});
               
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
	    	case R.id.action_load_save_game:
	    		i = new Intent(this, GameActivity.class);
	    		i.putExtra("RestoreGameFromXML", true);
	    		startActivity(i);
	    		break;	

    	}
    	return true;
    }
    
}

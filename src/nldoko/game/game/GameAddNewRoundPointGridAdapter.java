package nldoko.game.game;

import nldoko.game.R;
import nldoko.game.data.DokoData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

 
public class GameAddNewRoundPointGridAdapter extends BaseAdapter {
   	
    private LayoutInflater mInflater;
    private Context mContext;
    private EditText mEditText;
	
	public GameAddNewRoundPointGridAdapter(Context c, EditText editText){
		mContext = c;
		mEditText = editText;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
    public int getCount() {
        return DokoData.mPointSuggestions.length;
    }
 

    public Object getItem(int position) {
    	if(position < DokoData.mPointSuggestions.length)
    		return DokoData.mPointSuggestions[position];
    	return null;
    }
 

    public long getItemId(int position) {
        return 0;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Button mBtn;
    
        view = convertView;
        if(view == null) view = mInflater.inflate(R.layout.fragment_game_new_round_grid_point, null);

        mBtn = (Button)view.findViewById(R.id.game_new_round_point_grid);
        mBtn.setText(Integer.toString(DokoData.mPointSuggestions[position]));
        mBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				mEditText.setText( Integer.valueOf( ((Button) v).getText().toString()).toString() );
			}
		});

        return view;
    }
 
}
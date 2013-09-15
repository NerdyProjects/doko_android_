package nldoko.android;

import android.text.Editable;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

//Issue with editText 

//https://code.google.com/p/android/issues/detail?id=17508#c6
//http://stackoverflow.com/questions/5988976/edittext-stops-displaying-characters-as-i-type

public class EditTextFixListener implements    OnEditorActionListener {

	private String TAG = "EditText OnEditorActionListener";
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    	if(event != null)
    		Log.d(TAG,"clear"+actionId+"#"+event.getKeyCode());
    	else Log.d(TAG,"clear"+actionId);
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
        	TextKeyListener.clear((Editable) v.getText());
        	return true;
        	/*String sendText = v.getText().toString();
            
            if (sendText.length() == 0) {
                Log.d(TAG,"clear");
                TextKeyListener.clear((Editable) v.getText());
                
            }*/
         }
    	return false;
         
    }

}

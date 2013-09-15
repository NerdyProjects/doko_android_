package nldoko.android;

import java.math.BigDecimal;

import android.util.Log;

public class Functions {
	
	private static String TAG = "FUNCTIONS";
	
	public static String getFloatAsString(float points){
		if(points == 0) return "0";
		return new BigDecimal(Float.toString(points)).stripTrailingZeros().toPlainString();
	}
	
	public static String getBockCountAsRom(int bockcount){
		String mStr = "";
		for(int i=0;i<bockcount;i++) mStr += "I";
		return mStr;
	}

}

package com.kuxhausen.huemore.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FireReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
            
        }
	}

}

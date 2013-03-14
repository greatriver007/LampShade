package com.kuxhausen.huemore;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class NewMoodDialogFragment extends DialogFragment implements OnClickListener , OnKeyListener, ColorPickerDialogFragment.OnColorChangedListener{

	ListView bulbsListView;
	MoodRowAdapter rayAdapter;
	ArrayList<MoodRow> moodRowArray;
	EditText nameEditText;
	EditText stateName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		moodRowArray = new ArrayList<MoodRow>();
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View groupDialogView = inflater.inflate(R.layout.edit_mood_dialog,
				null);
		bulbsListView = ((ListView) groupDialogView
				.findViewById(R.id.listView1));
		rayAdapter = new MoodRowAdapter(this.getActivity(),
				moodRowArray);
		bulbsListView.setAdapter(rayAdapter);
		builder.setView(groupDialogView);

		nameEditText = (EditText) groupDialogView.findViewById(R.id.editText1);
		
		stateName = (EditText) groupDialogView.findViewById(R.id.stateNameTextView);
		stateName.setOnKeyListener(this);

		ImageButton stateColor = (ImageButton) groupDialogView.findViewById(R.id.stateColorButton);
		stateColor.setOnClickListener(this);
		
		builder.setPositiveButton(R.string.accept,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//TODO
					}
				}).setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				}).setNeutralButton(R.string.preview,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//TODO launch group selector then apply preview
					}
				});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	
	private void addState() {
		ColorPickerDialogFragment cpdf = new ColorPickerDialogFragment();
		cpdf.show(getFragmentManager(), "dialog");
		
		MoodRow mr = new MoodRow();
        mr.name =stateName.getText().toString();
        if(mr.equals(""))
        	return;
        mr.color = 0xffff0000;
        moodRowArray.add(mr);
        rayAdapter.add(mr);
        stateName.setText(null);
    }

	@Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.FLAG_EDITOR_ACTION:
                    addState();
                    return true;
            }
        }
        return false;
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.stateColorButton:
			addState();
			
			break;
		}
	}


	@Override
	public void colorChanged(int color) {
		// TODO Auto-generated method stub
		
	}
}
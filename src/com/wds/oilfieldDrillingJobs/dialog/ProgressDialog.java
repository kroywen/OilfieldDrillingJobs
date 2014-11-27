package com.wds.oilfieldDrillingJobs.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wds.oilfieldDrillingJobs.R;

public class ProgressDialog extends DialogFragment {
	
	private TextView textView;
	private String text;
	
	private void initializeViews(View view) {
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(text);
	}
	
	public void setText(String text) {
		this.text = text;
		if (textView != null) {
			textView.setText(text);
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.progress_dialog, null);
	    initializeViews(view);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setInverseBackgroundForced(true);
	    AlertDialog dialog = builder.create();
	    dialog.setView(view, 0, 0, 0, 0);
	    
	    return dialog;
	}

}

package com.wds.oilfieldDrillingJobs.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wds.oilfieldDrillingJobs.R;

public class ConfirmationDialog extends DialogFragment {
	
	private TextView titleView;
	private TextView textView;
	private Button okBtn;
	private Button cancelBtn;
	
	private String title;
	private String text;
	private String okText;
	private String cancelText;
	private OnClickListener okListener;
	private OnClickListener cancelListener;
	
	private void initializeViews(View view) {
		titleView = (TextView) view.findViewById(R.id.titleView);
		titleView.setText(title);
		
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(text);
		
		okBtn = (Button) view.findViewById(R.id.okBtn);
		if (!TextUtils.isEmpty(okText)) {
			okBtn.setText(okText);
		}
		okBtn.setOnClickListener(okListener != null ? okListener : stdListener);
		
		cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
		if (!TextUtils.isEmpty(cancelText)) {
			cancelBtn.setText(cancelText);
		}
		cancelBtn.setOnClickListener(cancelListener != null ? cancelListener : stdListener);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setOkListener(OnClickListener okListener) {
		this.okListener = okListener;
	}
	
	public void setOkListener(String okText, OnClickListener okListener) {
		this.okText = okText; 
		this.okListener = okListener;
	}
	
	public void setCancelListener(OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}
	
	public void setCancelListener(String cancelText, OnClickListener cancelListener) {
		this.cancelText = cancelText;
		this.cancelListener = cancelListener;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.confirmation_dialog, null);
	    initializeViews(view);
	    if (TextUtils.isEmpty(title)) {
	    	titleView.setVisibility(View.GONE);
	    }
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setInverseBackgroundForced(true);
	    AlertDialog dialog = builder.create();
	    dialog.setView(view, 0, 0, 0, 0);
	    
	    return dialog;
	}
	
	View.OnClickListener stdListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

}

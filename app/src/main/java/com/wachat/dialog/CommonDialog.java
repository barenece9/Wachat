package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;

import com.wachat.R;
import com.wachat.callBack.DialogCallback;
import com.wachat.data.DataRegistration;
import com.wachat.util.Prefs;

public class CommonDialog  {


	public  static void ShowDialog(Context context, final DialogCallback mCallback, final Object mObject) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("");
		builder.setMessage(context.getResources().getString(R.string.already_paired));
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCallback.OnClickYes(mObject);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCallback.OnClickNo(mObject);
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);

		builder.show();
	}
}

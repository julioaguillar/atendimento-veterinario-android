package br.com.aonsistemas.appvet.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.com.aonsistemas.appvet.R;

public class Message {
	
	public static final int ID_YES = 0;
	public static final int ID_NO  = 1;
	
	private static int valueReturn;
	
	public static void showMessage(Context context, int messageId) {
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(messageId);
		dialog.setNegativeButton("OK", null);
		
		dialog.show();
		
	}
	
	public static void showMessage(Context context, String message) {
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(message);
		dialog.setNegativeButton("OK", null);
		
		dialog.show();
		
	}
	
	public static int showMessageDialog(Context context, int message) {
		
		valueReturn = ID_NO;
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(message);
		
		dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				valueReturn = ID_YES;				 
			}
		});
		
		dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				valueReturn = ID_NO;				
			}
		});
		
		dialog.show();
		
		return valueReturn;		
		
	}

}

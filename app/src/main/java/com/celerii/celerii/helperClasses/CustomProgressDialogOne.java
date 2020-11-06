package com.celerii.celerii.helperClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.celerii.celerii.R;

/**
 * Created by DELL on 1/17/2019.
 */

public class CustomProgressDialogOne {

    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    public CustomProgressDialogOne(Context context) {
        this.context = context;
    }

    public void show() {
        if (context != null) {
            this.dialogBuilder = new AlertDialog.Builder(this.context);
            final View dialogView = LayoutInflater.from(this.context).inflate(R.layout.custom_progress_dialog_one, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            this.alertDialog = dialogBuilder.create();
            this.alertDialog.show();
            this.alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void showWithMessage(String message) {
        if (context != null) {
            this.dialogBuilder = new AlertDialog.Builder(this.context);
            final View dialogView = LayoutInflater.from(this.context).inflate(R.layout.custom_progress_dialog_one, null);
            TextView messageTextView = (TextView) dialogView.findViewById(R.id.message);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            this.alertDialog = dialogBuilder.create();
            this.alertDialog.show();
            this.alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void dismiss()
    {
        if (alertDialog != null)
            alertDialog.dismiss();
    }
}

package com.celerii.celerii.helperClasses;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.celerii.celerii.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class InviteParentsBottomSheet extends BottomSheetDialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_parents_bottom_sheet_layout, container, false);
        Bundle bundle = getArguments();
        final String referralLinkWithStudentID = bundle.getString("Referral Link");
        final String studentName = bundle.getString("Student Name");

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getContext());
        final String referralLink = sharedPreferencesManager.getMyReferralLink();
        final String referralText = sharedPreferencesManager.getMyReferralText();
        final String referralSubject = sharedPreferencesManager.getMyReferralSubject();
        final String secondaryReferralSubject = sharedPreferencesManager.getMyReferralSubject();

        LinearLayout copyLink = view.findViewById(R.id.copylink);
        LinearLayout email = view.findViewById(R.id.email);
        LinearLayout sms = view.findViewById(R.id.sms);
        LinearLayout whatsApp = view.findViewById(R.id.whatsapp);
        LinearLayout more = view.findViewById(R.id.more);
        Button close = view.findViewById(R.id.close);

        copyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Celerii Referral Link", referralLinkWithStudentID);
                clipboard.setPrimaryClip(clip);
                CustomToast.primaryBackgroundToast(getContext(), "Copied to Clipboard!");
                dismiss();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String referralSubjectMod = referralSubject.replace("your kid", studentName);
                String secondaryReferralSubjectMod = secondaryReferralSubject.replace("your kid", studentName);
                String referralTextMod = referralText.replace("your kid", studentName) + " " + referralLinkWithStudentID;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{});
                i.putExtra(Intent.EXTRA_SUBJECT, referralSubjectMod);
                i.putExtra(Intent.EXTRA_TEXT   , (Html.fromHtml("<b>" + secondaryReferralSubjectMod + "</b>" + "\n\n" + referralTextMod))).toString();
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.custom_unary_message_dialog);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                    TextView OK = (TextView) dialog.findViewById(R.id.optionone);
                    dialog.show();

                    message.setText("There are no email clients installed on your device");

                    OK.setText("OK");

                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsBody = referralText + " " + referralLinkWithStudentID;
                smsBody = smsBody.replace("your kid", studentName);
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address","");
                smsIntent.putExtra("sms_body", smsBody);
                startActivity(smsIntent);
            }
        });

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageBody = referralText + " " + referralLinkWithStudentID;
                messageBody = messageBody.replace("your kid", studentName);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, messageBody);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageBody = referralText + " " + referralLinkWithStudentID;
                messageBody = messageBody.replace("your kid", studentName);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, messageBody);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}

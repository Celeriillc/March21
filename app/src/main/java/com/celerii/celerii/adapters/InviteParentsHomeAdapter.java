package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Settings.InviteParentsHomeActivity;
import com.celerii.celerii.Activities.Settings.ReportAbuseActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.InviteParentsBottomSheet;
import com.celerii.celerii.models.ReportUserModel;
import com.celerii.celerii.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class InviteParentsHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Student> studentList;
    String activeAccountID;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView profilePic;
        public LinearLayout clipper;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            profilePic = (ImageView) view.findViewById(R.id.picture);
            clipper = (LinearLayout) view.findViewById(R.id.clipper);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public InviteParentsHomeAdapter(List<Student> studentList, String activeAccountID, Context context) {
        this.studentList = studentList;
        this.activeAccountID = activeAccountID;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_abuse, parent, false);
                return new InviteParentsHomeAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_abuse_header, parent, false);
                return new InviteParentsHomeAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_abuse, parent, false);
                return new InviteParentsHomeAdapter.MyViewHolder(rowView);
        }
    }


    String referralLink = "";
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).header.setText("Whose parent are you inviting?");
        }
        else if (holder instanceof MyViewHolder){
            final Student student = studentList.get(position);
            String firstName = student.getFirstName();
            String lastName = student.getLastName();
            final String name = firstName + " " + lastName;
            ((MyViewHolder) holder).name.setText(name);
            ((MyViewHolder) holder).clipper.setClipToOutline(true);

            Drawable textDrawable;
            String studentName = student.getFirstName() + " " + student.getLastName();
            if (!studentName.trim().isEmpty()) {
                String[] nameArray = studentName.replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                ((MyViewHolder) holder).profilePic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            if (!student.getImageURL().isEmpty()) {
                Glide.with(context)
                        .load(student.getImageURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).profilePic);
            }

            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CustomProgressDialogOne customProgressDialogOne = new CustomProgressDialogOne(context);
                    customProgressDialogOne.show();
                    String link = "https://celerii.com/?invitedby=" + activeAccountID + "&invitedbyaccounttype=" + "Teacher" + "&studentid=" + student.getStudentID();
                    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                            .setLink(Uri.parse(link))
                            .setDomainUriPrefix("https://celerii.page.link")
                            .buildShortDynamicLink()
                            .addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
                                @Override
                                public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                    if (task.isSuccessful()) {
                                        referralLink = task.getResult().getShortLink().toString();
                                    } else {
                                        referralLink = "https://celerii.com/";
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putString("Student Name", name);
                                    bundle.putString("Referral Link", referralLink);
                                    InviteParentsBottomSheet bottomSheet = new InviteParentsBottomSheet();
                                    bottomSheet.setArguments(bundle);
                                    bottomSheet.show(((InviteParentsHomeActivity) context).getSupportFragmentManager(), "inviteParentsBottomSheet");
                                    customProgressDialogOne.dismiss();
                                }
                            });
                }
            });
        }
    }

    public int getItemCount() {
        return studentList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(isPositionHeader (position)) {
            return Header;
        } else if(isPositionFooter (position)) {
            return Footer;
        }
        return Normal;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == studentList.size () + 1;
    }
}

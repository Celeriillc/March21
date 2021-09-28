package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Utility.OpenPDFActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.GalleryDetailForSingleImageActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Chats;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 7/8/2017.
 */

public class ChatRowAdapter extends RecyclerView.Adapter<ChatRowAdapter.MyViewHolder>{
    private List<Chats> chatsList;
    private String chatTitle;
    private Context context;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private SharedPreferencesManager sharedPreferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, time, noOfmesages, fileName;
        public ImageView messageStatus, otherProfilePic, imageFile/*, fileImage, fileIcon*/;
        public LinearLayout imageClipper, profilePictureClipper, layout, fileImageClipper;
        public View divider;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time);
            fileName = (TextView) view.findViewById(R.id.filename);
            imageClipper = (LinearLayout) view.findViewById(R.id.imageClipper);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            fileImageClipper = (LinearLayout) view.findViewById(R.id.fileimageclipper);
            messageStatus = (ImageView) view.findViewById(R.id.messagestatus);
            otherProfilePic = (ImageView) view.findViewById(R.id.otherprofilepic);
            imageFile = (ImageView) view.findViewById(R.id.imagefile);
//            fileImage = (ImageView) view.findViewById(R.id.fileimage);
//            fileIcon = (ImageView) view.findViewById(R.id.fileicon);
            divider = view.findViewById(R.id.divider);
        }
    }

    public ChatRowAdapter(List<Chats> chatsList, String chatTitle, Context context) {
        this.chatsList = chatsList;
        this.chatTitle = chatTitle;
        this.context = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        sharedPreferencesManager = new SharedPreferencesManager(this.context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
        return new ChatRowAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Chats chatList = chatsList.get(position);

        holder.imageClipper.setClipToOutline(true);
        holder.profilePictureClipper.setClipToOutline(true);
        if (chatList.getReceiverID().equals(sharedPreferencesManager.getMyUserID())){ // Set isSeen = true in Firebase Database
            mDatabaseReference = mFirebaseDatabase.getReference();

            Map<String, Object> userUpdates = new HashMap<String, Object>();
            userUpdates.put("Messages/" + chatList.getReceiverID() + "/" + chatList.getSenderID() + "/" + chatList.getMessageID() + "/seen", true);
            userUpdates.put("Messages/" + chatList.getSenderID() + "/" + chatList.getReceiverID() + "/" + chatList.getMessageID() + "/seen", true);
            userUpdates.put("Messages Recent/" + chatList.getReceiverID() + "/" + chatList.getSenderID() + "/seen", true);
            userUpdates.put("Messages Recent/" + chatList.getSenderID() + "/" + chatList.getReceiverID() + "/seen", true);

            userUpdates.put("Messages/" + chatList.getReceiverID() + "/" + chatList.getSenderID() + "/" + chatList.getMessageID() + "/received", true);
            userUpdates.put("Messages/" + chatList.getSenderID() + "/" + chatList.getReceiverID() + "/" + chatList.getMessageID() + "/received", true);
            userUpdates.put("Messages Recent/" + chatList.getReceiverID() + "/" + chatList.getSenderID() + "/received", true);
            userUpdates.put("Messages Recent/" + chatList.getSenderID() + "/" + chatList.getReceiverID() + "/received", true);

            mDatabaseReference.updateChildren(userUpdates);
        }

        holder.imageFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chatList.getFileURL().equals("")) {
                    if (chatList.getFileType().equals("image")) {
                        Bundle b = new Bundle();
                        b.putString("ImageURL", chatList.getFileURL());
                        Intent I = new Intent(context, GalleryDetailForSingleImageActivity.class);
                        I.putExtras(b);
                        holder.imageFile.setTransitionName("imageTransition");
                        Pair<View, String> pair1 = Pair.create((View) holder.imageFile, holder.imageFile.getTransitionName());

                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.imageFile, holder.imageFile.getTransitionName());
                        context.startActivity(I, optionsCompat.toBundle());
                    }
                }
            }
        });

        holder.otherProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle b = new Bundle();
//                b.putString("URL", chatList.getOtherProfilePicURL());
//                Intent I = new Intent(context, GalleryDetailForMultipleImagesActivity.class);
//                I.putExtras(b);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ((MyViewHolder) holder).otherProfilePic.setTransitionName("imageTransition");
//                    Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).otherProfilePic, ((MyViewHolder) holder).otherProfilePic.getTransitionName());
//
//                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).otherProfilePic, ((MyViewHolder) holder).otherProfilePic.getTransitionName());
//                    context.startActivity(I, optionsCompat.toBundle());
//                }
//                else {
//                    context.startActivity(I);
//                }
            }
        });

        holder.fileImageClipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chatList.getFileURL().equals("")) {
                    if (chatList.getFileType().equals("pdf")) {
                        Intent intent = new Intent(context, OpenPDFActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("PDFTitle", chatList.getFileName());
                        bundle.putString("PDFURL", chatList.getFileURL());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                }
            }
        });

        if (!chatList.getFileURL().equals("")) {
            holder.message.setVisibility(View.GONE);
            if (chatList.getFileType().equals("pdf")) {
                holder.imageClipper.setVisibility(View.GONE);
                holder.fileImageClipper.setVisibility(View.VISIBLE);
                holder.fileName.setVisibility(View.VISIBLE);

                holder.fileName.setText(chatList.getFileName());
            } else {
                holder.imageClipper.setVisibility(View.VISIBLE);
                holder.fileImageClipper.setVisibility(View.GONE);
                holder.fileName.setVisibility(View.GONE);

                Glide.with(context)
                        .load(chatList.getFileURL())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.imageFile);
            }
        } else {
            holder.message.setVisibility(View.VISIBLE);
            holder.imageClipper.setVisibility(View.GONE);
            holder.fileImageClipper.setVisibility(View.GONE);
            holder.fileName.setVisibility(View.GONE);

            (holder).message.setText(Html.fromHtml(chatList.getMessage()));
        }

        if (position == chatsList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        holder.time.setText(Date.getRelativeTimeSpan(chatList.getDatestamp()));

        if (chatList.isMine()) {
            String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
            holder.name.setText(myName);

            Drawable textDrawable;
            if (!myName.isEmpty()) {
                String[] nameArray = myName.replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawableColor(context, nameArray[0], 40, 4);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawableColor(context, nameArray[0], nameArray[1], 40, 4);
                }
                holder.otherProfilePic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40);
            }

            if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
                Glide.with(context)
                        .load(sharedPreferencesManager.getMyPicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.otherProfilePic);
            }
        } else {
            holder.name.setText(chatTitle);
            Drawable textDrawable;
            if (!chatTitle.isEmpty()) {
                String[] nameArray = chatTitle.replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawableColor(context, nameArray[0], 40, 1);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawableColor(context, nameArray[0], nameArray[1], 40, 1);
                }
                holder.otherProfilePic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40);
            }

            if (!chatList.getOtherProfilePicURL().isEmpty()) {
                Glide.with(context)
                        .load(chatList.getOtherProfilePicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.otherProfilePic);
            }
        }
    }

    public int getItemCount() {
        return chatsList.size();
    }

    private void chatBubbleBackground(final ChatRowAdapter.MyViewHolder holder, int position){
        Chats chatList = chatsList.get(position);

        if (chatsList.size() > 0) {
            if (chatList.isMine()) {
                holder.otherProfilePic.setVisibility(View.GONE);

                holder.message.setBackgroundResource(R.drawable.chat_bubble_me);
                holder.layout.setGravity(Gravity.END);
                holder.message.setTextColor(Color.WHITE);
                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
                marginParams.setMargins(0, 10, 25, 10);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                holder.layout.setLayoutParams(layoutParams);

            } else {
                Drawable textDrawable;
                if (!chatTitle.isEmpty()) {
                    String[] nameArray = chatTitle.replaceAll("\\s+", " ").trim().split(" ");
                    if (nameArray.length == 1) {
                        textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 40);
                    } else {
                        textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 40);
                    }
                    holder.otherProfilePic.setImageDrawable(textDrawable);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40);
                }

                if (!chatList.getOtherProfilePicURL().isEmpty()) {
                    Glide.with(context)
                            .load(chatList.getOtherProfilePicURL())
                            .placeholder(textDrawable)
                            .error(textDrawable)
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(holder.otherProfilePic);
                }

                holder.message.setBackgroundResource(R.drawable.chat_bubble_you);
                holder.layout.setGravity(Gravity.START);
                holder.message.setTextColor(Color.WHITE);
                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
                marginParams.setMargins(25, 10, 0, 10);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                holder.otherProfilePic.setVisibility(View.VISIBLE);
                holder.layout.setLayoutParams(layoutParams);
            }
        }
//            if (chatList.isMine()) {
//                holder.messageStatus.setVisibility(View.VISIBLE);
////                holder.messageStatus.setVisibility(View.GONE);
//                holder.otherProfilePic.setVisibility(View.GONE);
//
//                holder.message.setBackgroundResource(R.drawable.chat_bubble_me);
//                holder.layout.setGravity(Gravity.END);
//                holder.message.setTextColor(Color.WHITE);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 10, 5, 10);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//
//            } else {
//                holder.messageStatus.setVisibility(View.GONE);
//                Glide.with(context)
//                        .load(chatList.getOtherProfilePicURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .centerCrop()
//                        .bitmapTransform(new CropCircleTransformation(context))
//                        .into(holder.otherProfilePic);
//
//                holder.message.setBackgroundResource(R.drawable.chat_bubble_you);
//                holder.layout.setGravity(Gravity.START);
//                holder.message.setTextColor(Color.BLACK);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 10, 0, 10);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//            }
//            return;
//        }
//
//
//        if (chatList.isMine()) {
//            holder.messageStatus.setVisibility(View.VISIBLE);
////            holder.messageStatus.setVisibility(View.GONE);
//            holder.otherProfilePic.setVisibility(View.GONE);
//            if (position == 0) {
//                if (chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me_bottom);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 1, 5, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else{
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 10, 5, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//            }
//            else if (position == chatsList.size() - 1){
//                if (chatsList.get(position - 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me_top);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 10, 5, 1);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else{
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 10, 5, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//            }
//            else {
//                if (chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me_middle);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 1, 5, 1);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else if(!chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me_bottom);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 1, 5, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else if(chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me_top);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 10, 5, 1);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else if(!chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_me);
//                    holder.layout.setGravity(Gravity.END);
//                    holder.message.setTextColor(Color.WHITE);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(0, 10, 5, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//            }
//        }
//        else {
//            holder.messageStatus.setVisibility(View.GONE);
//            Glide.with(context)
//                    .load(chatList.getOtherProfilePicURL())
//                    .placeholder(R.drawable.profileimageplaceholder)
//                    .error(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(context))
//                    .into(holder.otherProfilePic);
//
//            if (position == 0) {
//                if (chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 10, 0, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.VISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else{
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you_bottom);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 1, 0, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.INVISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//            }
//            else if (position == chatsList.size() - 1){
//                if (chatsList.get(position - 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 10, 0, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.VISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else{
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you_top);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 10, 0, 1);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.VISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//            }
//            else{
//                if (chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 10, 0, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.VISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else if (!chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you_top);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 10, 0, 1);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.VISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else if (chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you_bottom);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 1, 0, 10);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.INVISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//                else if (!chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                    holder.message.setBackgroundResource(R.drawable.chat_bubble_you_middle);
//                    holder.layout.setGravity(Gravity.START);
//                    holder.message.setTextColor(Color.BLACK);
//                    ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                    marginParams.setMargins(15, 1, 0, 1);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                    holder.otherProfilePic.setVisibility(View.INVISIBLE);
//                    holder.layout.setLayoutParams(layoutParams);
//                }
//            }
//        }
    }

    private void imageBackgroundForIsMine(ChatRowAdapter.MyViewHolder holder, int position){
        final Chats chatList = chatsList.get(position);

//        if (chatsList.get(position + 1).isMine()) {
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
            marginParams.setMargins(0, 10, 25, 10);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            holder.layout.setLayoutParams(layoutParams);
            Glide.with(context)
                    .load(chatList.getFileURL())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imageFile);
//        } else {
//            Glide.with(context)
//                    .load(chatList.getFileURL())
//                    .placeholder(R.drawable.ic_icons_google)
//                    .error(R.drawable.ic_icons_google)
//                    .fitCenter()
//                    .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                            new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(holder.imageFile);
//        }


//        Chats chatList = chatsList.get(position);
//
//        holder.messageStatus.setVisibility(View.VISIBLE);
////        holder.messageStatus.setVisibility(View.GONE);
//
//        if (position == 0) {
//            if (chatsList.get(position + 1).isMine()) {
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 1, 5, 15);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_TOP_RIGHT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.TOP_RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            } else {
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//        } else if (position == chatsList.size() - 1){
//            if (chatsList.get(position - 1).isMine()) {
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 10, 5, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .centerCrop()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_BOTTOM_RIGHT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            } else {
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .centerCrop()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//        } else {
//            if (chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 1, 5, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//            else if(!chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 1, 5, 10);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_TOP_RIGHT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.TOP_RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//            else if(chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 10, 5, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_BOTTOM_RIGHT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//            else if(!chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(0, 10, 5, 10);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//        }
    }

    private void imageBackgroundForNotIsMine(ChatRowAdapter.MyViewHolder holder, int position){
        Chats chatList = chatsList.get(position);

//        if (!chatsList.get(position + 1).isMine()) {
            holder.layout.setGravity(Gravity.START);
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
            marginParams.setMargins(25, 10, 0, 10);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            holder.otherProfilePic.setVisibility(View.VISIBLE);
            holder.layout.setLayoutParams(layoutParams);
            Glide.with(context)
                    .load(chatList.getFileURL())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imageFile);
//        } else {
//            holder.layout.setGravity(Gravity.START);
//            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//            marginParams.setMargins(15, 10, 0, 15);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//            holder.otherProfilePic.setVisibility(View.VISIBLE);
//            holder.layout.setLayoutParams(layoutParams);
//            Glide.with(context)
//                    .load(chatList.getFileURL())
//                    .placeholder(R.drawable.ic_icons_google)
//                    .error(R.drawable.ic_icons_google)
//                    .fitCenter()
//                    .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                            new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(holder.imageFile);
//        }
//
//        Chats chatList = chatsList.get(position);
//
//        holder.messageStatus.setVisibility(View.GONE);
//
//        if (position == 0) {
//            if (!chatsList.get(position + 1).isMine()) {
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 1, 0, 15);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_TOP_LEFT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.TOP_LEFT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            } else {
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 10, 0, 15);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//        } else if (position == chatsList.size() - 1){
//            if (!chatsList.get(position - 1).isMine()) {
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 10, 0, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_BOTTOM_LEFT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            } else {
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 10, 0, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//        } else {
//            if (chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 10, 0, 10);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.LEFT),
//                                new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//            else if(!chatsList.get(position - 1).isMine() && chatsList.get(position + 1).isMine()){
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 1, 0, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_BOTTOM_LEFT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//            else if(chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 1, 0, 10);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.OTHER_TOP_LEFT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.TOP_LEFT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//            else if(!chatsList.get(position - 1).isMine() && !chatsList.get(position + 1).isMine()){
//                holder.layout.setGravity(Gravity.START);
//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(holder.layout.getLayoutParams());
//                marginParams.setMargins(15, 1, 0, 1);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                holder.otherProfilePic.setVisibility(View.VISIBLE);
//                holder.layout.setLayoutParams(layoutParams);
//                Glide.with(context)
//                        .load(chatList.getFileURL())
//                        .placeholder(R.drawable.profileimageplaceholder)
//                        .error(R.drawable.profileimageplaceholder)
//                        .fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.RIGHT),
//                                new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.LEFT))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.imageFile);
//            }
//        }
    }
}

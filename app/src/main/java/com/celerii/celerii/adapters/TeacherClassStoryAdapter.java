package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.GalleryDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Home.Teacher.TeacherCreateClassPostActivity;
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.LikeNotification;
import com.celerii.celerii.models.NotificationModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 5/27/2018.
 */

public class TeacherClassStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ClassStory> classStoryList;
    public Boolean stillLoading;
    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private FirebaseUser mFirebaseUser = auth.getCurrentUser();
    private SharedPreferencesManager sharedPreferencesManager;

    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView poster, classreciepient, timestamp, story, url, noOfLikes, noOfComments;
        public TextView commentPoster, comment, time;
        public ImageView storyimage, profilepic, likebutton, commentbutton, commenterPic, posterPic;
        public LinearLayout commentLayout, createCommentLayout;

        public ImageView storyImageOne, storyImageTwo, storyImageThree, storyImageFour;
        public LinearLayout layoutImageOne, layoutImageTwo, layoutImageThree;
        public LinearLayout storyImageOneContainer, storyImageTwoContainer, storyImageThreeContainer;
        public LinearLayout storyImageOneClipper, storyImageTwoClipper, storyImageThreeClipper, storyImageFourClipper;
        public RelativeLayout layoutImageFour;
        public LinearLayout imageContainer;
        public View moreImagesScrim;
        public TextView moreImagesText;


        public MyViewHolder(final View view) {
            super(view);
            poster = (TextView) view.findViewById(R.id.name);
            classreciepient = (TextView) view.findViewById(R.id.classreciepient);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            story = (TextView) view.findViewById(R.id.txtstory);
            noOfLikes = (TextView) view.findViewById(R.id.likenumber);
            noOfComments = (TextView) view.findViewById(R.id.commentnumber);
            commentPoster = (TextView) view.findViewById(R.id.commentposter);
            comment = (TextView) view.findViewById(R.id.comment);
            time = (TextView) view.findViewById(R.id.time);
//            storyimage = (ImageView) view.findViewById(R.id.storyimage);
            profilepic = (ImageView) view.findViewById(R.id.profilePic);
            likebutton = (ImageView) view.findViewById(R.id.likebutton);
            commentbutton = (ImageView) view.findViewById(R.id.commentbutton);
            commenterPic = (ImageView) view.findViewById(R.id.commenterpic);
            posterPic = (ImageView) view.findViewById(R.id.posterpic);
            commentLayout = (LinearLayout) view.findViewById(R.id.commentlayout);
            createCommentLayout = (LinearLayout) view.findViewById(R.id.createcommentlayout);

            storyImageOne = (ImageView) view.findViewById(R.id.storyimageone);
            storyImageTwo = (ImageView) view.findViewById(R.id.storyimagetwo);
            storyImageThree = (ImageView) view.findViewById(R.id.storyimagethree);
            storyImageFour = (ImageView) view.findViewById(R.id.storyimagefour);
            layoutImageOne = (LinearLayout) view.findViewById(R.id.layoutimageone);
            layoutImageTwo = (LinearLayout) view.findViewById(R.id.layoutimagetwo);
            layoutImageThree = (LinearLayout) view.findViewById(R.id.layoutimagethree);
            layoutImageFour = (RelativeLayout) view.findViewById(R.id.layoutimagefour);
            imageContainer = (LinearLayout) view.findViewById(R.id.imagecontainer);
            moreImagesScrim = view.findViewById(R.id.moreimagesscrim);
            moreImagesText = (TextView) view.findViewById(R.id.moreimagestext);

            storyImageOneContainer = (LinearLayout) view.findViewById(R.id.storyimageonecontainer);
            storyImageTwoContainer = (LinearLayout) view.findViewById(R.id.storyimagetwocontainer);
            storyImageThreeContainer = (LinearLayout) view.findViewById(R.id.storyimagethreecontainer);

            storyImageOneClipper = (LinearLayout) view.findViewById(R.id.storyimageoneclipper);
            storyImageTwoClipper = (LinearLayout) view.findViewById(R.id.storyimagetwoclipper);
            storyImageThreeClipper = (LinearLayout) view.findViewById(R.id.storyimagethreeclipper);
            storyImageFourClipper = (LinearLayout) view.findViewById(R.id.storyimagefourclipper);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView poster;
        ImageView profilePicture;
        LinearLayout createClassStory, chiefLayout;
        RelativeLayout errorLayout;
        TextView errorLayoutText;

        public HeaderViewHolder(View view) {
            super(view);
            profilePicture = (ImageView) view.findViewById(R.id.profilepic);
            createClassStory = (LinearLayout) view.findViewById(R.id.createclassstory);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        LinearLayout footerLayout;

        public FooterViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.paginationprogressbar);
            footerLayout = (LinearLayout) view.findViewById(R.id.footerLayout);
        }
    }

    public TeacherClassStoryAdapter(List<ClassStory> classStoryList, Boolean stillLoading, Context context) {
        this.classStoryList = classStoryList;
        this.stillLoading = stillLoading;
        this.context = context;
        sharedPreferencesManager = new SharedPreferencesManager(this.context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_story_row, parent, false);
                return new TeacherClassStoryAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_story_header, parent, false);
                return new TeacherClassStoryAdapter.HeaderViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_story_footer, parent, false);
                return new TeacherClassStoryAdapter.FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_story_row, parent, false);
                return new TeacherClassStoryAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            if (classStoryList.size() <= 1){
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "You don't have any class stories on your timeline. To post your first class story, tap the " + "<b>" + "What's happening in your class" + "</b>" + " button";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            Glide.with(context)
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((HeaderViewHolder) holder).profilePicture);

            ((HeaderViewHolder) holder).createClassStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherCreateClassPostActivity.class);
                    context.startActivity(I);
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            if (stillLoading) {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            } else {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        } else if (holder instanceof MyViewHolder){
            final ClassStory classStory = classStoryList.get(position);

            final String[] imageArray = classStory.getImageURL().split(" ");
            if (imageArray.length == 1 && imageArray[0].equals("")) {
                ((MyViewHolder) holder).imageContainer.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).storyImageOneClipper.setClipToOutline(true);
                ((MyViewHolder) holder).storyImageTwoClipper.setClipToOutline(true);
                ((MyViewHolder) holder).storyImageThreeClipper.setClipToOutline(true);
                ((MyViewHolder) holder).storyImageFourClipper.setClipToOutline(true);
                if (imageArray.length == 1) {
                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
                    LinearLayout.LayoutParams paramOne = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
                    ((MyViewHolder) holder).storyImageOneContainer.setLayoutParams(paramOne);
                } else if (imageArray.length == 2) {
                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
                    LinearLayout.LayoutParams paramTwo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
                    ((MyViewHolder) holder).storyImageTwoContainer.setLayoutParams(paramTwo);
                } else if (imageArray.length == 3) {
                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((MyViewHolder) holder).storyImageThree);
                    LinearLayout.LayoutParams paramThree = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
                    ((MyViewHolder) holder).storyImageThreeContainer.setLayoutParams(paramThree);
                } else if (imageArray.length == 4) {
                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((MyViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((MyViewHolder) holder).storyImageFour);
                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).moreImagesScrim.setVisibility(View.GONE);
                    ((MyViewHolder) holder).moreImagesText.setVisibility(View.GONE);
                } else {
                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((MyViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((MyViewHolder) holder).storyImageFour);
                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
                    String remainingPictures = "+" + String.valueOf(imageArray.length - 4);
                    ((MyViewHolder) holder).moreImagesText.setText(remainingPictures);
                }

                ((MyViewHolder) holder).storyImageOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putString("URL", imageArray[0]);
                        Intent I = new Intent(context, GalleryDetailActivity.class);
                        I.putExtras(b);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ((MyViewHolder) holder).storyImageOne.setTransitionName("imageTransition");
                            Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).storyImageOne, ((MyViewHolder) holder).storyImageOne.getTransitionName());

                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).storyImageOne, ((MyViewHolder) holder).storyImageOne.getTransitionName());
                            context.startActivity(I, optionsCompat.toBundle());
                        }
                        else {
                            context.startActivity(I);
                        }
                    }
                });

                ((MyViewHolder) holder).storyImageTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putString("URL", imageArray[1]);
                        Intent I = new Intent(context, GalleryDetailActivity.class);
                        I.putExtras(b);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ((MyViewHolder) holder).storyImageTwo.setTransitionName("imageTransition");
                            Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).storyImageTwo, ((MyViewHolder) holder).storyImageTwo.getTransitionName());

                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).storyImageTwo, ((MyViewHolder) holder).storyImageTwo.getTransitionName());
                            context.startActivity(I, optionsCompat.toBundle());
                        }
                        else {
                            context.startActivity(I);
                        }
                    }
                });

                ((MyViewHolder) holder).storyImageThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putString("URL", imageArray[2]);
                        Intent I = new Intent(context, GalleryDetailActivity.class);
                        I.putExtras(b);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ((MyViewHolder) holder).storyImageThree.setTransitionName("imageTransition");
                            Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).storyImageThree, ((MyViewHolder) holder).storyImageThree.getTransitionName());

                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).storyImageThree, ((MyViewHolder) holder).storyImageThree.getTransitionName());
                            context.startActivity(I, optionsCompat.toBundle());
                        }
                        else {
                            context.startActivity(I);
                        }
                    }
                });

                ((MyViewHolder) holder).storyImageFour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("postKey", classStory.getPostID());
                        Intent I = new Intent(context, CommentStoryActivity.class);
                        I.putExtras(bundle);
                        context.startActivity(I);
                    }
                });
            }

            if (classStory.isLiked()) {
                ((MyViewHolder)holder).likebutton.setTag(R.drawable.ic_favorite_black_24dp);
                ((MyViewHolder)holder).likebutton.setImageResource((R.drawable.ic_favorite_black_24dp));
            }else{
                ((MyViewHolder)holder).likebutton.setTag(R.drawable.ic_favorite_border_black_24dp);
                ((MyViewHolder)holder).likebutton.setImageResource((R.drawable.ic_favorite_border_black_24dp));
            }

            ((MyViewHolder)holder).poster.setText(classStory.getPosterName());
            ((MyViewHolder)holder).classreciepient.setText(classStory.getClassReciepient());
            ((MyViewHolder)holder).timestamp.setText(Date.getRelativeTimeSpan(classStory.getDate()));
            ((MyViewHolder)holder).story.setText(classStory.getStory());

            String likes = String.valueOf(classStory.getNoOfLikes());
            ((MyViewHolder)holder).noOfLikes.setText(likes);
            String comments = String.valueOf(classStory.getNumberOfComments());
            ((MyViewHolder)holder).noOfComments.setText(comments);

            Glide.with(context)
                    .load(classStory.getProfilePicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder)holder).profilepic);

            Glide.with(context)
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder)holder).posterPic);

            if (classStory.getComment() != null) {
                ((MyViewHolder) holder).commentLayout.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).commentPoster.setText(classStory.getComment().getPosterName());
                ((MyViewHolder) holder).comment.setText(classStory.getComment().getComment());
                ((MyViewHolder) holder).time.setText(Date.getRelativeTimeSpan(classStory.getComment().getTime()));
                Glide.with(context)
                        .load(classStory.getComment().getPosterPic())
                        .placeholder(R.drawable.profileimageplaceholder)
                        .error(R.drawable.profileimageplaceholder)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).commenterPic);
            } else {
                ((MyViewHolder) holder).commentLayout.setVisibility(View.GONE);
            }

            ((MyViewHolder)holder).poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherProfileOneActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", classStory.getPosterID());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });

            ((MyViewHolder)holder).profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherProfileOneActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", classStory.getPosterID());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });

            ((MyViewHolder)holder).likebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer)((MyViewHolder)holder).likebutton.getTag();
                    if (!classStory.isLiked()) {
                        mDatabaseReference = mFirebaseDatabase.getReference();
                        Map<String, Object> userUpdates = new HashMap<String, Object>();
                        String path = "";
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
                            path = "ClassStoryParentFeed/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID();

                        }else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")){
                            path = "ClassStoryTeacherTimeline/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID();
                        }
                        String time = Date.getDate();
                        String sortableTime = Date.convertToSortableDate(time);
                        boolean isSeen = false;
                        NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), classStory.getPosterID(), "Teacher",
                                sharedPreferencesManager.getActiveAccount(), time, sortableTime, classStory.getPostID(), "Like", "", "", isSeen);

                        userUpdates.put(path, true);
                        userUpdates.put("ClassStoryLike/" + classStory.getPostID() + "/" + auth.getCurrentUser().getUid(), time);
                        userUpdates.put("ClassStoryUserLikeHistory/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID(), true);
                        if (!auth.getCurrentUser().getUid().equals(classStory.getPosterID())){
                            userUpdates.put("ClassStoryLikeNotification/" + classStory.getPosterID() + "/" + classStory.getPostID() + "/" + mFirebaseUser.getUid(), new LikeNotification(auth.getCurrentUser().getUid(), time));
                            userUpdates.put("NotificationTeacher/" + classStory.getPosterID() + "/" + classStory.getPostID() + "_" + mFirebaseUser.getUid(), notificationModel);
                        }
                        mDatabaseReference.updateChildren(userUpdates);
                        DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("ClassStory/" + classStory.getPostID() + "/" + "noOfLikes");
                        updateLikeRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Integer currentValue = mutableData.getValue(Integer.class);
                                if (currentValue == null) {
                                    mutableData.setValue(1);
                                } else {
                                    mutableData.setValue(currentValue + 1);
                                }

                                return Transaction.success(mutableData);

                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                        animateHeart(((MyViewHolder)holder).likebutton);
                        ((MyViewHolder)holder).likebutton.setImageResource((R.drawable.ic_favorite_black_24dp));
                        ((MyViewHolder)holder).likebutton.setTag(R.drawable.ic_favorite_black_24dp);
                        classStory.setLiked(true);

                    }
                    else{
                        mDatabaseReference = mFirebaseDatabase.getReference();
                        Map<String, Object> userUpdates = new HashMap<String, Object>();
                        String path = "";
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
                            path = "ClassStoryParentFeed/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID();

                        }else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")){
                            path = "ClassStoryTeacherTimeline/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID();
                        }
                        userUpdates.put("ClassStoryLike/" + classStory.getPostID() + "/" + auth.getCurrentUser().getUid(), null);
                        userUpdates.put("ClassStoryUserLikeHistory/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID(), null);
                        userUpdates.put(path, false);
                        if (!auth.getCurrentUser().getUid().equals(classStory.getPosterID())) {
                            userUpdates.put("ClassStoryLikeNotification/" + classStory.getPosterID() + "/" + classStory.getPostID(), null);
                        }
                        mDatabaseReference.updateChildren(userUpdates);
                        DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("ClassStory/" + classStory.getPostID() + "/" + "noOfLikes");
                        updateLikeRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Integer currentValue = mutableData.getValue(Integer.class);
                                if (currentValue == null) {
                                    mutableData.setValue(1);
                                } else {
                                    mutableData.setValue(currentValue - 1);
                                }

                                return Transaction.success(mutableData);

                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                        ((MyViewHolder)holder).likebutton.setImageResource((R.drawable.ic_favorite_border_black_24dp));
                        ((MyViewHolder)holder).likebutton.setTag(R.drawable.ic_favorite_border_black_24dp);
                        animateHeart(((MyViewHolder)holder).likebutton);
                        classStory.setLiked(false);

                    }
                }
            });

            ((MyViewHolder) holder).story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("postKey", classStory.getPostID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });

            ((MyViewHolder)holder).commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("postKey", classStory.getPostID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });

            ((MyViewHolder)holder).noOfComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("postKey", classStory.getPostID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });

            ((MyViewHolder)holder).commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("postKey", classStory.getPostID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return classStoryList.size();
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
        return position == classStoryList.size () - 1;
    }

    public void animateHeart(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.15f, 0.0f, 1.15f,
                Animation.RELATIVE_TO_SELF, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(200);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimation(Animation animation){
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    private void loadImageWithGlide(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .into(imageView);
    }
}

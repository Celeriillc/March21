package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.Home.Teacher.TeacherCreateClassPostActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ClassStoryDiffUtil;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.WrapContentViewPager;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.LikeNotification;
import com.celerii.celerii.models.NotificationModel;
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
    private String[] imageArray;

    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView poster, classreciepient, timestamp, story, url, noOfLikes, noOfComments;
        public TextView /*commentPoster,*/ comment, time;
        public ImageView storyimage, profilepic, likebutton, commentbutton; //,commenterPic, posterPic;
//        public LinearLayout commentLayout, firstCommentLayout, createCommentLayout;

//        public ImageView storyImageOne, storyImageTwo, storyImageThree, storyImageFour;
//        public LinearLayout layoutImageOne, layoutImageTwo, layoutImageThree;
//        public LinearLayout storyImageOneContainer, storyImageTwoContainer, storyImageThreeContainer;
//        public LinearLayout storyImageOneClipper, storyImageTwoClipper, storyImageThreeClipper, storyImageFourClipper;
        public LinearLayout profilePictureClipper; //, profilePictureClipper2, commenterPictureClipper;
//        public RelativeLayout layoutImageFour;
//        public LinearLayout imageContainer;
//        public View moreImagesScrim;
//        public TextView moreImagesText;

        public WrapContentViewPager viewPager;
        public MyViewPagerAdapter myViewPagerAdapter;
        public LinearLayout dotsLayout;
        public TextView[] dots;

        public MyViewHolder(final View view) {
            super(view);
            poster = (TextView) view.findViewById(R.id.name);
            classreciepient = (TextView) view.findViewById(R.id.classreciepient);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            story = (TextView) view.findViewById(R.id.txtstory);
            noOfLikes = (TextView) view.findViewById(R.id.likenumber);
            noOfComments = (TextView) view.findViewById(R.id.commentnumber);
//            commentPoster = (TextView) view.findViewById(R.id.commentposter);
//            comment = (TextView) view.findViewById(R.id.comment);
//            time = (TextView) view.findViewById(R.id.time);
//            storyimage = (ImageView) view.findViewById(R.id.storyimage);
            profilepic = (ImageView) view.findViewById(R.id.profilePic);
            likebutton = (ImageView) view.findViewById(R.id.likebutton);
            commentbutton = (ImageView) view.findViewById(R.id.commentbutton);
//            commenterPic = (ImageView) view.findViewById(R.id.commenterpic);
//            posterPic = (ImageView) view.findViewById(R.id.posterpic);
//            commentLayout = (LinearLayout) view.findViewById(R.id.commentlayout);
//            firstCommentLayout = (LinearLayout) view.findViewById(R.id.firstcommentlayout);
//            createCommentLayout = (LinearLayout) view.findViewById(R.id.createcommentlayout);

//            storyImageOne = (ImageView) view.findViewById(R.id.storyimageone);
//            storyImageTwo = (ImageView) view.findViewById(R.id.storyimagetwo);
//            storyImageThree = (ImageView) view.findViewById(R.id.storyimagethree);
//            storyImageFour = (ImageView) view.findViewById(R.id.storyimagefour);
//            layoutImageOne = (LinearLayout) view.findViewById(R.id.layoutimageone);
//            layoutImageTwo = (LinearLayout) view.findViewById(R.id.layoutimagetwo);
//            layoutImageThree = (LinearLayout) view.findViewById(R.id.layoutimagethree);
//            layoutImageFour = (RelativeLayout) view.findViewById(R.id.layoutimagefour);
//            imageContainer = (LinearLayout) view.findViewById(R.id.imagecontainer);
//            moreImagesScrim = view.findViewById(R.id.moreimagesscrim);
//            moreImagesText = (TextView) view.findViewById(R.id.moreimagestext);

//            storyImageOneContainer = (LinearLayout) view.findViewById(R.id.storyimageonecontainer);
//            storyImageTwoContainer = (LinearLayout) view.findViewById(R.id.storyimagetwocontainer);
//            storyImageThreeContainer = (LinearLayout) view.findViewById(R.id.storyimagethreecontainer);

//            storyImageOneClipper = (LinearLayout) view.findViewById(R.id.storyimageoneclipper);
//            storyImageTwoClipper = (LinearLayout) view.findViewById(R.id.storyimagetwoclipper);
//            storyImageThreeClipper = (LinearLayout) view.findViewById(R.id.storyimagethreeclipper);
//            storyImageFourClipper = (LinearLayout) view.findViewById(R.id.storyimagefourclipper);

            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
//            profilePictureClipper2 = (LinearLayout) view.findViewById(R.id.profilepictureclipper2);
//            commenterPictureClipper = (LinearLayout) view.findViewById(R.id.commenterpictureclipper);

            viewPager = (WrapContentViewPager) view.findViewById(R.id.view_pager);
            dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView poster;
        ImageView profilePicture;
        LinearLayout createClassStory, chiefLayout, profilePictureClipper;
        RelativeLayout errorLayout;
        TextView errorLayoutText;

        public HeaderViewHolder(View view) {
            super(view);
            profilePicture = (ImageView) view.findViewById(R.id.profilepic);
            createClassStory = (LinearLayout) view.findViewById(R.id.createclassstory);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
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
            ((HeaderViewHolder) holder).createClassStory.setVisibility(View.GONE);
            ((HeaderViewHolder) holder).profilePictureClipper.setClipToOutline(true);
            if (classStoryList.size() <= 1){
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "You don't have any class stories on your timeline. To post your first class story, tap the " + "<b>" + "What's happening in your class" + "</b>" + " button";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            Drawable textDrawable;
            String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
            if (!myName.trim().isEmpty()) {
                String[] nameArray = myName.replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 35);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 35);
                }
                ((HeaderViewHolder) holder).profilePicture.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 35);
            }

            if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
                Glide.with(context)
                        .load(sharedPreferencesManager.getMyPicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((HeaderViewHolder) holder).profilePicture);
            }

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
            ((MyViewHolder) holder).dots = new TextView[imageArray.length];

            if (imageArray.length <= 1 && imageArray[0].equals("")) {
                ((MyViewHolder) holder).viewPager.setVisibility(View.GONE);
                ((MyViewHolder) holder).dotsLayout.setVisibility(View.GONE);
            }
            else {
                if (imageArray.length > 1) {
                    ((MyViewHolder) holder).dotsLayout.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).dotsLayout.setVisibility(View.GONE);
                }
                ((MyViewHolder) holder).viewPager.setVisibility(View.VISIBLE);

                ((MyViewHolder) holder).dotsLayout.removeAllViews();
                for (int i = 0; i < ((MyViewHolder) holder).dots.length; i++) {
                    ((MyViewHolder) holder).dots[i] = new TextView(context);
                    ((MyViewHolder) holder).dots[i].setText(Html.fromHtml("&#8226;"));
                    ((MyViewHolder) holder).dots[i].setTextSize(20);
                    ((MyViewHolder) holder).dots[i].setTextColor(ContextCompat.getColor(context, R.color.colorLightGray));
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(5, 0, 5, 0); //(left, top, right, bottom);
                    ((MyViewHolder) holder).dots[i].setLayoutParams(llp);
                    ((MyViewHolder) holder).dotsLayout.addView(((MyViewHolder) holder).dots[i]);
                }

                if (((MyViewHolder) holder).dots.length > 0) {
                    ((MyViewHolder) holder).dots[((MyViewHolder) holder).viewPager.getCurrentItem()].setTextSize(25);
                    ((MyViewHolder) holder).dots[((MyViewHolder) holder).viewPager.getCurrentItem()].setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                }

                ((MyViewHolder) holder).myViewPagerAdapter = new MyViewPagerAdapter(context, imageArray);
                ((MyViewHolder) holder).viewPager.setAdapter(((MyViewHolder) holder).myViewPagerAdapter);
                ((MyViewHolder) holder).viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int innerPosition) {
                        ((MyViewHolder) holder).dotsLayout.removeAllViews();
                        for (int i = 0; i < ((MyViewHolder) holder).dots.length; i++) {
                            ((MyViewHolder) holder).dots[i] = new TextView(context);
                            ((MyViewHolder) holder).dots[i].setText(Html.fromHtml("&#8226;"));
                            ((MyViewHolder) holder).dots[i].setTextSize(20);
                            ((MyViewHolder) holder).dots[i].setTextColor(ContextCompat.getColor(context, R.color.colorLightGray));
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(5, 0, 5, 0); //(left, top, right, bottom);
                            ((MyViewHolder) holder).dots[i].setLayoutParams(llp);
                            ((MyViewHolder) holder).dotsLayout.addView(((MyViewHolder) holder).dots[i]);
                        }

                        if (((MyViewHolder) holder).dots.length > 0) {
                            ((MyViewHolder) holder).dots[innerPosition].setTextSize(25);
                            ((MyViewHolder) holder).dots[innerPosition].setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {

                    }
                });
            }

//            if (imageArray.length == 1 && imageArray[0].equals("")) {
//                ((MyViewHolder) holder).imageContainer.setVisibility(View.GONE);
//            } else {
//                ((MyViewHolder) holder).storyImageOneClipper.setClipToOutline(true);
//                ((MyViewHolder) holder).storyImageTwoClipper.setClipToOutline(true);
//                ((MyViewHolder) holder).storyImageThreeClipper.setClipToOutline(true);
//                ((MyViewHolder) holder).storyImageFourClipper.setClipToOutline(true);
//                if (imageArray.length == 1) {
//                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
//                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
//                    LinearLayout.LayoutParams paramOne = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
//                    ((MyViewHolder) holder).storyImageOneContainer.setLayoutParams(paramOne);
//                } else if (imageArray.length == 2) {
//                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
//                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
//                    LinearLayout.LayoutParams paramTwo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
//                    ((MyViewHolder) holder).storyImageTwoContainer.setLayoutParams(paramTwo);
//                } else if (imageArray.length == 3) {
//                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
//                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((MyViewHolder) holder).storyImageThree);
//                    LinearLayout.LayoutParams paramThree = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
//                    ((MyViewHolder) holder).storyImageThreeContainer.setLayoutParams(paramThree);
//                } else if (imageArray.length == 4) {
//                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((MyViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((MyViewHolder) holder).storyImageFour);
//                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
//                    ((MyViewHolder) holder).moreImagesScrim.setVisibility(View.GONE);
//                    ((MyViewHolder) holder).moreImagesText.setVisibility(View.GONE);
//                } else {
//                    loadImageWithGlide(imageArray[0], ((MyViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((MyViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((MyViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((MyViewHolder) holder).storyImageFour);
//                    ((MyViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
//                    String remainingPictures = "+" + String.valueOf(imageArray.length - 4);
//                    ((MyViewHolder) holder).moreImagesText.setText(remainingPictures);
//                }
//
//                ((MyViewHolder) holder).storyImageOne.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle b = new Bundle();
//                        b.putString("URL", imageArray[0]);
//                        Intent I = new Intent(context, GalleryDetailForMultipleImagesActivity.class);
//                        I.putExtras(b);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            ((MyViewHolder) holder).storyImageOne.setTransitionName("imageTransition");
//                            Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).storyImageOne, ((MyViewHolder) holder).storyImageOne.getTransitionName());
//
//                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).storyImageOne, ((MyViewHolder) holder).storyImageOne.getTransitionName());
//                            context.startActivity(I, optionsCompat.toBundle());
//                        }
//                        else {
//                            context.startActivity(I);
//                        }
//                    }
//                });
//
//                ((MyViewHolder) holder).storyImageTwo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle b = new Bundle();
//                        b.putString("URL", imageArray[1]);
//                        Intent I = new Intent(context, GalleryDetailForMultipleImagesActivity.class);
//                        I.putExtras(b);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            ((MyViewHolder) holder).storyImageTwo.setTransitionName("imageTransition");
//                            Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).storyImageTwo, ((MyViewHolder) holder).storyImageTwo.getTransitionName());
//
//                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).storyImageTwo, ((MyViewHolder) holder).storyImageTwo.getTransitionName());
//                            context.startActivity(I, optionsCompat.toBundle());
//                        }
//                        else {
//                            context.startActivity(I);
//                        }
//                    }
//                });
//
//                ((MyViewHolder) holder).storyImageThree.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle b = new Bundle();
//                        b.putString("URL", imageArray[2]);
//                        Intent I = new Intent(context, GalleryDetailForMultipleImagesActivity.class);
//                        I.putExtras(b);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            ((MyViewHolder) holder).storyImageThree.setTransitionName("imageTransition");
//                            Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).storyImageThree, ((MyViewHolder) holder).storyImageThree.getTransitionName());
//
//                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).storyImageThree, ((MyViewHolder) holder).storyImageThree.getTransitionName());
//                            context.startActivity(I, optionsCompat.toBundle());
//                        }
//                        else {
//                            context.startActivity(I);
//                        }
//                    }
//                });
//
//                ((MyViewHolder) holder).storyImageFour.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("postKey", classStory.getPostID());
//                        Intent I = new Intent(context, CommentStoryActivity.class);
//                        I.putExtras(bundle);
//                        context.startActivity(I);
//                    }
//                });
//            }

            if (classStory.isLiked()) {
                ((MyViewHolder)holder).likebutton.setTag(R.drawable.ic_like_filled);
                ((MyViewHolder)holder).likebutton.setImageResource((R.drawable.ic_like_filled));
            } else {
                ((MyViewHolder)holder).likebutton.setTag(R.drawable.ic_like);
                ((MyViewHolder)holder).likebutton.setImageResource((R.drawable.ic_like));
            }

            ((MyViewHolder)holder).poster.setText(classStory.getPosterName());
            ((MyViewHolder)holder).classreciepient.setText(classStory.getClassReciepient());
            ((MyViewHolder)holder).timestamp.setText(Date.getRelativeTimeSpan(classStory.getDate()));
            ((MyViewHolder)holder).story.setText(classStory.getStory());
            ((MyViewHolder)holder).profilePictureClipper.setClipToOutline(true);
//            ((MyViewHolder)holder).profilePictureClipper2.setClipToOutline(true);
//            ((MyViewHolder)holder).commenterPictureClipper.setClipToOutline(true);
            if (classStory.getStory().equals("")) {
                ((MyViewHolder)holder).story.setVisibility(View.GONE);
            } else {
                ((MyViewHolder)holder).story.setVisibility(View.VISIBLE);
            }

            String likes = String.valueOf(classStory.getNoOfLikes());
            String likeString = likes + " Likes";
            ((MyViewHolder) holder).noOfLikes.setText(likeString);
            String comments = String.valueOf(classStory.getNumberOfComments());
            String commentString = comments + " Comments";
            ((MyViewHolder) holder).noOfComments.setText(commentString);

            Drawable textDrawable;
            if (!classStory.getPosterName().isEmpty()) {
                String[] nameArray = classStory.getPosterName().replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 50);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 50);
                }
                ((MyViewHolder) holder).profilepic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 50);
            }

            if (!classStory.getProfilePicURL().isEmpty()) {
                Glide.with(context)
                        .load(classStory.getProfilePicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).profilepic);
            }

//            String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
//            if (!myName.trim().equals("")) {
//                String[] nameArray = myName.replaceAll("\\s+", " ").split(" ");
////                Drawable textDrawable;
//                if (nameArray.length == 1) {
//                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
//                } else {
//                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
//                }
//                ((MyViewHolder) holder).posterPic.setImageDrawable(textDrawable);
//            } else {
//                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
//            }
//
//            if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
//                Glide.with(context)
//                        .load(sharedPreferencesManager.getMyPicURL())
//                        .placeholder(textDrawable)
//                        .error(textDrawable)
//                        .centerCrop()
//                        .bitmapTransform(new CropCircleTransformation(context))
//                        .into(((MyViewHolder) holder).posterPic);
//            }

//            if (classStory.getComment() != null) {
//                ((MyViewHolder) holder).firstCommentLayout.setVisibility(View.VISIBLE);
//                ((MyViewHolder) holder).commentPoster.setText(classStory.getComment().getPosterName());
//                ((MyViewHolder) holder).comment.setText(classStory.getComment().getComment());
//                ((MyViewHolder) holder).time.setText(Date.getRelativeTimeSpan(classStory.getComment().getTime()));
//
//                if (!classStory.getComment().getPosterName().isEmpty()) {
//                    String[] nameArray = classStory.getComment().getPosterName().replaceAll("\\s+", " ").split(" ");
//                    if (nameArray.length == 1) {
//                        textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
//                    } else {
//                        textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
//                    }
//                    ((MyViewHolder) holder).commenterPic.setImageDrawable(textDrawable);
//                } else {
//                    textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
//                }
//
//                if (!classStory.getComment().getPosterPic().isEmpty()) {
//                    Glide.with(context)
//                            .load(classStory.getComment().getPosterPic())
//                            .placeholder(textDrawable)
//                            .error(textDrawable)
//                            .centerCrop()
//                            .bitmapTransform(new CropCircleTransformation(context))
//                            .into(((MyViewHolder) holder).commenterPic);
//                }
//            } else {
//                ((MyViewHolder) holder).firstCommentLayout.setVisibility(View.GONE);
//            }

            ((MyViewHolder)holder).poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (classStory.getPosterAccountType().equals("School")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("schoolID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else if (classStory.getPosterAccountType().equals("Teacher")) {
                        Intent I = new Intent(context, TeacherProfileOneActivity.class);
                        Bundle b = new Bundle();
                        b.putString("ID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }

                }
            });

            ((MyViewHolder)holder).profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (classStory.getPosterAccountType().equals("School")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("schoolID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else if (classStory.getPosterAccountType().equals("Teacher")) {
                        Intent I = new Intent(context, TeacherProfileOneActivity.class);
                        Bundle b = new Bundle();
                        b.putString("ID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });

            ((MyViewHolder)holder).likebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer)((MyViewHolder)holder).likebutton.getTag();
                    if (!classStory.getPosterAccountType().equals("Admin")) {
                        if (!classStory.isLiked()) {
                            mDatabaseReference = mFirebaseDatabase.getReference();
                            Map<String, Object> userUpdates = new HashMap<String, Object>();
                            String time = Date.getDate();
                            String sortableTime = Date.convertToSortableDate(time);
                            boolean isSeen = false;
                            NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), classStory.getPosterID(), classStory.getPosterAccountType(),
                                    sharedPreferencesManager.getActiveAccount(), time, sortableTime, classStory.getPostID(), "Like", "", classStory.getClassReciepient(), isSeen);

                            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                                userUpdates.put("ClassStoryParentFeed/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), true);
                            } else {
                                userUpdates.put("ClassStoryTeacherFeed/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), true);
                                if (mFirebaseUser.getUid().equals(classStory.getPosterID())) {
                                    userUpdates.put("ClassStoryTeacherTimeline/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), true);
                                }
                            }

                            userUpdates.put("ClassStoryLike/" + classStory.getPostID() + "/" + mFirebaseUser.getUid(), time);
                            userUpdates.put("ClassStoryUserLikeHistory/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), true);
                            if (!mFirebaseUser.getUid().equals(classStory.getPosterID())) {
//                                userUpdates.put("ClassStoryLikeNotification/" + classStory.getPosterID() + "/" + classStory.getPostID() + "/" + mFirebaseUser.getUid(), new LikeNotification(auth.getCurrentUser().getUid(), time));
                                if (classStory.getPosterAccountType().equals("Teacher")) {
                                    userUpdates.put("NotificationTeacher/" + classStory.getPosterID() + "/" + classStory.getPostID() + "_" + mFirebaseUser.getUid(), notificationModel);
                                } else if (classStory.getPosterAccountType().equals("School")) {
                                    userUpdates.put("NotificationSchool/" + classStory.getPosterID() + "/" + classStory.getPostID() + "_" + mFirebaseUser.getUid(), notificationModel);
                                }
                            }
                            mDatabaseReference.updateChildren(userUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                                    if (databaseError != null) {

                                    }
                                }
                            });
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
                            animateHeart(((MyViewHolder) holder).likebutton);
                            ((MyViewHolder) holder).likebutton.setImageResource((R.drawable.ic_like_filled));
                            ((MyViewHolder) holder).likebutton.setTag(R.drawable.ic_like_filled);
                            classStory.setLiked(true);

                        } else {
                            mDatabaseReference = mFirebaseDatabase.getReference();
                            Map<String, Object> userUpdates = new HashMap<String, Object>();

                            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                                userUpdates.put("ClassStoryParentFeed/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), false);
                            } else {
                                userUpdates.put("ClassStoryTeacherFeed/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), false);
                                if (mFirebaseUser.getUid().equals(classStory.getPosterID())) {
                                    userUpdates.put("ClassStoryTeacherTimeline/" + mFirebaseUser.getUid() + "/" + classStory.getPostID(), false);
                                }
                            }

                            userUpdates.put("ClassStoryLike/" + classStory.getPostID() + "/" + mFirebaseUser.getUid(), null);
                            userUpdates.put("ClassStoryUserLikeHistory/" + auth.getCurrentUser().getUid() + "/" + classStory.getPostID(), null);
                            if (!auth.getCurrentUser().getUid().equals(classStory.getPosterID())) {
//                                userUpdates.put("ClassStoryLikeNotification/" + classStory.getPosterID() + "/" + classStory.getPostID(), null);
                                if (classStory.getPosterAccountType().equals("Teacher")) {
                                    userUpdates.put("NotificationTeacher/" + classStory.getPosterID() + "/" + classStory.getPostID() + "_" + mFirebaseUser.getUid(), null);
                                } else if (classStory.getPosterAccountType().equals("School")) {
                                    userUpdates.put("NotificationSchool/" + classStory.getPosterID() + "/" + classStory.getPostID() + "_" + mFirebaseUser.getUid(), null);
                                }
                            }
                            mDatabaseReference.updateChildren(userUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                                    if (databaseError != null) {

                                    }
                                }
                            });
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
                            ((MyViewHolder) holder).likebutton.setImageResource((R.drawable.ic_like));
                            ((MyViewHolder) holder).likebutton.setTag(R.drawable.ic_like);
                            animateHeart(((MyViewHolder) holder).likebutton);
                            classStory.setLiked(false);

                        }
                    } else {
                        CustomToast.primaryBackgroundToast(context, "Likes for this post are turned off");
                    }
                }
            });

            ((MyViewHolder) holder).story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!classStory.getPosterAccountType().equals("Admin")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("postKey", classStory.getPostID());
                        Intent I = new Intent(context, CommentStoryActivity.class);
                        I.putExtras(bundle);
                        context.startActivity(I);
                    } else {
                        CustomToast.primaryBackgroundToast(context, "Comments for this post are turned off");
                    }
                }
            });

            ((MyViewHolder)holder).commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!classStory.getPosterAccountType().equals("Admin")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("postKey", classStory.getPostID());
                        Intent I = new Intent(context, CommentStoryActivity.class);
                        I.putExtras(bundle);
                        context.startActivity(I);
                    } else {
                        CustomToast.primaryBackgroundToast(context, "Comments for this post are turned off");
                    }
                }
            });

            ((MyViewHolder)holder).noOfComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!classStory.getPosterAccountType().equals("Admin")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("postKey", classStory.getPostID());
                        Intent I = new Intent(context, CommentStoryActivity.class);
                        I.putExtras(bundle);
                        context.startActivity(I);
                    } else {
                        CustomToast.primaryBackgroundToast(context, "Comments for this post are turned off");
                    }
                }
            });

//            ((MyViewHolder)holder).commentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!classStory.getPosterAccountType().equals("Admin")) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("postKey", classStory.getPostID());
//                        Intent I = new Intent(context, CommentStoryActivity.class);
//                        I.putExtras(bundle);
//                        context.startActivity(I);
//                    } else {
//                        CustomToast.primaryBackgroundToast(context, "Comments for this post are turned off");
//                    }
//                }
//            });
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

    //View pager adapter
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Context context;
        private String[] imageURLs;

        public MyViewPagerAdapter(Context context, String[] imageURLs) {
            this.context = context;
            this.imageURLs = imageURLs;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);

            Glide.with(context)
                    .load(imageURLs[position])
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(imageView);

            container.addView(imageView);

            return imageView;
        }

        @Override
        public int getCount() {
            return imageURLs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void updateClassStoryListItems(List<ClassStory> classStoryNewList) {
        final ClassStoryDiffUtil diffCallback = new ClassStoryDiffUtil(this.classStoryList, classStoryNewList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.classStoryList.clear();
        this.classStoryList.addAll(classStoryNewList);
        diffResult.dispatchUpdatesTo(this);
    }
}

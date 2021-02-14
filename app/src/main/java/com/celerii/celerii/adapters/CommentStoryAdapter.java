package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.WrapContentViewPager;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.Comment;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 7/4/2017.
 */

public class CommentStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Comment> commentList;
    private ClassStory classStory;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView poster, time, comment;
        public ImageView posterPic;
        public LinearLayout picLayout;

        public MyViewHolder(final View view) {
            super(view);
            posterPic = (ImageView) view.findViewById(R.id.commenterpic);
            poster = (TextView) view.findViewById(R.id.commentposter);
            comment = (TextView) view.findViewById(R.id.comment);
            time = (TextView) view.findViewById(R.id.time);
            picLayout = (LinearLayout) view.findViewById(R.id.piclayout);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView poster, posttime, rclass, classstory, urlLink, noOfLikes, noOfComments;
        ImageView posterPic;

        HorizontalScrollView imageContainer;
        LinearLayout picLayout;

        public WrapContentViewPager viewPager;
        public MyViewPagerAdapter myViewPagerAdapter;
        public LinearLayout dotsLayout;
        public TextView[] dots;

        public HeaderViewHolder(View view) {
            super(view);
            poster = (TextView) view.findViewById(R.id.name);
            posttime = (TextView) view.findViewById(R.id.timestamp);
            rclass = (TextView) view.findViewById(R.id.classreciepient);
            classstory = (TextView) view.findViewById(R.id.txtstory);
            urlLink = (TextView) view.findViewById(R.id.txtUrl);
            noOfLikes = (TextView) view.findViewById(R.id.likenumber);
            noOfComments = (TextView) view.findViewById(R.id.commentnumber);
            posterPic = (ImageView) view.findViewById(R.id.profilePic);

            picLayout = (LinearLayout) view.findViewById(R.id.piclayout);

            viewPager = (WrapContentViewPager) view.findViewById(R.id.view_pager);
            dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);
        }
    }

    public CommentStoryAdapter(List<Comment> commentList, ClassStory classStory, Context context) {
        this.commentList = commentList;
        this.classStory = classStory;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_story_row, parent, false);
                return new CommentStoryAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_story_row_header, parent, false);
                return new CommentStoryAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_story_row, parent, false);
                return new CommentStoryAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){
            final ClassStory classStory = this.classStory;

            ((HeaderViewHolder) holder).poster.setText(classStory.getPosterName());
            ((HeaderViewHolder) holder).posttime.setText(Date.getRelativeTimeSpan(classStory.getDate()));
            ((HeaderViewHolder) holder).rclass.setText(classStory.getClassReciepient());
            String likes = classStory.getNoOfLikes() + " Likes";
            ((HeaderViewHolder) holder).noOfLikes.setText(likes);
            String comments = classStory.getNumberOfComments() + " Comments";
            ((HeaderViewHolder) holder).noOfComments.setText(comments);
            ((HeaderViewHolder) holder).classstory.setText(classStory.getStory());
            ((HeaderViewHolder) holder).urlLink.setVisibility(View.GONE);
            ((HeaderViewHolder) holder).picLayout.setClipToOutline(true);
            if (classStory.getStory().equals("")) {
                ((HeaderViewHolder)holder).classstory.setVisibility(View.GONE);
            } else {
                ((HeaderViewHolder)holder).classstory.setVisibility(View.VISIBLE);
            }

            String[] imageArray = classStory.getImageURL().split(" ");
            ((HeaderViewHolder) holder).dots = new TextView[imageArray.length];

            if (imageArray.length <= 1 && imageArray[0].equals("")) {
                ((HeaderViewHolder) holder).viewPager.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).dotsLayout.setVisibility(View.GONE);
            } else {
                if (imageArray.length > 1) {
                    ((HeaderViewHolder) holder).dotsLayout.setVisibility(View.VISIBLE);
                } else {
                    ((HeaderViewHolder) holder).dotsLayout.setVisibility(View.GONE);
                }
                ((HeaderViewHolder) holder).viewPager.setVisibility(View.VISIBLE);

                ((HeaderViewHolder) holder).dotsLayout.removeAllViews();
                for (int i = 0; i < ((HeaderViewHolder) holder).dots.length; i++) {
                    ((HeaderViewHolder) holder).dots[i] = new TextView(context);
                    ((HeaderViewHolder) holder).dots[i].setText(Html.fromHtml("&#8226;"));
                    ((HeaderViewHolder) holder).dots[i].setTextSize(20);
                    ((HeaderViewHolder) holder).dots[i].setTextColor(ContextCompat.getColor(context, R.color.colorLightGray));
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(5, 0, 5, 0); //(left, top, right, bottom);
                    ((HeaderViewHolder) holder).dots[i].setLayoutParams(llp);
                    ((HeaderViewHolder) holder).dotsLayout.addView(((HeaderViewHolder) holder).dots[i]);
                }

                if (((HeaderViewHolder) holder).dots.length > 0) {
                    ((HeaderViewHolder) holder).dots[((HeaderViewHolder) holder).viewPager.getCurrentItem()].setTextSize(25);
                    ((HeaderViewHolder) holder).dots[((HeaderViewHolder) holder).viewPager.getCurrentItem()].setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                }

                ((HeaderViewHolder) holder).myViewPagerAdapter = new MyViewPagerAdapter(context, imageArray);
                ((HeaderViewHolder) holder).viewPager.setAdapter(((HeaderViewHolder) holder).myViewPagerAdapter);
                ((HeaderViewHolder) holder).viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int innerPosition) {
                        ((HeaderViewHolder) holder).dotsLayout.removeAllViews();
                        for (int i = 0; i < ((HeaderViewHolder) holder).dots.length; i++) {
                            ((HeaderViewHolder) holder).dots[i] = new TextView(context);
                            ((HeaderViewHolder) holder).dots[i].setText(Html.fromHtml("&#8226;"));
                            ((HeaderViewHolder) holder).dots[i].setTextSize(20);
                            ((HeaderViewHolder) holder).dots[i].setTextColor(ContextCompat.getColor(context, R.color.colorLightGray));
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(5, 0, 5, 0); //(left, top, right, bottom);
                            ((HeaderViewHolder) holder).dots[i].setLayoutParams(llp);
                            ((HeaderViewHolder) holder).dotsLayout.addView(((HeaderViewHolder) holder).dots[i]);
                        }

                        if (((HeaderViewHolder) holder).dots.length > 0) {
                            ((HeaderViewHolder) holder).dots[innerPosition].setTextSize(25);
                            ((HeaderViewHolder) holder).dots[innerPosition].setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {

                    }
                });

//                ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
//
//                    @Override
//                    public void onPageSelected(int innerPosition) {
//                        ((MyViewHolder) holder).viewPager.reMeasureCurrentPage(((MyViewHolder) holder).viewPager.getCurrentItem());
//                        imageArray = classStory.getImageURL().split(" ");
//                        ((MyViewHolder) holder).dots = new TextView[imageArray.length];
//
//                        ((MyViewHolder) holder).dotsLayout.removeAllViews();
//                        for (int i = 0; i < ((MyViewHolder) holder).dots.length; i++) {
//                            ((MyViewHolder) holder).dots[i] = new TextView(context);
//                            ((MyViewHolder) holder).dots[i].setText(Html.fromHtml("&#8226;"));
//                            ((MyViewHolder) holder).dots[i].setTextSize(20);
//                            ((MyViewHolder) holder).dots[i].setTextColor(ContextCompat.getColor(context, R.color.colorLightGray));
//                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            llp.setMargins(5, 0, 5, 0); //(left, top, right, bottom);
//                            ((MyViewHolder) holder).dots[i].setLayoutParams(llp);
//                            ((MyViewHolder) holder).dotsLayout.addView(((MyViewHolder) holder).dots[i]);
//                        }
//
//                        if (((MyViewHolder) holder).dots.length > 0) {
//                            ((MyViewHolder) holder).dots[innerPosition].setTextSize(25);
//                            ((MyViewHolder) holder).dots[innerPosition].setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
//                        }
//                    }
//
//                    @Override
//                    public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int arg0) {
//
//                    }
//                };
            }

//            final String[] imageArray = classStory.getImageURL().split(" ");
//            if (imageArray.length == 1 && imageArray[0].equals("")) {
//                ((HeaderViewHolder) holder).imageContainer.setVisibility(View.GONE);
//            } else {
//                ((HeaderViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
//                ((HeaderViewHolder) holder).imageLayoutOne.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutTwo.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutThree.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutFour.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutFive.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutSix.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutSeven.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutEight.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutNine.setClipToOutline(true);
//                ((HeaderViewHolder) holder).imageLayoutTen.setClipToOutline(true);
//
//                if (imageArray.length == 1) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 2) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 3) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 4) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 5) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 6) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 7) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
//                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 8) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
//                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
//                    loadImageWithGlide(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutEight.setVisibility(View.VISIBLE);
//                } else if (imageArray.length == 9) {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
//                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
//                    loadImageWithGlide(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
//                    loadImageWithGlide(imageArray[8], ((HeaderViewHolder) holder).storyImageNine);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutEight.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutNine.setVisibility(View.VISIBLE);
//                } else {
//                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
//                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
//                    loadImageWithGlide(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
//                    loadImageWithGlide(imageArray[8], ((HeaderViewHolder) holder).storyImageNine);
//                    loadImageWithGlide(imageArray[9], ((HeaderViewHolder) holder).storyImageTen);
//                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutEight.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutNine.setVisibility(View.VISIBLE);
//                    ((HeaderViewHolder) holder).imageLayoutTen.setVisibility(View.VISIBLE);
//                }
//
//                ((HeaderViewHolder) holder).storyImageOne.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageTwo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageThree.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageFour.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageFive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageSix.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageSeven.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageEight.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageNine.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[8], ((HeaderViewHolder) holder).storyImageNine);
//                    }
//                });
//
//                ((HeaderViewHolder) holder).storyImageTen.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showImage(imageArray[9], ((HeaderViewHolder) holder).storyImageTen);
//                    }
//                });
//            }

            Drawable textDrawable;
            if (!classStory.getPosterName().isEmpty()) {
                String[] nameArray = classStory.getPosterName().replaceAll("\\s+", " ").split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                ((HeaderViewHolder) holder).posterPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            if (!classStory.getProfilePicURL().isEmpty()) {
                Glide.with(context)
                        .load(classStory.getProfilePicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((HeaderViewHolder) holder).posterPic);
            }

//            if (!classStory.getImageURL().isEmpty()){
//                Glide.with(context)
//                        .load(classStory.getImageURL())
//                        .centerCrop()
//                        .into(((HeaderViewHolder) holder).storyimage);
//                        ((HeaderViewHolder) holder).storyimage.setVisibility(View.VISIBLE);
//            }
//            else {
//                ((HeaderViewHolder) holder).storyimage.setVisibility(View.GONE);
//            }

            ((HeaderViewHolder) holder).poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (classStory.getPosterAccountType().equals("School")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("schoolID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherProfileOneActivity.class);
                        Bundle b = new Bundle();
                        b.putString("ID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });

            ((HeaderViewHolder) holder).posterPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (classStory.getPosterAccountType().equals("School")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("schoolID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherProfileOneActivity.class);
                        Bundle b = new Bundle();
                        b.putString("ID", classStory.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });
        }
        else if (holder instanceof MyViewHolder){
            final Comment comment = commentList.get(position);
            final String commenterAccountType = comment.getAccountType();

            ((MyViewHolder) holder).poster.setText(comment.getPosterName());
            ((MyViewHolder) holder).time.setText(Date.getRelativeTimeSpan(comment.getTime()));
            ((MyViewHolder) holder).comment.setText(comment.getComment());
            ((MyViewHolder) holder).picLayout.setClipToOutline(true);

            Drawable textDrawable;
            if (!comment.getPosterName().isEmpty()) {
                String[] nameArray = comment.getPosterName().replaceAll("\\s+", " ").split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                ((MyViewHolder) holder).posterPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            if (!comment.getPosterPic().isEmpty()) {
                Glide.with(context)
                        .load(comment.getPosterPic())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).posterPic);
            }


            ((MyViewHolder) holder).posterPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commenterAccountType.equals("School")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("schoolID", comment.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else if (commenterAccountType.equals("Parent")){
                        Intent I = new Intent(context, ParentProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("parentID", comment.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherProfileOneActivity.class);
                        Bundle b = new Bundle();
                        b.putString("ID", comment.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });

            ((MyViewHolder) holder).poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commenterAccountType.equals("School")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("schoolID", comment.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else if (commenterAccountType.equals("Parent")){
                        Intent I = new Intent(context, ParentProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("parentID", comment.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherProfileOneActivity.class);
                        Bundle b = new Bundle();
                        b.putString("ID", comment.getPosterID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
        return position == commentList.size () + 1;
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

            Glide.with(context)
                    .load(imageURLs[position])
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(imageView);

//            View view = layoutInflater.inflate(layouts[position], container, false);
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
}

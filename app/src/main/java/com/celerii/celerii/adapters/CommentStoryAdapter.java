package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.GalleryDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.helperClasses.Date;
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

        public MyViewHolder(final View view) {
            super(view);
            posterPic = (ImageView) view.findViewById(R.id.commenterpic);
            poster = (TextView) view.findViewById(R.id.commentposter);
            comment = (TextView) view.findViewById(R.id.comment);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView poster, posttime, rclass, classstory, urlLink, noOfLikes, noOfComments;
        ImageView posterPic, storyimage;

        HorizontalScrollView imageContainer;
        LinearLayout imageLayoutOne, imageLayoutTwo, imageLayoutThree, imageLayoutFour, imageLayoutFive, imageLayoutSix, imageLayoutSeven, imageLayoutEight, imageLayoutNine, imageLayoutTen;
        ImageView storyImageOne, storyImageTwo, storyImageThree, storyImageFour, storyImageFive, storyImageSix, storyImageSeven, storyImageEight, storyImageNine, storyImageTen;

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
            storyimage = (ImageView) view.findViewById(R.id.storyimage);

            imageContainer = (HorizontalScrollView) view.findViewById(R.id.imagecontainer);

            imageLayoutOne = (LinearLayout) view.findViewById(R.id.imagelayoutone);
            imageLayoutTwo = (LinearLayout) view.findViewById(R.id.imagelayouttwo);
            imageLayoutThree = (LinearLayout) view.findViewById(R.id.imagelayoutthree);
            imageLayoutFour = (LinearLayout) view.findViewById(R.id.imagelayoutfour);
            imageLayoutFive = (LinearLayout) view.findViewById(R.id.imagelayoutfive);
            imageLayoutSix = (LinearLayout) view.findViewById(R.id.imagelayoutsix);
            imageLayoutSeven = (LinearLayout) view.findViewById(R.id.imagelayoutseven);
            imageLayoutEight = (LinearLayout) view.findViewById(R.id.imagelayouteight);
            imageLayoutNine = (LinearLayout) view.findViewById(R.id.imagelayoutnine);
            imageLayoutTen = (LinearLayout) view.findViewById(R.id.imagelayoutten);

            storyImageOne = (ImageView) view.findViewById(R.id.storyimageone);
            storyImageTwo = (ImageView) view.findViewById(R.id.storyimagetwo);
            storyImageThree = (ImageView) view.findViewById(R.id.storyimagethree);
            storyImageFour = (ImageView) view.findViewById(R.id.storyimagefour);
            storyImageFive = (ImageView) view.findViewById(R.id.storyimagefive);
            storyImageSix = (ImageView) view.findViewById(R.id.storyimagesix);
            storyImageSeven = (ImageView) view.findViewById(R.id.storyimageseven);
            storyImageEight = (ImageView) view.findViewById(R.id.storyimageeight);
            storyImageNine = (ImageView) view.findViewById(R.id.storyimagenine);
            storyImageTen = (ImageView) view.findViewById(R.id.storyimageten);
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
            ((HeaderViewHolder) holder).noOfLikes.setText(String.valueOf(classStory.getNoOfLikes()) + " Likes");
            ((HeaderViewHolder) holder).noOfComments.setText(String.valueOf(classStory.getNumberOfComments()) + " Comments");
            ((HeaderViewHolder) holder).classstory.setText(classStory.getStory());
            ((HeaderViewHolder) holder).urlLink.setVisibility(View.GONE);

            final String[] imageArray = classStory.getImageURL().split(" ");
            if (imageArray.length == 1 && imageArray[0].equals("")) {
                ((HeaderViewHolder) holder).imageContainer.setVisibility(View.GONE);
            } else {
                ((HeaderViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).imageLayoutOne.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutTwo.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutThree.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutFour.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutFive.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutSix.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutSeven.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutEight.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutNine.setClipToOutline(true);
                ((HeaderViewHolder) holder).imageLayoutTen.setClipToOutline(true);

                if (imageArray.length == 1) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 2) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 3) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 4) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 5) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 6) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 7) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 8) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
                    loadImageWithGlide(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutEight.setVisibility(View.VISIBLE);
                } else if (imageArray.length == 9) {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
                    loadImageWithGlide(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
                    loadImageWithGlide(imageArray[8], ((HeaderViewHolder) holder).storyImageNine);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutEight.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutNine.setVisibility(View.VISIBLE);
                } else {
                    loadImageWithGlide(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    loadImageWithGlide(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    loadImageWithGlide(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    loadImageWithGlide(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    loadImageWithGlide(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    loadImageWithGlide(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
                    loadImageWithGlide(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
                    loadImageWithGlide(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
                    loadImageWithGlide(imageArray[8], ((HeaderViewHolder) holder).storyImageNine);
                    loadImageWithGlide(imageArray[9], ((HeaderViewHolder) holder).storyImageTen);
                    ((HeaderViewHolder) holder).imageLayoutOne.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTwo.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutThree.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFour.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutFive.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSix.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutSeven.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutEight.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutNine.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).imageLayoutTen.setVisibility(View.VISIBLE);
                }

                ((HeaderViewHolder) holder).storyImageOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[0], ((HeaderViewHolder) holder).storyImageOne);
                    }
                });

                ((HeaderViewHolder) holder).storyImageTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[1], ((HeaderViewHolder) holder).storyImageTwo);
                    }
                });

                ((HeaderViewHolder) holder).storyImageThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[2], ((HeaderViewHolder) holder).storyImageThree);
                    }
                });

                ((HeaderViewHolder) holder).storyImageFour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[3], ((HeaderViewHolder) holder).storyImageFour);
                    }
                });

                ((HeaderViewHolder) holder).storyImageFive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[4], ((HeaderViewHolder) holder).storyImageFive);
                    }
                });

                ((HeaderViewHolder) holder).storyImageSix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[5], ((HeaderViewHolder) holder).storyImageSix);
                    }
                });

                ((HeaderViewHolder) holder).storyImageSeven.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[6], ((HeaderViewHolder) holder).storyImageSeven);
                    }
                });

                ((HeaderViewHolder) holder).storyImageEight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[7], ((HeaderViewHolder) holder).storyImageEight);
                    }
                });

                ((HeaderViewHolder) holder).storyImageNine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[8], ((HeaderViewHolder) holder).storyImageNine);
                    }
                });

                ((HeaderViewHolder) holder).storyImageTen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(imageArray[9], ((HeaderViewHolder) holder).storyImageTen);
                    }
                });
            }

            Glide.with(context)
                    .load(classStory.getProfilePicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((HeaderViewHolder) holder).posterPic);

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
                    Intent I = new Intent(context, TeacherProfileOneActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", classStory.getPosterID());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });

            ((HeaderViewHolder) holder).posterPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherProfileOneActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", classStory.getPosterID());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });
        }
        else if (holder instanceof MyViewHolder){
            final Comment comment = commentList.get(position);
            final String commenterAccountType = comment.getAccountType();

            ((MyViewHolder) holder).poster.setText(comment.getPosterName());
            ((MyViewHolder) holder).time.setText(Date.getRelativeTimeSpan(comment.getTime()));
            ((MyViewHolder) holder).comment.setText(comment.getComment());

            Glide.with(context)
                    .load(comment.getPosterPic())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).posterPic);


            ((MyViewHolder) holder).posterPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commenterAccountType.equals("Parent")){
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
                    if (commenterAccountType.equals("Parent")){
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

    private void loadImageWithGlide(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .into(imageView);
    }

    private void showImage(String url, ImageView imageView) {
        Bundle b = new Bundle();
        b.putString("URL", url);
        Intent I = new Intent(context, GalleryDetailActivity.class);
        I.putExtras(b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName("imageTransition");
            Pair<View, String> pair1 = Pair.create((View) imageView, imageView.getTransitionName());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, imageView, imageView.getTransitionName());
            context.startActivity(I, optionsCompat.toBundle());
        }
        else {
            context.startActivity(I);
        }
    }
}

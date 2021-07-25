package com.celerii.celerii.Activities.ELibrary.Parent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.ELibraryBooksDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryMaterialsModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ParentMyBooksFragment extends Fragment {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    TextView errorLayoutButton;

    LinearLayout myBooksErrorLayout, recommendedBooksErrorLayout, myBooksLayout, recommendedBooksLayout;
    LinearLayout myBooksLayoutOne, myBooksLayoutTwo, myBooksLayoutThree, myBooksLayoutFour, myBooksLayoutFive, myBooksLayoutSix, myBooksLayoutSeven,
            myBooksLayoutEight, myBooksLayoutNine, myBooksLayoutTen;
    LinearLayout recommendedBooksLayoutOne, recommendedBooksLayoutTwo, recommendedBooksLayoutThree, recommendedBooksLayoutFour,
            recommendedBooksLayoutFive, recommendedBooksLayoutSix, recommendedBooksLayoutSeven, recommendedBooksLayoutEight,
            recommendedBooksLayoutNine, recommendedBooksLayoutTen;
    LinearLayout myBooksImageClipperOne, myBooksImageClipperTwo, myBooksImageClipperThree, myBooksImageClipperFour, myBooksImageClipperFive, myBooksImageClipperSix, myBooksImageClipperSeven,
            myBooksImageClipperEight, myBooksImageClipperNine, myBooksImageClipperTen;
    LinearLayout recommendedBooksImageClipperOne, recommendedBooksImageClipperTwo, recommendedBooksImageClipperThree, recommendedBooksImageClipperFour,
            recommendedBooksImageClipperFive, recommendedBooksImageClipperSix, recommendedBooksImageClipperSeven, recommendedBooksImageClipperEight,
            recommendedBooksImageClipperNine, recommendedBooksImageClipperTen;
    LinearLayout viewMoreMyBooks, viewMoreRecommendedBooks;
    ImageView myBooksImageOne, myBooksImageTwo, myBooksImageThree, myBooksImageFour, myBooksImageFive, myBooksImageSix, myBooksImageSeven,
            myBooksImageEight, myBooksImageNine, myBooksImageTen;
    ImageView recommendedBooksImageOne, recommendedBooksImageTwo, recommendedBooksImageThree, recommendedBooksImageFour,
            recommendedBooksImageFive, recommendedBooksImageSix, recommendedBooksImageSeven, recommendedBooksImageEight,
            recommendedBooksImageNine, recommendedBooksImageTen;
    ImageView myBooksIconOne, myBooksIconTwo, myBooksIconThree, myBooksIconFour, myBooksIconFive, myBooksIconSix, myBooksIconSeven,
            myBooksIconEight, myBooksIconNine, myBooksIconTen;
    ImageView recommendedBooksIconOne, recommendedBooksIconTwo, recommendedBooksIconThree, recommendedBooksIconFour,
            recommendedBooksIconFive, recommendedBooksIconSix, recommendedBooksIconSeven, recommendedBooksIconEight,
            recommendedBooksIconNine, recommendedBooksIconTen;
    TextView myBooksTitleOne, myBooksTitleTwo, myBooksTitleThree, myBooksTitleFour, myBooksTitleFive, myBooksTitleSix, myBooksTitleSeven,
            myBooksTitleEight, myBooksTitleNine, myBooksTitleTen;
    TextView recommendedBooksTitleOne, recommendedBooksTitleTwo, recommendedBooksTitleThree, recommendedBooksTitleFour,
            recommendedBooksTitleFive, recommendedBooksTitleSix, recommendedBooksTitleSeven, recommendedBooksTitleEight,
            recommendedBooksTitleNine, recommendedBooksTitleTen;
    TextView myBooksAuthorOne, myBooksAuthorTwo, myBooksAuthorThree, myBooksAuthorFour, myBooksAuthorFive, myBooksAuthorSix, myBooksAuthorSeven,
            myBooksAuthorEight, myBooksAuthorNine, myBooksAuthorTen;
    TextView recommendedBooksAuthorOne, recommendedBooksAuthorTwo, recommendedBooksAuthorThree, recommendedBooksAuthorFour,
            recommendedBooksAuthorFive, recommendedBooksAuthorSix, recommendedBooksAuthorSeven, recommendedBooksAuthorEight,
            recommendedBooksAuthorNine, recommendedBooksAuthorTen;

    ArrayList<LinearLayout> myBooksLinearLayoutList = new ArrayList<>();
    ArrayList<LinearLayout> recommendedBooksLinearLayoutList = new ArrayList<>();
    ArrayList<ImageView> myBooksImageViewList = new ArrayList<>();
    ArrayList<ImageView> recommendedBooksImageViewList = new ArrayList<>();
    ArrayList<ImageView> myBooksIconViewList = new ArrayList<>();
    ArrayList<ImageView> recommendedBooksIconViewList = new ArrayList<>();
    ArrayList<TextView> myBooksTitleTextViewList = new ArrayList<>();
    ArrayList<TextView> recommendedBooksTitleTextViewList = new ArrayList<>();
    ArrayList<TextView> myBooksAuthorTextViewList = new ArrayList<>();
    ArrayList<TextView> recommendedBooksAuthorTextViewList = new ArrayList<>();

    ArrayList<ELibraryMaterialsModel> myBooksList;
    ArrayList<ELibraryMaterialsModel> recommendedBooksList;
    int schoolCount = 0;

    String activeStudentID = "";
    String activeStudent = "";
    String activeStudentName;

    String featureUseKey = "";
    String featureName = "Parent My Books";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentMyBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_my_books, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        superLayout = (ScrollView) view.findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) view.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) view.findViewById(R.id.errorlayoutbutton);

        ParentELibraryHomeActivity activity = (ParentELibraryHomeActivity) getActivity();
        activeStudent = activity.getData();

        if (activeStudent == null) {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Gson gson = new Gson();
                ArrayList<Student> myChildren = new ArrayList<>();
                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                Type type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                myChildren = gson.fromJson(myChildrenJSON, type);

                if (myChildren != null) {
                    if (myChildren.size() > 0) {
                        gson = new Gson();
                        activeStudent = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeStudent);
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return view;
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                    return view;
                }
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText("We couldn't find this student's account");
                return view;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {}.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {}.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student: myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return view;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();

        myBooksErrorLayout = (LinearLayout) view.findViewById(R.id.mybookserrorlayout);
        recommendedBooksErrorLayout = (LinearLayout) view.findViewById(R.id.recommendedbookserrorlayout);
        myBooksLayout = (LinearLayout) view.findViewById(R.id.mybookslayout);
        recommendedBooksLayout = (LinearLayout) view.findViewById(R.id.recommendedbookslayout);

        myBooksLayoutOne = (LinearLayout) view.findViewById(R.id.mybookslayoutone);
        myBooksLayoutTwo = (LinearLayout) view.findViewById(R.id.mybookslayouttwo);
        myBooksLayoutThree = (LinearLayout) view.findViewById(R.id.mybookslayoutthree);
        myBooksLayoutFour = (LinearLayout) view.findViewById(R.id.mybookslayoutfour);
        myBooksLayoutFive = (LinearLayout) view.findViewById(R.id.mybookslayoutfive);
        myBooksLayoutSix = (LinearLayout) view.findViewById(R.id.mybookslayoutsix);
        myBooksLayoutSeven = (LinearLayout) view.findViewById(R.id.mybookslayoutseven);
        myBooksLayoutEight = (LinearLayout) view.findViewById(R.id.mybookslayouteight);
        myBooksLayoutNine = (LinearLayout) view.findViewById(R.id.mybookslayoutnine);
        myBooksLayoutTen = (LinearLayout) view.findViewById(R.id.mybookslayoutten);

        recommendedBooksLayoutOne = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutone);
        recommendedBooksLayoutTwo = (LinearLayout) view.findViewById(R.id.recommendedbookslayouttwo);
        recommendedBooksLayoutThree = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutthree);
        recommendedBooksLayoutFour = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutfour);
        recommendedBooksLayoutFive = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutfive);
        recommendedBooksLayoutSix = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutsix);
        recommendedBooksLayoutSeven = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutseven);
        recommendedBooksLayoutEight = (LinearLayout) view.findViewById(R.id.recommendedbookslayouteight);
        recommendedBooksLayoutNine = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutnine);
        recommendedBooksLayoutTen = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutten);

        myBooksImageClipperOne = (LinearLayout) myBooksLayoutOne.findViewById(R.id.booksimageclipper);
        myBooksImageClipperTwo = (LinearLayout) myBooksLayoutTwo.findViewById(R.id.booksimageclipper);
        myBooksImageClipperThree = (LinearLayout) myBooksLayoutThree.findViewById(R.id.booksimageclipper);
        myBooksImageClipperFour = (LinearLayout) myBooksLayoutFour.findViewById(R.id.booksimageclipper);
        myBooksImageClipperFive = (LinearLayout) myBooksLayoutFive.findViewById(R.id.booksimageclipper);
        myBooksImageClipperSix = (LinearLayout) myBooksLayoutSix.findViewById(R.id.booksimageclipper);
        myBooksImageClipperSeven = (LinearLayout) myBooksLayoutSeven.findViewById(R.id.booksimageclipper);
        myBooksImageClipperEight = (LinearLayout) myBooksLayoutEight.findViewById(R.id.booksimageclipper);
        myBooksImageClipperNine = (LinearLayout) myBooksLayoutNine.findViewById(R.id.booksimageclipper);
        myBooksImageClipperTen = (LinearLayout) myBooksLayoutTen.findViewById(R.id.booksimageclipper);

        recommendedBooksImageClipperOne = (LinearLayout) recommendedBooksLayoutOne.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperTwo = (LinearLayout) recommendedBooksLayoutTwo.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperThree = (LinearLayout) recommendedBooksLayoutThree.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperFour = (LinearLayout) recommendedBooksLayoutFour.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperFive = (LinearLayout) recommendedBooksLayoutFive.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperSix = (LinearLayout) recommendedBooksLayoutSix.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperSeven = (LinearLayout) recommendedBooksLayoutSeven.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperEight = (LinearLayout) recommendedBooksLayoutEight.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperNine = (LinearLayout) recommendedBooksLayoutNine.findViewById(R.id.booksimageclipper);
        recommendedBooksImageClipperTen = (LinearLayout) recommendedBooksLayoutTen.findViewById(R.id.booksimageclipper);

        viewMoreMyBooks = (LinearLayout) view.findViewById(R.id.viewmoremybooks);
        viewMoreRecommendedBooks = (LinearLayout) view.findViewById(R.id.viewmorerecommendedbooks);

        myBooksImageOne = (ImageView) myBooksLayoutOne.findViewById(R.id.booksimage);
        myBooksImageTwo = (ImageView) myBooksLayoutTwo.findViewById(R.id.booksimage);
        myBooksImageThree = (ImageView) myBooksLayoutThree.findViewById(R.id.booksimage);
        myBooksImageFour = (ImageView) myBooksLayoutFour.findViewById(R.id.booksimage);
        myBooksImageFive = (ImageView) myBooksLayoutFive.findViewById(R.id.booksimage);
        myBooksImageSix = (ImageView) myBooksLayoutSix.findViewById(R.id.booksimage);
        myBooksImageSeven = (ImageView) myBooksLayoutSeven.findViewById(R.id.booksimage);
        myBooksImageEight = (ImageView) myBooksLayoutEight.findViewById(R.id.booksimage);
        myBooksImageNine = (ImageView) myBooksLayoutNine.findViewById(R.id.booksimage);
        myBooksImageTen = (ImageView) myBooksLayoutTen.findViewById(R.id.booksimage);

        recommendedBooksImageOne = (ImageView) recommendedBooksLayoutOne.findViewById(R.id.booksimage);
        recommendedBooksImageTwo = (ImageView) recommendedBooksLayoutTwo.findViewById(R.id.booksimage);
        recommendedBooksImageThree = (ImageView) recommendedBooksLayoutThree.findViewById(R.id.booksimage);
        recommendedBooksImageFour = (ImageView) recommendedBooksLayoutFour.findViewById(R.id.booksimage);
        recommendedBooksImageFive = (ImageView) recommendedBooksLayoutFive.findViewById(R.id.booksimage);
        recommendedBooksImageSix = (ImageView) recommendedBooksLayoutSix.findViewById(R.id.booksimage);
        recommendedBooksImageSeven = (ImageView) recommendedBooksLayoutSeven.findViewById(R.id.booksimage);
        recommendedBooksImageEight = (ImageView) recommendedBooksLayoutEight.findViewById(R.id.booksimage);
        recommendedBooksImageNine = (ImageView) recommendedBooksLayoutNine.findViewById(R.id.booksimage);
        recommendedBooksImageTen = (ImageView) recommendedBooksLayoutTen.findViewById(R.id.booksimage);

        myBooksIconOne = (ImageView) myBooksLayoutOne.findViewById(R.id.booksicon);
        myBooksIconTwo = (ImageView) myBooksLayoutTwo.findViewById(R.id.booksicon);
        myBooksIconThree = (ImageView) myBooksLayoutThree.findViewById(R.id.booksicon);
        myBooksIconFour = (ImageView) myBooksLayoutFour.findViewById(R.id.booksicon);
        myBooksIconFive = (ImageView) myBooksLayoutFive.findViewById(R.id.booksicon);
        myBooksIconSix = (ImageView) myBooksLayoutSix.findViewById(R.id.booksicon);
        myBooksIconSeven = (ImageView) myBooksLayoutSeven.findViewById(R.id.booksicon);
        myBooksIconEight = (ImageView) myBooksLayoutEight.findViewById(R.id.booksicon);
        myBooksIconNine = (ImageView) myBooksLayoutNine.findViewById(R.id.booksicon);
        myBooksIconTen = (ImageView) myBooksLayoutTen.findViewById(R.id.booksicon);

        recommendedBooksIconOne = (ImageView) recommendedBooksLayoutOne.findViewById(R.id.booksicon);
        recommendedBooksIconTwo = (ImageView) recommendedBooksLayoutTwo.findViewById(R.id.booksicon);
        recommendedBooksIconThree = (ImageView) recommendedBooksLayoutThree.findViewById(R.id.booksicon);
        recommendedBooksIconFour = (ImageView) recommendedBooksLayoutFour.findViewById(R.id.booksicon);
        recommendedBooksIconFive = (ImageView) recommendedBooksLayoutFive.findViewById(R.id.booksicon);
        recommendedBooksIconSix = (ImageView) recommendedBooksLayoutSix.findViewById(R.id.booksicon);
        recommendedBooksIconSeven = (ImageView) recommendedBooksLayoutSeven.findViewById(R.id.booksicon);
        recommendedBooksIconEight = (ImageView) recommendedBooksLayoutEight.findViewById(R.id.booksicon);
        recommendedBooksIconNine = (ImageView) recommendedBooksLayoutNine.findViewById(R.id.booksicon);
        recommendedBooksIconTen = (ImageView) recommendedBooksLayoutTen.findViewById(R.id.booksicon);

        myBooksTitleOne = (TextView) myBooksLayoutOne.findViewById(R.id.bookstitle);
        myBooksTitleTwo = (TextView) myBooksLayoutTwo.findViewById(R.id.bookstitle);
        myBooksTitleThree = (TextView) myBooksLayoutThree.findViewById(R.id.bookstitle);
        myBooksTitleFour = (TextView) myBooksLayoutFour.findViewById(R.id.bookstitle);
        myBooksTitleFive = (TextView) myBooksLayoutFive.findViewById(R.id.bookstitle);
        myBooksTitleSix = (TextView) myBooksLayoutSix.findViewById(R.id.bookstitle);
        myBooksTitleSeven = (TextView) myBooksLayoutSeven.findViewById(R.id.bookstitle);
        myBooksTitleEight = (TextView) myBooksLayoutEight.findViewById(R.id.bookstitle);
        myBooksTitleNine = (TextView) myBooksLayoutNine.findViewById(R.id.bookstitle);
        myBooksTitleTen = (TextView) myBooksLayoutTen.findViewById(R.id.bookstitle);

        recommendedBooksTitleOne = (TextView) recommendedBooksLayoutOne.findViewById(R.id.bookstitle);
        recommendedBooksTitleTwo = (TextView) recommendedBooksLayoutTwo.findViewById(R.id.bookstitle);
        recommendedBooksTitleThree = (TextView) recommendedBooksLayoutThree.findViewById(R.id.bookstitle);
        recommendedBooksTitleFour = (TextView) recommendedBooksLayoutFour.findViewById(R.id.bookstitle);
        recommendedBooksTitleFive = (TextView) recommendedBooksLayoutFive.findViewById(R.id.bookstitle);
        recommendedBooksTitleSix = (TextView) recommendedBooksLayoutSix.findViewById(R.id.bookstitle);
        recommendedBooksTitleSeven = (TextView) recommendedBooksLayoutSeven.findViewById(R.id.bookstitle);
        recommendedBooksTitleEight = (TextView) recommendedBooksLayoutEight.findViewById(R.id.bookstitle);
        recommendedBooksTitleNine = (TextView) recommendedBooksLayoutNine.findViewById(R.id.bookstitle);
        recommendedBooksTitleTen = (TextView) recommendedBooksLayoutTen.findViewById(R.id.bookstitle);

        myBooksAuthorOne = (TextView) myBooksLayoutOne.findViewById(R.id.booksauthor);
        myBooksAuthorTwo = (TextView) myBooksLayoutTwo.findViewById(R.id.booksauthor);
        myBooksAuthorThree = (TextView) myBooksLayoutThree.findViewById(R.id.booksauthor);
        myBooksAuthorFour = (TextView) myBooksLayoutFour.findViewById(R.id.booksauthor);
        myBooksAuthorFive = (TextView) myBooksLayoutFive.findViewById(R.id.booksauthor);
        myBooksAuthorSix = (TextView) myBooksLayoutSix.findViewById(R.id.booksauthor);
        myBooksAuthorSeven = (TextView) myBooksLayoutSeven.findViewById(R.id.booksauthor);
        myBooksAuthorEight = (TextView) myBooksLayoutEight.findViewById(R.id.booksauthor);
        myBooksAuthorNine = (TextView) myBooksLayoutNine.findViewById(R.id.booksauthor);
        myBooksAuthorTen = (TextView) myBooksLayoutTen.findViewById(R.id.booksauthor);

        recommendedBooksAuthorOne = (TextView) recommendedBooksLayoutOne.findViewById(R.id.booksauthor);
        recommendedBooksAuthorTwo = (TextView) recommendedBooksLayoutTwo.findViewById(R.id.booksauthor);
        recommendedBooksAuthorThree = (TextView) recommendedBooksLayoutThree.findViewById(R.id.booksauthor);
        recommendedBooksAuthorFour = (TextView) recommendedBooksLayoutFour.findViewById(R.id.booksauthor);
        recommendedBooksAuthorFive = (TextView) recommendedBooksLayoutFive.findViewById(R.id.booksauthor);
        recommendedBooksAuthorSix = (TextView) recommendedBooksLayoutSix.findViewById(R.id.booksauthor);
        recommendedBooksAuthorSeven = (TextView) recommendedBooksLayoutSeven.findViewById(R.id.booksauthor);
        recommendedBooksAuthorEight = (TextView) recommendedBooksLayoutEight.findViewById(R.id.booksauthor);
        recommendedBooksAuthorNine = (TextView) recommendedBooksLayoutNine.findViewById(R.id.booksauthor);
        recommendedBooksAuthorTen = (TextView) recommendedBooksLayoutTen.findViewById(R.id.booksauthor);

        myBooksImageClipperOne.setClipToOutline(true);
        myBooksImageClipperTwo.setClipToOutline(true);
        myBooksImageClipperThree.setClipToOutline(true);
        myBooksImageClipperFour.setClipToOutline(true);
        myBooksImageClipperFive.setClipToOutline(true);
        myBooksImageClipperSix.setClipToOutline(true);
        myBooksImageClipperSeven.setClipToOutline(true);
        myBooksImageClipperEight.setClipToOutline(true);
        myBooksImageClipperNine.setClipToOutline(true);
        myBooksImageClipperTen.setClipToOutline(true);

        recommendedBooksImageClipperOne.setClipToOutline(true);
        recommendedBooksImageClipperTwo.setClipToOutline(true);
        recommendedBooksImageClipperThree.setClipToOutline(true);
        recommendedBooksImageClipperFour.setClipToOutline(true);
        recommendedBooksImageClipperFive.setClipToOutline(true);
        recommendedBooksImageClipperSix.setClipToOutline(true);
        recommendedBooksImageClipperSeven.setClipToOutline(true);
        recommendedBooksImageClipperEight.setClipToOutline(true);
        recommendedBooksImageClipperNine.setClipToOutline(true);
        recommendedBooksImageClipperTen.setClipToOutline(true);

        myBooksLinearLayoutList.add(myBooksLayoutOne);
        myBooksLinearLayoutList.add(myBooksLayoutTwo);
        myBooksLinearLayoutList.add(myBooksLayoutThree);
        myBooksLinearLayoutList.add(myBooksLayoutFour);
        myBooksLinearLayoutList.add(myBooksLayoutFive);
        myBooksLinearLayoutList.add(myBooksLayoutSix);
        myBooksLinearLayoutList.add(myBooksLayoutSeven);
        myBooksLinearLayoutList.add(myBooksLayoutEight);
        myBooksLinearLayoutList.add(myBooksLayoutNine);
        myBooksLinearLayoutList.add(myBooksLayoutTen);

        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutOne);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutTwo);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutThree);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutFour);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutFive);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutSix);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutSeven);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutEight);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutNine);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutTen);

        myBooksImageViewList.add(myBooksImageOne);
        myBooksImageViewList.add(myBooksImageTwo);
        myBooksImageViewList.add(myBooksImageThree);
        myBooksImageViewList.add(myBooksImageFour);
        myBooksImageViewList.add(myBooksImageFive);
        myBooksImageViewList.add(myBooksImageSix);
        myBooksImageViewList.add(myBooksImageSeven);
        myBooksImageViewList.add(myBooksImageEight);
        myBooksImageViewList.add(myBooksImageNine);
        myBooksImageViewList.add(myBooksImageTen);

        recommendedBooksImageViewList.add(recommendedBooksImageOne);
        recommendedBooksImageViewList.add(recommendedBooksImageTwo);
        recommendedBooksImageViewList.add(recommendedBooksImageThree);
        recommendedBooksImageViewList.add(recommendedBooksImageFour);
        recommendedBooksImageViewList.add(recommendedBooksImageFive);
        recommendedBooksImageViewList.add(recommendedBooksImageSix);
        recommendedBooksImageViewList.add(recommendedBooksImageSeven);
        recommendedBooksImageViewList.add(recommendedBooksImageEight);
        recommendedBooksImageViewList.add(recommendedBooksImageNine);
        recommendedBooksImageViewList.add(recommendedBooksImageTen);

        myBooksIconViewList.add(myBooksIconOne);
        myBooksIconViewList.add(myBooksIconTwo);
        myBooksIconViewList.add(myBooksIconThree);
        myBooksIconViewList.add(myBooksIconFour);
        myBooksIconViewList.add(myBooksIconFive);
        myBooksIconViewList.add(myBooksIconSix);
        myBooksIconViewList.add(myBooksIconSeven);
        myBooksIconViewList.add(myBooksIconEight);
        myBooksIconViewList.add(myBooksIconNine);
        myBooksIconViewList.add(myBooksIconTen);

        recommendedBooksIconViewList.add(recommendedBooksIconOne);
        recommendedBooksIconViewList.add(recommendedBooksIconTwo);
        recommendedBooksIconViewList.add(recommendedBooksIconThree);
        recommendedBooksIconViewList.add(recommendedBooksIconFour);
        recommendedBooksIconViewList.add(recommendedBooksIconFive);
        recommendedBooksIconViewList.add(recommendedBooksIconSix);
        recommendedBooksIconViewList.add(recommendedBooksIconSeven);
        recommendedBooksIconViewList.add(recommendedBooksIconEight);
        recommendedBooksIconViewList.add(recommendedBooksIconNine);
        recommendedBooksIconViewList.add(recommendedBooksIconTen);

        myBooksTitleTextViewList.add(myBooksTitleOne);
        myBooksTitleTextViewList.add(myBooksTitleTwo);
        myBooksTitleTextViewList.add(myBooksTitleThree);
        myBooksTitleTextViewList.add(myBooksTitleFour);
        myBooksTitleTextViewList.add(myBooksTitleFive);
        myBooksTitleTextViewList.add(myBooksTitleSix);
        myBooksTitleTextViewList.add(myBooksTitleSeven);
        myBooksTitleTextViewList.add(myBooksTitleEight);
        myBooksTitleTextViewList.add(myBooksTitleNine);
        myBooksTitleTextViewList.add(myBooksTitleTen);

        recommendedBooksTitleTextViewList.add(recommendedBooksTitleOne);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleTwo);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleThree);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleFour);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleFive);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleSix);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleSeven);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleEight);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleNine);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleTen);

        myBooksAuthorTextViewList.add(myBooksAuthorOne);
        myBooksAuthorTextViewList.add(myBooksAuthorTwo);
        myBooksAuthorTextViewList.add(myBooksAuthorThree);
        myBooksAuthorTextViewList.add(myBooksAuthorFour);
        myBooksAuthorTextViewList.add(myBooksAuthorFive);
        myBooksAuthorTextViewList.add(myBooksAuthorSix);
        myBooksAuthorTextViewList.add(myBooksAuthorSeven);
        myBooksAuthorTextViewList.add(myBooksAuthorEight);
        myBooksAuthorTextViewList.add(myBooksAuthorNine);
        myBooksAuthorTextViewList.add(myBooksAuthorTen);

        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorOne);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorTwo);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorThree);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorFour);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorFive);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorSix);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorSeven);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorEight);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorNine);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorTen);

        for (LinearLayout layout: myBooksLinearLayoutList) {
            layout.setVisibility(View.GONE);
        }

        for (LinearLayout layout: recommendedBooksLinearLayoutList) {
            layout.setVisibility(View.GONE);
        }

        myBooksList = new ArrayList<>();
        recommendedBooksList = new ArrayList<>();

        superLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        loadMyBooksFromFirebase();

        myBooksImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(0).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(0).getTitle());
                    bundle.putString("authorString", myBooksList.get(0).getAuthor());
                    bundle.putString("typeString", myBooksList.get(0).getType());
                    bundle.putString("descriptionString", myBooksList.get(0).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(0).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(0).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(0).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(1).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(1).getTitle());
                    bundle.putString("authorString", myBooksList.get(1).getAuthor());
                    bundle.putString("typeString", myBooksList.get(1).getType());
                    bundle.putString("descriptionString", myBooksList.get(1).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(1).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(1).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(1).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(2).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(2).getTitle());
                    bundle.putString("authorString", myBooksList.get(2).getAuthor());
                    bundle.putString("typeString", myBooksList.get(2).getType());
                    bundle.putString("descriptionString", myBooksList.get(2).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(2).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(2).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(2).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(3).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(3).getTitle());
                    bundle.putString("authorString", myBooksList.get(3).getAuthor());
                    bundle.putString("typeString", myBooksList.get(3).getType());
                    bundle.putString("descriptionString", myBooksList.get(3).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(3).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(3).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(3).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(4).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(4).getTitle());
                    bundle.putString("authorString", myBooksList.get(4).getAuthor());
                    bundle.putString("typeString", myBooksList.get(4).getType());
                    bundle.putString("descriptionString", myBooksList.get(4).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(4).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(4).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(4).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(5).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(5).getTitle());
                    bundle.putString("authorString", myBooksList.get(5).getAuthor());
                    bundle.putString("typeString", myBooksList.get(5).getType());
                    bundle.putString("descriptionString", myBooksList.get(5).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(5).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(5).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(5).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(6).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(6).getTitle());
                    bundle.putString("authorString", myBooksList.get(6).getAuthor());
                    bundle.putString("typeString", myBooksList.get(6).getType());
                    bundle.putString("descriptionString", myBooksList.get(6).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(6).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(6).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(6).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(7).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(7).getTitle());
                    bundle.putString("authorString", myBooksList.get(7).getAuthor());
                    bundle.putString("typeString", myBooksList.get(7).getType());
                    bundle.putString("descriptionString", myBooksList.get(7).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(7).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(7).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(7).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(8).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(8).getTitle());
                    bundle.putString("authorString", myBooksList.get(8).getAuthor());
                    bundle.putString("typeString", myBooksList.get(8).getType());
                    bundle.putString("descriptionString", myBooksList.get(8).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(8).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(8).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(8).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        myBooksImageTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myBooksList.get(9).getMaterialID());
                    bundle.putString("titleString", myBooksList.get(9).getTitle());
                    bundle.putString("authorString", myBooksList.get(9).getAuthor());
                    bundle.putString("typeString", myBooksList.get(9).getType());
                    bundle.putString("descriptionString", myBooksList.get(9).getDescription());
                    bundle.putString("thumbnailURL", myBooksList.get(9).getMaterialThumbnailURL());
                    bundle.putString("materialURL", myBooksList.get(9).getMaterialURL());
                    bundle.putString("materialUploader", myBooksList.get(9).getUploader());
                    bundle.putString("activeStudent", activeStudent);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        viewMoreMyBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ParentELibraryBooksListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("requestType", "MyBooks");
                bundle.putString("activeStudent", activeStudent);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        viewMoreRecommendedBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ParentELibraryBooksListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("requestType", "RecommendedBooks");
                bundle.putString("activeStudent", activeStudent);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadMyBooksFromFirebase();
                    }
                }
        );

        return view;
    }

    int i;
    private void loadMyBooksFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Student School").child(activeStudentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                schoolCount = 0;
                myBooksList.clear();

                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        final String schoolID = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Private Materials").child("School").child(schoolID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                schoolCount++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        ELibraryMaterialsModel eLibraryMaterialsModel = postSnapshot.getValue(ELibraryMaterialsModel.class);
                                        myBooksList.add(eLibraryMaterialsModel);
                                    }
                                }

                                if (schoolCount == childrenCount) {
                                    if (myBooksList.size() > 0) {
                                        if (myBooksList.size() > 1) {
                                            Collections.sort(myBooksList, new Comparator<ELibraryMaterialsModel>() {
                                                @Override
                                                public int compare(ELibraryMaterialsModel o1, ELibraryMaterialsModel o2) {
                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                }
                                            });
                                        }

                                        Collections.reverse(myBooksList);
                                        for (i = 0; i < myBooksList.size(); i++) {
                                            myBooksLinearLayoutList.get(i).setVisibility(View.VISIBLE);
                                            myBooksTitleTextViewList.get(i).setText(myBooksList.get(i).getTitle());
                                            myBooksAuthorTextViewList.get(i).setText(myBooksList.get(i).getAuthor());
                                            loadMyBooksImages(i);
                                        }

                                        for (int i = myBooksList.size(); i < 10; i++) {
                                            myBooksLinearLayoutList.get(i).setVisibility(View.GONE);
                                        }

                                        myBooksLayout.setVisibility(View.VISIBLE);
                                        myBooksErrorLayout.setVisibility(View.GONE);
                                    } else {
                                        myBooksLayout.setVisibility(View.GONE);
                                        myBooksErrorLayout.setVisibility(View.VISIBLE);
                                    }
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    superLayout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    if (myBooksList.size() > 0) {
                        if (myBooksList.size() > 1) {
                            Collections.sort(myBooksList, new Comparator<ELibraryMaterialsModel>() {
                                @Override
                                public int compare(ELibraryMaterialsModel o1, ELibraryMaterialsModel o2) {
                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                }
                            });
                        }

                        for (i = 0; i < myBooksList.size(); i++) {
                            myBooksLinearLayoutList.get(i).setVisibility(View.VISIBLE);
                            myBooksTitleTextViewList.get(i).setText(myBooksList.get(i).getTitle());
                            myBooksAuthorTextViewList.get(i).setText(myBooksList.get(i).getAuthor());
                            loadMyBooksImages(i);
                        }

                        myBooksLayout.setVisibility(View.VISIBLE);
                        myBooksErrorLayout.setVisibility(View.GONE);
                    } else {
                        myBooksLayout.setVisibility(View.GONE);
                        myBooksErrorLayout.setVisibility(View.VISIBLE);
                    }
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyBooksImages(int position) {
        if (myBooksList.get(i).getMaterialThumbnailURL().trim().isEmpty()) {
            if (myBooksList.get(i).getType().equals("pdf")) {
                if (position % 2 == 0) {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_purple_24));
                } else {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_accent_24));
                }
            } else if (myBooksList.get(i).getType().equals("video")) {
                if (position % 2 == 0) {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_purple_24));
                } else {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_accent_24));
                }
            } else if (myBooksList.get(i).getType().equals("audio")) {
                if (position % 2 == 0) {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_purple_24));
                } else {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_accent_24));
                }
            } else {
                if (position % 2 == 0) {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_purple_24));
                } else {
                    myBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_accent_24));
                }
            }
        } else {
            if (myBooksList.get(i).getType().equals("pdf")) {
                myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_white_24));
            } else if (myBooksList.get(i).getType().equals("video")) {
                myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_white_24));
            } else if (myBooksList.get(i).getType().equals("audio")) {
                myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_white_24));
            } else {
                myBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_white_24));
            }
            Glide.with(context)
                    .load(myBooksList.get(i).getMaterialThumbnailURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(myBooksImageViewList.get(i));
        }
    }

    private void loadRecommendedBooksFromFirebase() {

    }

    private void loadRecommendedBooksImages(int position) {
        if (recommendedBooksList.get(i).getMaterialThumbnailURL().trim().isEmpty()) {
            if (recommendedBooksList.get(i).getType().equals("pdf")) {
                if (position % 2 == 0) {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_purple_24));
                } else {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_accent_24));
                }
            } else if (recommendedBooksList.get(i).getType().equals("video")) {
                if (position % 2 == 0) {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_purple_24));
                } else {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_accent_24));
                }
            } else if (recommendedBooksList.get(i).getType().equals("audio")) {
                if (position % 2 == 0) {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_purple_24));
                } else {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_accent_24));
                }
            } else {
                if (position % 2 == 0) {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_purple_24));
                } else {
                    recommendedBooksImageViewList.get(i).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_accent_24));
                }
            }
        } else {
            if (recommendedBooksList.get(i).getType().equals("pdf")) {
                recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_white_24));
            } else if (recommendedBooksList.get(i).getType().equals("video")) {
                recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_white_24));
            } else if (recommendedBooksList.get(i).getType().equals("audio")) {
                recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_white_24));
            } else {
                recommendedBooksIconViewList.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_white_24));
            }
            Glide.with(context)
                    .load(recommendedBooksList.get(i).getMaterialThumbnailURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(recommendedBooksImageViewList.get(i));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public void onResume() {
        UpdateDataFromFirebase.populateEssentials(context);
        super.onResume();
    }
}
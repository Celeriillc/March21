package com.celerii.celerii.Activities.ELibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
    ImageView viewMoreMyBooks, viewMoreRecommendedBooks;
    ImageView myBooksImageOne, myBooksImageTwo, myBooksImageThree, myBooksImageFour, myBooksImageFive, myBooksImageSix, myBooksImageSeven,
            myBooksImageEight, myBooksImageNine, myBooksImageTen;
    ImageView recommendedBooksImageOne, recommendedBooksImageTwo, recommendedBooksImageThree, recommendedBooksImageFour,
            recommendedBooksImageFive, recommendedBooksImageSix, recommendedBooksImageSeven, recommendedBooksImageEight,
            recommendedBooksImageNine, recommendedBooksImageTen;
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
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
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
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                    } else {
                        errorLayoutText.setText("You do not have the permission to view this student's academic record");
                    }

                    return view;
                }
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                } else {
                    errorLayoutText.setText("You do not have the permission to view this student's academic record");
                }

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
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                            errorLayoutButton.setText("Find my child");
                            errorLayoutButton.setVisibility(View.VISIBLE);
                        } else {
                            errorLayoutText.setText("You do not have the permission to view this student's academic record");
                        }

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

        myBooksImageClipperOne = (LinearLayout) view.findViewById(R.id.mybooksimageclipperone);
        myBooksImageClipperTwo = (LinearLayout) view.findViewById(R.id.mybooksimageclippertwo);
        myBooksImageClipperThree = (LinearLayout) view.findViewById(R.id.mybooksimageclipperthree);
        myBooksImageClipperFour = (LinearLayout) view.findViewById(R.id.mybooksimageclipperfour);
        myBooksImageClipperFive = (LinearLayout) view.findViewById(R.id.mybooksimageclipperfive);
        myBooksImageClipperSix = (LinearLayout) view.findViewById(R.id.mybooksimageclippersix);
        myBooksImageClipperSeven = (LinearLayout) view.findViewById(R.id.mybooksimageclipperseven);
        myBooksImageClipperEight = (LinearLayout) view.findViewById(R.id.mybooksimageclippereight);
        myBooksImageClipperNine = (LinearLayout) view.findViewById(R.id.mybooksimageclippernine);
        myBooksImageClipperTen = (LinearLayout) view.findViewById(R.id.mybooksimageclipperten);

        recommendedBooksImageClipperOne = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclipperone);
        recommendedBooksImageClipperTwo = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclippertwo);
        recommendedBooksImageClipperThree = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclipperthree);
        recommendedBooksImageClipperFour = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclipperfour);
        recommendedBooksImageClipperFive = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclipperfive);
        recommendedBooksImageClipperSix = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclippersix);
        recommendedBooksImageClipperSeven = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclipperseven);
        recommendedBooksImageClipperEight = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclippereight);
        recommendedBooksImageClipperNine = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclippernine);
        recommendedBooksImageClipperTen = (LinearLayout) view.findViewById(R.id.recommendedbooksimageclipperten);

        viewMoreMyBooks = (ImageView) view.findViewById(R.id.viewmoremybooks);
        viewMoreRecommendedBooks = (ImageView) view.findViewById(R.id.viewmorerecommendedbooks);

        myBooksImageOne = (ImageView) view.findViewById(R.id.mybooksimageone);
        myBooksImageTwo = (ImageView) view.findViewById(R.id.mybooksimagetwo);
        myBooksImageThree = (ImageView) view.findViewById(R.id.mybooksimagethree);
        myBooksImageFour = (ImageView) view.findViewById(R.id.mybooksimagefour);
        myBooksImageFive = (ImageView) view.findViewById(R.id.mybooksimagefive);
        myBooksImageSix = (ImageView) view.findViewById(R.id.mybooksimagesix);
        myBooksImageSeven = (ImageView) view.findViewById(R.id.mybooksimageseven);
        myBooksImageEight = (ImageView) view.findViewById(R.id.mybooksimageeight);
        myBooksImageNine = (ImageView) view.findViewById(R.id.mybooksimagenine);
        myBooksImageTen = (ImageView) view.findViewById(R.id.mybooksimageten);

        recommendedBooksImageOne = (ImageView) view.findViewById(R.id.recommendedbooksimageone);
        recommendedBooksImageTwo = (ImageView) view.findViewById(R.id.recommendedbooksimagetwo);
        recommendedBooksImageThree = (ImageView) view.findViewById(R.id.recommendedbooksimagethree);
        recommendedBooksImageFour = (ImageView) view.findViewById(R.id.recommendedbooksimagefour);
        recommendedBooksImageFive = (ImageView) view.findViewById(R.id.recommendedbooksimagefive);
        recommendedBooksImageSix = (ImageView) view.findViewById(R.id.recommendedbooksimagesix);
        recommendedBooksImageSeven = (ImageView) view.findViewById(R.id.recommendedbooksimageseven);
        recommendedBooksImageEight = (ImageView) view.findViewById(R.id.recommendedbooksimageeight);
        recommendedBooksImageNine = (ImageView) view.findViewById(R.id.recommendedbooksimagenine);
        recommendedBooksImageTen = (ImageView) view.findViewById(R.id.recommendedbooksimageten);

        myBooksTitleOne = (TextView) view.findViewById(R.id.mybookstitleone);
        myBooksTitleTwo = (TextView) view.findViewById(R.id.mybookstitletwo);
        myBooksTitleThree = (TextView) view.findViewById(R.id.mybookstitlethree);
        myBooksTitleFour = (TextView) view.findViewById(R.id.mybookstitlefour);
        myBooksTitleFive = (TextView) view.findViewById(R.id.mybookstitlefive);
        myBooksTitleSix = (TextView) view.findViewById(R.id.mybookstitlesix);
        myBooksTitleSeven = (TextView) view.findViewById(R.id.mybookstitleseven);
        myBooksTitleEight = (TextView) view.findViewById(R.id.mybookstitleeight);
        myBooksTitleNine = (TextView) view.findViewById(R.id.mybookstitlenine);
        myBooksTitleTen = (TextView) view.findViewById(R.id.mybookstitleten);

        recommendedBooksTitleOne = (TextView) view.findViewById(R.id.recommendedbookstitleone);
        recommendedBooksTitleTwo = (TextView) view.findViewById(R.id.recommendedbookstitletwo);
        recommendedBooksTitleThree = (TextView) view.findViewById(R.id.recommendedbookstitlethree);
        recommendedBooksTitleFour = (TextView) view.findViewById(R.id.recommendedbookstitlefour);
        recommendedBooksTitleFive = (TextView) view.findViewById(R.id.recommendedbookstitlefive);
        recommendedBooksTitleSix = (TextView) view.findViewById(R.id.recommendedbookstitlesix);
        recommendedBooksTitleSeven = (TextView) view.findViewById(R.id.recommendedbookstitleseven);
        recommendedBooksTitleEight = (TextView) view.findViewById(R.id.recommendedbookstitleeight);
        recommendedBooksTitleNine = (TextView) view.findViewById(R.id.recommendedbookstitlenine);
        recommendedBooksTitleTen = (TextView) view.findViewById(R.id.recommendedbookstitleten);

        myBooksAuthorOne = (TextView) view.findViewById(R.id.mybooksauthorone);
        myBooksAuthorTwo = (TextView) view.findViewById(R.id.mybooksauthortwo);
        myBooksAuthorThree = (TextView) view.findViewById(R.id.mybooksauthorthree);
        myBooksAuthorFour = (TextView) view.findViewById(R.id.mybooksauthorfour);
        myBooksAuthorFive = (TextView) view.findViewById(R.id.mybooksauthorfive);
        myBooksAuthorSix = (TextView) view.findViewById(R.id.mybooksauthorsix);
        myBooksAuthorSeven = (TextView) view.findViewById(R.id.mybooksauthorseven);
        myBooksAuthorEight = (TextView) view.findViewById(R.id.mybooksauthoreight);
        myBooksAuthorNine = (TextView) view.findViewById(R.id.mybooksauthornine);
        myBooksAuthorTen = (TextView) view.findViewById(R.id.mybooksauthorten);

        recommendedBooksAuthorOne = (TextView) view.findViewById(R.id.recommendedbooksauthorone);
        recommendedBooksAuthorTwo = (TextView) view.findViewById(R.id.recommendedbooksauthortwo);
        recommendedBooksAuthorThree = (TextView) view.findViewById(R.id.recommendedbooksauthorthree);
        recommendedBooksAuthorFour = (TextView) view.findViewById(R.id.recommendedbooksauthorfour);
        recommendedBooksAuthorFive = (TextView) view.findViewById(R.id.recommendedbooksauthorfive);
        recommendedBooksAuthorSix = (TextView) view.findViewById(R.id.recommendedbooksauthorsix);
        recommendedBooksAuthorSeven = (TextView) view.findViewById(R.id.recommendedbooksauthorseven);
        recommendedBooksAuthorEight = (TextView) view.findViewById(R.id.recommendedbooksauthoreight);
        recommendedBooksAuthorNine = (TextView) view.findViewById(R.id.recommendedbooksauthornine);
        recommendedBooksAuthorTen = (TextView) view.findViewById(R.id.recommendedbooksauthorten);

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
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(0).getMaterialID());
                bundle.putString("titleString", myBooksList.get(0).getTitle());
                bundle.putString("authorString", myBooksList.get(0).getAuthor());
                bundle.putString("typeString", myBooksList.get(0).getType());
                bundle.putString("descriptionString", myBooksList.get(0).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(0).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(1).getMaterialID());
                bundle.putString("titleString", myBooksList.get(1).getTitle());
                bundle.putString("authorString", myBooksList.get(1).getAuthor());
                bundle.putString("typeString", myBooksList.get(1).getType());
                bundle.putString("descriptionString", myBooksList.get(1).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(1).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(2).getMaterialID());
                bundle.putString("titleString", myBooksList.get(2).getTitle());
                bundle.putString("authorString", myBooksList.get(2).getAuthor());
                bundle.putString("typeString", myBooksList.get(2).getType());
                bundle.putString("descriptionString", myBooksList.get(2).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(2).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(3).getMaterialID());
                bundle.putString("titleString", myBooksList.get(3).getTitle());
                bundle.putString("authorString", myBooksList.get(3).getAuthor());
                bundle.putString("typeString", myBooksList.get(3).getType());
                bundle.putString("descriptionString", myBooksList.get(3).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(3).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(4).getMaterialID());
                bundle.putString("titleString", myBooksList.get(4).getTitle());
                bundle.putString("authorString", myBooksList.get(4).getAuthor());
                bundle.putString("typeString", myBooksList.get(4).getType());
                bundle.putString("descriptionString", myBooksList.get(4).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(4).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(5).getMaterialID());
                bundle.putString("titleString", myBooksList.get(5).getTitle());
                bundle.putString("authorString", myBooksList.get(5).getAuthor());
                bundle.putString("typeString", myBooksList.get(5).getType());
                bundle.putString("descriptionString", myBooksList.get(5).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(5).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(6).getMaterialID());
                bundle.putString("titleString", myBooksList.get(6).getTitle());
                bundle.putString("authorString", myBooksList.get(6).getAuthor());
                bundle.putString("typeString", myBooksList.get(6).getType());
                bundle.putString("descriptionString", myBooksList.get(6).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(6).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(7).getMaterialID());
                bundle.putString("titleString", myBooksList.get(7).getTitle());
                bundle.putString("authorString", myBooksList.get(7).getAuthor());
                bundle.putString("typeString", myBooksList.get(7).getType());
                bundle.putString("descriptionString", myBooksList.get(7).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(7).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(8).getMaterialID());
                bundle.putString("titleString", myBooksList.get(8).getTitle());
                bundle.putString("authorString", myBooksList.get(8).getAuthor());
                bundle.putString("typeString", myBooksList.get(8).getType());
                bundle.putString("descriptionString", myBooksList.get(8).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(8).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        myBooksImageTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myBooksList.get(9).getMaterialID());
                bundle.putString("titleString", myBooksList.get(9).getTitle());
                bundle.putString("authorString", myBooksList.get(9).getAuthor());
                bundle.putString("typeString", myBooksList.get(9).getType());
                bundle.putString("descriptionString", myBooksList.get(9).getDescription());
                bundle.putString("thumbnailURL", myBooksList.get(9).getMaterialThumbnailURL());
                intent.putExtras(bundle);
                context.startActivity(intent);
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

        schoolCount = 0;
        myBooksList.clear();

        for (i = 0; i < myBooksLinearLayoutList.size(); i++) {
            myBooksLinearLayoutList.get(i).setVisibility(View.GONE);
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Student School").child(activeStudentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

                                        for (i = 0; i < myBooksList.size(); i++) {
                                            myBooksLinearLayoutList.get(i).setVisibility(View.VISIBLE);
                                            myBooksTitleTextViewList.get(i).setText(myBooksList.get(i).getTitle());
                                            myBooksAuthorTextViewList.get(i).setText(myBooksList.get(i).getAuthor());
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

    private void loadRecommendedBooksFromFirebase() {

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
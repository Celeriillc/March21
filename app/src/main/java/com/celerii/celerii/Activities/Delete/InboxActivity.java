package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Inbox.Teacher.TeacherMessageHome;
import com.celerii.celerii.adapters.InboxAdapter;
import com.celerii.celerii.models.MessageList;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    private ArrayList<MessageList> inboxList;
    public RecyclerView recyclerView;
    public InboxAdapter mAdapter;
    public FloatingActionButton fab;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        inboxList = new ArrayList<>();
        yeah();
        mAdapter = new InboxAdapter(inboxList, this);
        recyclerView.setAdapter(mAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(InboxActivity.this, TeacherMessageHome.class);
                startActivity(I);
            }
        });
    }

    void yeah(){
//        MessageList ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://i.onthe.io/vllkyt4c8haautcss.d62e45bb.jpg", 90);
//        inboxList.add(ml);
//
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://travel.jumia.com/blog/ng/wp-content/uploads/2015/10/oma-4-660x400.png", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "http://beautifulng.com/wp-content/uploads/2016/04/Natural-hair.jpg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://i0.wp.com/otownloaded.com/wp-content/uploads/2016/01/rita.jpeg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://i.onthe.io/vllkyt4c8haautcss.d62e45bb.jpg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://travel.jumia.com/blog/ng/wp-content/uploads/2015/10/oma-4-660x400.png", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "http://beautifulng.com/wp-content/uploads/2016/04/Natural-hair.jpg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://i.onthe.io/vllkyt4c8haautcss.d62e45bb.jpg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://travel.jumia.com/blog/ng/wp-content/uploads/2015/10/oma-4-660x400.png", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://i0.wp.com/otownloaded.com/wp-content/uploads/2016/01/rita.jpeg", 90);
//        inboxList.add(ml);
//        ml = new MessageList("Esther Oriabure", "We hope you'll be availabe for the open day, well love to have you", "3h", "https://i.onthe.io/vllkyt4c8haautcss.d62e45bb.jpg", 90);
//        inboxList.add(ml);
    }
}

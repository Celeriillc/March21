package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.celerii.celerii.Activities.Settings.FAQActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.FAQModel;

import java.util.List;

/**
 * Created by DELL on 8/12/2017.
 */

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyViewHolder>{

    private List<FAQModel> faqModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView header;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.faqheader);
            clickableView = view;
        }
    }

    public FAQAdapter(List<FAQModel> faqModelList, Context context) {
        this.faqModelList = faqModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FAQModel faqModel = faqModelList.get(position);

        holder.header.setText(faqModel.getQuestion());
        final String header = faqModel.getQuestion();
        final String body = faqModel.getAnswer();


        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("header", header);
                bundle.putString("body", body);
                Intent I = new Intent(context, FAQActivity.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqModelList.size();
    }
}

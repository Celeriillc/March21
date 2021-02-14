package com.celerii.celerii.helperClasses;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.celerii.celerii.models.ClassStory;

import java.util.List;

public class ClassStoryDiffUtil extends DiffUtil.Callback{

    List<ClassStory> oldList;
    List<ClassStory> newList;

    public ClassStoryDiffUtil(List<ClassStory> newList, List<ClassStory> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getPostID() == newList.get(newItemPosition).getPostID();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

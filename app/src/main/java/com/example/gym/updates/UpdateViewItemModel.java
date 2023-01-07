package com.example.gym.updates;

import java.util.Date;

// The object in the fragment's list
public class UpdateViewItemModel extends Update implements Comparable<UpdateViewItemModel> {
    private boolean isSectionHeader = false;

    public UpdateViewItemModel(Date date) {
        isSectionHeader = true;
        this.date = date;
    }
    public UpdateViewItemModel(){

    }

    public UpdateViewItemModel(Update update){
        this.date = update.date;
        this.content = update.content;
        this.id = update.id;
    }

    public boolean isSectionHeader() {
        return isSectionHeader;
    }

    @Override
    public int compareTo(UpdateViewItemModel itemModel) {
        return this.date.compareTo(itemModel.date);
    }
}

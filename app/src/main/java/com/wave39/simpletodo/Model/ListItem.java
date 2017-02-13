package com.wave39.simpletodo.Model;

import java.util.ArrayList;

/**
 * ListItem
 * Created by bp on 2/7/17.
 */

public class ListItem {
    public int listItemID;
    public String listItemString;
    public int sortOrder;

    public ListItem() { }

    public ListItem(String s) {
        this.listItemString = s;
    }

    @Override
    public String toString() {
        return listItemString; //+ " " + sortOrder;
    }

    static public void reorder(ArrayList<ListItem> arrayList) {
        int ctr = 0;
        for (ListItem item : arrayList) {
            item.sortOrder = ctr;
            ctr++;
        }
    }
}

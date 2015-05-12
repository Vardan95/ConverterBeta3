package com.example.vpetrosyan.converterbeta3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by vpetrosyan on 27.04.2015.
 */
public class SortedList extends ArrayList<String>
{
    public void sort() {
        Collections.sort(this, new Comparator<String>() {
            @Override
            public int compare(String item1, String item2) {
                return item1.compareTo(item2);
            }
        });
    }
}

package com.example.mrr.final_project_mobile_programming.Utilities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UtilityListViewHeight {

    public static void setListViewHeightBasedOnItemsInList(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);
            int measureSize = View.MeasureSpec.makeMeasureSpec(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            listItem.measure(measureSize , measureSize);
            totalHeight += listItem.getMeasuredHeight();
            System.out.println(listItem.getMeasuredHeight());
        }

        System.out.println("TOTAL HIGH: " + totalHeight);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}

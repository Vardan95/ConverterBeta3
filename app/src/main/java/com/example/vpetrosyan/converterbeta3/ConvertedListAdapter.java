package com.example.vpetrosyan.converterbeta3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vpetrosyan on 08.05.2015.
 */
public class ConvertedListAdapter extends BaseAdapter {

   private Context context;
   private String[] data;
   private ArrayList<Double> values_;
   private static LayoutInflater inflater = null;

    public ConvertedListAdapter(Context context,String[] data,ArrayList<Double> values)
    {
        this.context = context;
        this.data = data;
        this.values_ = new ArrayList<>();

        for(int i = 0; i < values.size(); ++i)
        {
            values_.add(values.get(i));
        }

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setNewConvertedValues(ArrayList<Double> newValues)
    {
        if(values_ != null) {
            if (values_.size() == 0) {
                values_.clear();
            }

            if(data.length != newValues.size()){
                return;
            }

            for(int i = 0; i < newValues.size(); ++i)
            {
                values_.set(0,newValues.get(i));
            }

            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return String.valueOf(values_.get(position)) + data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.my_list_item, null);

        if (position != -1 && position != values_.size()) {

            TextView value_text = (TextView) vi.findViewById(R.id.item_convert_value);
            value_text.setText(String.valueOf(values_.get(position)));

            TextView to_item = (TextView) vi.findViewById(R.id.item_convert_to);
            to_item.setText(data[position]);
        }
        return vi;
    }
}
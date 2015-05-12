package com.example.vpetrosyan.converterbeta3;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by vpetrosyan on 08.05.2015.
 */
public class CategoryView extends Fragment {

    public CategoryView() {
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d("category view", "onAttach");
        context_ = activity;
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("category view", "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("category view","onCreateView");

        View rootView = inflater.inflate(R.layout.main_converter_category_view, container, false);

        entered_value = (EditText)rootView.findViewById(R.id.cc_convert_value_txt);
        converted_list = (ListView)rootView.findViewById(R.id.converted_list);
        units_spinner_ = (Spinner)rootView.findViewById(R.id.category_units);

        units_spinner_.setMinimumWidth(entered_value.getWidth());

        setSpinnerData();
        units_spinner_.setSelection(0);

        entered_value.setText("1");

        value_to_convert = 1.0;

        convertUnitName_ = new String("");

        executeConvertAction();

        setListData();

        entered_value.addTextChangedListener(edit_watcher);

        converted_list.setOnItemLongClickListener(long_click_listener);

        return rootView;
    }

    private void setListData() {
        if(list_adapter == null)
        {
            String[] units = new String[units_.size() + 1];

            for(int i=0; i < units_.size(); ++i)
            {
                units[i] = units_.get(i);
            }

            list_adapter = new ConvertedListAdapter(context_,units,convertedValues_);
        }

        converted_list.setAdapter(list_adapter);
        list_adapter.notifyDataSetChanged();
    }

    private void setSpinnerData() {

        if(countrylist_ == null) {
            countrylist_ = new SortedList();
        }

        ArrayList<String>  tempCountry = converter.getSupportedCountries();

        for(int i = 0; i < tempCountry.size(); ++i)
        {
            countrylist_.add(tempCountry.get(i));
        }

        countrylist_.sort();

        ArrayAdapter<String> countyAdapter_= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,countrylist_);

        ((ArrayAdapter<String>) countyAdapter_).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        units_spinner_.setAdapter(countyAdapter_);

        ((ArrayAdapter<String>) countyAdapter_).notifyDataSetChanged();

        units_spinner_.setOnItemSelectedListener(selectCountryListener);
    }

    public void setTable(UnitCategory category)
    {
        if(this.category_ == null) {
            this.category_ = new UnitCategory(category.getCategoryName(), category.getBaseName());
            this.category_.assign(category);
        }
    }

    public void setUnits(ArrayList<String> units)
    {
        if(units_ == null)
        {
            this.units_ = new ArrayList<String>();
        }

        for(int i = 0; i < units.size(); ++i)
        {
            this.units_.add(units.get(i));
        }
    }

    public void setBaseUnit(String base)
    {
        this.baseUnit_ = base;
    }

    private UnitCategory category_;
    private ArrayList<String> units_;
    private String baseUnit_;
    private Double value_to_convert;
    private String convertUnitName_;
    private ArrayList<Double> convertedValues_;

    private EditText entered_value;
    private ListView converted_list;
    private Spinner units_spinner_;

    private Activity context_;

    SortedList countrylist_;


    private final UnitConverter converter = ConverterEngine.getUnitConverter();

    //TODO change to my adapter
    private ConvertedListAdapter list_adapter;

    private TextWatcher edit_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d("text watcher","beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //TODO implement
            Log.d("text watcher","onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (entered_value.getText().toString().isEmpty())
            {
                entered_value.setText("1");
                value_to_convert = 1.0;
            }
            else
            {
                value_to_convert = Double.valueOf(entered_value.getText().toString());
                executeConvertAction();
            }
        }
    };

    private Spinner.OnItemSelectedListener selectCountryListener = new Spinner.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position != -1) {
                convertUnitName_ = parent.getItemAtPosition(position).toString();
                executeConvertAction();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            convertUnitName_ = parent.getItemAtPosition(0).toString();
            executeConvertAction();
        }
    };

    private void executeConvertAction() {

        if(convertedValues_ == null)
        {
            convertedValues_ = new ArrayList<>();
        }

        if(convertedValues_.size() != 0)
        {
            convertedValues_.clear();
        }

        if(convertUnitName_.isEmpty())
        {
            convertUnitName_ = baseUnit_;
        }

        if(!convertUnitName_.isEmpty()){
        for(int i = 0; i < units_.size(); ++i)
             {
                convertedValues_.add(category_.convert(convertUnitName_,units_.get(i),value_to_convert));
             }

            if(list_adapter != null) {
                list_adapter.setNewConvertedValues(convertedValues_);
            }
        }
    }

    private AdapterView.OnItemLongClickListener long_click_listener = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(category_.getCategoryName().equalsIgnoreCase(Constants.CONVERTER_CURRENCY_CATEGORY_TITLE))
            {
                 String countryName = converter.getCountryNameFromCurrencyCode(countrylist_.get(position));
                 Toast.makeText(context_,countryName,Toast.LENGTH_SHORT).show();
                 return false;
            }
            return true;
        }
    };
}
package com.example.vpetrosyan.converterbeta3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by vpetrosyan on 06.05.2015.
 */
public class CurrencyConverterFragment extends Fragment implements UnitConverter.updateCurrencyRatesListener {

    public CurrencyConverterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.currency_converter_fragment, container, false);

        converter_btn = (Button) rootView.findViewById(R.id.cc_converter_sub_fragment_button);
        location_btn = (Button) rootView.findViewById(R.id.cc_location_sub_fragment_button);

        converter_btn.setOnClickListener(converter_listener);
        location_btn.setOnClickListener(location_listener);

        isLocationFragment_ = false;

        ConverterEngine.getUnitConverter().registerCurrencyUpdateListener(this);

        return rootView;
    }

    private Button converter_btn;
    private Button location_btn;
    private LocationSubFragment location_fragment;
    private DummySectionFragment empty_category_fragment;
    private  boolean isLocationFragment_;
    private CategoryView currencyCategoryView;

    private Button.OnClickListener converter_listener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            if(isLocationFragment_) {

                converter_btn.setBackgroundColor(getResources().getColor(R.color.my_green));
                location_btn.setBackgroundColor(getResources().getColor(R.color.my_red));

                if (getActivity().findViewById(R.id.cc_fragment_container) != null) {

                    if ((location_fragment != null) && location_fragment.isVisible()) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                        ft.remove(location_fragment);
                        ft.commit();
                    }

                    if(ConverterEngine.getUnitConverter().getCategoryList().contains(Constants.CONVERTER_CURRENCY_CATEGORY_TITLE) == false)
                    {
                        if(empty_category_fragment == null)
                        {
                            {
                                empty_category_fragment = new DummySectionFragment();
                            }

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                            ft.add(R.id.cc_fragment_container, empty_category_fragment);
                            ft.commit();
                        }
                    }
                    else
                    {

                        if(currencyCategoryView == null) {
                            currencyCategoryView = new CategoryView();
                        }

                        UnitCategory currencyCategory = ConverterEngine.getUnitConverter().getCategory(Constants.CONVERTER_CURRENCY_CATEGORY_TITLE);
                        if(currencyCategory != null)
                        {
                            currencyCategoryView.setBaseUnit(currencyCategory.getBaseName());
                            currencyCategoryView.setTable(currencyCategory);
                            currencyCategoryView.setUnits(currencyCategory.getAllUnits());

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom);

                            if(location_fragment != null && location_fragment.isVisible()) {
                                ft.remove(location_fragment);
                            }
                            if(empty_category_fragment != null && empty_category_fragment.isVisible()) {
                                ft.remove(empty_category_fragment);
                            }
                            ft.add(R.id.cc_fragment_container, currencyCategoryView);
                            ft.commit();

                        }

                    }

                }

                isLocationFragment_ = false;
            }
        }
    };

    private Button.OnClickListener location_listener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
             if(!isLocationFragment_) {
                 location_btn.setBackgroundColor(getResources().getColor(R.color.my_green));
                 converter_btn.setBackgroundColor(getResources().getColor(R.color.my_red));

                 if (location_fragment == null) {
                     location_fragment = new LocationSubFragment();
                 }

                 if (getActivity().findViewById(R.id.cc_fragment_container) != null) {

                     if((empty_category_fragment != null) && empty_category_fragment.isVisible())
                     {
                         FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                         ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                         ft.remove(empty_category_fragment);
                         ft.commit();
                     }

                     if((currencyCategoryView != null) && currencyCategoryView.isVisible())
                     {
                         FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                         ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                         ft.remove(currencyCategoryView);
                         ft.commit();
                     }

                     FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                     ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                     ft.add(R.id.cc_fragment_container, location_fragment);
                     ft.commit();
                 }

                 isLocationFragment_ = true;
             }
        }
    };

    @Override
    public void onCurrencyUpdated(String currencyCategoryName) {
        converter_btn.setBackgroundColor(getResources().getColor(R.color.my_green));
        location_btn.setBackgroundColor(getResources().getColor(R.color.my_red));

        if (getActivity().findViewById(R.id.cc_fragment_container) != null) {

                if ((location_fragment != null) && location_fragment.isVisible()) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                    ft.remove(location_fragment);
                    ft.commit();
                }

                if(ConverterEngine.getUnitConverter().getCategoryList().contains(Constants.CONVERTER_CURRENCY_CATEGORY_TITLE) == false)
                {
                    if(empty_category_fragment == null)
                    {
                        {
                            empty_category_fragment = new DummySectionFragment();
                        }

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom,R.anim.abc_shrink_fade_out_from_bottom);
                        ft.add(R.id.cc_fragment_container, empty_category_fragment);
                        ft.commit();
                    }
                }
                else
                {

                    if(currencyCategoryView == null) {
                        currencyCategoryView = new CategoryView();
                    }

                    UnitCategory currencyCategory = ConverterEngine.getUnitConverter().getCategory(Constants.CONVERTER_CURRENCY_CATEGORY_TITLE);
                    if(currencyCategory != null)
                    {
                        currencyCategoryView.setBaseUnit(currencyCategory.getBaseName());
                        currencyCategoryView.setTable(currencyCategory);
                        currencyCategoryView.setUnits(currencyCategory.getAllUnits());

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom);

                        if(location_fragment != null && location_fragment.isVisible()) {
                            ft.remove(location_fragment);
                        }
                        if(empty_category_fragment != null && empty_category_fragment.isVisible()) {
                            ft.remove(empty_category_fragment);
                        }
                        ft.add(R.id.cc_fragment_container, currencyCategoryView);
                        ft.commit();
                    }

                }

            }

            isLocationFragment_ = false;
    }
}
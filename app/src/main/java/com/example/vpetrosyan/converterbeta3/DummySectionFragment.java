package com.example.vpetrosyan.converterbeta3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by vpetrosyan on 08.05.2015.
 */
public class DummySectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public DummySectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);

        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        dummyTextView.setVisibility(View.GONE);

        dummyTextView.setText(getString(R.string.update_null_warning));

        dummyTextView.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        dummyTextView.startAnimation(anim);


        return rootView;
    }
}
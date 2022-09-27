package com.example.tour_it_app.fragments.others;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.tour_it_app.R;

public class SettingsFragment extends Fragment {

    private RadioButton rbMetric;
    private RadioButton rbPopular;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //Finding Id's
        rbMetric = getView().findViewById(R.id.rbMetric);
        rbPopular = getView().findViewById(R.id.rbPopular);

        //Setting default selection for radio buttons
        rbMetric.setChecked(true);
        rbPopular.setChecked(true);
    }


}
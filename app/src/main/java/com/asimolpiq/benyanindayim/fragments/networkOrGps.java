package com.asimolpiq.benyanindayim.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimolpiq.benyanindayim.R;
import com.asimolpiq.benyanindayim.databinding.FragmentNetworkOrGpsBinding;


public class networkOrGps extends Fragment {

    private FragmentNetworkOrGpsBinding binding;
    private SharedPreferences sharedPreferences;
    private Boolean toggle;
    public networkOrGps() {
        // Required empty public constructor
    }

    public static networkOrGps newInstance() {
        networkOrGps fragment = new networkOrGps();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("com.asimolpiq.benyanindayim", Context.MODE_PRIVATE);
        toggle = sharedPreferences.getBoolean("location_provider",false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNetworkOrGpsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.networkOrGpsButton.setChecked(toggle);
        binding.networkOrGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.networkOrGpsButton.isChecked()){
                    sharedPreferences.edit().putBoolean("location_provider",false).apply();

                }
                else{
                    sharedPreferences.edit().putBoolean("location_provider",true).apply();

                }
            }
        });

    }
}
package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskmaster.databinding.HomeBinding;

import java.util.Objects;

public class Home extends Fragment {

    private HomeBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = HomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addTask.setOnClickListener(view1 -> NavHostFragment.findNavController(Home.this)
                .navigate(R.id.action_Home_to_AddTask));

        binding.allTasks.setOnClickListener(view12 -> NavHostFragment.findNavController(Home.this)
                .navigate(R.id.action_Home_to_AllTasks));

        binding.shopButton.setOnClickListener(view13 -> {
            Intent shopDetail = new Intent(getActivity(),TaskDetail.class);
            shopDetail.putExtra("title", "Shopping");
            requireActivity().startActivity(shopDetail);
        });
        binding.trainButton.setOnClickListener(view13 -> {
            Intent trainDetail = new Intent(getActivity(),TaskDetail.class);
            trainDetail.putExtra("title", "Training");
            requireActivity().startActivity(trainDetail);
        });
        binding.studyButton.setOnClickListener(view13 -> {
            Intent studyDetail = new Intent(getActivity(),TaskDetail.class);
            studyDetail.putExtra("title", "Studying");
            requireActivity().startActivity(studyDetail);
        });

        binding.settingsButton.setOnClickListener(view14 -> {
            Intent settings = new Intent(getActivity(),Settings.class);
            requireActivity().startActivity(settings);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //homePageTitle
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.i("HOME", "onResume: " + preferences.getString("userName", "My Tasks"));

        binding.homePageTitle.setText(preferences.getString("userName", "My Tasks") + "'s Tasks");
    }
}
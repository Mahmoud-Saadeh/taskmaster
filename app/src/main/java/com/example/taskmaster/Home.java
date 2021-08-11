//package com.example.taskmaster;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.taskmaster.databinding.HomeBinding;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class Home extends Fragment {
//
//    private HomeBinding binding;
//    private List<Task> taskList = new ArrayList<>();
//
////    public Home(List<Task> taskList) {
////        this.taskList = taskList;
////    }
//
//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState
//    ) {
//
//        binding = HomeBinding.inflate(inflater, container, false);
//
////        getView().setContentView(R.layout.task_list);
//
//        return binding.getRoot();
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        binding.addTask.setOnClickListener(view1 -> NavHostFragment.findNavController(Home.this)
////                .navigate(R.id.action_Home_to_AddTask));
////
////        binding.allTasks.setOnClickListener(view12 -> NavHostFragment.findNavController(Home.this)
////                .navigate(R.id.action_Home_to_AllTasks));
////
////        binding.shopButton.setOnClickListener(view13 -> {
////            Intent shopDetail = new Intent(getActivity(),TaskDetail.class);
////            shopDetail.putExtra("title", "Shopping");
////            requireActivity().startActivity(shopDetail);
////        });
////        binding.trainButton.setOnClickListener(view13 -> {
////            Intent trainDetail = new Intent(getActivity(),TaskDetail.class);
////            trainDetail.putExtra("title", "Training");
////            requireActivity().startActivity(trainDetail);
////        });
////        binding.studyButton.setOnClickListener(view13 -> {
////            Intent studyDetail = new Intent(getActivity(),TaskDetail.class);
////            studyDetail.putExtra("title", "Studying");
////            requireActivity().startActivity(studyDetail);
////        });
//
//        RecyclerView taskRecyclerView = requireView().findViewById(R.id.task_list);
//
//        taskList.add(new Task("Do 100 push-ups","Training", TaskStates.IN_PROGRESS));
//        taskList.add(new Task("Solve ASAC assignments","Studying", TaskStates.COMPLETE));
//        taskList.add(new Task("Buy Coffee","Shopping", TaskStates.ASSIGNED));
//        taskList.add(new Task("play CS GO","Playing", TaskStates.NEW));
//        taskList.add(new Task("Eat dinner","Eating", TaskStates.COMPLETE));
//
//        TaskViewAdapter adapter = new TaskViewAdapter(taskList, new TaskViewAdapter.OnTaskItemClickListener() {
//            @Override
//            public void onItemClicked(int position) {
//                Intent goToDetailsIntent = new Intent(getActivity(), TaskDetail.class);
//                goToDetailsIntent.putExtra("task_title", taskList.get(position).getTitle());
//                goToDetailsIntent.putExtra("task_body", taskList.get(position).getBody());
//                goToDetailsIntent.putExtra("task_state", taskList.get(position).getState().toString());
//                startActivity(goToDetailsIntent);
//            }
//        });
//
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
//                getActivity(),
//                LinearLayoutManager.VERTICAL,
//                false);
//
//        taskRecyclerView.setLayoutManager(linearLayoutManager);
//        taskRecyclerView.setAdapter(adapter);
//
//        binding.settingsButton.setOnClickListener(view14 -> {
//            Intent settings = new Intent(getActivity(),Settings.class);
//            requireActivity().startActivity(settings);
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //homePageTitle
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        Log.i("HOME", "onResume: " + preferences.getString("userName", "My Tasks"));
//
//        binding.homePageTitle.setText(preferences.getString("userName", "My Tasks") + "'s Tasks");
//    }
//}
package com.example.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.ViewHolder> {

    private final List<Task> taskList;
    private OnTaskItemClickListener listener;

    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
//        void onDeleteItem(int position);
    }

    public TaskViewAdapter(List<Task> taskList, OnTaskItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.state.setText(task.getState().toString());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private TextView body;
        private final TextView state;


        ViewHolder(@NonNull View itemView, OnTaskItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.task_title);
            state = itemView.findViewById(R.id.task_state);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }
}

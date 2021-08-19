//package com.example.taskmaster.room;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.Query;
//
////import com.example.taskmaster.Task;
//
//import com.amplifyframework.datastore.generated.model.Task;
//
//import java.util.List;
//
//@Dao
//public interface TaskDao {
//    @Insert
//    void addTask(Task task);
//
//    @Query("SELECT * FROM Task")
//    List<Task> findAll();
//
//    @Query("SELECT * FROM Task WHERE uid LIKE :id")
//    Task findTaskByUid(Long id);
//}

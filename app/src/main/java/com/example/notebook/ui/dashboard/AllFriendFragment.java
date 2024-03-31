package com.example.notebook.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.notebook.databinding.FragmentAllFriendsBinding;
import com.example.notebook.room.AppDatabase;
import com.example.notebook.room.StudentDao;

public class AllFriendFragment extends Fragment {

    private FragmentAllFriendsBinding binding;

    private AppDatabase appDatabase;
    private StudentDao studentDao;
    private  StudentAdapter studentAdapter;
    RecyclerView rv_note_book;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rv_note_book=binding.rvNoteBook;
        studentAdapter=new StudentAdapter();
        rv_note_book.setAdapter(studentAdapter);

        appDatabase= Room.databaseBuilder(binding.getRoot().getContext()
                ,AppDatabase.class,"database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        studentDao=appDatabase.studentDao();
        studentAdapter.setList(studentDao.getAll());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.notebook.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.example.notebook.MainActivity;
import com.example.notebook.R;
import com.example.notebook.databinding.FragmentInputBinding;
import com.example.notebook.models.Student;
import com.example.notebook.room.AppDatabase;
import com.example.notebook.room.StudentDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InputFragment extends Fragment {

    private FragmentInputBinding binding;
    private AppDatabase appDatabase;
    private StudentDao studentDao;
    private Bitmap bitmap_imageStudent;
    private ActivityResultLauncher<String> content_l;
    private boolean isImgSelected=false;
    NavController navController;


    public View onCreateView(@NonNull LayoutInflater inflater,
                                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInputBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnLoadFoto.setOnClickListener(v1 -> {
            InputFragment.this.content_l.launch("image/*");
        });
        content_l=registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        try {
                            bitmap_imageStudent= MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getContext().getContentResolver(),result);

                            binding.imageFromForm.setImageBitmap((bitmap_imageStudent));
                            isImgSelected=true;
                        }catch (IOException error){
                            error.printStackTrace();
                            isImgSelected=false;
                        }
                    }
                });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSave.setOnClickListener(v2 -> {
            String nameSurnameStudent=binding.editNameSurname.getText().toString();
            String telStudent=binding.editTelNubmer.getText().toString();

            if(nameSurnameStudent.isEmpty() || telStudent.isEmpty()){
                Toast.makeText(requireActivity(),"Заполните поля ИМЯ КОНТАКТЫ",Toast.LENGTH_LONG).show();
                isImgSelected=false;
            }else {
                if(isImgSelected){
                    ByteArrayOutputStream baos_imageStudent=new ByteArrayOutputStream();
                    bitmap_imageStudent.compress(Bitmap.CompressFormat.PNG,100,baos_imageStudent);

                    byte[] imageStudent=baos_imageStudent.toByteArray();

                    Student student= new Student(nameSurnameStudent,telStudent,imageStudent);
                    this.appDatabase= Room.databaseBuilder(binding.getRoot().getContext(),
                            AppDatabase.class,"database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
                    studentDao= appDatabase.studentDao();
                    studentDao.insert(student);
                    Intent intent= new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    navController= Navigation.findNavController(requireActivity(), R.id.nav_host);
                    navController.navigate(R.id.action_navigation_input_to_navigation_all_friends);
                }else {
                    Toast.makeText(requireActivity(),"Upload photo",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
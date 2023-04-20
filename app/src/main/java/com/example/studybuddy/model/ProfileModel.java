package com.example.studybuddy.model;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studybuddy.model.api.RetrofitClient;
import com.example.studybuddy.objects.Student;
import com.example.studybuddy.objects.Teacher;
import com.example.studybuddy.viewModel.ProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModel {

    Activity activity;
    private FirebaseUser user;
    private FirebaseFirestore db;

    public FirebaseFirestore getDb() {
        return db;
    }

    public ProfileModel (Activity activity){
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
        this.user  = FirebaseAuth.getInstance().getCurrentUser();
    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void modelOnStart(EditText name, EditText age, EditText year, EditText degree, ProfileActivity thisactivity) {

        Call<Teacher> call = RetrofitClient.getInstance().getAPI().getTeacherDetails(user.getUid());
        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                Teacher curr_teacher;
                curr_teacher = response.body();
                String nameResult = curr_teacher.getName();
                String ageResult = curr_teacher.getAge();
                String yearResult = curr_teacher.getYear();
                String degreeResult = curr_teacher.getDegree();
                name.setText(nameResult);
                age.setText(ageResult);
                year.setText(yearResult);
                degree.setText(degreeResult);
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                Log.d("Fail", t.getMessage());
                Toast.makeText(thisactivity, "no profile yet" , Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void updateData(String date_and_time) {
//        db.collection("teachers")
//                .document(user.getUid())
//                .update("dates", FieldValue.arrayUnion(date_and_time));
    }

    public void updateProfileModel(String textName, String textYear, String textDegree, String textGender, String textAge, String textPhone, String textPayBox){

        assert user != null;

        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().updateTeacherDetails(user.getUid(), textName, textYear, textDegree, textGender, textAge, textPhone, textPayBox);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("done", "done");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }
}

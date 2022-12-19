package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    EditText name, degree, year;
//    Button save;
//    FirebaseDatabase database =  FirebaseDatabase.getInstance();
//    DatabaseReference reference;
//    DocumentReference documentReference;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        name = findViewById(R.id.name);
//        degree = findViewById(R.id.degree);
//        year = findViewById(R.id.year);
//        save = findViewById(R.id.save);




import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studybuddy.User;
import com.example.studybuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class StudentProfileActivity extends AppCompatActivity {

    EditText name, degree, year, age;
    Button save, add_courses;
    RadioGroup gender_group;
    RadioButton gender;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUID = user.getUid();
        documentReference = db.collection("students").document(userUID);


        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        degree = findViewById(R.id.degree);
        year = findViewById(R.id.year);
        save = findViewById(R.id.save);
        gender_group = findViewById(R.id.gender_group);


        //onclick listener for updating profile button
        save.setOnClickListener(v -> {
            //Converting fields to text
            int radioID = gender_group.getCheckedRadioButtonId();
            gender = findViewById(radioID);
            String textGender = gender.getText().toString();
            String textAge = age.getText().toString();
            String textName = name.getText().toString();
            String textDegree = degree.getText().toString();
            String textYear = year.getText().toString();

            if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textDegree) || TextUtils.isEmpty(textYear) || TextUtils.isEmpty(textGender) || TextUtils.isEmpty(textAge)) {
                Toast.makeText(StudentProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                updateProfile(textName, textYear, textDegree, textGender, textAge, user, db);
                startActivity(new Intent(this, StudentHomeActivity.class));
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String nameResult = task.getResult().getString("name");
                            String ageResult = task.getResult().getString("age");
                            String yearResult = task.getResult().getString("year");
                            String degreeResult = task.getResult().getString("degree");
                            name.setText(nameResult);
                            age.setText(ageResult);
                            year.setText(yearResult);
                            degree.setText(degreeResult);
                        }else{
                            Toast.makeText(StudentProfileActivity.this, "no profile yet" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateProfile(String textName, String textYear, String textDegree, String textGender, String textAge, FirebaseUser user, FirebaseFirestore database) {

        assert user != null;
        String userUID = user.getUid();

        User userToAdd = new User(textName, textYear, textDegree, textGender, textAge, userUID); //creating a new user
        database.collection("students").document(userUID).set(userToAdd); //adding user data to database

        Toast.makeText(StudentProfileActivity.this, "Updated Profile successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StudentProfileActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_student_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_classes:
                startActivity(new Intent(StudentProfileActivity.this, StudentHomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(StudentProfileActivity.this, StudentProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(StudentProfileActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_book_a_class:
                startActivity(new Intent(StudentProfileActivity.this, BookClass.class));
                finish();
                return true;
            case R.id.action_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(StudentProfileActivity.this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
package com.akila.treasureplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.akila.treasureplant.model.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.signupToSignIn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        findViewById(R.id.signupButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.signupEditTextEmail);
                EditText fname = findViewById(R.id.signupEditTextFirstName);
                EditText lname = findViewById(R.id.signupEditTextLastName);
                EditText password = findViewById(R.id.signupEditTextPasswordd);

                TextInputLayout emailTextInputLayout = findViewById(R.id.signUpEmailTextInputLayout);
                TextInputLayout firstNameTextInputLayout = findViewById(R.id.signUpFNameTextInputLayout);
                TextInputLayout lastNameTextInputLayout = findViewById(R.id.signUpLNameTextInputLayout);
                TextInputLayout passwordNameTextInputLayout = findViewById(R.id.signUpPasswordTextInputLayout);

                if (String.valueOf(fname.getText()).isEmpty()){
                    firstNameTextInputLayout.setErrorEnabled(true);
                    firstNameTextInputLayout.setError("Please Enter Your First Name");

                    emailTextInputLayout.setErrorEnabled(false);
                    lastNameTextInputLayout.setErrorEnabled(false);
                    passwordNameTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(lname.getText()).isEmpty()) {
                    lastNameTextInputLayout.setErrorEnabled(true);
                    lastNameTextInputLayout.setError("Please Enter Your Last Name");

                    emailTextInputLayout.setErrorEnabled(false);
                    firstNameTextInputLayout.setErrorEnabled(false);
                    passwordNameTextInputLayout.setErrorEnabled(false);
                } else if (!Validations.isEmailValid(String.valueOf(email.getText()))) {
                    emailTextInputLayout.setErrorEnabled(true);
                    emailTextInputLayout.setError("Please Enter Valid Email Address");

                    lastNameTextInputLayout.setErrorEnabled(false);
                    firstNameTextInputLayout.setErrorEnabled(false);
                    passwordNameTextInputLayout.setErrorEnabled(false);
                } else if (!Validations.isPassowordValid(String.valueOf(password.getText()))) {
                    passwordNameTextInputLayout.setErrorEnabled(true);
                    passwordNameTextInputLayout.setError("Please Enter Valid Password");

                    lastNameTextInputLayout.setErrorEnabled(false);
                    firstNameTextInputLayout.setErrorEnabled(false);
                    emailTextInputLayout.setErrorEnabled(false);
                } else {

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    firestore.collection("user")
                            .where(Filter.equalTo("email", String.valueOf(email.getText())))
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                                    if (documentSnapshotList.isEmpty()) {
                                        emailTextInputLayout.setErrorEnabled(false);
                                        lastNameTextInputLayout.setErrorEnabled(false);
                                        firstNameTextInputLayout.setErrorEnabled(false);
                                        passwordNameTextInputLayout.setErrorEnabled(false);

                                        int id = new Random().nextInt(10000000);

                                        HashMap<String, Object> document = new HashMap<>();
                                        document.put("email", String.valueOf(email.getText()));
                                        document.put("fname", String.valueOf(fname.getText()));
                                        document.put("lname", String.valueOf(lname.getText()));
                                        document.put("id", id);
                                        document.put("isGoogle", false);
                                        document.put("password", String.valueOf(password.getText()));

                                        firestore.collection("user").add(document)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.i("logTP", "Google User Successfully save firestore");

                                                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putBoolean("isGoogle", false);
                                                        editor.putString("fname", String.valueOf(fname.getText()));
                                                        editor.putString("lname", String.valueOf(lname.getText()));
                                                        editor.putString("id", String.valueOf(id));
                                                        editor.apply();

                                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i("logTP", "Google User failed save firestore");
                                                    }
                                                });

                                    } else {
                                        emailTextInputLayout.setErrorEnabled(true);
                                        emailTextInputLayout.setError("This Email Already Used");

                                        lastNameTextInputLayout.setErrorEnabled(false);
                                        firstNameTextInputLayout.setErrorEnabled(false);
                                        passwordNameTextInputLayout.setErrorEnabled(false);
                                    }

                                }
                            });
                }
            }
        });

    }
}
package com.akila.treasureplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SignInActivity extends AppCompatActivity {
    // Initialize variables
    Button btSignIn;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btSignIn = findViewById(R.id.loginGoogleButton);

        firestore = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("287846202436-tt8vtesu0m9ah0456a85hjb2tidna2f0.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, googleSignInOptions);

        btSignIn.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100);
        });

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id",null);

        if(id != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }

        TextView textView = findViewById(R.id.signinToSignUp);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        findViewById(R.id.loginButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.loginEditTextEmail);
                EditText password = findViewById(R.id.loginEditTextPassword);



                firestore.collection("user")
                        .where(Filter.and(
                                Filter.equalTo("email", String.valueOf(email.getText())),
                                Filter.equalTo("password", String.valueOf(password.getText())),
                                Filter.equalTo("isGoogle", false)
                        ))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                                Log.i("logTP", String.valueOf(documentSnapshotList.size()));
                                if (documentSnapshotList.size() == 1) {
                                    Log.i("logTP","Login Success");

                                    SharedPreferences sharedPreferences = getSharedPreferences("user",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isGoogle", false);
                                    editor.putString("fname", String.valueOf(documentSnapshotList.get(0).get("fname")));
                                    editor.putString("lname", String.valueOf(documentSnapshotList.get(0).get("lname")));
                                    editor.putString("id", String.valueOf(documentSnapshotList.get(0).get("id")));
                                    editor.apply();

                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                } else {
                                    Log.i("logTP","Login Failed");

                                    firestore.collection("user")
                                            .where(Filter.and(
                                                    Filter.equalTo("email", String.valueOf(email.getText())),
                                                    Filter.equalTo("isGoogle", false)
                                            )).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<DocumentSnapshot> documentSnapshotList1 = task.getResult().getDocuments();
                                                    if (documentSnapshotList1.size() == 1) {
                                                        //invalid Password
                                                        TextInputLayout textInputLayout = findViewById(R.id.signUpPasswordTextInputLayout);
                                                        textInputLayout.setErrorEnabled(true);
                                                        textInputLayout.setError("Invalid Password");

                                                        TextInputLayout textInputLayout2 = findViewById(R.id.signInEmailTextInputLayout);
                                                        textInputLayout2.setErrorEnabled(false);
                                                    } else {
                                                        // invalid email
                                                        TextInputLayout textInputLayout = findViewById(R.id.signInEmailTextInputLayout);
                                                        textInputLayout.setErrorEnabled(true);
                                                        textInputLayout.setError("Invalid Email");

                                                        TextInputLayout textInputLayout2 = findViewById(R.id.signUpPasswordTextInputLayout);
                                                        textInputLayout2.setErrorEnabled(false);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                String s = "Google sign in successful";
                displayToast(s);
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    Log.i("logTP",firebaseUser.getEmail()+" "+firebaseUser.getDisplayName());

                                    String email = firebaseUser.getEmail();
                                    String name = firebaseUser.getDisplayName();
                                    String image = firebaseUser.getPhotoUrl().toString();

                                    firestore.collection("user")
                                            .where(Filter.equalTo("email",email))
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                                                    int id = new Random().nextInt(10000000);

                                                    if (documentSnapshotList.isEmpty()) {
                                                        HashMap<String, Object> document = new HashMap<>();
                                                        document.put("email", email);
                                                        document.put("fname", name);
                                                        document.put("lname", "");
                                                        document.put("id", id);
                                                        document.put("isGoogle", true);
                                                        document.put("password", "");

                                                        firestore.collection("user").add(document)
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                        Log.i("logTP", "Google User Successfully save firestore");



                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.i("logTP", "Google User failed save firestore");
                                                                    }
                                                                });
                                                    } else {
                                                        id = Integer.parseInt(String.valueOf(documentSnapshotList.get(0).get("id")));
                                                        Log.i("logTP", "Already log : "+ String.valueOf(documentSnapshotList.get(0).get("id")));
                                                    }

                                                    SharedPreferences sharedPreferences = getSharedPreferences("user",Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putBoolean("isGoogle", true);
                                                    editor.putString("fname", name);
                                                    editor.putString("image", image);
                                                    editor.putString("id", String.valueOf(id));
                                                    editor.apply();
                                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));

                                                }
                                            });

                                    displayToast("Firebase authentication successful");
                                } else {
                                    displayToast("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
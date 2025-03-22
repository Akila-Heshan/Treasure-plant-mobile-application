package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.akila.treasureplant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ProfileUpdateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        EditText emailEditText = view.findViewById(R.id.userUpdateEmailEditText);
        EditText fnameEditText = view.findViewById(R.id.userUpdateFnameEditText);
        EditText lnameEditText = view.findViewById(R.id.userUpdateLnameEditText);

        TextInputLayout fnameInputLayout = view.findViewById(R.id.userUpdateFnameLayout);
        TextInputLayout lnameInputLayout = view.findViewById(R.id.userUpdateLnameLayout);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id",null);

        String[] documentId = {""};

        firestore.collection("user")
                        .where(Filter.equalTo("id", Integer.parseInt(id)))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    emailEditText.setText(String.valueOf(documentSnapshot.get("email")));
                                    fnameEditText.setText(String.valueOf(documentSnapshot.get("fname")));
                                    lnameEditText.setText(String.valueOf(documentSnapshot.get("lname")));

                                    documentId[0] = documentSnapshot.getId();
                                }
                            }
                        });

        view.findViewById(R.id.userUpdateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(fnameEditText.getText()).isEmpty()) {
                    fnameInputLayout.setErrorEnabled(true);
                    fnameInputLayout.setError("Please Enter First Name");

                    lnameInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(lnameEditText.getText()).isEmpty()) {
                    lnameInputLayout.setErrorEnabled(true);
                    lnameInputLayout.setError("Please Enter Last Name");

                    fnameInputLayout.setErrorEnabled(false);
                } else {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("fname", String.valueOf(fnameEditText.getText()));
                    hashMap.put("lname", String.valueOf(lnameEditText.getText()));

                    firestore.collection("user").document(documentId[0])
                            .update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}
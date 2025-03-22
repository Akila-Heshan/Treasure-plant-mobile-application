package com.akila.treasureplant.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.akila.treasureplant.R;
import com.akila.treasureplant.model.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellerRegistation_1 extends Fragment {

    public static String[] data = {"","","",""};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_registation_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        HashMap<String, String> cityMap = new HashMap<>();

        firestore.collection("city")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        List<String> city = new ArrayList<>();
//                        city.add("Select City");
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            city.add(String.valueOf(documentSnapshot.get("name")));
                            cityMap.put(String.valueOf(documentSnapshot.get("name")), documentSnapshot.getId());
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                getActivity(),
                                R.layout.simple_spinner_layout,
                                city
                        );

                        Spinner spinner = view.findViewById(R.id.sellerSignUpCitySpinner);
                        spinner.setAdapter(arrayAdapter);
                        spinner.invalidate();
                    }
                });

        EditText line1 = view.findViewById(R.id.sellerSignUpAddressLine1EditText);
        EditText line2 = view.findViewById(R.id.sellerSignUpAddressLine2EditText);
        EditText mobile = view.findViewById(R.id.sellerSignMobileNumberEditText);
        Spinner city = view.findViewById(R.id.sellerSignUpCitySpinner);

        TextInputLayout line1TextInputLayout = view.findViewById(R.id.sellerSignUpAddressLine1EditTextLayout);
        TextInputLayout line2TextInputLayout = view.findViewById(R.id.sellerSignUpAddressLine2EditTextLayout);
        TextInputLayout mobileTextInputLayout = view.findViewById(R.id.sellerSignMobileNumberEditTextLayout);



        view.findViewById(R.id.sellerSignUpNextStepButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(line1.getText()).isEmpty()) {
                    line1TextInputLayout.setErrorEnabled(true);
                    line1TextInputLayout.setError("Please Enter Address Line 1");

                    line2TextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(line2.getText()).isEmpty()) {
                    line2TextInputLayout.setErrorEnabled(true);
                    line2TextInputLayout.setError("Please Enter Address Line 2");

                    line1TextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(mobile.getText()).isEmpty()) {
                    mobileTextInputLayout.setErrorEnabled(true);
                    mobileTextInputLayout.setError("Please Enter Mobile Number");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                } else if (!Validations.isMobileNumberValid(String.valueOf(mobile.getText()))) {
                    mobileTextInputLayout.setErrorEnabled(true);
                    mobileTextInputLayout.setError("Please Enter Valid Mobile Number");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                } else {
                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);

                    data[0] = String.valueOf(line1.getText());
                    data[1] = String.valueOf(line2.getText());
                    data[2] = String.valueOf(mobile.getText());
                    data[3] = String.valueOf(cityMap.get(String.valueOf(city.getSelectedItem())));

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.sellerFrameLayout1, SellerRegistation_2.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }
        });
    }
}
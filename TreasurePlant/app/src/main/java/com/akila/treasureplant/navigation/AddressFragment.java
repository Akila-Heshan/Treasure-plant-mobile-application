package com.akila.treasureplant.navigation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.akila.treasureplant.R;
import com.akila.treasureplant.model.SqLiteHelper;
import com.akila.treasureplant.model.Validations;
import com.google.android.material.textfield.TextInputLayout;

public class AddressFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SqLiteHelper sqLiteHelper = new SqLiteHelper(getActivity().getApplicationContext(), "treasure_plant.db", null, 1);

        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getReadableDatabase();

        EditText line1 = view.findViewById(R.id.address_line1);
        EditText line2 = view.findViewById(R.id.address_line2);
        EditText postalCode = view.findViewById(R.id.address_postal_code);
        EditText name = view.findViewById(R.id.address_name);
        EditText mobile = view.findViewById(R.id.address_mobile);

        TextInputLayout line1TextInputLayout = view.findViewById(R.id.address_line1_layout);
        TextInputLayout line2TextInputLayout = view.findViewById(R.id.address_line2_layout);
        TextInputLayout postalCodeTextInputLayout = view.findViewById(R.id.address_postal_code_layout);
        TextInputLayout nameTextInputLayout = view.findViewById(R.id.address_name_layout);
        TextInputLayout mobileTextInputLayout = view.findViewById(R.id.address_mobile_layout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor2 = sqLiteDatabase2.rawQuery("SELECT * FROM `address`", new String[]{});
                if (cursor2.moveToNext()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            line1.setText(cursor2.getString(1));
                            line2.setText(cursor2.getString(2));
                            postalCode.setText(cursor2.getString(3));
                            name.setText(cursor2.getString(4));
                            mobile.setText(cursor2.getString(5));
                        }
                    });
                }
            }
        }).start();

        view.findViewById(R.id.address_remove_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor2 = sqLiteDatabase2.rawQuery("SELECT * FROM `address`", new String[]{});
                        if (cursor2.moveToNext()) {

                            int id = cursor2.getInt(0);

                            sqLiteDatabase.execSQL("DELETE FROM `address` WHERE `id` = '"+id+"'; ");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    line1.setText("");
                                    line2.setText("");
                                    postalCode.setText("");
                                    name.setText("");
                                    mobile.setText("");
                                    Toast.makeText(getActivity().getApplicationContext(), "Address Removed Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(), "Address Not Found", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        view.findViewById(R.id.address_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(line1.getText()).isEmpty()) {
                    line1TextInputLayout.setErrorEnabled(true);
                    line1TextInputLayout.setError("Please Enter Address Line 1");

                    line2TextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(line2.getText()).isEmpty()) {
                    line2TextInputLayout.setErrorEnabled(true);
                    line2TextInputLayout.setError("Please Enter Address Line 2");

                    line1TextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(postalCode.getText()).isEmpty()) {
                    postalCodeTextInputLayout.setErrorEnabled(true);
                    postalCodeTextInputLayout.setError("Please Enter Postal Code");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(postalCode.getText()).length() != 5) {
                    postalCodeTextInputLayout.setErrorEnabled(true);
                    postalCodeTextInputLayout.setError("Please Enter Valid Postal Code");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(name.getText()).isEmpty()) {
                    nameTextInputLayout.setErrorEnabled(true);
                    nameTextInputLayout.setError("Please Enter Name");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(mobile.getText()).isEmpty()) {
                    mobileTextInputLayout.setErrorEnabled(true);
                    mobileTextInputLayout.setError("Please Enter Mobile Number");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                } else if (!Validations.isMobileNumberValid(String.valueOf(mobile.getText()))) {
                    mobileTextInputLayout.setErrorEnabled(true);
                    mobileTextInputLayout.setError("Please Enter Valid Mobile Number");

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                } else {
                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);


                    String line1Value = String.valueOf(line1.getText());
                    String line2Value = String.valueOf(line2.getText());
                    String nameValue = String.valueOf(name.getText());
                    String mobileValue = String.valueOf(mobile.getText());
                    int postalCodeValue = Integer.parseInt(String.valueOf(postalCode.getText()));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = sqLiteDatabase2.rawQuery("SELECT * FROM `address`", new String[]{});
                            if (cursor.moveToNext()) {
                                int id = cursor.getInt(0);

                                sqLiteDatabase.execSQL("UPDATE `address` " +
                                        "SET `line1`='"+line1Value+"', `line2`='"+line2Value+"', `postal_code`='"+postalCodeValue+"', `name`='"+nameValue+"', `mobile`='"+mobileValue+"' " +
                                        "WHERE `id` = '"+id+"'; ");

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Address Updated Successfully", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                sqLiteDatabase.execSQL("INSERT INTO `address` " +
                                        "(`line1`, `line2`, `postal_code`, `name`, `mobile`)" +
                                        "VALUES ('"+line1Value+"', '"+line2Value+"', '"+postalCodeValue+"', '"+nameValue+"', '"+mobileValue+"');");

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Address Added Successfully", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }
                    }).start();

                }
            }
        });
    }
}

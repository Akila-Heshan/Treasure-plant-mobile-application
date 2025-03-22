package com.akila.treasureplant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akila.treasureplant.model.CartItem;
import com.akila.treasureplant.model.SqLiteHelper;
import com.akila.treasureplant.model.Validations;
import com.akila.treasureplant.navigation.CartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutFragment extends Fragment {

    FirebaseFirestore firestore;
    ArrayList<CartItem> cartItemArrayList;

    EditText line1;
    EditText line2;
    EditText postalCode;
    EditText name;
    EditText mobile;
    String orderId;
    String userId;
    String total;

    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            String msg = response.isSuccess() ? "Payment Success: "+ response.getData() : "Payment failed: " +response;
                            Log.i("logTP", msg);

                            for (CartItem cartItem : cartItemArrayList) {
                                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                hashMap.put("qty", cartItem.getQuantity());

                                firestore.collection("product").document(cartItem.getTitle())
                                        .update(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firestore.collection("cart").document(CartFragment.cartDocumentId).delete();
                                            }
                                        });

//                                cartItem.setQuantity("0");
                            }

                            HashMap<String, Object> document = new HashMap<>();
                            document.put("address_line1", String.valueOf(line1.getText()));
                            document.put("address_line2", String.valueOf(line2.getText()));
                            document.put("id", orderId);
                            document.put("date", new Date());
                            document.put("mobile", String.valueOf(mobile.getText()));
                            document.put("name", String.valueOf(name.getText()));
                            document.put("postalcode", String.valueOf(postalCode.getText()));
                            document.put("order_array", cartItemArrayList);
                            document.put("user_id", userId);

                            firestore.collection("order").add(document)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Order Successfully placed", Toast.LENGTH_LONG).show();

                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            String formattedDate = dateFormat.format(new Date());

                                            firestore.collection("chart")
                                                    .where(Filter.equalTo("date", formattedDate))
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                                                            if (documentSnapshots.size() > 0) {
                                                                DocumentSnapshot snapshot = documentSnapshots.get(0);

                                                                String value = String.valueOf(snapshot.get("value"));
                                                                int newValue = Integer.parseInt(value) + Integer.parseInt(CartFragment.cartTotalPrice);
                                                                firestore.collection("chart").document(snapshot.getId())
                                                                        .update("value", newValue);


                                                            } else {
                                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                                hashMap.put("date", formattedDate);
                                                                hashMap.put("value", CartFragment.cartTotalPrice);

                                                                firestore.collection("chart").add(hashMap);
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Log.i("logTP", "Payment canceled");
                    Toast.makeText(getActivity().getApplicationContext(), "Payment canceled", Toast.LENGTH_LONG).show();
                }
            }
    );

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderId = String.valueOf(new Random().nextInt(1000000000));

        firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("id",null);

        TextView subTotal = view.findViewById(R.id.checkout_sub_total);
        subTotal.setText(getString(R.string.checkout_text2)+" Rs. "+CartFragment.cartTotalPrice);

        int shippingPrice = 350;

        TextView shipping = view.findViewById(R.id.checkout_shipping_coast);
        shipping.setText(getString(R.string.checkout_text3)+" Rs. "+shippingPrice);

        TextView total = view.findViewById(R.id.checkout_total);
        total.setText(getString(R.string.checkout_text4)+" Rs. " + (Integer.parseInt(CartFragment.cartTotalPrice) + shippingPrice));

        SqLiteHelper sqLiteHelper = new SqLiteHelper(getActivity().getApplicationContext(), "treasure_plant.db", null, 1);
        SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getReadableDatabase();

        line1 = view.findViewById(R.id.checkout_address_line1);
        line2 = view.findViewById(R.id.checkout_address_line2);
        postalCode = view.findViewById(R.id.checkout_address_postal_code);
        name = view.findViewById(R.id.checkout_address_name);
        mobile = view.findViewById(R.id.checkout_address_mobile);

        TextInputLayout line1TextInputLayout = view.findViewById(R.id.checkout_address_line1_layout);
        TextInputLayout line2TextInputLayout = view.findViewById(R.id.checkout_address_line2_layout);
        TextInputLayout postalCodeTextInputLayout = view.findViewById(R.id.checkout_address_postal_code_layout);
        TextInputLayout nameTextInputLayout = view.findViewById(R.id.checkout_address_name_layout);
        TextInputLayout mobileTextInputLayout = view.findViewById(R.id.checkout_address_mobile_layout);

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

        view.findViewById(R.id.checkout_pay_now_button).setOnClickListener(new View.OnClickListener() {
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

                    firestore.collection("cart").document(CartFragment.cartDocumentId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        ArrayList<HashMap> productArray = (ArrayList<HashMap>) documentSnapshot.get("product_array");

                                        final boolean[] notProcess = {true};

                                        cartItemArrayList = new ArrayList<>();

                                        for (HashMap<String, Object> hashMap : productArray) {

                                            String id = String.valueOf(hashMap.get("id"));
                                            String title = String.valueOf(hashMap.get("title"));
                                            String price = String.valueOf(hashMap.get("price"));
                                            String quantity = String.valueOf(hashMap.get("quantity"));

                                            firestore.collection("product")
                                                    .where(Filter.equalTo("id", Integer.parseInt(id)))
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            DocumentSnapshot snapshot  = task.getResult().getDocuments().get(0);
                                                            int qty = Integer.parseInt(String.valueOf(snapshot.get("qty")));
                                                            if (qty >= Integer.parseInt(quantity)) {
                                                                // ok
                                                                cartItemArrayList.add(new CartItem(id, snapshot.getId(), String.valueOf(Integer.parseInt(price) * qty), String.valueOf(qty)));
                                                            } else {
                                                                Toast.makeText(getActivity().getApplicationContext(), "Unavailable Quantity in "+title, Toast.LENGTH_LONG).show();
                                                                notProcess[0] = false;
                                                            }
                                                        }
                                                    });
                                        }

                                        if (notProcess[0]) {
                                            InitRequest req = new InitRequest();
                                            req.setMerchantId("1229511");
                                            req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                                            req.setAmount((double) Integer.parseInt(CartFragment.cartTotalPrice) + 350);             // Final Amount to be charged
                                            req.setOrderId("1234567892");        // Unique Reference ID
                                            req.setItemsDescription("Door bell wireless");  // Item description title
                                            req.setCustom1("This is the custom message 1");
                                            req.setCustom2("This is the custom message 2");
                                            req.getCustomer().setFirstName("Akila");
                                            req.getCustomer().setLastName("Peera");
                                            req.getCustomer().setEmail("akila@gmail.com");
                                            req.getCustomer().setPhone("+94771239667");
                                            req.getCustomer().getAddress().setAddress("No.1,  Road");
                                            req.getCustomer().getAddress().setCity("Kandy");
                                            req.getCustomer().getAddress().setCountry("Sri Lanka");

                                            req.getCustomer().getDeliveryAddress().setAddress("No.6, Kandy Road");
                                            req.getCustomer().getDeliveryAddress().setCity("Mawanella");
                                            req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
                                            req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));
                                            req.setNotifyUrl("https://sample.com/test");

                                            Intent intent = new Intent(getActivity().getApplicationContext(), PHMainActivity.class);
                                            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                                            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                                            payHereLauncher.launch(intent);

                                        }

                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    line1TextInputLayout.setErrorEnabled(false);
                    line2TextInputLayout.setErrorEnabled(false);
                    nameTextInputLayout.setErrorEnabled(false);
                    postalCodeTextInputLayout.setErrorEnabled(false);
                    mobileTextInputLayout.setErrorEnabled(false);
                }
            }
        });

    }
}
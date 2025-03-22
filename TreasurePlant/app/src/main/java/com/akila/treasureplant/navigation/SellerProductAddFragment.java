package com.akila.treasureplant.navigation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akila.treasureplant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SellerProductAddFragment extends Fragment {

    List<Uri> uriList = new ArrayList<>();
    Uri firstImageUri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(4), uris -> {
                if (uris != null) {
                    uriList = uris;
                    Log.d("logTP", "Selected URI: " + uris.size());
                    TextView textView = getView().findViewById(R.id.add_product_image_count);
                    textView.setText(getString(R.string.seller_product_add_text2)+" "+String.valueOf(uris.size()));
                } else {
                    Log.d("logTP", "No media selected");
                    TextView textView = getView().findViewById(R.id.add_product_image_count);
                    textView.setText(R.string.seller_product_add_text2);
                }
            });

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                                Log.i("logTP",String.valueOf(selectedImageUri));
                            }
                        }
                    });
    ActivityResultLauncher<PickVisualMediaRequest> firstImagePicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    firstImageUri = uri;
                    Log.d("logTP", "Selected first Image URI: " + uri.toString());
                } else {
                    Log.d("logTP", "No media selected");
                    firstImageUri = null;
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_product_add, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        HashMap<String, Integer> brandMap = new HashMap<>();
        HashMap<String, Integer> categoryMap = new HashMap<>();

        firestore.collection("brand").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                                List<String> brands = new ArrayList<>();
                                brands.add(getString(R.string.brand_default_value));
                                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                                    brands.add(String.valueOf(documentSnapshot.get("name")));
                                    brandMap.put(String.valueOf(documentSnapshot.get("name")), Integer.parseInt(String.valueOf(documentSnapshot.get("id") )));
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                        getActivity(),
                                        R.layout.simple_spinner_layout,
                                        brands
                                );

                                Spinner spinner = view.findViewById(R.id.product_add_brand);
                                spinner.setAdapter(arrayAdapter);
                                spinner.invalidate();
                            }
                        });

        firestore.collection("category").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        List<String> categories = new ArrayList<>();
                        categories.add(getString(R.string.category_default_value));
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            categories.add(String.valueOf(documentSnapshot.get("name")));
                            categoryMap.put(String.valueOf(documentSnapshot.get("name")), Integer.parseInt(String.valueOf(documentSnapshot.get("id") )));
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                getActivity(),
                                R.layout.simple_spinner_layout,
                                categories
                        );

                        Spinner spinner = view.findViewById(R.id.product_add_category);
                        spinner.setAdapter(arrayAdapter);
                        spinner.invalidate();
                    }
                });

        view.findViewById(R.id.product_add_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

                Log.i("logTP",String.valueOf(uriList.size()));
            }
        });

        Button firstImageButton = view.findViewById(R.id.product_add_first_image);

        firstImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Set the type to image
                pickImageLauncher.launch(intent);
            }
        });

        EditText title = view.findViewById(R.id.product_add_title);
        EditText price = view.findViewById(R.id.product_add_price);
        EditText description = view.findViewById(R.id.product_add_description);
        EditText quantity = view.findViewById(R.id.product_add_quantity);
        Spinner brand = view.findViewById(R.id.product_add_brand);
        Spinner category = view.findViewById(R.id.product_add_category);

        TextView brandTextView = view.findViewById(R.id.product_add_brand_textView);
        TextView categoryTextView = view.findViewById(R.id.product_add_category_textView);

        TextInputLayout titleTextInputLayout = view.findViewById(R.id.product_add_title_layout);
        TextInputLayout priceTextInputLayout = view.findViewById(R.id.product_add_price_layout);
        TextInputLayout descriptionTextInputLayout = view.findViewById(R.id.product_add_description_layout);
        TextInputLayout quantityTextInputLayout = view.findViewById(R.id.product_add_quantity_layout);

        view.findViewById(R.id.product_add_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (String.valueOf(title.getText()).isEmpty()) {
                    titleTextInputLayout.setErrorEnabled(true);
                    titleTextInputLayout.setError("Please Enter Title");

                    priceTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(price.getText()).isEmpty()) {
                    priceTextInputLayout.setErrorEnabled(true);
                    priceTextInputLayout.setError("Please Enter Price");

                    titleTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(description.getText()).isEmpty()) {
                    descriptionTextInputLayout.setErrorEnabled(true);
                    descriptionTextInputLayout.setError("Please Enter Description");

                    priceTextInputLayout.setErrorEnabled(false);
                    titleTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(quantity.getText()).isEmpty()) {
                    quantityTextInputLayout.setErrorEnabled(true);
                    quantityTextInputLayout.setError("Please Enter Quantity");

                    priceTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    titleTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(brand.getSelectedItem()).equals(getString(R.string.brand_default_value))) {
                    brandTextView.setTextColor(getResources().getColor(R.color.color3));
                    brandTextView.setText("Brand ( Please Select Brand )");

                    categoryTextView.setTextColor(getResources().getColor(R.color.black));
                    categoryTextView.setText(getString(R.string.seller_product_add_input6));

                    priceTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    titleTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);
                } else if (String.valueOf(category.getSelectedItem()).equals(getString(R.string.category_default_value))) {
                    categoryTextView.setTextColor(getResources().getColor(R.color.color3));
                    categoryTextView.setText("Category ( Please Select Category )");

                    brandTextView.setTextColor(getResources().getColor(R.color.black));
                    brandTextView.setText(getString(R.string.seller_product_add_input5));

                    priceTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    titleTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);
                } else if (selectedImageUri == null) {
                    firstImageButton.setTextColor(getResources().getColor(R.color.color3));

                    categoryTextView.setTextColor(getResources().getColor(R.color.black));
                    categoryTextView.setText(getString(R.string.seller_product_add_input6));
                    brandTextView.setTextColor(getResources().getColor(R.color.black));
                    brandTextView.setText(getString(R.string.seller_product_add_input5));
                    priceTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    titleTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);
                } else {
                    firstImageButton.setTextColor(getResources().getColor(R.color.black));
                    categoryTextView.setTextColor(getResources().getColor(R.color.black));
                    categoryTextView.setText(getString(R.string.seller_product_add_input6));
                    brandTextView.setTextColor(getResources().getColor(R.color.black));
                    brandTextView.setText(getString(R.string.seller_product_add_input5));
                    priceTextInputLayout.setErrorEnabled(false);
                    descriptionTextInputLayout.setErrorEnabled(false);
                    titleTextInputLayout.setErrorEnabled(false);
                    quantityTextInputLayout.setErrorEnabled(false);

                    int brandId = brandMap.get(String.valueOf(brand.getSelectedItem()));
                    int categoryId = categoryMap.get(String.valueOf(category.getSelectedItem()));
                    int id = new Random().nextInt(100000000);
                    int imageCount = uriList.size() + 1;

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("seller", Context.MODE_PRIVATE);

                    int sellerId = Integer.parseInt(sharedPreferences.getString("seller_id",null));

                    HashMap<String, Object> product = new HashMap<>();
                    product.put("brand_id", brandId);
                    product.put("category_id", categoryId);
                    product.put("description", String.valueOf(description.getText()));
                    product.put("id", id);
                    product.put("image", imageCount);
                    product.put("price", String.valueOf(price.getText()));
                    product.put("qty", String.valueOf(quantity.getText()));
                    product.put("review", 0);
                    product.put("review_count", 0);
                    product.put("seller_id", sellerId);
                    product.put("sold", 0);
                    product.put("title", String.valueOf(title.getText()));

                    uploadImages(String.valueOf(id));

                    firestore.collection("product").add(product)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    title.setText("");
                                    price.setText("");
                                    description.setText("");
                                    quantity.setText("");
                                    category.setSelection(0);
                                    brand.setSelection(0);
                                    firstImageUri = null;
                                    uriList = new ArrayList<>();

                                    Toast.makeText(getActivity().getApplicationContext(), "Product Add Successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();

                                }
                            });
                }
            }
        });
    }

    private File getFileFromContentUri(Uri contentUri) {
        File file = null;
        try {
            ContentResolver contentResolver = getActivity().getContentResolver();
            String fileName = getFileName(contentUri);
            if (fileName != null) {
                file = new File(getActivity().getCacheDir(), fileName);
                InputStream inputStream = contentResolver.openInputStream(contentUri);
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[4 * 1024]; // 4KB buffer
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        } catch (Exception e) {
            Log.e("logTP", "Error getting file from content URI: " + e.getMessage());
        }
        return file;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadImages(String productId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("logTP", "Image Thread");
                OkHttpClient client = new OkHttpClient();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                File firstImageFile = getFileFromContentUri(selectedImageUri);
                builder.addFormDataPart("image0", firstImageFile.getName(), RequestBody.create(firstImageFile, MEDIA_TYPE_PNG));

                for (int i = 0; i < uriList.size(); i++) {
                    Uri uri = uriList.get(i);
                    File imageFile = getFileFromContentUri(uri);
                    builder.addFormDataPart("image" + (i + 1), imageFile.getName(), RequestBody.create(imageFile, MEDIA_TYPE_PNG));
                }

                builder.addFormDataPart("product_id", productId);
                builder.addFormDataPart("image_count", String.valueOf(uriList.size()));

                RequestBody requestBody = builder.build();

                Request request = new Request.Builder()
                        .url(getString(R.string.url)+"/ImageUpload")
                        .post(requestBody)
                        .build();


                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Log.d("logTP", "Images uploaded successfully");
                    } else {
                        Log.e("logTP", "Failed to upload images: " + response.body().string());
                    }
                } catch (IOException e) {
                    Log.e("logTP", "Error uploading images: " + e.getMessage());
                }
            }
        }
        ).start();
    }
}
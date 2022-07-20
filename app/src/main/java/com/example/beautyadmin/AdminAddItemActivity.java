package com.example.beautyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
 import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddItemActivity extends AppCompatActivity {

    private String CategoryName,Title, Details,Notes , saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputImage;
    private EditText InputItemTitle, InputItemDetails, InputItemNotes;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String itemRandomKey, downloadImageUrl;
    private StorageReference ItemImagesRef;
    private DatabaseReference ItemsRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_item);



        CategoryName = getIntent().getExtras().get("category").toString();
        ItemImagesRef = FirebaseStorage.getInstance().getReference().child("Item Images");
        ItemsRef = FirebaseDatabase.getInstance().getReference().child("Items");


        AddNewProductButton = (Button) findViewById(R.id.add_new_item);
        InputImage = (ImageView) findViewById(R.id.select_image);
        InputItemTitle = (EditText) findViewById(R.id.item_title);
        InputItemDetails = (EditText) findViewById(R.id.item_details);
        InputItemNotes = (EditText) findViewById(R.id.item_notes);


        loadingBar = new ProgressDialog(this);


        InputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

    }



    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputImage.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData()
    {
        Title = InputItemTitle.getText().toString();
        Details = InputItemDetails.getText().toString();
        Notes = InputItemNotes.getText().toString();



        if (ImageUri == null)
        {
            Toast.makeText(this,"قم بإضافة الصورة!" , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Title))
        {
            Toast.makeText(this,"قم بإضافة عنوان للموضوع!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Details))
        {
            Toast.makeText(this,"قم بإضافة تفاصيل الموضوع!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Notes))
        {
            Toast.makeText(this,"قم بإضافة الملاحظات!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }



    private void StoreProductInformation()
    {
        loadingBar.setTitle("إضافة موضوع جديد");
        loadingBar.setMessage("انتظر من فضلك يتم إضافة الموضوع" );
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        itemRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = ItemImagesRef.child(ImageUri.getLastPathSegment() +itemRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddItemActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }



    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("itemId", itemRandomKey);
        itemMap.put("date", saveCurrentDate);
        itemMap.put("time", saveCurrentTime);
        itemMap.put("image", downloadImageUrl);
        itemMap.put("category", CategoryName);
        itemMap.put("title", Title);
        itemMap.put("details", Details);
        itemMap.put("notes", Notes);
        itemMap.put("favorite", "No");


        ItemsRef.child(itemRandomKey).updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddItemActivity.this, MainActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddItemActivity.this, "تمت الإضافة بنجاح" , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddItemActivity.this, "نأسف حدث خطأ و حاول مرة أخرى!" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

package com.example.beautyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminUpdateItemActivity extends AppCompatActivity {

    private EditText title, details, notes;
    private Button saveChangesBtn, deleteBtn;
    private ImageView imageView;

    private String itemId = "";
    private DatabaseReference itemRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_item);

        itemId = getIntent().getStringExtra("itemId");
        itemRef = FirebaseDatabase.getInstance().getReference().child("Items").child(itemId);

        saveChangesBtn = (Button) findViewById(R.id.update_save_changes);
        deleteBtn = (Button) findViewById(R.id.update_delete_item);

        title = (EditText) findViewById(R.id.update_item_title);
        details = (EditText) findViewById(R.id.update_item_details);
        notes = (EditText) findViewById(R.id.update_item_notes);
        imageView = (ImageView) findViewById(R.id.update_image);


        displaySpecificItemInfo();

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteItem();
            }
        });
    }

    private void deleteItem() {

        itemRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(AdminUpdateItemActivity.this,"تمت إزالته بنجاح!",Toast.LENGTH_SHORT).show();

                onBackPressed();
            }
        });
    }

    private void saveChanges() {

        String itemTitle = title.getText().toString();
        String itemDetails = details.getText().toString();
        String itemNotes = notes.getText().toString();

        if(itemTitle.equals(""))
        {
            Toast.makeText(this, "قم بإضافة عنوان للموضوع!",Toast.LENGTH_SHORT).show();
        }
        else if(itemDetails.equals(""))
        {
            Toast.makeText(this, "قم بإضافة تفاصيل الموضوع!",Toast.LENGTH_SHORT).show();
        }
        else if(itemNotes.equals(""))
        {
            Toast.makeText(this, "قم بإضافة الملاحظات!",Toast.LENGTH_SHORT).show();
        }

        else
        {
            HashMap<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemId", itemId);
            itemMap.put("title", itemTitle);
            itemMap.put("details", itemDetails);
            itemMap.put("notes", itemNotes);


            itemRef.updateChildren(itemMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AdminUpdateItemActivity.this, "تم حفظ التعديلات",Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    }

                }
            });
        }

    }

    private void displaySpecificItemInfo() {

        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String itemTitle = snapshot.child("title").getValue().toString();
                    String itemDetails = snapshot.child("details").getValue().toString();
                    String itemNotes = snapshot.child("notes").getValue().toString();
                    String itemImage = snapshot.child("image").getValue().toString();

                    title.setText(itemTitle);
                    details.setText(itemDetails);
                    notes.setText(itemNotes);

                    Picasso.get().load(itemImage).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
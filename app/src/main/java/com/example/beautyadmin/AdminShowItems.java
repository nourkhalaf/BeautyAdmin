package com.example.beautyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.beautyadmin.Model.Item;
import com.example.beautyadmin.ViewHolder.ItemViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class AdminShowItems extends AppCompatActivity {


    private DatabaseReference ItemsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private Button addNewItem;

    private String CategoryName;
    private TextView category, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_items);


        ItemsRef = FirebaseDatabase.getInstance().getReference().child("Items");


        CategoryName = getIntent().getExtras().get("category").toString();

        addNewItem = findViewById(R.id.add_new_item_button);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(AdminShowItems.this, AdminAddItemActivity.class);
                intent.putExtra("category", CategoryName);
                startActivity(intent);
            }
        });

       back = findViewById(R.id.back);
       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent (AdminShowItems.this,MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
           }
       });

        category = findViewById(R.id.category_title);
        category.setText(CategoryName);
        recyclerView = findViewById(R.id.admin_recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(ItemsRef.orderByChild("category").equalTo(CategoryName), Item.class)
                        .build();


        FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull final Item  model)
                    {
                        holder.txtItemTitle.setText(model.getTitle());
                         holder.txtItemDetails.setText( model.getDetails());
                         holder.txtItemNotes.setText( model.getNotes());

                        Picasso.get().load(model.getImage()).into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(AdminShowItems.this, AdminUpdateItemActivity.class);
                                intent.putExtra("itemId", model.getItemId());
                                startActivity(intent);
                            }

                        });
                    }

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
                        ItemViewHolder holder = new ItemViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
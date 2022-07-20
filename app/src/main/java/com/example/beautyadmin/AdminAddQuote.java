package com.example.beautyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beautyadmin.Interface.ItemClickListener;
import com.example.beautyadmin.Model.Item;
import com.example.beautyadmin.Model.Quote;
import com.example.beautyadmin.ViewHolder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddQuote extends AppCompatActivity {

    private String quote, saveCurrentDate, saveCurrentTime,quoteRandomKey;
    private EditText quote_txt;
    private Button add_quote;
     private DatabaseReference QuotesRef;
    private ProgressDialog loadingBar;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_quote);

        quote_txt = findViewById(R.id.txt_quote);
        add_quote = findViewById(R.id.add_quote);
        QuotesRef = FirebaseDatabase.getInstance().getReference().child("Quotes");

        loadingBar = new ProgressDialog(this);

        recyclerView = findViewById(R.id.admin_quotes_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        add_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quote = quote_txt.getText().toString();
                if (TextUtils.isEmpty(quote))
                {
                    Toast.makeText(AdminAddQuote.this,"قم بإضافة الاقتباس!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("إضافة اقتباس جديد");
                    loadingBar.setMessage("انتظر من فضلك يتم إضافة الاقتباس" );
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime = currentTime.format(calendar.getTime());

                    quoteRandomKey = saveCurrentDate + saveCurrentTime;


                    SaveQuote();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Quote> options =
                new FirebaseRecyclerOptions.Builder<Quote>()
                        .setQuery(QuotesRef, Quote.class)
                        .build();


        FirebaseRecyclerAdapter<Quote, QuoteViewHolder> adapter =
                new FirebaseRecyclerAdapter<Quote, QuoteViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull QuoteViewHolder holder, int position, @NonNull final Quote model) {
                        holder.txtQuote.setText(model.getQuote());
                        holder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteQuote(model.getQuoteId());
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_layout, parent, false);
                        QuoteViewHolder holder = new QuoteViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteQuote(String quoteId) {
        QuotesRef.child(quoteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(AdminAddQuote.this,"تمت إزالة الاقتباس",Toast.LENGTH_SHORT).show();

                onBackPressed();
            }
        });
    }

    private class QuoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtQuote;
        public ImageView delete;
        public ItemClickListener listener;


        public QuoteViewHolder(View itemView) {
            super(itemView);


            txtQuote = (TextView) itemView.findViewById(R.id.txt_quote);
            delete = (ImageView) itemView.findViewById(R.id.delete_icon);

        }

        public void setItemClickListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }


    private void SaveQuote() {

        HashMap<String, Object> quoteMap = new HashMap<>();
        quoteMap.put("quoteId", quoteRandomKey);
        quoteMap.put("quote", quote);


        QuotesRef.child(quoteRandomKey).updateChildren(quoteMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddQuote.this, MainActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddQuote.this, "تمت الإضافة بنجاح" , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddQuote.this, "نأسف حدث خطأ و حاول مرة أخرى!" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


}
}
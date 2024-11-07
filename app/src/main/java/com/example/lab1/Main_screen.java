package com.example.lab1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main_screen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button btn_add;
    private RecyclerView recyclerView_DS;
    private ArrayList<DTO> list;
    private Adapter_RecyclerView adapterRecyclerView;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...!");


        btn_add = findViewById(R.id.btn_add);
        recyclerView_DS = findViewById(R.id.recyclerView_DS);

        recyclerView_DS.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapterRecyclerView = new Adapter_RecyclerView(this, list);
        recyclerView_DS.setAdapter(adapterRecyclerView);

        progressDialog.show();
        addInforFirebase();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_screen.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.add_item, null);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();

                EditText edt_name = view.findViewById(R.id.edt_name);
                EditText edt_date = view.findViewById(R.id.edt_date);
                EditText edt_brand = view.findViewById(R.id.edt_brand);
                Button btn_submit = view.findViewById(R.id.btn_submit);

                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();

                        String name = edt_name.getText().toString().trim();
                        String date = edt_date.getText().toString().trim();
                        String brand = edt_brand.getText().toString().trim();

                        if(name.isEmpty() || date.isEmpty() || brand.isEmpty()){
                            Toast.makeText(Main_screen.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> item = new HashMap<>();
                        item.put("name", name);
                        item.put("date", date);
                        item.put("brand", brand);

                        firestore.collection("Car").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Main_screen.this, "Add thành công", Toast.LENGTH_SHORT).show();
                                addInforFirebase();
//                                adapterRecyclerView.notifyDataSetChanged();
                                progressDialog.dismiss();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main_screen.this, "Add thất bại", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        });

                    }
                });

            }
        });

    }

    private void addInforFirebase() {
        firestore = FirebaseFirestore.getInstance();
        list.clear();
        firestore.collection("Car").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        DTO dto = queryDocumentSnapshot.toObject(DTO.class);
                    dto.setId(queryDocumentSnapshot.getId());
                    list.add(dto);
                }
                adapterRecyclerView.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Main_screen.this, "Add lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }
}
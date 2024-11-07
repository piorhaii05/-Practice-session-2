package com.example.lab1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_RecyclerView extends RecyclerView.Adapter<Adapter_RecyclerView.ViewHolderItem> {

    private final Context context;
    private final ArrayList<DTO> list;
    private DAO dao;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;

    public Adapter_RecyclerView(Context context, ArrayList<DTO> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_lits, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {
        DTO dto = list.get(position);
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading ...!");

        holder.txt_name.setText(dto.getName());
        holder.txt_date.setText(dto.getDate());
        holder.txtbrand.setText(dto.getBrand());

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận!")
                        .setMessage("Bạn có chắc chắn muốn xóa item này không?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String documentID = dto.getId();

                                firestore.collection("Car").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_LONG).show();
                                        list.remove(position);
                                        dialog.dismiss();
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Xóa lỗi", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();

                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View view = inflater.inflate(R.layout.add_item, null);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();

                EditText edt_name = view.findViewById(R.id.edt_name);
                EditText edt_date = view.findViewById(R.id.edt_date);
                EditText edt_brand = view.findViewById(R.id.edt_brand);
                Button btn_submit = view.findViewById(R.id.btn_submit);

                DTO dto1 = list.get(position);

                edt_name.setText(dto1.getName());
                edt_brand.setText(dto1.getBrand());
                edt_date.setText(dto1.getDate());

                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        String name = edt_name.getText().toString().trim();
                        String brand = edt_brand.getText().toString().trim();
                        String date = edt_date.getText().toString().trim();

                        Map<String, Object> item1 = new HashMap<>();
                        item1.put("name", name);
                        item1.put("date", date);
                        item1.put("brand", brand);

                        if (name.isEmpty() || brand.isEmpty() || date.isEmpty()) {
                            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firestore.collection("Car").document(dto.getId()).update(item1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Update thành công", Toast.LENGTH_SHORT).show();
                                        dto.setDate(date);
                                        dto.setBrand(brand);
                                        dto.setName(name);
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Update lỗi", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView txt_name, txt_date, txtbrand;
        Button btn_delete, btn_update;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.edt_name_item);
            txt_date = itemView.findViewById(R.id.edt_date_item);
            txtbrand = itemView.findViewById(R.id.edt_brand_item);

            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_update = itemView.findViewById(R.id.btn_update);

        }
    }
}


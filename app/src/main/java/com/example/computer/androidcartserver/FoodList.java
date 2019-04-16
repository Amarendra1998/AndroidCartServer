package com.example.computer.androidcartserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Interface.ItemClickListener;
import com.example.computer.androidcartserver.Model.Category;
import com.example.computer.androidcartserver.Model.Food;
import com.example.computer.androidcartserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fb;
    FirebaseDatabase db;
    DatabaseReference foodlist;
    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food,FoodViewHolder>adapter;
    MaterialEditText edtname,edtprice,edtdiscount,edtdescription;
    Button btnselect,btnupload;
    Food newFood;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        db = FirebaseDatabase.getInstance();
        foodlist = db.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference= storage.getReference();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        relativeLayout = (RelativeLayout)findViewById(R.id.foodlayout);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fb =(FloatingActionButton)findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showAddFoodDialog();
            }
        });
        if (getIntent()!=null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }if (!categoryId.isEmpty()){
            loadListfood(categoryId);
        }
    }

    private void showAddFoodDialog(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtname = add_menu_layout.findViewById(R.id.editname);
        edtprice = add_menu_layout.findViewById(R.id.editprice);
        edtdiscount = add_menu_layout.findViewById(R.id.editdiscount);
        edtdescription = add_menu_layout.findViewById(R.id.editdescription);

        btnselect = add_menu_layout.findViewById(R.id.btnselect);
        btnupload = add_menu_layout.findViewById(R.id.btnupload);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newFood!=null){
                    foodlist.push().setValue(newFood);
                    Snackbar.make(relativeLayout,"New Category"+newFood.getName()+"was added",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void uploadImage() {
        final ProgressDialog mdialoge = new ProgressDialog(this);
        mdialoge.setMessage("Uploading....");
        mdialoge.show();
        String imagename  =UUID.randomUUID().toString();
        final StorageReference imagefolder = storageReference.child("images/"+imagename);
        imagefolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mdialoge.dismiss();
                Toast.makeText(FoodList.this,"Uploaded...",Toast.LENGTH_SHORT).show();
                imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newFood = new Food();
                        newFood.setName(edtname.getText().toString());
                        newFood.setDiscount(edtdiscount.getText().toString());
                        newFood.setMenuId(categoryId);
                        newFood.setPrice(edtprice.getText().toString());
                        newFood.setDescription(edtdescription.getText().toString());
                        newFood.setImage(uri.toString());
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mdialoge.dismiss();
                        Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        mdialoge.setMessage("Uploaded"+progress+"%");
                    }
                });
    }
    private void chooseImage() {
        Intent intent  = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_RIQUEST);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void loadListfood(String categoryId) {
        Query listFoodByCategoryId = foodlist.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food>options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByCategoryId,Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.textmenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.imageView);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemview = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.food_item,viewGroup,false);

                return new FoodViewHolder(itemview);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        /*
        adapter =  new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.textmenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                     //
                    }
                });
            }
        };

        */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Common.PICK_IMAGE_RIQUEST && resultCode== RESULT_OK && data!=null && data.getData()!=null)
        {
            uri = data.getData();
            btnselect.setText("Image Selected!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Update)){
            showudateFoodDialoge(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.Delete)){
              deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodlist.child(key).removeValue();
    }

    private void showudateFoodDialoge(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtname = add_menu_layout.findViewById(R.id.editname);
        edtprice = add_menu_layout.findViewById(R.id.editprice);
        edtdiscount = add_menu_layout.findViewById(R.id.editdiscount);
        edtdescription = add_menu_layout.findViewById(R.id.editdescription);

        edtname.setText(item.getName());
        edtprice.setText(item.getPrice());
        edtdiscount.setText(item.getDiscount());
        edtdescription.setText(item.getDescription());

        btnselect = add_menu_layout.findViewById(R.id.btnselect);
        btnupload = add_menu_layout.findViewById(R.id.btnupload);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                    item.setName(edtname.getText().toString());
                    item.setPrice(edtprice.getText().toString());
                    item.setDiscount(edtdiscount.getText().toString());
                    item.setDescription(edtdescription.getText().toString());

                    foodlist.child(key).setValue(item);
                    //Snackbar.make(relativeLayout, " Food" + item.getEventName() + "was edited", Snackbar.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void changeImage(final Food item) {
        if (uri != null) {
            final ProgressDialog mdialoge = new ProgressDialog(this);
            mdialoge.setMessage("Uploading....");
            mdialoge.show();
            String imagename = UUID.randomUUID().toString();
            final StorageReference imagefolder = storageReference.child("images/" + imagename);
            imagefolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialoge.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded...", Toast.LENGTH_SHORT).show();
                    imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mdialoge.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mdialoge.setMessage("Uploaded" + progress + "%");
                        }
                    });
        }
      }
    }

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
import com.example.computer.androidcartserver.Model.Banner;
import com.example.computer.androidcartserver.Model.Food;
import com.example.computer.androidcartserver.ViewHolder.BannerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.http.Url;

public class BannerActivity extends AppCompatActivity{
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fb;
    FirebaseDatabase db;
    DatabaseReference banners;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Banner,BannerViewHolder>adapter;
    MaterialEditText edtName,edtFoodId;
    Button btnUpload,btnselect;
    Banner newBanner;
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        db = FirebaseDatabase.getInstance();
        banners = db.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        storageReference= storage.getReference();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_banner);
        recyclerView.setHasFixedSize(true);
        relativeLayout = (RelativeLayout)findViewById(R.id.bannerlayout);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        fb =(FloatingActionButton)findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBannerDialog();
            }
        });
        loadlistbanner();
    }

    private void loadlistbanner() {
        FirebaseRecyclerOptions<Banner>allBanner=new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banners,Banner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(allBanner) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.banner_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.banner_image);
            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemview = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.banner_layout,viewGroup,false);
                return new BannerViewHolder(itemview);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showAddBannerDialog() {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
            alertDialog.setTitle("Add new Banner");
            alertDialog.setMessage("Please fill full information");

            LayoutInflater inflater = this.getLayoutInflater();
            View v = inflater.inflate(R.layout.add_new_banner,null);

            edtFoodId = v.findViewById(R.id.editfoodId);
            edtName = v.findViewById(R.id.editfoodname);

            btnselect = v.findViewById(R.id.btnselect);
            btnUpload = v.findViewById(R.id.btnupload);

            btnselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     chooseImage();
                }
            });
            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPicture();
                }
            });
            alertDialog.setView(v);
            alertDialog.setIcon(R.drawable.ic_laptop_chromebook_black_24dp);
            alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (newBanner!= null)
                    banners.push().setValue(newBanner);
                    loadlistbanner();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    newBanner = null;
                    loadlistbanner();
                }
            });
            alertDialog.show();

    }

    private void uploadPicture() {
        final ProgressDialog mdialoge = new ProgressDialog(this);
        mdialoge.setMessage("Uploading....");
        mdialoge.show();
        String imagename  =UUID.randomUUID().toString();
        final StorageReference imagefolder = storageReference.child("images/"+imagename);
        imagefolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mdialoge.dismiss();
                Toast.makeText(BannerActivity.this,"Uploaded...",Toast.LENGTH_SHORT).show();
                imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newBanner = new Banner();
                        newBanner.setName(edtName.getText().toString());
                        newBanner.setName(edtFoodId.getText().toString());
                        newBanner.setImage(uri.toString());
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mdialoge.dismiss();
                        Toast.makeText(BannerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Common.PICK_IMAGE_RIQUEST && resultCode== RESULT_OK && data!=null && data.getData()!=null)
        {
            filePath =  data.getData();
            btnselect.setText("Image Selected!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Update)){
            showudateBannerDialoge(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.Delete)){
            deleteBanner(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteBanner(String key) {
        banners.child(key).removeValue();

    }

    private void showudateBannerDialoge(final String key, final Banner item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Edit Banner");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_banner_layout = inflater.inflate(R.layout.add_new_banner,null);

        edtName = add_banner_layout.findViewById(R.id.editfoodname);
        edtFoodId = add_banner_layout.findViewById(R.id.editfoodId);

        edtName.setText(item.getName());
       edtFoodId.setText(item.getId());

        btnselect = add_banner_layout.findViewById(R.id.btnselect);
        btnUpload = add_banner_layout.findViewById(R.id.btnupload);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        alertDialog.setView(add_banner_layout);
        alertDialog.setIcon(R.drawable.ic_laptop_chromebook_black_24dp);
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                 item.setName(edtName.getText().toString());
                 item.setId(edtFoodId.getText().toString());

                Map<String,Object>update = new HashMap<>();
                update.put("id",item.getId());
                update.put("name",item.getName());
                update.put("image",item.getImage());
                banners.child(key).updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(relativeLayout,"Updated",Snackbar.LENGTH_SHORT).show();
                                loadlistbanner();
                            }
                        });
               // banners.child(key).setValue(item);
                Snackbar.make(relativeLayout, " Food" + item.getName() + "was edited", Snackbar.LENGTH_SHORT).show();
                loadlistbanner();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadlistbanner();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Banner item) {
        if (filePath != null) {
            final ProgressDialog mdialoge = new ProgressDialog(this);
            mdialoge.setMessage("Uploading....");
            mdialoge.show();
            String imagename = UUID.randomUUID().toString();
            final StorageReference imagefolder = storageReference.child("images/" + imagename);
            imagefolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialoge.dismiss();
                    Toast.makeText(BannerActivity.this, "Uploaded...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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


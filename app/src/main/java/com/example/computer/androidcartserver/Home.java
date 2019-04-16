package com.example.computer.androidcartserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Interface.ItemClickListener;
import com.example.computer.androidcartserver.Model.Category;
import com.example.computer.androidcartserver.Model.Food;
import com.example.computer.androidcartserver.Model.Token;
import com.example.computer.androidcartserver.ViewHolder.FoodViewHolder;
import com.example.computer.androidcartserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        TextView txtfullname;
        FirebaseDatabase database;
        DatabaseReference categories;
        FirebaseStorage storage;
        StorageReference storageReference;
        FirebaseRecyclerAdapter<Category,MenuViewHolder>adapter;
        RecyclerView recycler_menu;
        RecyclerView.LayoutManager layoutManager;
        MaterialEditText edtname;
        Button btnselect,btnupload;
        Category newcategory;
       DrawerLayout drawer;
        Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Menu Management");
        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
        storage  = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
         View headerview = navigationView.getHeaderView(0);
        txtfullname = (TextView)headerview.findViewById(R.id.textfullname);
        txtfullname.setText(Common.currentUser.getName());

        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadmenu();

      updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,true);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtname = add_menu_layout.findViewById(R.id.editname);
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
                 if (newcategory!=null){
                     categories.push().setValue(newcategory);
                     Snackbar.make(drawer,"New Category"+newcategory.getName()+"was added",Snackbar.LENGTH_SHORT).show();
                 }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
                Toast.makeText(Home.this,"Uploaded...",Toast.LENGTH_SHORT).show();
                imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newcategory = new Category(edtname.getText().toString(),uri.toString());
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mdialoge.dismiss();
                Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Common.PICK_IMAGE_RIQUEST && resultCode== RESULT_OK && data!=null && data.getData()!=null)
        {
            uri = data.getData();
            btnselect.setText("Image Selected!");
        }
    }

    private void chooseImage() {
        Intent  intent  = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_RIQUEST);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void loadmenu() {
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categories,Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.textmenuname.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage()).into(holder.imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent mine  = new Intent(Home.this,FoodList.class);
                        mine.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(mine);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemview = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item,viewGroup,false);

                return new MenuViewHolder(itemview);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
        /*
      adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
              Category.class,
              R.layout.menu_item,
              MenuViewHolder.class,
              categories
      ) {
          @Override
          protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
             viewHolder.textmenuname.setText(model.getName());
              Picasso.with(Home.this).load(model.getImage()).into(viewHolder.imageView);

              viewHolder.setItemClickListener(new ItemClickListener() {
                  @Override
                  public void onClick(View view, int position, boolean isLongClick) {
                      Intent mine  = new Intent(Home.this,FoodList.class);
                      mine.putExtra("CategoryId",adapter.getRef(position).getKey());
                      startActivity(mine);
                  }
              });
          }
      };*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id==R.id.nav_orders){
            Intent myorder = new Intent(Home.this,OrderStatus.class);
            startActivity(myorder);
        }else if (id==R.id.nav_banner){
            Intent banner = new Intent(Home.this,BannerActivity.class);
            startActivity(banner);
        }else if (id==R.id.nav_message){
            Intent banner = new Intent(Home.this,SendMessage.class);
            startActivity(banner);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Update))
        {
            showUpdatedialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else  if (item.getTitle().equals(Common.Delete))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        DatabaseReference foods = database.getReference("Foods");
        Query foodInCategory = foods.orderByChild("menuId");
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        categories.child(key).removeValue();
        Toast.makeText(Home.this,"Item Deleted",Toast.LENGTH_SHORT).show();
    }

    private void showUpdatedialog(final String key, final Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtname = add_menu_layout.findViewById(R.id.editname);
        btnselect = add_menu_layout.findViewById(R.id.btnselect);
        btnupload = add_menu_layout.findViewById(R.id.btnupload);

        edtname.setText(item.getName());
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
               categories.child(key).setValue(item);
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
    private void changeImage(final Category item) {
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
                    Toast.makeText(Home.this, "Uploaded...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

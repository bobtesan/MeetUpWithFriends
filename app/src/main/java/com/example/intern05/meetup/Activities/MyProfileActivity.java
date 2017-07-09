package com.example.intern05.meetup.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.support.annotation.RequiresApi;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.intern05.meetup.Other.CircleTransform;
import com.example.intern05.meetup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Permissions;

import static android.R.attr.data;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageView imgProfile;
    private TextView dateBirth;
    private CheckBox chMale,chFemale;
    private Button saveB,changePwdB;
    private AlertDialog.Builder alertDialog;
    private String username;
    private String userID;
    private TextView txtName;
    private Activity activity;
    private StorageReference mStorageRef;
    private Uri imageUri;

    private int Request_File_Write_Permission=1003;
    private int Request_File_Read_Permission=1002;
    private static final int Get_From_Gallery=2;

    DatabaseReference root= FirebaseDatabase.getInstance().getReference("Users");

    private static final String urlProfileImg = "https://www.wnmlive.com/images/Default-Profile.jpg";

    int day, year, month;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth auth=FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        activity = this;

        mStorageRef = FirebaseStorage.getInstance().getReference();

        txtName=(TextView)findViewById(R.id.textView17);
        chMale=(CheckBox)findViewById(R.id.checkBoxMale);
        chFemale=(CheckBox)findViewById(R.id.checkBoxFemale);

        saveB=(Button)findViewById(R.id.saveB);
        changePwdB=(Button)findViewById(R.id.chPwdB);

        changePwdB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog= new AlertDialog.Builder(MyProfileActivity.this);
                alertDialog.setTitle("Change Password");
                alertDialog.setMessage("Please enter your password");

                LinearLayout layout = new LinearLayout(MyProfileActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);



                final EditText newPwd=new EditText(MyProfileActivity.this);
                final EditText oldPwd=new EditText(MyProfileActivity.this);
                oldPwd.setHint("Old password");
                newPwd.setHint("New password");
                layout.addView(oldPwd);
                layout.addView(newPwd);

                oldPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);




                alertDialog.setView(layout);


                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email;
                        email=auth.getCurrentUser().getEmail().toString();

                        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPwd.getText().toString());
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(newPwd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),"Password updated",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(),"Error: Password not updated",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(getApplicationContext(),"Old password is wrong.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                alertDialog.show();


            }
        });

        userID=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    if (children.getKey().equals(userID)) {
                        for (DataSnapshot child : children.getChildren()) {
                            if (child.getKey().equals("Username")) {
                                username = child.getValue(String.class);
                                txtName.setText(username);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        chMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    chFemale.setChecked(false);
            }
        });

        chFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chMale.setChecked(false);
            }
        });
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadPhoto(imageUri);
            }
        });

        imgProfile=(ImageView)findViewById(R.id.profile_picture);

        Glide.with(this)
                .load(urlProfileImg)
                .crossFade()
                .override(400,400)
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Request_File_Read_Permission);
                }else{
                    GalleryOpen();
                }
            }
        });

        dateBirth=(TextView) findViewById(R.id.dateBirth);


        dateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });



    }

    Calendar calander = Calendar.getInstance();
    int yearC = calander.get(Calendar.YEAR);
    int monthC = calander.get(Calendar.MONTH);
    int dayC = calander.get(Calendar.DAY_OF_MONTH);



    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id){
        return new DatePickerDialog(this, datePickerListener, yearC, monthC, dayC);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
            dateBirth.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Request_File_Write_Permission){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                GalleryOpen();
            }else {
                boolean showRationale= ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0]);
                if(!showRationale){
                    AlertDialog.Builder builder;
                    builder=new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setTitle("Warning.")
                            .setMessage("You won't be able to add pictures")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        }

    }

    private void GalleryOpen() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), Get_From_Gallery);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(this)
                        .load(imageUri)
                        .crossFade()
                        .override(400,400)
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MyProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(MyProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
  /*  public void uploadPhoto(Uri imageUri){
        Uri file =  Uri.fromFile(new File("path/to/images/rivers.jpg"));;
        StorageReference riversRef = mStorageRef.child("images/"+imageUri.toString());

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                       // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
}

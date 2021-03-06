package yam.womensafety;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserInfo extends AppCompatActivity {

    private Button mConfirm;
    private EditText mName;
    private ImageView mProfileImage;

    private Uri resultUri;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference mCustomerDatabase;

    private String mUserId,mname,mprofileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

            mProfileImage=(ImageView) findViewById(R.id.profileImage);

            mConfirm=(Button)findViewById(R.id.confirm);

            mName=(EditText)findViewById(R.id.name);

            firebaseAuth= FirebaseAuth.getInstance();
            mUserId=firebaseAuth.getCurrentUser().getUid();

            mCustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId);
            mProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            });

            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mname=mName.getText().toString();
                    if (mName==null&&resultUri==null){
                        Toast.makeText(UserInfo.this, "Please fill all the details!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(UserInfo.this, "Submitting", Toast.LENGTH_SHORT).show();
                        saveUserInformation();
                    }
                }
            });

        }


        private void saveUserInformation(){

            Map userName=new HashMap();
            userName.put("name",mname);

            mCustomerDatabase.updateChildren(userName);

            if (resultUri!=null){
                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(mUserId);

                Bitmap bitmap=null;
                try {
                    bitmap= MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,20, baos);
                byte[] data=baos.toByteArray();
                UploadTask uploadTask=filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                        return;
                    }
                });

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl=taskSnapshot.getDownloadUrl();

                        Map imageMap=new HashMap();
                        imageMap.put("profile_image",downloadUrl.toString());
                        mCustomerDatabase.updateChildren(imageMap);

                        Toast.makeText(UserInfo.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(UserInfo.this,Main2Activity.class));
                        finish();
                    }
                });

            }
            else{
                Toast.makeText(this, "not uploaded", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode==1&&resultCode== Activity.RESULT_OK){
                final Uri imageUri=data.getData();
                resultUri=imageUri;
                mProfileImage.setImageURI(resultUri);
            }
        }

    }

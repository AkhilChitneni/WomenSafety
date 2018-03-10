package yam.womensafety;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class AddContacts extends AppCompatActivity {
    private String fileName = "file.txt";
    private Button add;
    private EditText ed;
    private String PhoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        add = (Button)findViewById(R.id.add);
        ed = (EditText)findViewById(R.id.editText2);

        add.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                PhoneNum = ed.getText().toString();
                saveFile(fileName,PhoneNum);
                Toast.makeText(AddContacts.this, PhoneNum, Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void saveFile(String file,String text){
        try{


            File num_file = new File(fileName);

            PrintWriter out = new PrintWriter(num_file);
            out.println(text);
            out = null;
        }
        catch(Exception e){
            Toast.makeText(this, "cannot save the file", Toast.LENGTH_SHORT).show();
        }
    }
    public String readFile(String file){
        try{
            String text = "";
            FileInputStream fin = openFileInput(file);
            int size = fin.available();
            byte[] buffer = new byte[size];
            fin.read(buffer);

            text = new String(buffer);fin.close();
            return text;
        }
        catch(Exception e){
            return "";
            //Toast.makeText(this, "error ocurred while reading", Toast.LENGTH_SHORT).show();
        }

    }
}

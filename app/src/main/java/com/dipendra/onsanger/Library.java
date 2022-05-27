package com.dipendra.onsanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

public class Library extends AppCompatActivity {
ArrayList<String> name,_id;
ArrayList<Bitmap> img;
MyDataBase myDB;
    int i=0;
CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        myDB=new MyDataBase(Library.this);
img=new ArrayList<>();
_id=new ArrayList<>();
        name=new ArrayList<>();
        storeDataInArrays();
        customAdapter = new CustomAdapter(Library.this,this, name,_id,img);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Library.this));

    }
    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(Library.this, "No Data to show", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){

                _id.add(String.valueOf(i));
                name.add(cursor.getString(0));
             byte[] is = cursor.getBlob(1);
                Bitmap bitmap = BitmapFactory.decodeByteArray(is,0,is.length);
img.add(bitmap);
            }

        }
    }

}
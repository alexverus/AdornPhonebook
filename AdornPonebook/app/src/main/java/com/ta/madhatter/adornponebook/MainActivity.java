package com.ta.madhatter.adornponebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /** Called when the user clicks the Send button */
    public void initiateUpdating(View view) {
        Intent intent = new Intent(this, UpdateContactPhotos.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "Fini!";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}

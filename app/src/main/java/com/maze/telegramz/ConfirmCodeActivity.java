package com.maze.telegramz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ConfirmCodeActivity extends AppCompatActivity {

    EditText verfCodeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);
        verfCodeField = findViewById(R.id.verfCodeField);
        Bundle extra = getIntent().getExtras();
        String ph = extra.getString("phoneToVerify");
        if(ph!=null)
            Toast.makeText(getApplicationContext(),ph,Toast.LENGTH_LONG).show();
        setTitle(ph);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirm_code_activity,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case R.id.confirm_in_tick:
//                Toast.makeText(getApplicationContext(),"code is " + verfCodeField.getText().toString() ,Toast.LENGTH_LONG).show();
                Authentication.sendVerfCode(verfCodeField.getText().toString());
                final Intent intent = new Intent(this, ChatsCallsProfileActivity.class);
                startActivity(intent);
        }
        return true;
    }
}

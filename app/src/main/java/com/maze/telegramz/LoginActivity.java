package com.maze.telegramz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private CountryCodePicker ccpMini;
    EditText phoneNumField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ccp = findViewById(R.id.ccp);
        ccpMini = findViewById(R.id.ccpMini);
        phoneNumField = findViewById(R.id.phoneNumField);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                ccpMini.setCountryForPhoneCode(ccp.getSelectedCountryCodeAsInt());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login_activity,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
            switch (mi.getItemId()){
                case R.id.sign_in_tick:
                    String countryCode = ccp.getSelectedCountryCodeWithPlus();
                    String phoneNum = phoneNumField.getText().toString();
//                    Toast.makeText(getApplicationContext(),countryCode+phoneNum,Toast.LENGTH_SHORT).show();

                    Telegram.setPhoneNum(countryCode + phoneNum);
                    final Intent intent = new Intent(this, ConfirmCodeActivity.class);
                    intent.putExtra("phoneToVerify",countryCode+phoneNum);
                    startActivity(intent);
                    finish();
            }
        return true;
    }

}

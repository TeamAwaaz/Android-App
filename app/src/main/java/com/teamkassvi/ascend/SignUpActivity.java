package com.teamkassvi.ascend;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText editTextUserName,editTextPassword,editTextName,editTextEmail,editTextConfirmPassword;
    Button btnCreateAccount;
    TextView dateChanger;
    RadioButton r1,r2,r3;
    Button mdateChanger;
    LoginDataBaseAdapter loginDataBaseAdapter;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mdateChanger=(Button) findViewById(R.id.date);
        dateChanger=(TextView) findViewById(R.id.editText6);
        r1=(RadioButton) findViewById(R.id.male);
        r2=(RadioButton) findViewById(R.id.female);
        r3=(RadioButton) findViewById(R.id.others);

//        Intent intent=getIntent();
//        String date=intent.getStringExtra("DATE: ");
//        dateChanger.setText(date);

        mdateChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignUpActivity.this,CalendarActivity.class);
                startActivity(i);
            }
        });

        // get Instance  of Database Adapter
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        // Get References of Views
        editTextUserName=(EditText)findViewById(R.id.editText2);
        editTextEmail=(EditText)findViewById(R.id.editText3);
        editTextName=(EditText)findViewById(R.id.editText1);
        editTextPassword=(EditText)findViewById(R.id.editText4);
        editTextConfirmPassword=(EditText)findViewById(R.id.editText5);

        btnCreateAccount=(Button)findViewById(R.id.signup);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();
                String name=editTextName.getText().toString();

                String email=editTextEmail.getText().toString();
                String confirmPassword=editTextConfirmPassword.getText().toString();

                // check if any of the fields are vacant
                if(userName.equals("")||password.equals("")||confirmPassword.equals("")||name.equals("")||email.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Field vacant", Toast.LENGTH_LONG).show();
                    return;
                }
                // check if both password matches
                if(!password.equals(confirmPassword))
                {
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.length()<=6)
                {
                    Toast.makeText(getApplicationContext(), "Password should be greater than 6 characters", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Save the Data in Database
                    String accountType = "local";
                    String uri = null;
                    loginDataBaseAdapter.insertEntry(userName, password, email, accountType, uri);
                    Toast.makeText(getApplicationContext(), "Account successfully created ", Toast.LENGTH_LONG).show();
                    Intent i =new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(i);
                }
            }
        });


        Date dt = new Date();
        datePickerDialog = new DatePickerDialog(
                this, SignUpActivity.this, 2000, 0, 1);

    }


    public void selectDate(View v){
        datePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        loginDataBaseAdapter.close();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        month++;
        dateChanger.setText(date+"/"+month+"/"+year);
    }
}

package com.iri.crisiseye;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.iri.crisiseye.asynctask.RegisterAsyncTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tsarkar on 02/10/17.
 */
public class UserRegistrationActivity extends Activity {

    TextView txtview_login;
    EditText edittext_firstname;
    EditText edittext_lastname;
    EditText edittext_email_address;
    EditText edittext_password;
    EditText edittext_conf_password;

    private int year, month, day;
    private Calendar calendar;
    String deviceID;
    Toolbar toolbar;
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isvalidphoneno(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }












    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);


        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setActionBar(toolbar);
//        toolbar.setTitle("Create Account");
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText("Create Account");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        deviceID = telephonyManager.getDeviceId();
        txtview_login = (TextView) findViewById(R.id.txtview_login);
        edittext_firstname = (EditText) findViewById(R.id.edittext_firstname);
        edittext_lastname = (EditText) findViewById(R.id.edittext_lastname);
        edittext_email_address = (EditText) findViewById(R.id.edittext_email_address);
        edittext_password = (EditText) findViewById(R.id.edittext_password);
        edittext_conf_password = (EditText) findViewById(R.id.edittext_conf_password);


        txtview_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isConnectingToInternet() == true) {
                    String firstname = edittext_firstname.getText().toString();
                    String lastname = edittext_lastname.getText().toString();
                    String email_address = edittext_email_address.getText().toString();
                    String password = edittext_password.getText().toString();
                    String conf_password = edittext_conf_password.getText().toString();


                    HashMap<String, String> registration_params = new HashMap<String, String>();
                    System.out.println("edittext_firstname is " + firstname);
                    System.out.println("edittext_lastname is " + lastname);
                    System.out.println("edittext_email_address is " + email_address);
                    System.out.println("password is " + password);
                    System.out.println("conf_password is " + conf_password);


//                    registration_params.put("c", "registration");
//                    registration_params.put("method", "registration");
                    registration_params.put("lastname", lastname);
                    registration_params.put("firstname", firstname);
                    registration_params.put("email", email_address);

                    registration_params.put("password", password);
                    registration_params.put("confirm_password", conf_password);




                    if ((isValidEmail(email_address)== true))
                    {
                        RegisterAsyncTask regtask = new RegisterAsyncTask(UserRegistrationActivity.this,registration_params);
                        regtask.execute(registration_params);
                    }
                    else
                    {
                        if (isValidEmail(email_address)== false)
                        {
                            Toast.makeText(UserRegistrationActivity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {
                    final AlertDialog ad = new AlertDialog.Builder(UserRegistrationActivity.this).create();
                    ad.setTitle("No data connection !!!");
                    ad.setMessage("Please retry later");

                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ad.dismiss();
                        }
                    });

                    // Showing Alert Message
                    ad.show();
                }
            }
        });
    }

}

package com.iri.crisiseye.asynctask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iri.crisiseye.LoginActivity;
import com.iri.crisiseye.UserRegistrationActivity;

public class RegisterAsyncTask extends AsyncTask<HashMap<String, String>, Void, String> {

    Context mContext;
    UserRegistrationActivity activity;
    Dialog dialog;
//    String mobile_no;
    String firstname ;
    String lastname ;
    String email ;

    String confirm_password ;
    String password ;
    String uuid ;
    ProgressDialog mProgress;

    HashMap<String,String> user_detls;
    public RegisterAsyncTask(UserRegistrationActivity context,HashMap<String,String> user_dtls) {
        mContext = context;
        this.activity = context;

        this.user_detls = user_dtls;



        System.out.println("lastname is "+user_detls.get("lastname"));
        System.out.println("firstname is "+user_detls.get("firstname"));
        System.out.println("email is "+user_detls.get("email"));
        System.out.println("confirm_password is "+user_detls.get("confirm_password"));
        System.out.println("password is "+user_detls.get("password"));






    }




    @Override
    protected String doInBackground(HashMap<String,String>... params) {
        // TODO Auto-generated method stub

        String response ="";
//		String loginURL = "http://14.140.25.155:81/api/v1/index.php";
        String webserviceURL = "http://192.168.1.22:8080/CrisisEyeWebServices/crisiseye/webservice/register";
//        String webserviceURL = "http://192.168.10.200:8080/CrisisEyeWebServices/crisiseye/webservice/login";
//        String webserviceURL = "http://192.168.201.53:8080/CrisisEyeWebServices/crisiseye/webservice/register";
        System.out.println("webserviceURL url is "+ webserviceURL);
        try{
//			user_detls = params[0];

            System.out.println("In new connection function");
//			String parameters = getQuery(params[0]);
            URL url = new URL(webserviceURL);
            System.out.println("hello 1");
//            URLConnection connection = url.openConnection();
            System.out.println("hello 2");
//            HttpURLConnection httpConnection = (HttpURLConnection) connection;
//            System.out.println("url is " +connection.getURL());
            String params_formed = getQuery(params[0]);


            byte[] postData       = params_formed.getBytes();
            System.out.println("hello 3");
            int    postDataLength = postData.length;

             url = new URL(webserviceURL+"?"+params_formed);
            System.out.println("hello 4");
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            System.out.println("url is " +connection.getURL());
            httpConnection.setRequestMethod( "GET" );
            System.out.println("hello 5");
//            httpConnection.setInstanceFollowRedirects( false );
            System.out.println("hello 6");
            connection.setDoInput(true);
            httpConnection.setUseCaches(false);
//            httpConnection.setDoOutput( true );
            System.out.println("hello 7");
            httpConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            System.out.println("hello 8");
            httpConnection.setRequestProperty( "charset", "utf-8");
            System.out.println("hello 9");
//  			httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            System.out.println("hello 10");
            httpConnection.setUseCaches( false );

            System.out.println("hello 11");
            System.out.println("final url is " +connection.getURL());





            int responseCode=httpConnection.getResponseCode();
            System.out.println("responseCode is "+ responseCode );


            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                System.out.println("responseCode is HTTP_OK ");
                BufferedReader br=new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    System.out.println("line"+line);
                    response+=line;
                }


            }
            else {
                response="";

            }
            System.out.println("response length is "+response.length());
            System.out.println("response is "+response);



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }
    private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;


        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pairs.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pairs.getValue(), "UTF-8"));
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("result in url parameter query formation is "+ result);
        return result.toString();
    }

    @Override
    protected void onPreExecute() {
        System.out.println("in on preexecute");
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Registering User");
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }
    @Override
    protected void onPostExecute(String response) {
        // TODO Auto-generated method stub
        super.onPostExecute(response); if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }
        System.out.println("+ response is " + response);

        if (response == null) {
            Toast.makeText(mContext, "Sorry some error occurred",
                    Toast.LENGTH_LONG).show();
        } else {
            System.out.println("++ response is " + response);
            System.out.println("response length is  " + response.length());
//            JsonParser json_parser = new JsonParser();
//            HashMap parking_detls = json_parser.parse_parkingresults(response);
//            int size = parking_detls.size();
//            System.out.println("size of returned hashmap is >> "+ size);
//            delegate.processFinish(parking_detls);
            if (response.trim().equals("success")) {
                Intent login_activity = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(login_activity);
            }


            else if (response.trim().equals("failure")) {
                Toast.makeText(mContext, "Registration Unsuccessful",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mContext, response.trim(),
                        Toast.LENGTH_LONG).show();
            }

        }
    }


}



package com.iri.crisiseye.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tsarkar on 06/10/17.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    private String email = "";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
        System.out.println(">< Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        System.out.println(">< sendRegistrationToServer : " + token);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());

        SharedPreferences pref = getApplicationContext().getSharedPreferences("login_pref", 0);
        email = pref.getString("email", "wrong");
        System.out.println("email is >< :: " + email);
        if (email.equals("wrong")) {
            SharedPreferences.Editor editor = pref.edit();
            System.out.println("email is  :: " + email);
            editor.putString("token",token);
            editor.commit();
        }
        else
        {
        try {
            HashMap hm = new HashMap();
            hm.put("token", token);
            hm.put("email", email);
            String params_formed = getQuery(hm);
            byte[] postData = params_formed.getBytes();

            String webserviceURL = "http://192.168.1.22:8080/CrisisEyeWebServices/crisiseye/webservice/hello_post";

            /*String token_data = URLEncoder.encode("token", "UTF-8")
                    + "=" + URLEncoder.encode("76876hvv", "UTF-8");

            String email_data = URLEncoder.encode("email", "UTF-8")
                    + "=" + URLEncoder.encode("saf@qwe.com", "UTF-8");*/

//            URL url = new URL(webserviceURL);

//            URL url = new URL(webserviceURL + "/" + token);
//            URL url = new URL(webserviceURL + "?" + token_data + "&" + email_data);
            URL url = new URL(webserviceURL + "?" + params_formed);

            System.out.println("URL is " + url.getPath() + " :: " + url.getQuery());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            /*connection.setInstanceFollowRedirects( false );

//          connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);
//            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty( "charset", "utf-8");
//            connection.setUseCaches( false );
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write( postData );
//            dos.writeBytes("token=" + "76876hvv");
//            dos.writeBytes("email=" + "saf@qwe.com");


//            dos.writeBytes("token=" + token);
//            dos.writeBytes("email=" + email);
            dos.flush();
//            dos.close();*/
            String response = "";
//            connection.connect();
            System.out.println(">< connection.getResponseCode() :: " + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String line;
                System.out.println("responseCode is HTTP_OK ");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    System.out.println("line" + line);
                    response += line;
                }

                System.out.println("response is " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}

package com.iri.crisiseye.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tsarkar on 24/05/17.
 */
public class WebService_AsyncTask extends AsyncTask<HashMap<String,String>, Void, String> {
//    String fetchWeatherURL = "http://tsarkarwebservice.ddns.net/RestfulWebServiceCA/contextaware/webservice/hello";
//    String fetchWeatherURL = "http://192.168.1.181:8080/CrisisEyeWebServices/crisiseye/webservice/hello";

    InputStream in = null;
    String weather_url = "";//AppConstants.login_url;
    private Context mContext;

    String deviceID = "";

    public void setDeviceId(String deviceID) {
        this.deviceID = deviceID;
    }
    public WebService_AsyncTask(Context context){
        this.mContext = context;
//        System.out.println(" ++??? in constructor webservice asynctask");
    }
    private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;




        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
//            System.out.println(pairs.getKey() + " = " + pairs.getValue());

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pairs.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pairs.getValue(), "UTF-8"));
            it.remove(); // avoids a ConcurrentModificationException
        }
//        System.out.println("result in url parameter query formation is "+ result);
        return result.toString();
    }


    @Override
    protected void onPreExecute() {
//        System.out.println(" ++??? in on preexecute");

    }

    @Override
    protected String doInBackground(HashMap<String,String>... params) {
        // TODO Auto-generated method stub

//        String webserviceURL = "http://tsarkarwebservice.ddns.net/RestfulWebServiceCA/contextaware/webservice/ccstationlocations";
//        String webserviceURL = "http://192.168.1.2:8080/CrisisEyeWebServices/crisiseye/webservice/hi?";
//        String webserviceURL = "http://192.168.80.181:8080/CrisisEyeWebServices/crisiseye/webservice/hi?";
        String webserviceURL = "http://192.168.1.22:8080/CrisisEyeWebServices/crisiseye/webservice/device_use_info?";
//        String webserviceURL = "http://192.168.201.53:8080/CrisisEyeWebServices/crisiseye/webservice/hi";


//        http://192.168.80.181:8080
//        192.168.43.198  //192.168.1.106
        InputStream in = null;
        String result = "";
        try {
//            System.out.println("++ In new connection function");

            String params_formed = getQuery(params[0]);
            byte[] postData  = params_formed.getBytes();
//            URL url = new URL(webserviceURL);
            URL url = new URL(webserviceURL + params_formed);
//            System.out.println("url is "+ url);
            URLConnection connection = url.openConnection();

//            System.out.println("++ hello 1");
//            URLConnection connection = url.openConnection();
//            System.out.println("++ hello 2");
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

//            System.out.println("++ hello 3");
//            System.out.println("++ url is " +connection.getURL());







//            System.out.println("++ hello 3");
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
//            httpConnection.setDoOutput(true);
//            System.out.println("++ hello 4");
//            httpConnection.setInstanceFollowRedirects( false );
//            System.out.println("++ hello 5");
//            connection.setDoInput(true);
//            httpConnection.setDoOutput( true );
//            System.out.println("++ hello 6");
//            httpConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
//            System.out.println("++ hello 7");
//            httpConnection.setRequestProperty( "charset", "utf-8");
//            System.out.println("++ hello 8");
//  			httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));

            httpConnection.setUseCaches( false );

//            System.out.println("++ hello 9");
            System.out.println("++ final url is " +connection.getURL());








            httpConnection.setRequestProperty("Content-Type", "application/plain");
//			httpConnection.setRequestProperty ("user-key", new String(URLEncoder.encode("a472f0195e685a4eeae803aef02d0d4a")));
//                httpConnection.setRequestProperty ("user-key", "a472f0195e685a4eeae803aef02d0d4a");
            httpConnection.setRequestProperty("Content-Language", "en-US");
//                httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
//		    httpConnection.setConnectTimeout(5000);
            httpConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            httpConnection.setRequestProperty("Accept","*/*");

            in = httpConnection.getInputStream();
//            System.out.println("++ hello 8");
            int responseCode = httpConnection.getResponseCode();

//            System.out.println("++ hello 9");
            if (responseCode == HttpURLConnection.HTTP_OK) {
//                System.out.println("++ respone code is ok");
                StringBuilder sb = new StringBuilder("");
                // int line = 0;
                int length = httpConnection.getContentLength();
//                System.out.println("++ Length = " + length);

                int i;
                do {
                    i = in.read();
                    if (i != -1) {
                        // System.out.println("Character >>> "+ (char)i);
                        sb.append((char) i);
                    }
                } while (i != -1);

                result = new String(sb);
                in.close();
                // result = sb.toString();
//                System.out.println("++ result is !!!" + result);
                // return result;
            }
        } catch (Exception e) {

            e.printStackTrace();
            result = "failure";

//            System.out.println("++ Exception e is " + e);
            //timeout = true;

        }

        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        System.out.println("Final result....." + result);
        return result;

/*        InputStream in = null;
        String result = "";
        try {
                System.out.println("In new connection function");
//                String params_formed = getQuery(params[0]);
//                byte[] postData  = params_formed.getBytes();
//                int postDataLength = postData.length;
//                URL url = new URL(fetchWeatherURL + params_formed);
                URL url = new URL(fetchWeatherURL);
                System.out.println("hello 1");
                URLConnection connection = url.openConnection();
                System.out.println("hello 2");
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                System.out.println("hello 3");

                httpConnection.setRequestProperty("Content-Type", "application/plain");
//			httpConnection.setRequestProperty ("user-key", new String(URLEncoder.encode("a472f0195e685a4eeae803aef02d0d4a")));
//                httpConnection.setRequestProperty ("user-key", "a472f0195e685a4eeae803aef02d0d4a");
                httpConnection.setRequestProperty("Content-Language", "en-US");
//                httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
//		    httpConnection.setConnectTimeout(5000);


                in = httpConnection.getInputStream();
                System.out.println("hello 8");
                int responseCode = httpConnection.getResponseCode();

                System.out.println("hello 9");
                if (responseCode == HttpURLConnection.HTTP_OK) {
                        System.out.println("respone code is ok");
                        StringBuilder sb = new StringBuilder("");
                        // int line = 0;
                        int length = httpConnection.getContentLength();
                        System.out.println(" Length= " + length);

                        int i;
                        do {
                                i = in.read();
                                if (i != -1) {
                                        // System.out.println("Character >>> "+ (char)i);
                                        sb.append((char) i);
                                }
                        } while (i != -1);

                        result = new String(sb);
                        in.close();
                        // result = sb.toString();
                        System.out.println("result is !!!" + result);
                        // return result;
                }
        } catch (Exception e) {

                e.printStackTrace();
                result = "failure";

                System.out.println("Exception e is " + e);
                //timeout = true;

        }

        finally {
                if (in != null) {
                        try {
                                in.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
        System.out.println("Final result....." + result);
        return result;*/
    }


    /*private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException
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
    }*/

    @Override
    protected void onPostExecute(String response) {
        // TODO Auto-generated method stub
//			super.onPostExecute(result);


//        System.out.println("++ result is " + response);




    }
}

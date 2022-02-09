package com.example.bluetoothexample.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.bluetoothexample.model.BTScan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class HttpClient {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    //private static final String HOST = "192.168.0.106";
    private static String HOST = "13.1.1.4";
    private static final String PORT = ":8001";
    private static final String USERLOGIN = "vova";
    private static final String USERPASSWORD = "python";
    private static final Protocol PROTOCOL	= Protocol.HTTP;

    private final JsonParser jsonParser;

    public HttpClient(String host) {
        jsonParser = new JsonParser();
        HOST = host;
    }

    public HttpClient() {
        jsonParser = new JsonParser();
    }

    public static boolean checkInternetConnection(Context context) {
        // Get Connectivity Manager
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Details about the currently active default data network
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(context, "No default network is currently active", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(context, "Network is not connected", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(context, "Network not available", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(context, "Network OK", Toast.LENGTH_LONG).show();
        return true;
    }


    public static void closeQuietly(InputStream in)  {
        try {
            in.close();
        }catch (Exception e) {

        }
    }

    public static void closeQuietly(Reader reader)  {
        try {
            reader.close();
        }catch (Exception e) {

        }
    }

    protected String getRequestUrl(String apiString) {
        if (PROTOCOL == Protocol.HTTPS) {
            //нижеследующая строка нужна для автоматического принимания самоподписанных сертификатов,
            //без проверки их подлинности при работе с HTTPS
            FakeX509TrustManager.allowAllSSL();
            return "https://" + HOST + PORT + apiString;
        } else {
            return "http://" + HOST + PORT + apiString;
        }
    }

    private String readStream(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        stream.close();

        return sb.toString();
    }

    public HashMap<String, Object> sendScanData(List<BTScan> scans, String deviceID) throws IOException, JSONException, ParseException {
        String apiString = "/api/v1.0/btscan/sendresults";

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERLOGIN, USERPASSWORD.toCharArray());
            }
        });

        URL url = new URL(getRequestUrl(apiString));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // add auth header to request
        //String loginInfo	= USERLOGIN+":"+USERPASSWORD;
        //connection.setRequestProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(loginInfo.getBytes()));

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);
        //connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.connect();

        //строки документа
        JSONArray lines = new JSONArray();
        for (BTScan line:
                scans) {
            JSONObject jline = new JSONObject();
            jline.put("barcode", line.getBarcode().trim());
            jline.put("excise", line.getExcise().trim());
            lines.put(jline);
        }

        //наш передаваемый запрос
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("divice", deviceID); //кто отправляет
        jsonParam.put("status", 0); //какой статус необходимо установить
        jsonParam.put("scandata", lines); //отправляемые данные сканирования

        //отправляем
        DataOutputStream localDataOutputStream = new DataOutputStream(connection.getOutputStream());
        localDataOutputStream.write(jsonParam.toString().getBytes("UTF-8"));
        localDataOutputStream.flush();
        localDataOutputStream.close();

        //читаем ответ
        InputStream in;
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            in = connection.getErrorStream();
        } else {
            in = connection.getInputStream();
        }

        String response = readStream(in);

        connection.disconnect();

        return jsonParser.getScanDataSendResponse(response);
    }
}

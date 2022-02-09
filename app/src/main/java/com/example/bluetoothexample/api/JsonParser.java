package com.example.bluetoothexample.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class JsonParser {
    public static Date getDate(String datestring) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.parse(datestring);
    }

    public static String getDateString(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.format(date);
    }

    public HashMap<String, Object> getScanDataSendResponse(String response) throws JSONException, ParseException {
        HashMap<String, Object> answer = new HashMap<>();
        JSONObject responseObj = new JSONObject(response);
        int success = 0;

        //вначале проверим на ошибку
        String error = responseObj.optString("error");
        if (error.isEmpty()) {
            //если нет ошибки
            success = responseObj.getInt("success");
        }
        answer.put("success", success);
        answer.put("error", error);

        return answer;
    }
}

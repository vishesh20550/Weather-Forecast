package com.example.getmausam;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    EditText editText;
    String[] urls;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    Toast toast;

    public void DownloadTask(){
        executor.execute(() -> {
            StringBuilder result= new StringBuilder();
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url= new URL(urls[0]);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data= reader.read();
                while(data!=-1){
                    char current = (char) data;
                    result.append(current);
                    data=reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                try{
                    JSONObject jsonObject= new JSONObject(result.toString());
                    String weatherInfo= jsonObject.getString("weather");
                    JSONArray jsonArray= new JSONArray(weatherInfo);
                    StringBuilder weather= new StringBuilder();
                    StringBuilder temperature=new StringBuilder();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1= jsonArray.getJSONObject(i);
                        String main=jsonObject1.getString("main");
                        String des= jsonObject1.getString("description");
                        if(!main.equals("")&&!des.equals("")){
                            weather.append(main).append(" : ").append(des).append("\r\n");
                        }
                    }
                    String temp_info= jsonObject.getString("main");
                    JSONObject jsonObject2= new JSONObject(temp_info);
                    String temp=jsonObject2.getString("temp");
                    String temp_min= jsonObject2.getString("temp_min");
                    String temp_max= jsonObject2.getString("temp_max");
                    temp=Float.toString(Math.round(Float.parseFloat(temp)-273.15f));
                    temp_max=Float.toString(Math.round(Float.parseFloat(temp_max)-273.15f));
                    temp_min=Float.toString(Math.round(Float.parseFloat(temp_min)-273.15f));
                    temperature.append("Temperature : ").append(temp).append(" °C\n").append("Max Temperature : ").append(temp_max).append(" °C\n").append("Min Temperature : ").append(temp_min).append(" °C");
                    String final_message= weather.toString()+temperature.toString();
                    if(!final_message.equals("")){
                        textView.setText(final_message);
                        textView.setVisibility(View.VISIBLE);
                    }
                    else{
                        toast.show();
                    }

                }catch (Exception e){
                    toast.show();
                    e.printStackTrace();
                }
            });
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    public void search(View view){
        try {
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            urls=new String[]{"https://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid="+"YourAPI"};
            DownloadTask();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"Weather Not Found!!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        textView=findViewById(R.id.textView);
        editText = findViewById(R.id.editTextCityName);
        toast=Toast.makeText(MainActivity.this, "Weather Not Found!!", Toast.LENGTH_SHORT);
    }
}
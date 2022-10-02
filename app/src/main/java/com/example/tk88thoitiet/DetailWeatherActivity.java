package com.example.tk88thoitiet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class DetailWeatherActivity extends AppCompatActivity {
    private String tentp;
    public String getTentp() {
        return tentp;
    }

    public void setTentp(String tentp) {
        this.tentp = tentp;
    }

    DatePicker  datePicker;
    TextView tvNhietDo, tvDoAm, tvGio, tvMay,   tvTrangThai;
    Button btnTiepTheo;
    ImageView imgIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_weather);
        init();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            String ThanhPho =(String) b.get("tentp");
            tentp = ThanhPho;
        }
        getCurrentWeatherData(tentp);
        clickCalendar();
    }
    private void init() {
        datePicker  = findViewById(R.id.date_picker);
        tvDoAm = findViewById(R.id.tvDoAm);
        tvGio = findViewById(R.id.tvGio);
        tvMay = findViewById(R.id.tvMay);
        btnTiepTheo = findViewById(R.id.btnNgayTiepTheo);
        imgIcon = findViewById(R.id.imgThoiTiet);
        tvNhietDo = findViewById(R.id.tvNhietDo);
        tvTrangThai = findViewById(R.id.tvTrangThai);
    }
    private void getCurrentWeatherData(final String city) {
        RequestQueue queue = Volley.newRequestQueue(DetailWeatherActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/find?lang=vi&q=" + city + "&units=metric&appid=05dab36db00d455448c0b83ceaeb67d7";
        StringRequest request = new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Loi", error.toString());
            }
        }
        );
        queue.add(request);
    }
    private void processResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONObject jsonObject = object.getJSONArray("list").getJSONObject(0);

            // Set icon cho imgIcon
            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
            String status = jsonObjectWeather.getString("main");
            String icon = jsonObjectWeather.getString("icon");

            tvTrangThai.setText(status);
            Picasso.with(DetailWeatherActivity.this).load("http://openweathermap.org/img/w/" + icon + ".png").into(imgIcon);

            // Lay gia tri nhiet do, do am , gio...
            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
            String strNhietDo = jsonObjectMain.getString("temp");
            String doAm = jsonObjectMain.getString("humidity");
            Double a = Double.valueOf(strNhietDo);
            String nhietDo = String.valueOf(a.intValue());
            tvNhietDo.setText(nhietDo + "*C");
            tvDoAm.setText(doAm + "%");

            // Lay gia tri gio, may
            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
            String gio = jsonObjectWind.getString("speed");
            tvGio.setText(gio + "m/s");
            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
            String may = jsonObjectCloud.getString("all");
            tvMay.setText(may + "%");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clickCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                getCurrentWeatherData(tentp);
            }
        });
    }

}
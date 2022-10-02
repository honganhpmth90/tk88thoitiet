package com.example.tk88thoitiet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    EditText edtTenThanhPho;
    TextView tvTenTp, tvTenQg, tvNhietDo, tvDoAm, tvGio, tvMay, tvNgayThang, tvTrangThai;
    Button btnChon, btnTiepTheo;
    ImageView imgIcon;
    static final String DEFAUT_CITY = "Ho chi minh";
    public static boolean isNullOrEmpty(String str) {
        return ((str == null || str.isEmpty()) ? true : false);
    }
    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        init();
        getCurrentWeatherData(DEFAUT_CITY);
        clickButton();

    }

    private void init() {
        edtTenThanhPho = findViewById(R.id.edtTenTp);
        tvTenTp = findViewById(R.id.tvThanhPho);
        tvTenQg = findViewById(R.id.tvQuocGia);
        tvDoAm = findViewById(R.id.tvDoAm);
        tvGio = findViewById(R.id.tvGio);
        tvMay = findViewById(R.id.tvMay);
        tvNgayThang = findViewById(R.id.tvNgayThang);
        btnChon = findViewById(R.id.btnThanhPho);
        btnTiepTheo = findViewById(R.id.btnNgayTiepTheo);
        imgIcon = findViewById(R.id.imgThoiTiet);
        tvNhietDo = findViewById(R.id.tvNhietDo);
        tvTrangThai = findViewById(R.id.tvTrangThai);
    }

    private void getCurrentWeatherData(final String city) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
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
            //Set ten thanh pho, quoc gia
            String name = jsonObject.getString("name");
            tvTenTp.setText(name);
            JSONObject jsonObjectCountry = jsonObject.getJSONObject("sys");
            String quocGia = jsonObjectCountry.getString("country");
            tvTenQg.setText(quocGia);

            // Set text cho ngay (Chua co)
            String day = jsonObject.getString("dt");
            long lDay = Long.valueOf(day);
            Date date = new Date(lDay * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
            String strDay = sdf.format(date);
            tvNgayThang.setText(strDay);

            // Set icon cho imgIcon
            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
            String status = jsonObjectWeather.getString("main");
            String icon = jsonObjectWeather.getString("icon");

            tvTrangThai.setText(status);
            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + icon + ".png").into(imgIcon);

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
    private void clickButton() {
        btnChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtTenThanhPho.getText().toString();
                getCurrentWeatherData(city);
            }
        });
        btnTiepTheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtTenThanhPho.getText().toString();
                Intent intent = new Intent(MainActivity.this, DetailWeatherActivity.class);
                if(isNullOrEmpty(city)){
                    city = DEFAUT_CITY;
                }
                intent.putExtra("tentp", city);
                startActivity(intent);
            }
        });
    }

}
package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    Spinner spinner1,spinner2;
    ImageView crypto_image;
    String CrypTotoUse;
    String selectedCurrencyItem;
    TextView exchcangerate,touch_to_convert,date;
AppBarLayout appBarLayout;
ArrayAdapter<String> adapter1;
ArrayAdapter<String> adapter2;
ArrayList<String> FirstList;
ArrayList<String> SecondList;
ArrayList<String> returnedrates;
SharedPreferences preferences;
public static String CHOOSEN_CURRENCY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner1=findViewById(R.id.spinner);
       spinner2=findViewById(R.id.currency);
       crypto_image=findViewById(R.id.crypto_image);
       date=findViewById(R.id.date);
       exchcangerate=findViewById(R.id.exchange_rate);
       touch_to_convert=findViewById(R.id.touch_to_convert);
        appBarLayout=findViewById(R.id.appbar);
     relativeLayout=findViewById(R.id.relativelayout);




        //initialize the arraylists
        FirstList=new ArrayList<>();
        SecondList=new ArrayList<>();
        returnedrates=new ArrayList<>();
        //add items to the first arraylist
        FirstList.add("BTC");
        FirstList.add("ETH");
        FirstList.add("NGN");
        TimeZone lagosTimeZone = TimeZone.getTimeZone("Africa/Lagos");
        Calendar calendar = Calendar.getInstance(lagosTimeZone);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date.setText(df.format(calendar.getTime()));
//initialize preferences
preferences=this.getSharedPreferences(CHOOSEN_CURRENCY,MODE_PRIVATE);
// set onclick to navigate to currencyconv screen
touch_to_convert.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(MainActivity.this, CurrencyConv.class);
        startActivity(intent);
    }
});
//set spinner adapters to populate it
adapter1=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,FirstList);
adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//attach spinner to adapter
spinner1.setAdapter(adapter1);
adapter1.notifyDataSetChanged();

spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CrypTotoUse = parent.getSelectedItem().toString();
        preferences.edit().putString("spinner1", CrypTotoUse).apply();
        FetchCurrencies();
        if (CrypTotoUse.equals("ETH")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                crypto_image.setImageDrawable(getResources().getDrawable(R.drawable.ether, getApplicationContext().getTheme()));
                appBarLayout.setBackgroundResource(R.color.etherColor);
             relativeLayout.setBackgroundResource(R.color.etherColor);
                exchcangerate.setTextColor(getResources().getColor(R.color.etherColor));
                GradientDrawable drawable = (GradientDrawable) exchcangerate.getBackground();
                drawable.setStroke(10, getResources().getColor(R.color.etherColor));}

            } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                crypto_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_bitcoin, getApplicationContext().getTheme()));
                appBarLayout.setBackgroundResource(R.color.bitcoinColor);
                relativeLayout.setBackgroundResource(R.color.bitcoinColor);
                exchcangerate.setTextColor(getResources().getColor(R.color.bitcoinColor));
                GradientDrawable drawable = (GradientDrawable) exchcangerate.getBackground();
                drawable.setStroke(10, getResources().getColor(R.color.bitcoinColor));
            }
        }
        if
                     (CrypTotoUse.equals("NGN")){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            crypto_image.setImageDrawable(getResources().getDrawable(R.drawable.naira,getApplicationContext().getTheme()));
                            appBarLayout.setBackgroundResource(R.color.nairacolor);
                            relativeLayout.setBackgroundResource(R.color.nairacolor);
                            exchcangerate.setTextColor(getResources().getColor(R.color.nairacolor));
                            GradientDrawable drawable=(GradientDrawable) exchcangerate.getBackground();
                            drawable.setStroke(10,getResources().getColor(R.color.nairacolor));
                        }
                    }
                }





    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});



adapter2=new ArrayAdapter<String>(this,R.layout.spinner_item,SecondList);
adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCurrencyItem = parent.getSelectedItem().toString();
        if (selectedCurrencyItem == SecondList.get(position)) {
            preferences.edit().putString("selectedCurrencyItem",selectedCurrencyItem).apply();
            preferences.edit().putString("selectedCurrencyValue",returnedrates.get(position)).apply();
            //  Toast.makeText(getApplicationContext(),"Selected Item is "+ReturnedCurrencies.get(position),Toast.LENGTH_LONG).show();
            exchcangerate.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
            exchcangerate.setText(returnedrates.get(position));
        }
    }


    public void onNothingSelected(AdapterView<?> parent) {

    }
});



    }




    void FetchCurrencies(){
        //Use Timestamp to get Latest Exchange Rates
        TimeZone lagosTimeZone = TimeZone.getTimeZone("Africa/Lagos");
        Calendar calendar = Calendar.getInstance(lagosTimeZone);
        Long dateMilli = calendar.getTimeInMillis() / 1000;
        final String Timestamp = dateMilli.toString();
        String FirstUrlPart = "https://min-api.cryptocompare.com/data/price?fsym=" + CrypTotoUse + "&tsyms=";
        String Currencies = "USD,EUR,NGN,JPY,GBP,AUD,CAD,CHF,HKD,INR,KRW,SEK,RUB,BRL,DKK,PLN,ZAR,MYR,THB,NZD&" + Timestamp;

        String url = FirstUrlPart + Currencies;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
               SecondList.clear();
                returnedrates.clear();
                try {
                    j = new JSONObject(response);
                    Map<String, String> map = new HashMap<>();
                    Iterator iter = j.keys();
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        String value = j.getString(key);
                        map.put(key, value);
                        SecondList.add(key);
                       returnedrates.add(value);
                        Log.d("KEY", key);
                        Log.d("VALUE", value);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Attach Spinners to adapter
                spinner2.setAdapter(adapter2);
                adapter2.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                   exchcangerate.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                   exchcangerate.setText("No Connection...");
                }
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}






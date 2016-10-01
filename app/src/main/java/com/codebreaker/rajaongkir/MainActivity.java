package com.codebreaker.rajaongkir;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String BASE_URL = "http://api.rajaongkir.com/starter/cost";
    final String API_KEY = "89e21c58e447b2c15c12ea99356fc229";
    EditText etBerat;
    TextView tvHarga;

    AutoCompleteTextView etAsal, etTujuan;
    Button btnCheck;
    RadioGroup radioGroup;
    String idAsal = "", idTujuan = "", namaAsal = "";
    ArrayList<String> arrKab, arrId;
    ArrayList<Alamat> alamatArrayList;
    ProgressBar progressBar;
    ListView listView;
    ArrayList<Ongkir> ongkirs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        etAsal = (AutoCompleteTextView) findViewById(R.id.main_origin);
        etBerat = (EditText) findViewById(R.id.main_weight);
        etTujuan = (AutoCompleteTextView) findViewById(R.id.main_destination);
        btnCheck = (Button) findViewById(R.id.main_check);
        radioGroup = (RadioGroup) findViewById(R.id.main_rg);
        tvHarga = (TextView) findViewById(R.id.main_harga);
        listView = (ListView) findViewById(R.id.main_lv);

        ongkirs = new ArrayList<>();

        DBHelper helper = new DBHelper(this);
        try {
            helper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        arrKab = new ArrayList<>();
        arrId = new ArrayList<>();

        if (arrId.size() <= 0) {
            alamatArrayList = helper.getAlamat();
            setAdapter();
        }

        etAsal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dipilih = adapterView.getItemAtPosition(i).toString();
                //int posisi = Arrays.asList(arrKab).indexOf(dipilih);
                int posisi = arrKab.indexOf(dipilih);
                idAsal = arrId.get(Integer.valueOf(posisi));
                //namaAsal = arrKab.get(Integer.valueOf(posisi));
            }
        });
        etTujuan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dipilih = adapterView.getItemAtPosition(i).toString();
                //int posisi = Arrays.asList(arrKab).indexOf(dipilih);
                int posisi = arrKab.indexOf(dipilih);
                idTujuan = arrId.get(Integer.valueOf(posisi));
                //namaAsal = arrKab.get(Integer.valueOf(posisi));
            }
        });

        //ongkir jne , tiki , pos
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sBerat = etBerat.getText().toString().trim();
                String sAsal = etAsal.getText().toString().trim();
                String sTujuan = etTujuan.getText().toString().trim();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton rbKurir = (RadioButton) findViewById(selectedId);
                String sKurir = rbKurir.getText().toString().trim();

                if (TextUtils.isEmpty(sBerat) || TextUtils.isEmpty(sAsal) || TextUtils.isEmpty(sTujuan)) {
                    Toast.makeText(MainActivity.this, "Masih kosong", Toast.LENGTH_SHORT).show();
                } else {
                    if (Integer.valueOf(sBerat) <= 0) {
                        Toast.makeText(MainActivity.this, "Berat harus di atas 0", Toast.LENGTH_SHORT).show();
                    } else {
                        //ceOngkir(idAsal, idTujuan, sBerat, sKurir);
                        ceOngkir(idAsal, idTujuan, sBerat, "jne");
                    }

                }


            }
        });
    }

    private void ceOngkir(String idAsal, String idTujuan, String berat, String sKurir) {
        Log.e("TAG", "idAsal : " + idAsal + "\nidTujuan : " + idTujuan + "\nBerat : " + berat + "\nKurir : " + sKurir);
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("key", API_KEY);
        params.add("origin", idAsal);
        params.add("destination", idTujuan);
        params.add("weight", berat);
        params.add("courier", sKurir);
        //params.add("android-key", "com.codebreaker.rajaongkir");
        client.post(BASE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);

                if (ongkirs.size() > 0)
                    ongkirs.clear();

                try {
                    JSONObject objRaja = response.getJSONObject("rajaongkir");
                    //JSONObject objQuery = objRaja.getJSONObject("query");
                    //String key = objQuery.getString("key");
                    //Toast.makeText(MainActivity.this, "key : " + key, Toast.LENGTH_SHORT).show();
                    JSONArray arrResults = objRaja.getJSONArray("results");
                    JSONObject objResults = arrResults.getJSONObject(0);//cuma ada satu
                    //Toast.makeText(MainActivity.this, "Kurir : " + objResults.getString("name"), Toast.LENGTH_SHORT).show();  //bener
                    JSONArray arrCosts = objResults.getJSONArray("costs");
                    for (int i = 0; i < arrCosts.length(); i++) {
                        JSONObject objCosts = arrCosts.getJSONObject(i);
                        String nama = objCosts.getString("service");
                        String desk = objCosts.getString("description");
                        String harga = null, waktu = null;

                        if (!desk.contains("Trucking")) {
                            JSONArray arrCost2 = objCosts.getJSONArray("cost");
                            for (int j = 0; j < arrCost2.length(); j++) {
                                JSONObject objCost2 = arrCost2.getJSONObject(j);
                                harga = objCost2.getString("value");
                                waktu = objCost2.getString("etd");
                            }

                            Ongkir o = new Ongkir();
                            o.setDesc(desk);
                            o.setHarga(harga);
                            o.setWaktu(waktu);
                            o.setNama(nama);
                            ongkirs.add(o);
                        }
                    }

                    AdapterList adapter = new AdapterList(MainActivity.this, R.layout.adapter_list, ongkirs);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.GONE);
                throwable.printStackTrace();
                Toast.makeText(MainActivity.this, "Cek koneksi internet!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setAdapter() {
        for (Alamat alamat : alamatArrayList) {
            arrKab.add(alamat.getKabupaten());
            arrId.add(alamat.getId());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrKab);
        etAsal.setAdapter(adapter);
        etTujuan.setAdapter(adapter);

    }

}

package com.developer.tanay.nertia;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.oHome.Home;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfDatePick extends AppCompatActivity implements View.OnClickListener {

    EditText sdate, edate;
    Calendar myCalendar, mycalendar2;
    Button button;
    JSONArray array;
    String sd, ed, tp;
    ProgressDialog dialog;
    Spinner spinner;
    List<String> types;
    private static final String currentBranchId = "current_branch_id";
    private static final String url = Nertiapp.server_url+"/getpdf/";
    private static final String surl = Nertiapp.server_url+"/ospdf/";
    private static final String nurl = Nertiapp.server_url+"/getunames/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_date_pick);
        sdate = findViewById(R.id.startdate);
        edate = findViewById(R.id.enddate);
        button = findViewById(R.id.export_btn);
        spinner = findViewById(R.id.type_pick);

        types = new ArrayList<>();
        types.add("Branch");
        get_unames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        myCalendar = Calendar.getInstance();
        mycalendar2 = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }
        };
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PdfDatePick.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PdfDatePick.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        button.setOnClickListener(this);

    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        sdate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel2() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        edate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.export_btn:
                sd = sdate.getText().toString().trim();
                ed = edate.getText().toString().trim();
                tp = spinner.getSelectedItem().toString();
                ActivityCompat.requestPermissions(PdfDatePick.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                //get_pdf(sd, ed);
                break;
        }
    }

    private void get_unames(){
        StringRequest request = new StringRequest(Request.Method.POST, nurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setNames(response.replaceAll("\"", ""));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PdfDatePick.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bid", getBid());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void setNames(String response){
        try {
            //JSONObject object = new JSONObject(response);
            //String[] names = (String[]) object.get("names");
            String[] names = response.split("/");
            types.addAll(Arrays.asList(names));
            for (int i=0;i<types.size();i++){
                Log.d("type", types.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get_pdf(final String sd, final String ed){
        //Log.d("In get pdf", "hereeeeeeeeeeeeeeeee");
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(PdfDatePick.this, "Downloading "+response, Toast.LENGTH_SHORT).show();
                //Log.d("response", response);
                dialog.setMessage("Saving...");
                //Log.d("response", response);
                makePdf(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toast.makeText(PdfDatePick.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("sdate", sd);
                params.put("edate", ed);
                params.put("bid", getBid());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void get_spdf(final String sd, final String ed){
        StringRequest request = new StringRequest(Request.Method.POST, surl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.setMessage("Saving...");
                Log.d("In response",response);
                makePdf(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PdfDatePick.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("sdate", sd);
                params.put("edate", ed);
                params.put("bid", getBid());
                params.put("uname", tp);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private String getBid(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(currentBranchId, "");
    }

    private void makePdf(String data){
        Log.d("In make pdf", "hereeeeeeeeeee");
        dialog.setMessage("Saving in /DeviceStorage/Download/SalonData/");
        String fpath="";
        String fname="";
        try {
            array = new JSONArray(data);
            Log.d("make pdf", "1111111111");
            Document document = new Document(PageSize.A4);
            Log.d("make pdf", "22222222222");

            File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "SalonData");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("PDF DIR", "Pdf Directory created");
            }
            fname = tp+"__"+getBid()+"__"+sdate.getText().toString()+"_to_"+edate.getText().toString()+".pdf";
            fpath = pdfFolder+"/"+fname;
            Log.d("F path", fpath);
            File file = new File(fpath);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            PdfPTable table = new PdfPTable(5);
            table.setPaddingTop(4);

            PdfPCell c1 = new PdfPCell(new Phrase("Service Person"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Service"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Cost"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Date"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Time"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            table.setHeaderRows(1);

            for (int i=array.length()-1;i>=0;i--){
                JSONObject object = (JSONObject) array.get(i);
                Log.d("FULL NAME" , object.getString("get_fname"));
                table.addCell(object.getString("get_fname").toUpperCase());
                table.addCell(object.getString("service_name").toUpperCase());
                table.addCell(object.getString("get_str_cost"));
                table.addCell(object.getString("get_str_date"));
                table.addCell(object.getString("get_str_time"));
            }

            Anchor anchor = new Anchor("Salon Data from "+ sdate.getText().toString()+" to "+edate.getText().toString());
            anchor.setName("Salon Data from"+ sdate.getText().toString()+" to "+edate.getText().toString());

            //Second parameter is the number of the chapter
            Chapter catPart = new Chapter(new Paragraph(anchor), 1);

            Paragraph subPara = new Paragraph("Salon Data from "+ sdate.getText().toString()+" to "+edate.getText().toString());
            Section subCatPart = catPart.addSection(subPara);

            subCatPart.add(table);
            document.add(subCatPart);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            dialog.cancel();
            Toast.makeText(PdfDatePick.this, "PDF file saved in /DeviceStorage/Download/SalonData as "+fname, Toast.LENGTH_LONG).show();
        }

    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PdfDatePick.this, Home.class);
        startActivity(intent);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
                    dialog.setTitle("Getting PDF");
                    dialog.setMessage("Downloading...");
                    dialog.show();
                    if (tp.equals("Branch")){
                        get_pdf(sd, ed);
                    }else {
                        get_spdf(sd, ed);
                    }
                } else {
                    Toast.makeText(PdfDatePick.this, "Permission denied to write to your External storage. PDF cannot be saved.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}

package com.example.lucky.fileupload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btnLand;
    private EditText etUserName, etUserPassword;
    private TextView tvShow;
    private Listen listen = new Listen();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find();
        binding();
    }

    private void find() {
        btnLand = (Button) findViewById(R.id.btnLand);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etUserPassword = (EditText) findViewById(R.id.etUserPassword);
        tvShow = (TextView) findViewById(R.id.tvShow);
    }

    private void binding() {
        btnLand.setOnClickListener(listen);
    }

    private void postMethod(Map<String, String> params) {
        StringBuilder sbf = new StringBuilder();
        for (Map.Entry entry : params.entrySet()) {
            sbf.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        sbf.delete(sbf.length() - 1, sbf.length());
        try {
            URL url = new URL("");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);  //连接超时时间
            conn.setReadTimeout(5000);   //读取超时时间
            conn.setDoInput(true);    //设置可以读取
            conn.setDoOutput(true);   //设置可以上传

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(sbf.toString().getBytes());
            dos.flush();  //立刻刷新过去
            dos.close();   //关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getMethod(Map<String, String> params) {
        String PATH = "";
        StringBuilder sbf = new StringBuilder(PATH);
        if (!params.isEmpty()) {
            sbf.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sbf.append(entry.getKey() + "=" + entry.getValue() + "&");
            }
            sbf.delete(sbf.length() - 1, sbf.length());
        }
        Log.w("TEST", sbf.toString());
        try {
            URL url = new URL(PATH);
            HttpURLConnection ccon = (HttpURLConnection) url.openConnection();
            ccon.setDoInput(true);  //可以读取
            ccon.setDoOutput(true);  //可以传输
            ccon.setConnectTimeout(5000);  //连接超时时间
            ccon.setReadTimeout(5000);   //读取超时时间
            ccon.setDefaultUseCaches(false);
            ccon.setRequestMethod("GET");  //连接方式
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upLoadImageFile() {
        String requestUrl = "";  //地址
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识
        String PREFIX = "--";  //分隔符
        String LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";
        File file = new File("");
        try {
            URL url = new URL(requestUrl); //连接地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(500);
    //准备协议头
    connection.setRequestMethod("POST"); //设置连接方式
    connection.setRequestProperty("Charset","UTF-8");  //设置编码方式
    connection.setRequestProperty("connection","keep-alive");  //设置保持连接
    connection.setRequestProperty("Content",CONTENT_TYPE+";boundary"+BOUNDARY);

    //写入文件
    if(file!=null){
        DataOutputStream dos=new DataOutputStream(connection.getOutputStream());
        StringBuilder sbf=new StringBuilder();
        sbf.append(PREFIX);
        sbf.append(BOUNDARY);
        sbf.append(LINE_END);
        sbf.append("Content-Disposition:form-data;name=\"fileName\";filename=\""+file.getName()+"\""+LINE_END);
        sbf.append("Content-Type:application/octet-stream;charset="+"UTF-8"+LINE_END+LINE_END);
        dos.write(sbf.toString().getBytes());  //写入前几行
        InputStream is=new FileInputStream(file);
        byte[]bytes=new byte[1024];
        int len=0;
        while((len=is.read(bytes))!=-1){
            dos.write(bytes,0,len);  //边读边写
        }
        is.close();
        dos.write(LINE_END.getBytes());
        byte[]end_data=(PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
    dos.write(end_data);
        dos.flush();
    }

}catch (Exception e){



}




    }
    private class Listen implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLand:
                   final Map<String, String> params = new HashMap<>();
                    params.put("username", etUserName.getText().toString());
                    params.put("password", etUserPassword.getText().toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getMethod(params);
                        }
                    }).start();
                    break;
            }
        }
    }
}

package avalidations.jgsu.com.avalidations.com.jgsu.avalidations;


import android.os.AsyncTask;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpData extends AsyncTask<String,Void,String>{
    /*
    以下是过时的用法
    private HttpClient mHttpClient;
    private HttpGet mHttpGet;
    private HttpResponse mHttpResponse;
    private HttpEntity mHttpEntity;*/
    private OkHttpClient mOkHttpClient;

    private InputStream in;
    private HttpGetdataListener listener;
    private String url;
    public HttpData(String url,HttpGetdataListener listener){
        this.url=url;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {/*
            mHttpClient = new DefaultHttpClient();
            mHttpGet = new HttpGet(url);
            mHttpResponse=mHttpClient.execute(mHttpGet);
            mHttpEntity=mHttpResponse.getEntity();
            in=mHttpEntity.getContent();*/
            mOkHttpClient=new OkHttpClient();
            //创建一个Request代替HttpGet
            final Request request=new Request.Builder().url(url).build();
            Call call = mOkHttpClient.newCall(request);
            Response response =call.execute();
            in= response.body().byteStream();
            //获取InputStream对象 并读取。
            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer sb=new StringBuffer();
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();
        }catch (Exception e){

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        listener.getDataUrl(s);
        super.onPostExecute(s);
    }
}

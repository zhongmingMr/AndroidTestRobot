package avalidations.jgsu.com.avalidations.com.jgsu.avalidations;


/*import android.content.Intent;
import android.content.pm.PackageManager;*/
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
//import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import avalidations.jgsu.com.avalidations.R;

public class MainActivity extends AppCompatActivity implements HttpGetdataListener, View.OnClickListener, ThemeManager.OnThemeChangeListener {
    private android.support.v7.app.ActionBar supportActionBar;
    private HttpData httpData;
    private List<ListData> lists;
    private ListView lv;
    private EditText sendText;
    private Button sendButton;
    //    private Button voiceButton;
    private String content_str;
    private TextAdapter adapter;
    private String[] welcome_array;
    ListData listData;

//    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private double currentTime = 0, oldTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeManager.registerThemeChangeListener(this);
        supportActionBar =getSupportActionBar();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //日间/夜间切换
            case R.id.change_item:
                ThemeManager.setThemeMode(ThemeManager.getThemeMode()== ThemeManager.ThemeMode.DAY
                ? ThemeManager.ThemeMode.NIGHT: ThemeManager.ThemeMode.DAY);
                break;
            case R.id.baidu_map:
                //切换到地图
                Intent intent=new Intent(MainActivity.this,BaiduLMap.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void initTheme(){
        lv.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this,R.color.backgroundColor)));
        //设置标题栏的颜色
        if (supportActionBar!=null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(ThemeManager.getCurrentThemeRes(MainActivity.this,R.color.colorPrimary))));
        }
        //设置状态栏
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes
                    (MainActivity.this,R.color.colorPrimary)));
        }
    }

    public void onThemeChanged(){
        initTheme();
    }
    public void onDestroy(){
        super.onDestroy();
        //退出销毁掉
        ThemeManager.unregisterThemeChangeListener(this);
    }
    private void initView(){
        lv= (ListView) findViewById(R.id.lv);
        sendText= (EditText) findViewById(R.id.sendText);
        sendButton= (Button) findViewById(R.id.send_btn);
//      voiceButton= (Button) findViewById(R.id.voice_btn);
        lists=new ArrayList<>();
       /* PackageManager pm = getPackageManager();
        List activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            voiceButton.setOnClickListener(this);
        } else {
            voiceButton.setEnabled(false);
            //没有找到语音设备
            voiceButton.setText("Recognizer not present");
        }*/
        //设置发送按钮，并编辑点击事件
        sendButton.setOnClickListener(this);
        //适配器 Text
        adapter=new TextAdapter(lists,this);
        //将ListView 添加到适配器
        lv.setAdapter(adapter);
        listData=new ListData(getRandomWelcomeTips(),ListData.RECEIVER,getTime());
        lists.add(listData);
    }

    private String getRandomWelcomeTips(){
        //机器人随机欢迎语
        String welcome_tip;
        welcome_array=this.getResources().getStringArray(R.array.welcome_tips);
        int index= (int) (Math.random()*(welcome_array.length-1));
        welcome_tip=welcome_array[index];
        return welcome_tip;

    }
    @Override
    public void getDataUrl(String data) {
        parseText(data);
    }
    public void parseText(String str){
        try {
//            解析返回数据
            JSONObject jb=new JSONObject(str);
            listData=new ListData(jb.getString("text"),ListData.RECEIVER,getTime());
            lists.add(listData);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId()==R.id.voice_btn){
            startVoiceRecognitionActivity();
        }*/
        getTime();
//        获取时间
        content_str=sendText.getText().toString();
        sendText.setText("");
        String k=content_str.replace(" ","");
        String h=k.replace("\n","");
//        上面 空格和换行导致应用崩溃的解决方法
        listData=new ListData(content_str,ListData.SEND,getTime());
        lists.add(listData);
        if (lists.size()>30){
            for (int i = 0;i<lists.size();i++){
                lists.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
        httpData= (HttpData) new HttpData
                ("http://www.tuling123.com/openapi/api?key=1923ce118d5141d19a17e3923fc8efc5&info="+h,
                this).execute();
    }
   /* private void startVoiceRecognitionActivity(){
        //语音识别功能
        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //启动一种语言识别模式
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //提示语音识别开始
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取从google云端返回的语音识别数据
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
                // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            lv.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                    matches));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/

    //获取时间
    private String getTime(){
        currentTime=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curData=new Date();
        String str=format.format(curData);
        if (currentTime-oldTime>=3*60*1000){
            oldTime=currentTime;
            return str;
        }else{
            return "";
        }
    }
}

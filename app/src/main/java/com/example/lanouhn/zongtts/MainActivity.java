package com.example.lanouhn.zongtts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /*********************** 文字转语音 ************************/
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private TextToSpeech mTts;
    public Button btn_bbwzyy;
    public EditText edt_bbwzyy;

    public Toast toast;
    public Dialog mDialog;

    public AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialog = new Dialog(this);
        toast = new Toast(this);
        btn_bbwzyy = (Button) findViewById(R.id.btn_bbwzyy);
        edt_bbwzyy = (EditText) findViewById(R.id.edt_bbwzyy);
		/* ********************** 文字语音播报 ************* */
        // 检查TTS数据是否已经安装并且可用
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);

        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTts.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        mTts.speak("sorry", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(MainActivity.this, "暂时不支持的语言",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btn_bbwzyy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mTts.speak(edt_bbwzyy.getText().toString(),
                        TextToSpeech.QUEUE_ADD, null);
            }
        });



        /*********************获取最大音量和最小音量****************************/
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int currentVolue = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

        Log.e("zdj","系统最大音量："+maxVolue);
        Log.e("zdj","系统此时音量："+currentVolue);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == REQ_TTS_STATUS_CHECK) {
                switch (resultCode) {
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                        // 这个返回结果表明TTS Engine可以用
                        mTts = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
                        break;
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:// 需要的语音数据已损坏
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:// 缺少需要语言的语音数据
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:// 缺少需要语言的发音数据
                        toast.setText("语音数据不可用");
                        break;
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:// 检查失败
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.shutdown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTts != null)
            mTts.stop();
    }

}

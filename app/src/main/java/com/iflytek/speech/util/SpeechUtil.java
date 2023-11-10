package com.iflytek.speech.util;

import android.content.Context;
import android.content.Intent;
import com.iflytek.speech.service.SpeechServcie;


public class SpeechUtil {
    /*用于开启播放音乐*/
    public static void startSpeech(Context context, int command,String content,String time_on) {
        Intent intent = null;

        intent = new Intent(context, SpeechServcie.class);

        intent.putExtra("command", command);
        intent.putExtra("content", content);
        intent.putExtra("time_on", time_on);
        context.startService(intent);
    }

    /*用于音乐停止播放*/
    public static void stopSpeech(Context context) {
        Intent intent = null;

        intent = new Intent(context, SpeechServcie.class);

        context.stopService(intent);
    }
}

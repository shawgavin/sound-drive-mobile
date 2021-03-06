package org.sounddrive.sounddrivemobile.service.speech;


import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class GoogleSpeechService implements ISpeechService {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
    private Activity activity;
    private Timer timer = new Timer();
    private IVoiceRecogniseHandler recogniseHandler;

    public GoogleSpeechService(Activity activity) {
        Speech.init(activity);
        this.activity = activity;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        //Speech.getInstance().setPreferOffline(true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!Speech.getInstance().isListening())
                    speakNow();
            }
        }, 0, 200);
    }

    public IVoiceRecogniseHandler getRecogniseHandler() {

        return recogniseHandler;
    }

    public void setRecogniseHandler(IVoiceRecogniseHandler recogniseHandler) {

        this.recogniseHandler = recogniseHandler;
    }

    public void toast(final String text) {
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (!activity.isFinishing()) {
//                        Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
    }

    private void speakNow() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    // you must have android.permission.RECORD_AUDIO granted at this point
                    Speech.getInstance().startListening(new SpeechDelegate() {
                        @Override
                        public void onStartOfSpeech() {

                        }

                        @Override
                        public void onSpeechRmsChanged(float value) {

                        }

                        @Override
                        public void onSpeechPartialResults(List<String> results) {

                        }

                        @Override
                        public void onSpeechResult(String result) {
                            toast(result);
                            getRecogniseHandler().onRecognise(result);
                        }
                    });
                } catch (SpeechRecognitionNotAvailable exc) {
                    Log.e("speech", "Speech recognition is not available on this device!");
                } catch (GoogleVoiceTypingDisabledException exc) {
                    Log.e("speech", "Google voice typing must be enabled!");
                } catch (IllegalStateException exc) {
                    Log.e("speech", exc.getMessage(), exc);
                }
            }
        });
    }

    public void onDestroy() {
        timer.cancel();
        Speech.getInstance().shutdown();

    }
}

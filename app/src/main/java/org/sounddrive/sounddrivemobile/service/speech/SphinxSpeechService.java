package org.sounddrive.sounddrivemobile.service.speech;


import android.Manifest;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class SphinxSpeechService implements RecognitionListener, ISpeechService {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
    private static final String COMMANDS_SEARCH = "commands";
    private SpeechRecognizer recognizer;
    private Activity activity;
    private IVoiceRecogniseHandler recogniseHandler;

    public SphinxSpeechService(Activity activity) {
        this.activity = activity;
        checkPermission();
        runRecognizerSetup();
    }

    public void toast(final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();
        recognizer.addListener(this);

        File digitsGrammar = new File(assetsDir, "commands.gram");
        recognizer.addGrammarSearch(COMMANDS_SEARCH, digitsGrammar);
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(activity);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    toast("Failed to init recognizer " + result);
                } else {
                    startSearch();
                }
            }
        }.execute();
    }

    private void startSearch() {
        recognizer.stop();
        recognizer.startListening(COMMANDS_SEARCH);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    public IVoiceRecogniseHandler getRecogniseHandler() {

        return recogniseHandler;
    }

    public void setRecogniseHandler(IVoiceRecogniseHandler recogniseHandler) {

        this.recogniseHandler = recogniseHandler;
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            getRecogniseHandler().onRecognise(text);
            startSearch();
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            getRecogniseHandler().onRecognise(text);
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }

    public void onDestroy() {

    }
}

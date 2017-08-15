package org.sounddrive.sounddrivemobile;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import net.gotev.speech.Speech;

public class Initializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Speech.init(this);
    }
}

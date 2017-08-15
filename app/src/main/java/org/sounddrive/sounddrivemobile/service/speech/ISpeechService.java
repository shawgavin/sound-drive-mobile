package org.sounddrive.sounddrivemobile.service.speech;


public interface ISpeechService {
    void setRecogniseHandler(IVoiceRecogniseHandler recogniseHandler);

    void onDestroy();
}

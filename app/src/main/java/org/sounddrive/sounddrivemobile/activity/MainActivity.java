package org.sounddrive.sounddrivemobile.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.sounddrive.sounddrivemobile.R;
import org.sounddrive.sounddrivemobile.model.BrainBoxLine;
import org.sounddrive.sounddrivemobile.model.DriveCommand;
import org.sounddrive.sounddrivemobile.service.bluetooth.BluetoothService;
import org.sounddrive.sounddrivemobile.service.bluetooth.IBluetoothService;
import org.sounddrive.sounddrivemobile.service.bluetooth.IDataReceiveHandler;
import org.sounddrive.sounddrivemobile.service.speech.GoogleSpeechService;
import org.sounddrive.sounddrivemobile.service.speech.ISpeechService;
import org.sounddrive.sounddrivemobile.service.speech.IVoiceRecogniseHandler;
import org.sounddrive.sounddrivemobile.view.JoystickView;

public class MainActivity extends AppCompatActivity {
    ISpeechService speechService;
    IBluetoothService bluetoothService;

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothService.connect();
        bluetoothService.sendData((byte)(int)DriveCommand.GetData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        speechService = new GoogleSpeechService(this);
        speechService.setRecogniseHandler(new IVoiceRecogniseHandler() {
            @Override
            public void onRecognise(String result) {
                Integer command = DriveCommand.interpret(result);

                if (command == null)
                    return;

                bluetoothService.sendData((byte) (int) command);
            }
        });
        bluetoothService = new BluetoothService(this);
        bluetoothService.setDataReceiveHandler(new IDataReceiveHandler() {
            @Override
            public void onLineReceived(String line) {
                updateJoystickFromLine(line);
            }
        });

    }

    private void updateJoystickFromLine(String line) {
//        if ((command >= DriveCommand.One) && (command <= DriveCommand.Twelve)) {
//            direction = (float) (command - DriveCommand.One + 1) / (float) (DriveCommand.Twelve - DriveCommand.One + 1);
//        }
//
//        if (command.equals(DriveCommand.Quick)) {
//            speed = 1;
//        }
//        if (command.equals(DriveCommand.Slow)) {
//            speed = (float) 0.4;
//        }
//        if (command.equals(DriveCommand.Stop)) {
//            speed = 0;
//        }

        BrainBoxLine brainBoxLine = BrainBoxLine.fromLine(line);
        if (brainBoxLine != null) {
            JoystickView joystickView = (JoystickView) findViewById(R.id.main_joystick);
            joystickView.setJoystickX((brainBoxLine.getxPos() - brainBoxLine.getxCenter()) / brainBoxLine.getxMax());
            joystickView.setJoystickY((brainBoxLine.getyPos() - brainBoxLine.getyCenter()) / brainBoxLine.getyMax());
        }
    }

    @Override
    protected void onDestroy() {
        speechService.onDestroy();
        bluetoothService.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

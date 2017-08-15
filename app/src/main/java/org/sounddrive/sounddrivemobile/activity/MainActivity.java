package org.sounddrive.sounddrivemobile.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.sounddrive.sounddrivemobile.R;
import org.sounddrive.sounddrivemobile.model.DriveCommand;
import org.sounddrive.sounddrivemobile.service.bluetooth.BluetoothService;
import org.sounddrive.sounddrivemobile.service.speech.RecogniseHandler;
import org.sounddrive.sounddrivemobile.service.speech.SpeechService;
import org.sounddrive.sounddrivemobile.view.JoystickView;

public class MainActivity extends AppCompatActivity {
    SpeechService speechService;
    BluetoothService bluetoothService;
    private float direction;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bluetoothService = new BluetoothService(this);
        bluetoothService.connect();

        speechService = new SpeechService(this);
        speechService.setRecogniseHandler(new RecogniseHandler() {
            @Override
            public void onRecognise(String result) {
                Integer command = DriveCommand.interpret(result);

                if (command == null)
                    return;

                if ((command >= DriveCommand.One) && (command <= DriveCommand.Twelve)) {
                    direction = (float) (command - DriveCommand.One + 1) / (float) (DriveCommand.Twelve - DriveCommand.One + 1);
                }

                if (command.equals(DriveCommand.Quick)) {
                    speed = 1;
                }
                if (command.equals(DriveCommand.Slow)) {
                    speed = (float) 0.4;
                }
                if (command.equals(DriveCommand.Stop)) {
                    speed = 0;
                }

                JoystickView joystickView = (JoystickView) findViewById(R.id.main_joystick);
                joystickView.setJoystickX((float) (speed * Math.sin(direction * 2 * Math.PI)));
                joystickView.setJoystickY((float) (-1 * speed * Math.cos(direction * 2 * Math.PI)));

                bluetoothService.sendData(command.toString());
            }
        });
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

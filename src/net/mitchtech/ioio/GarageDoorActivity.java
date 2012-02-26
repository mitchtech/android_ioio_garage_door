package net.mitchtech.ioio;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;

import java.util.Timer;
import java.util.TimerTask;

import net.mitchtech.ioio.garagedoor.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GarageDoorActivity extends AbstractIOIOActivity {

	private static final int DOOR_PIN = 34;
	private static final int PULSE_PERIOD = 200;

	private Button mDoorButton;
	private boolean mDoorState = true;
	private Timer mTimer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mDoorButton = (Button) findViewById(R.id.btn1);
		mDoorButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pulsePin(DOOR_PIN);
			}
		});
	}

	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private DigitalOutput mDoorPin;

		@Override
		protected void setup() throws ConnectionLostException {
			mDoorPin = ioio_.openDigitalOutput(DOOR_PIN, true);
		}

		@Override
		protected void loop() throws ConnectionLostException {
			mDoorPin.write(mDoorState);
			try {
				sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}

	private void turnPinOn(int pin) {
		if (pin == DOOR_PIN) {
			mDoorState = false;
		}
	}

	private void turnPinOff(int pin) {
		if (pin == DOOR_PIN) {
			mDoorState = true;
		}
	}

	private void pulsePin(int pin) {
		turnPinOn(pin);
		mTimer = new Timer();
		mTimer.schedule(new PinOffTask(pin), PULSE_PERIOD);
	}

	private class PinOffTask extends TimerTask {
		int pin;

		public PinOffTask(int pin) {
			super();
			this.pin = pin;
		}

		public void run() {
			turnPinOff(pin);
		}
	}
}
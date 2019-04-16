package com.znt.rtc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private int boot = 0;
	private int down = 0;
	private EditText editText1;
	private EditText editText2;
	//private static final String WAKE_PATH = "/sys/devices/meson-vfd.48/led";
	private static final String WAKE_PATH = "/sys/class/rtc-class-8563/demod_reset_pin";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText1 =(EditText)findViewById(R.id.boot);
		editText2 =(EditText)findViewById(R.id.down);
		Log.d("MainActivity","boot=>"+boot+",down=>>"+down);
		Button button1 = (Button) findViewById(R.id.boot_button);
		Button button2 = (Button) findViewById(R.id.down_button);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);

        doRtcByIntent();

	}

	private void doRtcByIntent()
    {
        String source = getIntent().getStringExtra("ZNT_RTC_ACTION");
		down = getIntent().getIntExtra("ZNT_RTC_TIME",0);
		boot = down;
        //boolean isFinish = getIntent().getBooleanExtra("ZNT_RTC_FINISH", false);
        if(source != null)
        {
            if(source.equals("ZNT_RTC_ACTION_BOOT"))//定时重启的动作
            {
                if (boot >= 3){
                    DtvControl mControl = new DtvControl(WAKE_PATH);
                    mControl.setValueForce(String.valueOf(boot));
                    Log.d("MainActivity","写成功=>boot=>"+boot);
                    shutDown();
                }else{
                    Toast.makeText(MainActivity.this, "设定的时间必须大于3", Toast.LENGTH_LONG).show();}
            }
            else if(source.equals("ZNT_RTC_ACTION_DOWN"))//定时关机的动作
            {
                boot = boot *1000*60;
                timer.schedule(task,boot );
                moveTaskToBack(false);
            }
        }
    }

	private void shutDown(){
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}
	Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
			down=Integer.parseInt(editText2.getText().toString());
			Log.d("MainActivity", "down2="+down);
            shutDown();
        }
    };

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.boot_button:
			boot=Integer.parseInt(editText1.getText().toString());
			Log.d("MainActivity", "boot><><><>"+boot);
			if (boot >= 3){
		        /*try {
		            BufferedWriter bufWriter = null;
		            bufWriter = new BufferedWriter(new FileWriter(WAKE_PATH));
		            bufWriter.write(boot);  // 写操作
		            bufWriter.close();
		            Log.d("MainActivity","写成功");
		        } catch (IOException e) {
		            e.printStackTrace();
		            Log.e("MainActivity","can't write the" + WAKE_PATH);
		        }
		        */
				DtvControl mControl = new DtvControl(WAKE_PATH);
				mControl.setValueForce(String.valueOf(boot));
		        Log.d("MainActivity","写成功=>boot=>"+boot);
				shutDown();
			}else{
				//Toast.makeText(MainActivity.this,"The input number must be greater than 3",Toast.LENGTH_SHORT).show();
				Toast.makeText(MainActivity.this, "必须大于3", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.down_button:
			down=Integer.parseInt(editText2.getText().toString());
			Log.d("MainActivity", "down1="+down);
			down = down*1000*60;
			timer.schedule(task,down);
			break;
		default:
			break;
		}
	}
}

package bemrr.com.periodicnotcheck;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //START THE SERVICE FOR RECEIVING NOTIFICATIONS and DATA.
        Intent i=new Intent(this, NotiService.class);
        startService(i);
    }
}

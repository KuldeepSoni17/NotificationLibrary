package bemrr.com.periodicnotcheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotiService extends Service {

    private boolean isServiceRunning;

    private CountDownTimer countDownTimer;

    public NotiService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.integer.startNotification);
        countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TICK","OnTICK");
            }
            @Override
            public void onFinish() {
                checkChange();
            }
        };
        start_service();
        return(START_NOT_STICKY);
    }
    @Override
    public void onDestroy() {
        stop_service();
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    private void start_service()
    {
        if (!isServiceRunning) {
            isServiceRunning=true;
            Intent i=new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("FEASIBLE",true);
            PendingIntent pi= PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new Notification.Builder(getApplicationContext()).setContentIntent(pi).setContentTitle("BEMRR").setContentText("FOREGROUND_SERVICE_NOTI").setSmallIcon(R.mipmap.ic_launcher).setOngoing(false).build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_ALL;
            startForeground(getResources().getInteger(R.integer.periodicNotification), notification);
         periodicCaller();
        }
    }

    private void periodicCaller()
    {
        countDownTimer.start();
    }

    public void buildNotification(int logoid, String title, String text)
    {
        NotificationManager myNotificationManager;
        myNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(getApplicationContext()).setContentIntent(pendingIntent).setContentTitle(title).setContentText(text).setSmallIcon(logoid).build();
        notification.defaults = notification.DEFAULT_ALL;
        myNotificationManager.notify(getResources().getInteger(R.integer.periodicNotification),notification);
    }

    private void checkChange()
    {
        Log.d("checkChange","HERE");
        String path = "http://videocall.primasolusoft.com/bemrrtest.php";
        Uri.Builder builder = new Uri.Builder();
        new DBthread().execute(path,builder.build().getEncodedQuery());
        periodicCaller();
    }
    private void stop_service()
    {
        if (isServiceRunning) {
            isServiceRunning=false;
            stopForeground(true);
        }
    }
    private class DBthread extends AsyncTask<String,String,String>
    {
        HttpURLConnection conn;
        URL url = null;

        public DBthread() {
            super();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(params[0]);
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                if(params[1]!=null)
                writer.write(params[1]);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int response_code = conn.getResponseCode();
                Log.d("RESPONSE_CODE",response_code+"");
                if (response_code == HttpURLConnection.HTTP_OK) {
                    Log.d("HTTPOK","HTTPOK");
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        Log.d("HTTPOK",line);
                        result.append(line);
                    }
                    return(result.toString());

                }else{
                    return("unsuccessful");
                }
            }
            catch (Exception e)
            {   e.printStackTrace();
                Log.d("Exception",e + "");
                return "Exception";
            }
            finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("DATABASE",s);
            super.onPostExecute(s);
            if(s.contains("BEMRR_TEST_SUCCESS"))
            {
                buildNotification(R.mipmap.ic_launcher,"Bemrr",s);
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

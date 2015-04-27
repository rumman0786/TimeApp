package timeapp.rumman.com.timeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    TimeTableContract timeTableContract ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeTableContract = new TimeTableContract(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
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

        if (id == R.id.navigate) {
            startActivity(new Intent(this, MonthlyDetails.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void start(View view){
        //date format for storing starting timestamp.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //date format for storing unique entry_key.
        SimpleDateFormat entryKeyFormat = new SimpleDateFormat("ddMMyyyy");
        //date format for storing entry date.
        SimpleDateFormat plainDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //current date and time.
        Date dCurrent = new Date();

        //formatting the date object to strings to store them in db.
        String entryKey = entryKeyFormat.format(dCurrent);
        String start = dateFormat.format(dCurrent);
        String date = plainDateFormat.format(dCurrent);

        Log.d("Time Start : ",entryKey + " " + start + " " + date);

        SQLiteDatabase db = timeTableContract.getWritableDatabase();

        //sqlite query to insert data into timetable for start of day or replace if entry key already exists.
        String query = "INSERT OR REPLACE INTO "+ TimeTableContract.timeTable +" ( "+ timeTableContract.colEntryKey +", "+ timeTableContract.colStart +", "+ timeTableContract.colDate +") "
                + "VALUES ( "+ entryKey +" , '"+ start +"' , '"+ date +"') ";

        db.execSQL(query);
    }

    public void stop(View view){
        //date format for storing starting timestamp.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //date format for storing unique entry_key.
        SimpleDateFormat entryKeyFormat = new SimpleDateFormat("ddMMyyyy");

        //current date and time.
        Date dCurrent = new Date();

        //formatting the date object to strings to store them in db.
        String entryKey = entryKeyFormat.format(dCurrent);
        String stopTime = dateFormat.format(dCurrent);

        SQLiteDatabase db = timeTableContract.getWritableDatabase();

        //sqlite query to insert data into timetable for start of day or replace if entry key already exists.
        String query = "SELECT * FROM "+timeTableContract.timeTable+" WHERE entry_key = " + entryKey;
        Cursor cursor = db.rawQuery(query, null);
        int id = -999 ;
        String startTime = null ;
        if (cursor.moveToFirst()) {
            do{
                id = Integer.parseInt(cursor.getString(0));
                startTime = cursor.getString(2);
                Log.d("ID", "" + id);
            }while(cursor.moveToNext());
        }

        if(id == -999){
            //toast no start time was available
            Toast.makeText(getApplicationContext(),"No start Time is defined please insert manually",Toast.LENGTH_SHORT).show();
            //still update insisting to update the start time manually
            ContentValues contentValues = new ContentValues();
            contentValues.put(timeTableContract.colStop, stopTime);
            db.update(timeTableContract.timeTable, contentValues, timeTableContract.colEntryKey+ " = ?",new String[]{ entryKey });
        }else{
            //update
            try {
                dCurrent = dateFormat.parse(dateFormat.format(dCurrent));
                Date dStart = dateFormat.parse(startTime);
                DateTime starting = new DateTime(dStart);
                DateTime stopping = new DateTime(dCurrent);

                StringBuilder sb = new StringBuilder();
                sb.append(Hours.hoursBetween(starting, stopping).getHours() % 24 + " : ");
                sb.append(Minutes.minutesBetween(starting, stopping).getMinutes() % 60 + " : ");
                sb.append(Seconds.secondsBetween(starting, stopping).getSeconds() % 60);
                String duration = sb.toString();
                //check if daily minutes are acurate
                String dailyMinutes = String.valueOf(Minutes.minutesBetween(starting, stopping).getMinutes());

//                String updateQuery = "UPDATE timetable SET stop = ? ,daily_minutes = ?, duration = ?"
//                        + " WHERE entry_key = ? ";
                ContentValues contentValues = new ContentValues();
                contentValues.put(timeTableContract.colStop, stopTime);
                contentValues.put(timeTableContract.colDailyMinutes, dailyMinutes);
                contentValues.put(timeTableContract.colDuration, duration);

                db.update(timeTableContract.timeTable, contentValues, timeTableContract.colEntryKey+ " = ?",new String[]{ entryKey });
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

}

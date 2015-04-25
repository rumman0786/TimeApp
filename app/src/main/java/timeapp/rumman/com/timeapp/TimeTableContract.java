package timeapp.rumman.com.timeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rumman on 4/4/15.
 */
public class TimeTableContract extends SQLiteOpenHelper {

    static final String dbName = "office_timetable.db" ;
    static final int dbVersion = 2 ;
    static final String timeTable = "timeTable" ;
    static final String colID = "id" ;
    static final String colEntryKey = "entry_key" ;
    static final String colStart = "start" ;
    static final String colStop = "stop" ;
    static final String colDailyMinutes = "daily_minutes" ;
    static final String colDuration = "duration" ;
    static final String colDate = "date" ;


    public TimeTableContract(Context context) {
        super(context, dbName, null , dbVersion );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ timeTable +" ( "+colID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colEntryKey+ " VARCHAR UNIQUE, " +
                colStart+  " VARCHAR, " +
                colStop+  " VARCHAR " +
                colDailyMinutes+  " VARCHAR "+
                colDuration+  " VARCHAR "+
                colDate+  " VARCHAR "+
                ") ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+timeTable);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

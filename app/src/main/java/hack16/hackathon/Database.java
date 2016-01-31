package hack16.hackathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Trevor on 1/30/2016.
 */
public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Custom Routes";
    private static final String KEY_ID = "id";
    private static final String KEY_KEY = "key";
    private static final String KEY_ROUTE = "route";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private int counter = 1;
    public Database(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_ROUTES_TABLE = "CREATE TABLE routes ( " +
                "id INTEGER PRIMARY KEY, "+
                "route TEXT";
        String CREATE_CUSTOM_ROUTES_TABLE = "CREATE TABLE customroutes ( "+
                "key INTEGER" +
                "latitude REAL,"+
                "longitude REAL";
        db.execSQL(CREATE_ROUTES_TABLE);
        db.execSQL(CREATE_CUSTOM_ROUTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS routes");
        this.onCreate(db);
    }

    public void addRoute(LatLng point){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, counter);
        values.put(KEY_ROUTE, "CustomRoute"+counter);


        long newRowId;
        newRowId = db.insert("routes",null,values);
        values.put(KEY_KEY, counter++);
        values.put(KEY_LATITUDE, point.latitude);
        values.put(KEY_LONGITUDE,point.longitude);
        newRowId = db.insert("customroute",null,values);
    }
}

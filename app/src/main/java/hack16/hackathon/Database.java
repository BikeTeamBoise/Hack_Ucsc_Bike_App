package hack16.hackathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Trevor on 1/30/2016.
 */
public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Custom Routes";
    public static final String KEY_ID = "id";
    public static final String KEY_KEY = "key";
    public static final String KEY_ROUTE = "route";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    private int counter = getCounter();
    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int getCounter(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] column = new String[1];
        column[0] = "id";
        Cursor cursor = db.query("routes",column,null,null,null,null,null);
        while(cursor.moveToNext()){}
        cursor.moveToPrevious();
        return cursor.getPosition()+1;

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_ROUTES_TABLE = "CREATE TABLE routes ( " +
                "id INTEGER PRIMARY KEY, "+
                "route TEXT)";
        String CREATE_CUSTOM_ROUTES_TABLE = "CREATE TABLE customroutes ( "+
                "key INTEGER," +
                "latitude REAL,"+
                "longitude REAL)";
        db.execSQL(CREATE_ROUTES_TABLE);
        db.execSQL(CREATE_CUSTOM_ROUTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS routes");
        db.execSQL("DROP TABLE IF EXISTS customroutes");
        this.onCreate(db);
    }

    public void addRoute(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, counter);
        values.put(KEY_ROUTE, "CustomRoute" + counter);

        long newRowId;
        newRowId = db.insert("routes",null,values);
        db.close();
    }

    public void addCustomRoute(LatLng point){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KEY, counter);
        values.put(KEY_LATITUDE, (double)point.latitude);
        values.put(KEY_LONGITUDE, (double)point.longitude);
        long newRowId;
        newRowId = db.insert("customroutes",null,values);
        db.close();
    }

    public Database open() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        return this;
    }

    public Cursor getAllRoutes(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT rowid _id, * FROM routes;", null);
        return c;
    }

    public Cursor fetchRoutes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("routes",new String[] {"route"},null,null,null,null,null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

}

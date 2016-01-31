package hack16.hackathon;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private Database db;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database(this);
        displayList();
        ListView listview = (ListView) findViewById(android.R.id.list);
        listview.setOnItemClickListener(this);
    }

    //Sends starting and destination point to map activity
    public void onItemClick(AdapterView<?> l, View v, int position, long id){
        Cursor cursor = db.getRoute(position);
        Intent intent = new Intent(this,MapsActivity.class);
        cursor.moveToNext();
        intent.putExtra("lat_1",cursor.getDouble(1));
        intent.putExtra("long_1",cursor.getDouble(2));
        cursor.moveToNext();
        intent.putExtra("lat_2",cursor.getDouble(1));
        intent.putExtra("long_2",cursor.getDouble(2));

        startActivity(intent);

    }

    public void displayList(){
        Cursor cursor=db.getAllRoutes();
        String from [] = new String[]{db.KEY_ROUTE};
        int to [] = new int[] {R.id.textView1};
        dataAdapter = new SimpleCursorAdapter(this, R.layout.row_item, cursor, from, to, 0);

        ListView lv = getListView();
        lv.setAdapter(dataAdapter);
    }
}

package hack16.hackathon;


import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ListActivity {

    private Database db;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database(this);
        displayList();
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

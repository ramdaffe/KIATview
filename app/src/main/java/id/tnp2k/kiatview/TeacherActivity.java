package id.tnp2k.kiatview;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.util.ArrayList;


public class TeacherActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{


    ListView listView ;
    String currentTeacher;
    String path;
    private static final int CHOOSE_TEACHER = 100;
    SimpleCursorAdapter mAdapter;
    ArrayList<String> teacherArray;
    ArrayList<String> monthArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);


        listView = (ListView) findViewById(R.id.teacherList);
        teacherArray = new ArrayList<String>();
        ReadDir();
        String[] values = teacherArray.toArray(new String[teacherArray.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String  itemValue    = (String) listView.getItemAtPosition(position);
                currentTeacher = itemValue;
                selectItem(view);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void selectItem(View view){
        Intent intent = new Intent(this, monthActivity.class);
        Bundle b  = new Bundle();
        b.putString("path",path + "/" + currentTeacher.toString());
        if (intent.getExtras() == null) {
            intent.putExtras(b);
        } else {
            intent.replaceExtras(b);
        }

        startActivityForResult(intent,CHOOSE_TEACHER);
        // Do something in response to button

    }


    public void ReadDir(){
        try {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/KIATcam";
            File curDir = new File(path);
            File[] ff = curDir.listFiles();
            for (File f : ff){
                if (f.isDirectory() && !f.getName().equals(".sync") && !f.getName().equals("FPL") && !f.getName().equals("Daftar Hadir Manual") && !f.getName().equals("Surat Permohonan Izin") && !f.getName().equals("Formulir Verifikasi Kehadiran Guru")){

                    teacherArray.add(f.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }
}

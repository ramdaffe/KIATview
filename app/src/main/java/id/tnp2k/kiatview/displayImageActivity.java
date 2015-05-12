package id.tnp2k.kiatview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.media.ExifInterface;
import android.widget.TextView;

import java.io.IOException;


public class displayImageActivity extends Activity {

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Bundle b = getIntent().getExtras();
        path = b.getString("path");
        ImageView v = (ImageView) findViewById(R.id.displayPhoto);
        TextView t = (TextView) findViewById(R.id.metadataPhoto);
        Bitmap bmImg = BitmapFactory.decodeFile(path);
        try{
            ExifInterface exif = new ExifInterface(path);
            String nama = exif.getAttribute("UserComment");
            String datetime = exif.getAttribute(exif.TAG_DATETIME);
            t.setText(datetime);
        } catch (IOException e) {

        }

        v.setImageBitmap(bmImg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
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
}

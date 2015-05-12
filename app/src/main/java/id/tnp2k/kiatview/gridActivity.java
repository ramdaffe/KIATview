package id.tnp2k.kiatview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class gridActivity extends Activity {

    String currentImage;
    String doctype;
    private static final int CHOOSE_IMAGE = 103;



    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        ArrayList<String> itemList = new ArrayList<String>();

        public ImageAdapter(Context c) {
            mContext = c;
        }

        void add(String path){
            itemList.add(path);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes


                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(480, 480));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView = (ImageView) convertView;
            }

            Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220, 220);

            imageView.setImageBitmap(bm);
            return imageView;
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

            Bitmap bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, options);

            return bm;
        }

        public int calculateInSampleSize(

                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }

            return inSampleSize;
        }

    }

    ImageAdapter myImageAdapter;
    String path;
    ArrayList<String> exifArrayList;
    ArrayList<Date> dateArrayList;
    ArrayList<Date> processeddateArrayList;
    SimpleDateFormat fullDate,timeSegment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        Bundle b = getIntent().getExtras();
        path = b.getString("path");
        GridView gridview = (GridView) findViewById(R.id.gridView);
        myImageAdapter = new ImageAdapter(this);
        gridview.setAdapter(myImageAdapter);
        File targetDirector = new File(path);
        fullDate = new SimpleDateFormat("yyyy:mm:dd HH:mm:ss");
        timeSegment = new SimpleDateFormat("hh:mm:ss aa");
        exifArrayList = new ArrayList<String>();
        dateArrayList = new ArrayList<Date>();
        processeddateArrayList = new ArrayList<Date>();
        File[] files = targetDirector.listFiles();
        for (File file : files){
            myImageAdapter.add(file.getAbsolutePath());
            String exifData;
            try{
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                exifData = exif.getAttribute(exif.TAG_DATETIME);
                exifArrayList.add(exifData);
                dateArrayList.add(convertDate(exifData));

            } catch (IOException e) {

            }
        }
        TextView t = (TextView) findViewById(R.id.statsText);

        String durText = CalculateDuration(dateArrayList);
        //t.setText(exifArrayList.get(0));
        t.setText(durText);

        gridview.setOnItemClickListener(myOnItemClickListener);

    }

    public String CalculateDuration(ArrayList<Date> datearray){
        if (datearray.size() == 1){
            return "Tidak dapat diproses";
        } else {
            for (int i = 0; i < datearray.size(); i++) {
                if (i != datearray.size()-1){ //if not last
                    if ( datearray.get(i+1).getTime() - datearray.get(i).getTime() > 1800000){
                        processeddateArrayList.add(datearray.get(i));
                    }
                } else {
                    processeddateArrayList.add(datearray.get(i));
                }
            }
            if (processeddateArrayList.size() < 2){
                return "Tidak dapat diproses";
            } else {
                return getDuration(processeddateArrayList.get(0),processeddateArrayList.get(processeddateArrayList.size()-1));
            }

        }
    }

    public String getDuration(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        String duration = "Durasi kehadiran: " + String.valueOf(hours) + " jam " + String.valueOf(minutes - (hours * 60)) + " menit";
        return duration;
    }

    public Date convertDate(String s){
        Date d = new Date();
        try{
            d = fullDate.parse(s);
        } catch (ParseException e){

        }
        return d;
    }


    public void selectItem(String p){
        Intent intent = new Intent(this, displayImageActivity.class);
        Bundle b  = new Bundle();
        b.putString("path",p);
        if (intent.getExtras() == null) {
            intent.putExtras(b);
        } else {
            intent.replaceExtras(b);
        }

        startActivityForResult(intent,CHOOSE_IMAGE);
        // Do something in response to button

    }


    AdapterView.OnItemClickListener myOnItemClickListener
            = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String prompt = (String)parent.getItemAtPosition(position);
            selectItem(prompt);
            Toast.makeText(getApplicationContext(),
                    prompt,
                    Toast.LENGTH_LONG).show();

        }};
    }



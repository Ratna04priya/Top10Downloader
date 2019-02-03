package com.aa.rp.top10downloader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private  String feedurl ="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topalbums/limit=%d/xml";
    private  int feedLimit = 10;

    private String feedCachedurl = "INVALID";
    public static final String STATE_URL = "feedurl";
    public static final String  STATE_LIMIT ="feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        if(savedInstanceState !=null){
            feedurl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedurl,feedLimit));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu,menu);
        // getting the menu holded ewhen in landscape mode
        if(feedLimit==10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }else{
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();


        switch (id){
            case R.id.mnuFree:
                feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";                                        // The required URL is filled over here.
                break;

            case R.id.mnuPaid:
                feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;

            case R.id.mnuSongs:
                feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;

            case R.id.mnu10:

            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG,"onOptionsItemSelected: " + item.getTitle() + "seeting feedlimit to " + feedLimit);

                }else {
                    Log.d(TAG,"onOptionsItemSelected: " + item.getTitle() + "feedlimit unchanged");
                }
                break;

            case R.id.mnuRefresh:
                feedCachedurl ="INVALIDATED";
                break;

            default:

                return super.onOptionsItemSelected(item);

        }
        downloadUrl(String.format(feedurl,feedLimit));

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedurl);
        outState.putInt(STATE_LIMIT,feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedurl){
        if(!feedurl.equalsIgnoreCase(feedCachedurl)){

            Log.d(TAG,"downloadURL: starting Asyntask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedurl);
            feedCachedurl = feedurl;
            Log.d(TAG,"downloadURL: done");


        }else{
            Log.d(TAG,"dowmloadUrl: URL Not changed");
        }

    }

    class DownloadData extends AsyncTask<String,Void,String>{

        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
  //          Log.d(TAG,"onPostExecute: parameter is " + s);
            ParseApplication parseApplication = new ParseApplication();
            parseApplication.parse(s);
// Given Adapter (we wre using this before we made our adapter (custom Adapter)

   /*         ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(
                    MainActivity.this,R.layout.list_item,parseApplication.getApplication());
            listApps.setAdapter(arrayAdapter);
   */

   // Custom Adapter -made :

         FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record,
                 parseApplication.getApplication());
         listApps.setAdapter(feedAdapter);

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG,"doInBackground: starts with  " + strings[0]);
            String rssfeed = downloadXML(strings[0]);
            if(rssfeed == null){
                Log.d(TAG,"doInBackground: Error Downloading");
            }
            return rssfeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlresult = new StringBuilder();
            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was "+ response);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int charsread;
                char[] inputBuffer = new char[500];
                while(true) {
                    charsread = reader.read(inputBuffer);
                    if(charsread < 0){
                        break;
                    }
                    if(charsread > 0){
                        xmlresult.append(String.copyValueOf(inputBuffer,0, charsread));

                    }

                }
                reader.close();
                return  xmlresult.toString();
            } catch (MalformedURLException e){
                Log.d(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e){
                Log.d(TAG,"doenloadData: IO Exception reading data: " + e.getMessage());
            }
           return null;
        }

    }
}

package com.aa.rp.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplication {
    private static  final  String TAG = "ParseApplication";
    private ArrayList<FeedEntry> application;

    public ParseApplication() {
        this.application = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplication() {
        return application;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        FeedEntry currentrecord = null ;
        boolean inEntry = false;
        String textvalue = "";
        try{
     // Line  28 to 31 sets the XML parsing
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp =  factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventtype = xpp.getEventType();
            while (eventtype != XmlPullParser.END_DOCUMENT){
                String tagname = xpp.getName();
                switch(eventtype){
                    case XmlPullParser.START_TAG:
               //         Log.d(TAG,"parse.Starting tag for " + tagname);
                        if("entry".equalsIgnoreCase(tagname)){
                            inEntry =true;
                            currentrecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textvalue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
            //            Log.d(TAG, "parse: Ending tag for " + tagname);
                        if (inEntry) {

                            if("entry".equalsIgnoreCase(tagname)){

                                application.add(currentrecord);
                                inEntry = false;
                            }else if ("name".equalsIgnoreCase(tagname)){
                                currentrecord.setName(textvalue);
                            } else if("artist".equalsIgnoreCase(tagname)){
                                currentrecord.setArtist(textvalue);
                            }else if ("releaseDate".equalsIgnoreCase(tagname)){
                                currentrecord.setReleaseDate(textvalue);
                            }else if("summary".equalsIgnoreCase(tagname)){
                                currentrecord.setSummary(textvalue);
                            }else if ("image".equalsIgnoreCase(tagname)){
                                currentrecord.setImageURL(textvalue);
                            }
                        }
                        break;

                    default:
                        //nothing to do
                }

                eventtype = xpp.next();
            }
 /*           for (FeedEntry app: application){
                Log.d(TAG,"**************************");
                Log.d(TAG,app.toString());
            }
*/
        }catch (Exception e){
            status =false;
            e.printStackTrace();
        }
        return status;
    }
}

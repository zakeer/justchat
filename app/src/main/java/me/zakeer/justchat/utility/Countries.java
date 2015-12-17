package me.zakeer.justchat.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Countries {
	
	private static final String TAG = "Countries";
	private ArrayList<String> countries;
	private String myCountry;
	private Context context;
		
	public Countries(Context context) {
		this.context = context;
		countries = new ArrayList<String>();
		getCountryCodeFromAvailableLocales();
	}
	
	// Method shud b made public. Change
    private void getCountryCodeFromAvailableLocales() {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
                if(country.equals(Locale.getDefault().getDisplayCountry()))
                {
                	myCountry = country;
                	Log.e(TAG, myCountry);
                }
                //Currency.getInstance(locale);
            }
        }   
        sortCountries();
    }
    
    // Method shud b made public. Change
    private void getCountryCodeFromXml(int xmlFileResource) {
    	XmlResourceParser xrp = context.getResources().getXml(xmlFileResource);
    	
    	try {
			xrp.next();
			int eventType = 0;
			eventType = xrp.getEventType();
	    	while (eventType != XmlPullParser.END_DOCUMENT) {
	    	    if (eventType == XmlPullParser.START_TAG
	    	            && xrp.getName().equalsIgnoreCase("your_target_tag")) {
	    	        String attrValue = xrp.getAttributeValue(null,
	    	                "attribute");
	    	        int intValue = xrp.getAttributeIntValue(null, "order", 0);
	    	        break;
	    	    }
	    	}
				eventType = xrp.next();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public String getJsonArrayCountryCodeFromFile(int jsonResource) {
    	try {
			return ReadFiles.readRawFileAsString(context, jsonResource);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    private void sortCountries() {
    	Collections.sort(countries);
	}
    
    public int getCount() {
		return countries.size();
	}
    
    public ArrayList<String> getCountries() {
		return countries;
	}    
    
    public String getMyCountry() {
		return myCountry;
	}
    
    public static boolean isSimPresent(Context context) {
    	// yet to be completed.
    	
    	PackageManager pm = context.getPackageManager();
    	boolean hasTelephony=pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);    		
		return hasTelephony;
	}
    
    public static String getCurrentCountry(Context context) {
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	
        String countryCode = tm.getSimCountryIso();
		return countryCode;
	}
    
    
}

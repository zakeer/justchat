package me.zakeer.justchat.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHandle extends SQLiteOpenHelper {
	
    // Database Version
    private static final int DATABASE_VERSION = 1;
    
    // Database Name
    private static final String DATABASE_NAME = "maindatabase";
      
    private static String FRIEND_TABLE = 	"tb_friends" ;
    // MAINCAT Table Columns names
    private static final String KEY_FRIEND_ID 		= "f_id";
    private static final String KEY_FRIEND_NAME 	= "f_name";
    private static final String KEY_FRIEND_PHONE 	= "f_phone";
    private static final String KEY_FRIEND_IMG 		= "f_img";
    private static final String KEY_FRIEND_CHECKED 	= "f_checked";
    
	public SqliteHandle(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		  String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE + "("
	                + KEY_FRIEND_ID + " INTEGER PRIMARY KEY,"
	        		+ KEY_FRIEND_NAME + " TEXT,"
	    	        + KEY_FRIEND_PHONE + " TEXT,"
	        		+ KEY_FRIEND_IMG + " TEXT,"
	                + KEY_FRIEND_CHECKED + " TEXT" + " )";
	        db.execSQL(CREATE_TABLE); 
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE);
        // Create tables again
        onCreate(db);
	}
	    
    public void insertInFriend(FriendData details) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_FRIEND_ID, details.getFriend_id()); 
        values.put(KEY_FRIEND_NAME, details.getFname()); 
        values.put(KEY_FRIEND_PHONE, details.getPhone()); 
        values.put(KEY_FRIEND_IMG, details.getPic());
        values.put(KEY_FRIEND_CHECKED, "0");
        
        // Inserting Row
        db.insert(FRIEND_TABLE, null, values);
        db.close(); // Closing database connection
    }
    
    // Getting All Details
    public List<FriendData> getAllFriendDetails() {
        List<FriendData> list = new ArrayList<FriendData>();
        // Select All Query
        
        String selectQuery = "SELECT  * FROM " + FRIEND_TABLE;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	FriendData details = new FriendData();
            	details.setFriend_id(cursor.getString(0));
            	details.setFname(cursor.getString(1));
            	details.setPhone(cursor.getString(2));
            	details.setPic(cursor.getString(3));
            	String ch=cursor.getString(4);
            	details.setStatus(ch);
            	Log.e("", ch);
            	
            /*	if(ch.equals("0"))
            		details.setIschecked(false);
            	else
            		details.setIschecked(true);
            */	// Adding to list
            	list.add(details);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }	
    
    
    // Getting All Checked
    public List<FriendData> getCheckedFriendDetails() {
        List<FriendData> list = new ArrayList<FriendData>();
        // Select All Query
        
        String selectQuery = "SELECT  * FROM " + FRIEND_TABLE+" WHERE "+KEY_FRIEND_CHECKED+" = '1'";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	FriendData details = new FriendData();
            	details.setFriend_id(cursor.getString(0));
            	details.setFname(cursor.getString(1));
            	details.setPhone(cursor.getString(2));
            	details.setPic(cursor.getString(3));
            	
            	String ch=cursor.getString(4);
            /*	if(ch.equals("0"))
            		details.setIschecked(false);
            	else
            		details.setIschecked(true);
            */	// Adding to list
            	
            	list.add(details);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }	
    
    // Getting Count Of All Checked
    public int getCheckedCount() {
    	String count="0";    
        String selectQuery = "SELECT count(*) FROM " + FRIEND_TABLE+" WHERE "+KEY_FRIEND_CHECKED+" = '1'";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {            	
            	count = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        int total = Integer.parseInt(count);
        
        return total;
    }	
    
    // Getting All Details
    public void changeCheck(String Stat,String f_id) 
    {
 	   String selectQuery;
 	   
         selectQuery = "UPDATE "+FRIEND_TABLE+" SET "+KEY_FRIEND_CHECKED+" = "+"'"+Stat+"'"
         		+" WHERE "+KEY_FRIEND_ID+" = '"+f_id+"'";
         
         Log.e("QUERY", selectQuery);
       
 	  
         SQLiteDatabase db = this.getWritableDatabase();
         Cursor cu = db.rawQuery(selectQuery, null);
         cu.moveToFirst();
         cu.close();  
        
     }	
   
    public void SelectAll(String Stat) 
    {
 	   String selectQuery;
 	   
         selectQuery = "UPDATE "+FRIEND_TABLE+" SET "+KEY_FRIEND_CHECKED+" = "+"'"+Stat+"'";
         
         Log.e("QUERY", selectQuery);
       
 	  
         SQLiteDatabase db = this.getWritableDatabase();
         Cursor cu = db.rawQuery(selectQuery, null);
         cu.moveToFirst();
         cu.close();  
        
     }	
   
    // delete All scan Details
    public void deleteAllFriendDetails() {
        
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FRIEND_TABLE, null, null);
       
    }
    
}

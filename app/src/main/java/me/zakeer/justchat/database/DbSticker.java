package me.zakeer.justchat.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.zakeer.justchat.items.StickerItem;
import me.zakeer.justchat.utility.Constant;

public class DbSticker extends SQLiteOpenHelper {
	
    // Database Version
    private static final int DATABASE_VERSION = DatabaseFreshIM.DATABASE_VERSION;
    
    // Database Name
    private static final String DATABASE_NAME = DatabaseFreshIM.DATABASE_NAME_STICKER;
    
    private static final String TABLE_NAME = DatabaseFreshIM.TABLE_STICKER;
    
	public DbSticker(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " 
                + TABLE_NAME + "("
                        + Constant.ID + " INTEGER PRIMARY KEY, "
                        + Constant.IMAGE + " TEXT, "
                		+ Constant.EXT + " TEXT, "
                		+ Constant.CATEGORY + " TEXT, "
                		+ Constant.TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + " )";
        	
        db.execSQL(CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        
        // Create tables again
        onCreate(db);
	}
    
    public void insertInTable(StickerItem details) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(Constant.ID, details.getId());
        values.put(Constant.IMAGE, details.getImage());      
        values.put(Constant.EXT, details.getExtension());
        values.put(Constant.CATEGORY, details.getCategory());
        values.put(Constant.TIMESTAMP, details.getTime());
        
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
    
    public void updateTable(StickerItem details) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(Constant.ID, details.getId());
        values.put(Constant.IMAGE, details.getImage());      
        values.put(Constant.EXT, details.getExtension());
        values.put(Constant.CATEGORY, details.getCategory());
        values.put(Constant.TIMESTAMP, details.getTime());
        
        // Update Row
        db.update(TABLE_NAME, values, Constant.ID+"='"+details.getId()+"'", null);
        db.close(); // Closing database connection
    }
    
    // Getting All Details
    public List<StickerItem> getAllDetails() {
        List<StickerItem> list = new ArrayList<StickerItem>();
        // Select All Query
        
        //String selectQuery = "SELECT * FROM " + TABLE_NAME; // + "WHERE (only from today)";
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY "+ Constant.TIMESTAMP +" DESC ";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db.rawQuery(selectQuery, null) != null)
        {
        	cursor = db.rawQuery(selectQuery, null);
        }
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	StickerItem details = new StickerItem();
            	details.setId(cursor.getString(0));
            	details.setImage(cursor.getString(1));
            	details.setExtension(cursor.getString(2));
            	details.setCategory(cursor.getString(3));
            	details.setTime(cursor.getString(4));
            	// Adding to list
            	list.add(details);
            } while (cursor.moveToNext());
        }	
        cursor.close();
        db.close();
        return list;
    }	
    
    // Check if event already present
    public boolean isImagePresent(int id) {
    	
        // Select All Query        
        String selectQuery = "SELECT id FROM " + TABLE_NAME + " WHERE " + Constant.ID + " = " + id + "";
        
        //String selectQuery = "SELECT EXISTS(SELECT id FROM " + TABLE_NAME + " WHERE " + Constant.EVENT_ID + " = " + event_id + " LIMIT 1);"; //  "SELECT id FROM " + TABLE_NAME + " WHERE " + Constant.EVENT_ID + " = " + event_id + "";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db.rawQuery(selectQuery, null) != null)
        {
        	cursor = db.rawQuery(selectQuery, null);
        }
        
        if (cursor.getCount()>0) {
        	db.close();
            return true;
        }
        
        db.close();
        return false;
    }	
    
    // Getting Details
    public List<StickerItem> getDetail(int id) {
        List<StickerItem> list = new ArrayList<StickerItem>();
        // Select All Query
        
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + Constant.ID + " = " + id + "";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db.rawQuery(selectQuery, null) != null)
        {
        	cursor = db.rawQuery(selectQuery, null);
        }
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	StickerItem details = new StickerItem();
            	details.setId(cursor.getString(0));
            	details.setImage(cursor.getString(1));
            	details.setExtension(cursor.getString(2));
            	details.setCategory(cursor.getString(3));
            	details.setTime(cursor.getString(4));
            	// Adding to list
            	list.add(details);
            } while (cursor.moveToNext());
        }	
        cursor.close();
        db.close();
        return list;
    }	
    
    // delete All Details
    public void deleteAllDetails() {        
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
    
    // delete All Details
    public void deleteDetails(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_NAME, Constant.EVENT_ID + "=?", new String[] { String.valueOf(id) });
        db.delete(TABLE_NAME, Constant.ID + "=" +id, null);
        db.close();
    }
    
    public boolean isTableEmpty() {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        
    	Cursor cur = db.rawQuery(selectQuery, null);
    	if (cur != null) {
    	    cur.moveToFirst();                       // Always one row returned.
    	    if (cur.getInt(0) == 0) {
    	    	db.close();
    	    	return true;
    	    }
    	}
        db.close();
    	return false;
	}    
    
    public int getCount() {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        int count = 0;
    	Cursor cur = db.rawQuery(selectQuery, null);
    	if (cur != null) {
    	    cur.moveToFirst();                       // Always one row returned.
    	    count = cur.getInt(0);
    	}
        db.close();
    	return count;
	}    
}
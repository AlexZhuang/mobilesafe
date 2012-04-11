package com.alex.mobilesafe.db.dao;

import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	
	public static SQLiteDatabase getDatabase(String dbPath){
		return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
	}
}

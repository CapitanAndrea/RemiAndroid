package it.capitanilproductions.remi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBList extends SQLiteOpenHelper {

	private static final String DATABASE_NAME="remi.db";
	private static final int DB_VERSION=1;
	
//	colonne comuni
	public static final String COLOUMN_ID="_id";
	public static final String COLOUMN_NAME="nome";
//	colonne per la tabella delle liste
	public static final String LIST_TABLE="liste";
	public static final String COLOUMN_TOTAL="numeroelementi";
	public static final String COLOUMN_CHECKED="elementisegnati";
	public static final String COLOUMN_ABORDER="ordinealfabetico";
	public static final String COLOUMN_MOVETOBOTTOM="spostainfondo";
//	colonne per la tabella degli elementi
	public static final String ITEM_TABLE="elementi";
	public static final String COLOUMN_LIST="lista";
	public static final String COLOUMN_POSITION="posizione";
	public static final String COLOUMN_IS_CHECKED="segnato";
	
//	stringa per create la tabella delle liste
	private static final String LIST_TABLE_CREATE=
			"CREATE TABLE "+LIST_TABLE+" ( "+
			COLOUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
			COLOUMN_NAME+" TEXT, "+
			COLOUMN_TOTAL+" NUMERIC, "+
			COLOUMN_CHECKED+" NUMERIC , "+
			COLOUMN_ABORDER+" NUMERIC, "+ //boolean
			COLOUMN_MOVETOBOTTOM+" NUMERIC "+ //boolean
			")";
//	stringa per create la tabella degli elementi
	private static final String ITEM_TABLE_CREATE=
			"CREATE TABLE "+ITEM_TABLE+" ( "+
			COLOUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
			COLOUMN_NAME+" TEXT, "+
			COLOUMN_LIST+" TEXT, "+
			COLOUMN_POSITION+" NUMERIC, "+
			COLOUMN_IS_CHECKED+" NUMERIC "+ //boolean
			")";
	
	
	public DBList(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LIST_TABLE_CREATE);
		db.execSQL(ITEM_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + LIST_TABLE);
		db.execSQL("DROP TABLE IF EXISTS" + ITEM_TABLE);
		onCreate(db);
	}

}

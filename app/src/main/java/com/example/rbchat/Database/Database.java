package com.example.rbchat.Database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;



public class Database extends SQLiteOpenHelper {

    public static Database db = null ;


    public static void initInstance(Context context)
    {
        if(db==null)
            db = new Database(context);
    }

    public static Database getInstance()
    {
        return db;
    }



    private static final String db_name="RB_CHAT";

    private static final int db_version=1;

    private static final String USERS_TABLE="USERS";
    private static final String USERS_UID="UID";
    private static final String USERS_UNAME="UNAME";
    private static final String USERS_UPHONENUMBER="UPHONENUMBER";
    private static final String USERS_UPROFILEIMAGE="UPROFILEIMAGE";
    private static final String USERS_ABOUT="ABOUT";


    private static String MESSAGE_TABLE="MESSAGES";
    private static String MESSAGE_CONTENT="CONTENT";
    private static String MESSAGE_ID="MID";
    private static String MESSAGE_TIMESTAMP="TIMESTAMP";

    private static String MESSAGE_SENDER_ID="SENDERID";
    private static String MESSAGE_RECEIVER_ID="RECEIVERID";
    private static String MESSAGE_FEELING="FEELING";




    public Database(@Nullable Context context) {
        super(context, db_name, null, db_version);
    }

    public static SQLiteDatabase beginTransaction()
    {
        SQLiteDatabase db = Database.getInstance().getWritableDatabase();
        db.execSQL("BEGIN TRANSACTION;");
        return db;
    }

    public static void commit(SQLiteDatabase db)
    {
        db.execSQL("COMMIT;");
    }

    public static void rollback(SQLiteDatabase db)
    {
        db.execSQL("ROLLBACK;");
    }




    static void create_table_message(SQLiteDatabase db){
        String query = "CREATE TABLE IF NOT EXISTS "+MESSAGE_TABLE+" ("+MESSAGE_ID+" TEXT,"+MESSAGE_CONTENT+" TEXT,"+MESSAGE_TIMESTAMP+" TEXT,"+MESSAGE_SENDER_ID+" TEXT, "+MESSAGE_RECEIVER_ID+"  TEXT,"+MESSAGE_FEELING+" INT  );";
        db.execSQL(query);

        Log.d("db","messages table created");
    }


    static void create_table_users (SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS "+USERS_TABLE+" ("+USERS_UID+" TEXT,"+USERS_UNAME+" TEXT,"+USERS_UPHONENUMBER+" TEXT,"+USERS_UPROFILEIMAGE+" TEXT, "+USERS_ABOUT+"  TEXT  );";
        db.execSQL(query);

        Log.d("db","users table created");

    }


    static void drop_table_users(SQLiteDatabase db)
    {
        String query = "DROP TABLE IF EXISTS assignment;";

        db.execSQL(query);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // create_table_question(db);
        create_table_users(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<newVersion)
        {
            //   drop_table_question(db);

            drop_table_users(db);

            onCreate(db);

        }
    }
}



package com.codebreaker.rajaongkir;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME = "rajaongkir.sqlite";
    private final Context mContext;
    private SQLiteDatabase mDataBase;
    Cursor cursor;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 6); // 1 is Database Version
        //check for path
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    public void createDataBase() throws IOException {
        // If database not exists copy it from the assets
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assests
                copyDataBase();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    // Check that the database exists here: /data/data/your package/databases/Da
    // Name
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        // Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    // Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        // Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
        // mDataBase = SQLiteDatabase.openDatabase(mPath, null,
        // SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    public ArrayList<Alamat> getAlamat(){
        mDataBase = DBHelper.this.getReadableDatabase();
        cursor = mDataBase.rawQuery("select * from domesticCities",null);
        ArrayList<Alamat> alamats = new ArrayList<>();

        cursor.moveToFirst();
        do {
            Alamat alamat = new Alamat();
            alamat.setId(cursor.getString(cursor.getColumnIndex("id")));
            alamat.setKabupaten(cursor.getString(cursor.getColumnIndex("kabupaten")));
            alamats.add(alamat);
        }while (cursor.moveToNext());
        return  alamats;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

}

package emadahmed.com.easynote;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;



/**
 * Created by Emad Ahmed on 5/05/2016. Added to manifest file
 */
public class NoteProvider extends ContentProvider {

    private static final String AUTHORITY = "emadahmed.com.easynote.noteprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constants to identify the requested operation, not used elsewhere
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    // parses uri and tells which you operation is requested (NOTES/NOTES_ID)
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static String CONTENT_ITEM_TYPE = "note";

    // executed at class initialization
    static {
        uriMatcher.addURI(AUTHORITY,BASE_PATH,NOTES);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#",NOTES_ID); // wildcard indicates looking for particular row in database
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext()); // accessing database
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri)== NOTES_ID)
        {
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_NOTES,DBOpenHelper.ALL_COLUMNS,selection,null,null,null,DBOpenHelper.NOTE_CREATED +
                " DESC"); // order by latest notes first

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    // returns uri that matches format of uriMatcher  for rows
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_NOTES,null,values); // table name, ContentValues object (passes data in backend)
        return Uri.parse(BASE_PATH + "/" + id); // passes in the new primary key
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES,values,selection,selectionArgs);


    }
}

package emadahmed.com.easynote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String action; // to remeber if updationg/inserting
    private EditText editor;
    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NoteProvider.CONTENT_ITEM_TYPE); // only null if user presses insert button

        if (uri == null)
        {
            action  = Intent.ACTION_INSERT; // represents state
            setTitle("New Note");

        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri,DBOpenHelper.ALL_COLUMNS,noteFilter,null,null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            setTitle(oldText);
            editor.setText(oldText);
            editor.requestFocus();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finishEditing(); // called when back button pressed on toolbar
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }
            return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NoteProvider.CONTENT_URI,noteFilter,null); // includes noteFilter which only includes that row
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (action.equals(Intent.ACTION_EDIT))
        {
            getMenuInflater().inflate(R.menu.menu_editor,menu);
        }
        return true;
    }


    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch(action)
        {
            case Intent.ACTION_INSERT:
                if (newText.length()==0)
                {
                    setResult(RESULT_CANCELED); // wont restart loader
                }else{
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0)
                {
                        deleteNote();
                }
                else if (oldText.equals(newText))
                {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateNote(newText);
                }
        }
        finish(); // goes to parent activity
    }

    private void updateNote(String noteText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NoteProvider.CONTENT_URI, contentValues, noteFilter, null);
        Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri =  getContentResolver().insert(NoteProvider.CONTENT_URI, contentValues);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed()
    {
        finishEditing();
    } // called when back button on device pressed

}

package emadahmed.com.easynote;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Emad Ahmed on 5/05/2016.
 */
public class NotesCursorAdapter extends CursorAdapter {

    // deals with multiline and lengthy notes

    public NotesCursorAdapter(Context context, Cursor c, int flags)
    {
        super(context,c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.note_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT)); // gets text for a particular note

        int pos = noteText.indexOf(10); // this is the ascii value of a new line

        if (pos !=-1) // means that there is a /n character
        {
            noteText = noteText.substring(0,pos) ;
        }

        TextView textView = (TextView) view.findViewById(R.id.tvNote);
        textView.setText(noteText);

    }
}

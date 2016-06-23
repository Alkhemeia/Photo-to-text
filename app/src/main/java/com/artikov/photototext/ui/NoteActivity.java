package com.artikov.photototext.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.artikov.photototext.R;
import com.artikov.photototext.notes.Note;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_EXTRA = "NOTE";

    @BindView(R.id.note_activity_edit_text_note_text)
    EditText mNoteTextTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        ButterKnife.bind(this);
        Note note = (Note) getIntent().getSerializableExtra(NOTE_EXTRA);
        mNoteTextTextView.setText(note.getText());
    }
}
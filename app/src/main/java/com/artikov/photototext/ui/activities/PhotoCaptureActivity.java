package com.artikov.photototext.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.artikov.photototext.R;
import com.artikov.photototext.data.Note;
import com.artikov.photototext.data.OcrProgress;
import com.artikov.photototext.presenters.PhotoCapturePresenter;
import com.artikov.photototext.views.PhotoCaptureView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoCaptureActivity extends MvpAppCompatActivity implements PhotoCaptureView {
    private static final int CHOOSE_IN_GALLERY_REQUEST_CODE = 1;
    private static final int TAKE_PHOTO_REQUEST_CODE = 2;

    @BindView(R.id.activity_photo_capture_layout_buttons)
    ViewGroup mButtonsLayout;

    @BindView(R.id.activity_photo_capture_layout_progress)
    ViewGroup mProgressLayout;

    @BindView(R.id.activity_photo_capture_text_view_progress)
    TextView mProgressTextView;

    @BindView(R.id.activity_photo_capture_button_choose_in_gallery)
    ImageButton mChooseInGalleryButton;

    @BindView(R.id.activity_photo_capture_button_take_photo)
    ImageButton mTakePhotoButton;

    @BindView(R.id.activity_photo_capture_button_cancel)
    Button mCancelButton;

    @InjectPresenter
    PhotoCapturePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        ButterKnife.bind(this);

        mChooseInGalleryButton.setOnClickListener(v -> chooseInGallery());
        mTakePhotoButton.setOnClickListener(v -> takePhoto());
        mCancelButton.setOnClickListener(v -> mPresenter.userClickCancel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_capture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.photo_capture_menu_item_notes:
                showNoteList();
                return true;
            case R.id.photo_capture_menu_item_settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_IN_GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.userChooseImage(data.getData());

            }
        } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.userChooseImage(getPhotoUri());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showProgress() {
        mButtonsLayout.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonsLayout.setVisibility(View.VISIBLE);
        mProgressLayout.setVisibility(View.GONE);
        mProgressTextView.setText("");
    }

    @Override
    public void setProgress(OcrProgress progress) {
        switch (progress) {
            case UPLOADING:
                mProgressTextView.setText(R.string.uploading);
                break;
            case RECOGNITION:
                mProgressTextView.setText(R.string.recognition);
                break;
            case DOWNLOADING:
                mProgressTextView.setText(R.string.downloading);
                break;
        }
    }

    @Override
    public void showNote(Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_EXTRA, note);
        startActivity(intent);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void chooseInGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CHOOSE_IN_GALLERY_REQUEST_CODE);
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
        }
    }

    private Uri getPhotoUri() {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.default_photo_name));
        Uri photoFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        return photoFile;
    }

    private void showNoteList() {
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
        mPresenter.userLeaveScreen();
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        mPresenter.userLeaveScreen();
    }
}

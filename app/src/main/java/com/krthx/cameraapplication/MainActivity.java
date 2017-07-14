package com.krthx.cameraapplication;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.krthx.cameraapplication.Adapter.BitmapUtils;
import com.krthx.cameraapplication.Adapter.GalleryAdapter;
import com.krthx.cameraapplication.Adapter.ImageRecyclerAdapter;
import com.krthx.cameraapplication.Data.DbImageManager;
import com.krthx.cameraapplication.Listeners.RecyclerItemClickListener;
import com.krthx.cameraapplication.Utils.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    private static final int SOLICITUD_PERMISO_CAMERA = 0;

    ImageView imagenSeleccionada;
    Gallery gallery;
    DbImageManager imgMan;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagenSeleccionada = (ImageView) findViewById(R.id.selected);

        imgMan = new DbImageManager(this);
        final List<Bitmap> images = imgMan.getImages();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_gallery);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ImageRecyclerAdapter(imgMan.getImages());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        try{
                            imagenSeleccionada.setImageBitmap(((ImageRecyclerAdapter) mAdapter).Images().get(position));
                        }catch(Exception ex) {}

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        //mRecyclerView.setOnClickListener(this);
        /*gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new GalleryAdapter(this, images));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                imagenSeleccionada.setImageBitmap(images.get(position));
            }

        });*/

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        final View actionA = findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);

                menuMultipleActions.collapseImmediately();
            }
        });

        final View actionB = findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);

                    menuMultipleActions.collapseImmediately();
                } else {
                    Motivo();
                    solicitarPermiso();

                    menuMultipleActions.collapseImmediately();
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try{
            if(requestCode == 0) {
                if(resultCode == RESULT_OK){

                    Uri selectedImage = imageReturnedIntent.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                    imgMan.insertImage(getFileName(selectedImage), bitmap);

                    ImageRecyclerAdapter gal = (ImageRecyclerAdapter)mRecyclerView.getAdapter();
                    gal.Images().add(bitmap);
                    //gal.notifyItemInserted(gal.Images().size() - 1);
                    gal.notifyDataSetChanged();

                    //gallery.setAdapter(new GalleryAdapter(this, imgMan.getImages()));
                }
            }
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bundle extras = imageReturnedIntent.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                imgMan.insertImage(Calendar.getInstance().getTime() + ".png", imageBitmap);

                ImageRecyclerAdapter gal = (ImageRecyclerAdapter)mRecyclerView.getAdapter();
                gal.Images().add(imageBitmap);

                //gal.notifyItemInserted(gal.Images().size() - 1);
                gal.notifyDataSetChanged();
            }
        }catch(Exception ex) {   }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void Motivo(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

            alertDialogBasico();
        }
    }

    public void solicitarPermiso() {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 1);
    }

    public void alertDialogBasico() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No podra tomar fotos");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }


}
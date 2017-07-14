package com.krthx.cameraapplication.Adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.krthx.cameraapplication.R;

import java.util.List;

/**
 * Created by bryam on 13/07/17.
 */
public class GalleryAdapter extends BaseAdapter
{
    Context context;
    //Integer[] imagenes;
    List<Bitmap> images;
    int background;
    //guardamos las imágenes reescaladas para mejorar el rendimiento ya que estas operaciones son costosas
    //se usa SparseArray siguiendo la recomendación de Android Lint
    SparseArray<Bitmap> imagenesEscaladas = new SparseArray<Bitmap>(7);

    public GalleryAdapter(Context context, List<Bitmap> _images)
    {
        super();
        this.images = _images;
        this.context = context;

        //establecemos un marco para las imágenes (estilo por defecto proporcionado)
        //por android y definido en /values/attr.xml
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.Gallery1);
        background = typedArray.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 1);
        typedArray.recycle();
    }

    @Override
    public int getCount()
    {
        return images.size();
    }

    public List<Bitmap> Images() {
        return this.images;
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imagen = new ImageView(context);

        //reescalamos la imagen para evitar "java.lang.OutOfMemory" en el caso de imágenes de gran resolución
        //como es este ejemplo
        if (imagenesEscaladas.get(position) == null)
        {
            //Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(context.getResources(), images[position], 120, 0);
            Bitmap bitmap = images.get(position);
            imagenesEscaladas.put(position, bitmap);
        }
        imagen.setImageBitmap(imagenesEscaladas.get(position));

        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(80, 80);
        imagen.setLayoutParams(parms);

        /*imagen.getLayoutParams().height = 80;
        imagen.getLayoutParams().width = 70;*/
        //se aplica el estilo
        imagen.setBackgroundResource(background);

        return imagen;
    }

}
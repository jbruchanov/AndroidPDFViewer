package com.scurab.android.pdf2jpg;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.artifex.mupdf.MuPDFCore;

/**
 * Created by jbruchanov on 02/04/2014.
 */
public class PDFViewPagerAdapter extends RecyclePagerAdapter<ImageView> {

    private Context mContext;

    private MuPDFCore mPDFCore;

    public PDFViewPagerAdapter(Context context, MuPDFCore pdfCore) {
        mContext = context;
        mPDFCore = pdfCore;
    }

    @Override
    public ImageView getView(int position, ImageView convertView, View container) {
        if (convertView == null) {
            convertView = new TouchImageView(mContext);
        }

        Bitmap b = PDFConvertHelper.convert(mPDFCore, position);
        convertView.setImageBitmap(b);
        return convertView;
    }

    @Override
    public int getCount() {
        return mPDFCore != null ? mPDFCore.countPages() : 0;
    }
}
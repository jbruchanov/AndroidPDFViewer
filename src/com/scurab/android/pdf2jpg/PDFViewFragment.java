package com.scurab.android.pdf2jpg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.artifex.mupdf.MuPDFCore;

/**
 * Created by jbruchanov on 02/04/2014.
 */
public class PDFViewFragment extends Fragment {

    static final String FILE_NAME = "FILE_NAME";

    private MuPDFCore mMuPDFCore;

    private ViewPager mViewPager;

    private PDFViewPagerAdapter mAdapter;

    public static PDFViewFragment newInstance(String fileName) {
        Bundle b = new Bundle();
        b.putString(FILE_NAME, fileName);
        PDFViewFragment f = new PDFViewFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (mViewPager = new ViewPager(inflater.getContext()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPDF();
    }

    private void initPDF() {
        String file = getFileName();
        try {
            mMuPDFCore = new MuPDFCore(file);
            mAdapter = new PDFViewPagerAdapter(getActivity(), mMuPDFCore);
            mViewPager.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), String.format("Unable to open PDFFile:'%s'", file), Toast.LENGTH_LONG).show();
        }
    }

    protected String getFileName() {
        return getArguments().getString(FILE_NAME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMuPDFCore != null) {
            mMuPDFCore.onDestroy();
            mMuPDFCore = null;
        }
    }
}

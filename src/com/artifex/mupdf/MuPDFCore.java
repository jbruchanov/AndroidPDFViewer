package com.artifex.mupdf;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * This is wrapper for native library.
 */
public class MuPDFCore {
    /* load our native library */
    static {
        try {
            System.loadLibrary("mupdf");
        } catch (Throwable e) {
            throw new RuntimeException("Unable to load libs/{platform}/libmupdf.so", e);
        }
    }

    private static final String TAG = "MuPDFCore";

    /* Readable members */
    private int numPages = -1;
    private int mDisplayPages = 1;
    public float mPageWidth;
    public float mPageHeight;
    private String mFileName;

    private long globals;
    private int mCurrentPage;

    //region native methods
    private native long openFile(String filename);

    private native long openBuffer();

    private native int countPagesInternal();

    private native void gotoPageInternal(int localActionPageNum);

    private native float getPageWidth();

    private native float getPageHeight();

    private native void drawPage(Bitmap bitmap,
                                 int pageW, int pageH,
                                 int patchX, int patchY,
                                 int patchW, int patchH);

    private native void updatePageInternal(Bitmap bitmap,
                                           int page,
                                           int pageW, int pageH,
                                           int patchX, int patchY,
                                           int patchW, int patchH);

    private native RectF[] searchPage(String text);

    private native int passClickEventInternal(int page, float x, float y);

    private native void setFocusedWidgetChoiceSelectedInternal(String[] selected);

    private native String[] getFocusedWidgetChoiceSelected();

    private native String[] getFocusedWidgetChoiceOptions();

    private native int setFocusedWidgetTextInternal(String text);

    private native String getFocusedWidgetTextInternal();

    private native int getFocusedWidgetTypeInternal();

    private native LinkInfo[] getPageLinksInternal(int page);

    private native RectF[] getWidgetAreasInternal(int page);

    private native OutlineItem[] getOutlineInternal();

    private native boolean hasOutlineInternal();

    private native boolean needsPasswordInternal();

    private native boolean authenticatePasswordInternal(String password);

    private native MuPDFAlertInternal waitForAlertInternal();

    private native void replyToAlertInternal(MuPDFAlertInternal alert);

    private native void startAlertsInternal();

    private native void stopAlertsInternal();

    private native void destroying();

    private native boolean hasChangesInternal();

    private native void saveInternal();
    //endregion

    /**
     * @param filename
     * @throws IllegalStateException when core can't load your file
     */
    public MuPDFCore(String filename) throws IllegalStateException {
        mFileName = filename;
        globals = openFile(filename);
        if (globals == 0) {
            throw new IllegalStateException("Failed to open " + filename);
        }
    }

    /**
     * Return opened file name
     *
     * @return
     */
    public String getFileName() {
        return mFileName;
    }

    public int countPages() {
        if (numPages < 0) {
            numPages = countPagesSynchronized();
        }
        if (mDisplayPages == 1) {
            return numPages;
        }
        if (numPages % 2 == 0) {
            return numPages / 2 + 1;
        }
        int toReturn = numPages / 2;
        return toReturn + 1;
    }

    private synchronized int countPagesSynchronized() {
        return countPagesInternal();
    }

    public void gotoPage(int page) {
        if (page > numPages - 1) {
            page = numPages - 1;
        } else if (page < 0) {
            page = 0;
        }
        gotoPageInternal(page);
        mCurrentPage = page;
        mPageWidth = getPageWidth();
        mPageHeight = getPageHeight();
    }

    public synchronized PointF getPageSize(int page) {
        // If we have only one page (portrait), or if is the first or the last page, we show only one page (centered).
        if (mDisplayPages == 1 || page == 0 || (mDisplayPages == 2 && page == numPages / 2)) {
            gotoPage(page);
            return new PointF(mPageWidth, mPageHeight);
        } else {
            gotoPage(page);
            if (page == numPages - 1 || page == 0) {
                // last page
                return new PointF(mPageWidth * 2, mPageHeight);
            }
            float leftWidth = mPageWidth;
            float leftHeight = mPageHeight;
            gotoPage(page + 1);
            float screenWidth = leftWidth + mPageWidth;
            float screenHeight = Math.max(leftHeight, mPageHeight);
            return new PointF(screenWidth, screenHeight);
        }
    }

    /**
     * Release any handlers.
     * Don't forget to call it
     */
    public synchronized void onDestroy() {
        destroying();
        globals = 0;
    }

    public synchronized PointF getSinglePageSize(int page) {
        gotoPage(page);
        return new PointF(mPageWidth, mPageHeight);
    }

    public synchronized void drawPageSynchronized(int page, Bitmap bitmap, int pageW,
                                                  int pageH, int patchX, int patchY, int patchW, int patchH) {
        gotoPage(page);
        drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
    }

    /**
     * Draw single page into bitmap<br/>
     * To get page size {@link #getSinglePageSize(int)}
     *
     * @param page   page to draw
     * @param bitmap bitmap to draw in
     * @param pageW  page width
     * @param pageH  page height
     */
    public synchronized void drawSinglePage(int page, Bitmap bitmap, int pageW, int pageH) {
        drawPageSynchronized(page, bitmap, pageW, pageH, 0, 0, pageW, pageH);
    }

    //not tested
    public synchronized Bitmap drawPage(final int page,
                                        int pageW, int pageH,
                                        int patchX, int patchY,
                                        int patchW, int patchH) {

        Canvas canvas = null;
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(patchW, patchH, Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT);
            Log.d(TAG, "drawPage " + page);

            Log.d(TAG, "canvas: " + canvas);
            // If we have only one page (portrait), or if is the first, we show only one page (centered).
            if (mDisplayPages == 1 || page == 0) {
                gotoPage(page);
                drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
                return bitmap;
                // If we are on two pages mode (landscape), and at the last page, we show only one page (centered).
            } else if (mDisplayPages == 2 && page == numPages / 2) {
                gotoPage(page * 2 + 1); // need to multiply per 2, because page counting is being divided by 2 (landscape mode)
                drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
                return bitmap;
            } else {
                final int drawPage = (page == 0) ? 0 : page * 2 - 1;
                int leftPageW = pageW / 2;
                int rightPageW = pageW - leftPageW;

                // If patch overlaps both bitmaps (left and right) - return the
                // width of overlapping left bitpam part of the patch
                // or return full patch width if it's fully inside left bitmap
                int leftBmWidth = Math.min(leftPageW, leftPageW - patchX);

                // set left Bitmap width to zero if patch is fully overlay right
                // Bitmap
                leftBmWidth = (leftBmWidth < 0) ? 0 : leftBmWidth;

                // set the right part of the patch width, as a rest of the patch
                int rightBmWidth = patchW - leftBmWidth;

                if (drawPage == numPages - 1) {
                    // draw only left page
                    canvas.drawColor(Color.BLACK);
                    if (leftBmWidth > 0) {
                        Bitmap leftBm = Bitmap.createBitmap(leftBmWidth, patchH,
                                getBitmapConfig());
                        gotoPage(drawPage);
                        drawPage(leftBm, leftPageW, pageH,
                                (leftBmWidth == 0) ? patchX - leftPageW : 0,
                                patchY, leftBmWidth, patchH);
                        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                        canvas.drawBitmap(leftBm, 0, 0, paint);
                        leftBm.recycle();
                    }
                } else if (drawPage == 0) {
                    // draw only right page
                    canvas.drawColor(Color.BLACK);
                    if (rightBmWidth > 0) {
                        Bitmap rightBm = Bitmap.createBitmap(rightBmWidth, patchH,
                                getBitmapConfig());
                        gotoPage(drawPage);
                        drawPage(rightBm, rightPageW, pageH,
                                (leftBmWidth == 0) ? patchX - leftPageW : 0,
                                patchY, rightBmWidth, patchH);
                        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                        canvas.drawBitmap(rightBm, leftBmWidth, 0, paint);
                        rightBm.recycle();
                    }
                } else {
                    // Need to draw two pages one by one: left and right
                    Log.d("bitmap width", "" + bitmap.getWidth());
//					canvas.drawColor(Color.BLACK);
                    Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                    if (leftBmWidth > 0) {
                        Bitmap leftBm = Bitmap.createBitmap(leftBmWidth,
                                patchH, getBitmapConfig());
                        gotoPage(drawPage);
                        drawPage(leftBm, leftPageW, pageH, patchX, patchY,
                                leftBmWidth, patchH);
                        canvas.drawBitmap(leftBm, 0, 0, paint);
                        leftBm.recycle();
                    }
                    if (rightBmWidth > 0) {
                        Bitmap rightBm = Bitmap.createBitmap(rightBmWidth,
                                patchH, getBitmapConfig());
                        gotoPage(drawPage + 1);
                        drawPage(rightBm, rightPageW, pageH,
                                (leftBmWidth == 0) ? patchX - leftPageW : 0,
                                patchY, rightBmWidth, patchH);

                        canvas.drawBitmap(rightBm, (float) leftBmWidth, 0,
                                paint);
                        rightBm.recycle();
                    }

                }
                return bitmap;
            }
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "draw page " + page + "failed", e);
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT);
            }
            return bitmap;
        }
    }

    //not tested
    public synchronized LinkInfo[] getPageLinks(int page) {
        if (mDisplayPages == 1) {
            return getPageLinksInternal(page);
        }
        LinkInfo[] leftPageLinkInfo = new LinkInfo[0];
        LinkInfo[] rightPageLinkInfo = new LinkInfo[0];
        LinkInfo[] combinedLinkInfo;
        int combinedSize = 0;
        int rightPage = page * 2;
        int leftPage = rightPage - 1;
        int count = countPages() * 2;
        if (leftPage > 0) {
            LinkInfo[] leftPageLinkInfoInternal = getPageLinksInternal(leftPage);
            if (null != leftPageLinkInfoInternal) {
                leftPageLinkInfo = leftPageLinkInfoInternal;
                combinedSize += leftPageLinkInfo.length;
            }
        }
        if (rightPage < count) {
            LinkInfo[] rightPageLinkInfoInternal = getPageLinksInternal(rightPage);
            if (null != rightPageLinkInfoInternal) {
                rightPageLinkInfo = rightPageLinkInfoInternal;
                combinedSize += rightPageLinkInfo.length;
            }
        }

        combinedLinkInfo = new LinkInfo[combinedSize];
        for (int i = 0; i < leftPageLinkInfo.length; i++) {
            combinedLinkInfo[i] = leftPageLinkInfo[i];
        }

        LinkInfo temp;
        for (int i = 0, j = leftPageLinkInfo.length; i < rightPageLinkInfo.length; i++, j++) {
            temp = rightPageLinkInfo[i];
            temp.rect.left += mPageWidth;
            temp.rect.right += mPageWidth;
            combinedLinkInfo[j] = temp;
        }
        for (LinkInfo linkInfo : combinedLinkInfo) {
            if (linkInfo instanceof LinkInfoExternal) {
                Log.d(TAG, "return " + ((LinkInfoExternal) linkInfo).url);
            }
        }
        return combinedLinkInfo;
    }

    public synchronized RectF[] searchPage(int page, String text) {
        gotoPage(page);
        return searchPage(text);
    }

    public synchronized boolean hasOutline() {
        return hasOutlineInternal();
    }

    public synchronized OutlineItem[] getOutline() {
        return getOutlineInternal();
    }

    public synchronized boolean needsPassword() {
        return needsPasswordInternal();
    }

    public synchronized boolean authenticatePassword(String password) {
        return authenticatePasswordInternal(password);
    }

    /**
     * Get pages in your document
     *
     * @return
     */
    public int getPages() {
        return countPagesInternal();
    }

    public Config getBitmapConfig() {
        return Config.ARGB_8888;
    }
}

AndroidPDFViewer
================

This is simple example project how can you convert PDF file into images and use it like a very simple viewer.
I wouldn't beleive how crazily bad is "free" showing PDF on Android :).

So after 1 day of testing different projects with a lot of memory leaks, TODOs, FIXMEs in a code, 
I found out it's not simple task at all.

This project uses [MuPDF](http://mupdf.com) library for PDF stuff.

I took few classes from in com.artifex.mupdf packages from some other project (can't find it now).
I also used a very nice android control [TouchImageView](https://github.com/MikeOrtiz/TouchImageView), 
I little updated it for usage inside ViewPager, so author(s) hopefully won't protest :).


-----------------
#### Download
https://github.com/jbruchanov/AndroidPDFViewer/raw/master/apk/PDF2JPG.apk

![alt tag](http://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=https://github.com/jbruchanov/AndroidPDFViewer/raw/master/apk/PDF2JPG.apk&chld=H|0)


-----------------
#### How to use it:
For converting PDF page into JPG just use this
```java
String file = "/sdcard/example.pdf"; //our PDF file
MuPDFCore core = new MuPDFCore(file); //initiate core
Bitmap b = PDFConvertHelper.convert(core, 0); //save 1st page into bitmap
PDFConvertHelper.convert(core, 0); //save 1st page into bitmap
PDFConvertHelper.saveBitmap(b,"/sdcard/example_page1.jpg"); //save bitmap into jpeg
core.onDestroy();//don't forget to release any handlers related to core
```

For android pdf viewer use this inside a android.support.v4.app.FragmentActivity (support library)
```java
FragmentManager fm = getSupportFragmentManager();
String file = "/sdcard/example.pdf"
Fragment f = PDFViewFragment.newInstance(file);//just instantiate PDFViewFragment with argument

//open fragment, R.id.fragment_container is just view container, check activity_main.xml
fm.beginTransaction().add(R.id.fragment_container, f).commit();
```

-------------------
#### Result:
So it's super simple, it works, it doesn't have memory leaks (i did just small memory allocation tests and looks fine).
It doesn't have messy code coupled inside Activity, it's super simple Fragment...

It doesn't support any features like text selections, link clicking etc because it's converted into image. But this is what i needed basically, **simple** & **functional** PDF Viewer.

-------------------
#### Little math at the end:
Using Bitmaps is not much memory friendly. My example is just simple one. ViewPager needs up to 4 pages keep in memory. 
=> 150dpi A4 page has 1240 x 1754. And if we have 4bytes per pixel it means that one Bitmap in memory takes ~8.3 [MiB](http://en.wikipedia.org/wiki/Mebibyte). So having 4 images in memory means allocate ~33[MiB](http://en.wikipedia.org/wiki/Mebibyte) what is basically Heap limit for the oldest devices. I tested it on 
Samsung Galaxy Ace, which is crap and it worked (slowly, but worked :D). So be aware of using it with bigger-DPI documents or check in detail MuPDFCore#drawPage(int, int, int, int , int, int, int) (i didn't test it)...
                                        
-------------------
So do whatever you need to do :). 
Don't blame me if it doesn't work i just took few pieces of existing code and put them together, because no one probably did it in some nice and simple way yet.

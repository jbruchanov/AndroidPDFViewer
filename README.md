AndroidPDFViewer
================

This is simple example project how can you convert PDF file into images and use it like a very simple viewer.
I wouldn't beleive how crazily bad is "free" showing PDF on Android :).

So after 1 day of testing different projects with a lot of memory leaks, TODOs, FIXMEs in a code, 
I found out it's not simple task at all.

This project uses [MuPDF](http://mupdf.com) library for PDF stuff.

I used a very nice android control [TouchImageView](https://github.com/MikeOrtiz/TouchImageView) which, 
I little updated for usage inside ViewPager, so author(s) hopefully won't protest :).


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


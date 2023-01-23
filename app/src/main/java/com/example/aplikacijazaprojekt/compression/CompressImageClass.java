package com.example.aplikacijazaprojekt.compression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CompressImageClass {


    public static void compressImage(Bitmap bitmap){

        //potrebujemo height in width;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        //first we need to fill image with zeroes

        fillImageWithZeroes(height, width);


    }


    public static void fillImageWithZeroes(int height, int width) {
        int divider = 0;

        if (height > width) {
            divider = (width % 8) + width;
        } else {
            divider = (height % 8) + height;
        }



    }

}

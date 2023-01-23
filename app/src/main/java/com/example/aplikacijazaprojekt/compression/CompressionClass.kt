package com.example.aplikacijazaprojekt.compression

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import java.io.Console
import java.lang.Integer.max


class CompressionClass {
    companion object {
        //compress image using DCT + RLE

        class COLOR {
            var R: Int = 0
            var G: Int = 0
            var B: Int = 0
            fun COLOR() {
                R = 0
                G = 0
                B = 0
            }
        }

        /*
        private static COLOR[,] colorPallet;
        private static COLOR[,] collorPalletFdct;
        private static COLOR[] collorPallet1D;
    */
        private var T = 0;
        private var colorPallet: Array<Array<COLOR>> = arrayOf(arrayOf(COLOR()))
        private var collorPalletFdct: Array<Array<COLOR>> = arrayOf(arrayOf(COLOR()))
        private var collorPallet1D: Array<COLOR> = arrayOf(COLOR())
        private var zacetekArraya=0
        private var imageWidthOrHeight=0;
        lateinit var resizedImage: Bitmap

        fun compressImage(image: Bitmap): ByteArray {

            resizedImage = Bitmap.createScaledBitmap(
                image, (image.width / 5.0).toInt(), (image.height / 5.0).toInt(), false
            )

            resizedImage = fillImageWithZeros2(resizedImage)

            val height = resizedImage.height
            val width = resizedImage.width
            imageWidthOrHeight=height

            //subtract 128
            subtract128(resizedImage, height, width)
            //function for compresion
            FDCT(resizedImage);

            var byteArray = convertToBytes();

            return byteArray;
        }

        private fun subtract128(filledImage: Bitmap, height: Int, width: Int) {
            colorPallet = Array(filledImage.width) { Array(filledImage.width) { COLOR() } }
            for(i in 0 until filledImage.width){
                for (j in 0 until filledImage.height){
                    var color = filledImage.getPixel(i,j)
                    var a = COLOR();
                    a.G = Color.green(color) - 128
                    a.R = Color.red(color) - 128
                    a.B = Color.blue(color) - 128
                    colorPallet[i][j] = a;
                }
            }

        }

        private fun fillImageWithZeros2(image: Bitmap): Bitmap {
            val width = image.width
            val height = image.height
            val divider = (max(width,height) / 8) * 8
            val filledImage = Bitmap.createBitmap(divider, divider, Bitmap.Config.ARGB_8888)
            Canvas(filledImage).drawColor(Color.BLACK)
            Canvas(filledImage).drawBitmap(image, 0f, 0f, null)
            // Log.println(Log.DEBUG, "fillImageWithZeros2", "width: $width, height: $height, divider: $divider, filledImage.width: ${filledImage.width}, filledImage.height: ${filledImage.height}")
            return filledImage
        }


        private fun FDCT(image: Bitmap) {
            // Initialize 2D array to store the FDCT transformed image
            // Initialize 1D array to store the FDCT transformed image in a 1D array
            collorPalletFdct = Array(image.width) { Array(image.height) { COLOR() } }
            collorPallet1D = Array(image.width * image.height) { COLOR() }

            // Loop through the image 8x8 blocks
            for (start in 0 until image.width step 8) {
                for (end in 0 until image.width step 8) {
                    // Loop through each element within the 8x8 block
                    for (i in 0 until 8) {
                        for (j in 0 until 8) {
                            // Initialize cu and cv values
                            var cu: Double
                            var cv: Double
                            // Initialize sums for R, G, and B channels
                            var sumR = 0.0
                            var sumG = 0.0
                            var sumB = 0.0

                            // Calculate cu value
                            if (i == 0)
                                cu = 1.0 / Math.sqrt(2.0)
                            else
                                cu = 1.0
                            // Calculate cv value
                            if (j == 0)
                                cv = 1.0 / Math.sqrt(2.0)
                            else
                                cv = 1.0

                            // Loop through each element within the 8x8 block again
                            // and calculate the FDCT for each channel
                            for (x in 0 until 8) {
                                for (y in 0 until 8) {
                                    sumR += colorPallet[x + start][y + end].R * Math.cos((2 * x + 1) * i * Math.PI / 16) * Math.cos(
                                        (2 * y + 1) * j * Math.PI / 16
                                    )
                                    sumG += colorPallet[x + start][y + end].G * Math.cos((2 * x + 1) * i * Math.PI / 16) * Math.cos(
                                        (2 * y + 1) * j * Math.PI / 16
                                    )
                                    sumB += colorPallet[x + start][y + end].B * Math.cos((2 * x + 1) * i * Math.PI / 16) * Math.cos(
                                        (2 * y + 1) * j * Math.PI / 16
                                    )
                                }
                            }
                            // Create a temporary COLOR object to store the FDCT transformed value for each channel
                            val tempC = COLOR()
                            tempC.R = (0.25 * cu * cv * sumR).toInt()
                            tempC.G = (0.25 * cu * cv * sumG).toInt()
                            tempC.B = (0.25 * cu * cv * sumB).toInt()

                            // Store the FDCT transformed value in the 2D and 1D arrays
                            collorPalletFdct[i + start][j + end] = tempC
                            //System.out.println("Sem tukaj kjer se fila collorPalletFdct");
                        }
                    }
                    // Call the crisCross function and pass in the image width,
                    // the start and end indices for the current 8x8 block, and the size parameters
                    crisCross(image.width, start, end)
                }
            }

        }

        fun shrani(x:Int,y:Int,prviX:Int,prviY: Int,zacetekArrayaObKlicu:Int){
            if(x>=0&&x<8&&y>=0&&y<8){
                if(zacetekArraya-zacetekArrayaObKlicu<64-T){
                    collorPallet1D[zacetekArraya]=collorPalletFdct[y+prviX][x+prviY]
                }
                //drugace napisemo oznacevalno vrednost 1000, ki pomeni da smo ta pixel porezali zaradi kompresije
                else{
                    val tempC = COLOR()
                    tempC.R = 1000
                    tempC.G = 1000
                    tempC.B = 1000

                    collorPallet1D[zacetekArraya]=tempC
                }

                zacetekArraya++;
            }
        }

        fun crisCross(width: Int, prviX: Int, prviY: Int) {
            var zacetekArrayaObKlicu = zacetekArraya;

            var x=-7
            var y=7

            var gor=false //gor=true -> povecuj y

            while(!(x==7 && y==7)){
                if(gor){
                    x--;
                    y++;

                    shrani(x,y,prviX,prviY,zacetekArrayaObKlicu);

                    if(y==7){
                        x++;
                        gor=!gor;

                        shrani(x,y,prviX,prviY,zacetekArrayaObKlicu);
                    }
                }
                else{
                    x++;
                    y--;

                    shrani(x,y,prviX,prviY,zacetekArrayaObKlicu);

                    if(y==0){
                        x++;
                        gor=!gor

                        shrani(x,y,prviX,prviY,zacetekArrayaObKlicu);
                    }
                }
            }
        }

        fun convertToBytes(): ByteArray {
            val byteArray = ByteArray((collorPallet1D.size - (collorPallet1D.size/64)*T)*3)

            var byteArrayIndex=0
            for (i in collorPallet1D.indices) {
                if(collorPallet1D[i].R!=1000) { //1000 oznacuje izbrisan pixel ki se ga ne prepise
                    byteArray[byteArrayIndex] = collorPallet1D[i].R.toByte()
                    byteArray[byteArrayIndex+1] = collorPallet1D[i].G.toByte()
                    byteArray[byteArrayIndex+2] = collorPallet1D[i].B.toByte()

                    byteArrayIndex+=3
                }
            }

            return byteArray
        }
    }

}


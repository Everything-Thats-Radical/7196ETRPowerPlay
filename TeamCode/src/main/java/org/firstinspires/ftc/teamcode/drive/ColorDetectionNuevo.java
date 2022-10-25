package org.firstinspires.ftc.teamcode.drive;
//import java.awt.image.*;
import java.util.*;
import java.io.*;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.*;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class ColorDetectionNuevo {



    // An open letter to the reader of this code:

    // You may notice that there are many spelling mistakes in this document,
    // and that many of those mistakes were copied and pasted several times,
    // making them even more pronounced and obvious. It is to my great
    // disappointment that I inform you that it was indeed the great
    // Rocco John D'Antico, in the 2021-2022 Freight Frenzy FTC season,
    // who misspelled everything here. I neither have the time nor the mental
    // strength necessary to correct whatever he managed to mess up while
    // the fact that he was messing up was highlighted before his very eyes
    // by JetBrains' very convenient but apparently unused grammar checking
    // tools in Android Studio.
    //
    // Instead, I compose this poem to commemorate Rocco for the great feats he
    // accomplished with his great coding ability, but to also add nuance to that
    // praise with the confusion and despair his team was left with once they opened
    // his code the following season:

    /*
    My fierce Rocco, you inspire me to write.
    How we love the way you code and misspell,
    Invading our minds day and through the night,
    Always dreaming about the mademoiselle.

    Let us compare you to a bold cherry?
    You are more mighty, hard-working and brave.
    Ice bites the debris of February,
    And wintertime has the amazing rave.

    How do we love you? Let us count the ways.
    We love your bad at spelling bravery.
    Wanting your handsomeness fills my days.
    Our love for you is the old slavery.

    Now we must away with a chemic heart,
    Remember our grave words whilst we're apart.
    */


    ArrayList<Integer> redXVaules = new ArrayList<Integer>();
    ArrayList<Integer> redYVaules = new ArrayList<Integer>();
    ArrayList<Integer> greenXVaules = new ArrayList<Integer>();
    ArrayList<Integer> greenYVaules = new ArrayList<Integer>();
    ArrayList<Integer> blueXVaules = new ArrayList<Integer>();
    ArrayList<Integer> blueYVaules = new ArrayList<Integer>();

    public String telemetryString = "";

    int graysConstant = 35; //We want to ignore all gray colors (the backround feild). This variable represents how close we decide the color vaules have to be to be considered gray.
    int scanEvery = 20; //We dont need to look at every single pixel. This is how many pixels to
    // skip after looking a pixel. Lower number should slow the calculations down, but make it more accurate.


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getLocation (Bitmap bmp) {

        //bmp = Bitmap.createBitmap(bmp, 0, 400, bmp.getWidth(), bmp.getHeight()-400 );

        //Skipping 'scanEvery' amout of pixels, go through every pixle in the photo
        for (int x = 0; x + scanEvery <= bmp.getWidth()-scanEvery; x += scanEvery) {
            for (int y = 0; y + scanEvery <= bmp.getHeight()-scanEvery; y += scanEvery) {

                //Get the RGB vaules of the individual pixel. I found this online and it works very well, but I can't say I know how. https://stackoverflow.com/questions/22391353/get-color-of-each-pixel-of-an-image-using-bufferedimages
                int rgb = bmp.getPixel(x, y);

                int red =   (rgb & 0x00ff0000) >> 16;
                int green = (rgb & 0x0000ff00) >> 8;
                int blue =   rgb & 0x000000ff;

                //If the RGB color vaules are about the same, the color is a shade of gray, white, or black and is not what we are looking for.
                if ( Math.abs(red-green) > graysConstant || Math.abs(red-blue) > graysConstant || Math.abs(green-blue) > graysConstant ) {

                    //If red is the most prominent color, save the position of this pixel to the lists of red pixels.
                    if (red>green && red>blue) {
                        redXVaules.add(x);
                        redYVaules.add(y);
                    }
                    //If green is the most prominet color, save the position of this pixel to the lists of green pixels.
                    else if (green>red && green>blue) {
                        greenXVaules.add(x);
                        greenYVaules.add(y);
                    }
                    //If blue is the most prominet color, save the position of this pixel to the lists of blue pixels.
                    else if (blue>red && blue>green) {
                        blueXVaules.add(x);
                        blueYVaules.add(y);
                    }
                }
            }
        }



        //Find the averge position of every color. The dots/object are organized horzontaily, so we only need to look at the x vaules.
        double redCenterX = center( redXVaules, bmp.getWidth() );
        double greenCenterX = center( greenXVaules, bmp.getWidth() );
        double blueCenterX = center( blueXVaules, bmp.getWidth() );

        int centerMarginOfError = 100; //When deciding if an object is in the center, we'll add a
        // little margin or error


        //If the average location of the object (the green pixels) is exactly in the center, there was no actual object found
        if (Math.abs(greenCenterX-blueCenterX) < 3) {
            return "No Object";
        }
        //If the average location of the object (the green pixels) is about the same as the average location of the dots (the red pixels), the object is in the center
        else if ( Math.abs(redCenterX-greenCenterX) < centerMarginOfError) {
            //telemetryString = "Center";
            return "Center";
        }
        //If the average location of the object (the green pixels) is to the left of the average location of the dots (the red pixels), the object is to the left
        else if ( greenCenterX < redCenterX ) {
            return "Left";
        }
        //Else, the average location of the object (the green pixels) is to the right of the average location of the dots (the red pixels), the object is to the right
        else { //if ( redCenterX < greenCenterX )
            return "Right";
        }


    }// end of GetLocation method

    static double center( ArrayList<Integer> array, int max ) {
        //This just find the averge value of an array, as long as there is data inside of it
        if( array.size() > 0 ) {
            double total = 0;
            double count = 0;
            for (int n = 0; n < array.size(); n++) {
                total += array.get(n);
                count += 1;
            }
            double result = total / count;
            return result ;
        }
        //If there is nothing in the array, say average position is the center of the photo
        else {
            double result = max / 2;
            return result ;
        }
    }
}

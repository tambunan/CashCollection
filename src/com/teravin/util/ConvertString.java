package com.teravin.util;

/**
 * Created by dumatambunan on 2/21/14.
 */

import android.util.Log;

import com.teravin.util.StringUtil;


public class ConvertString  {

    public final static byte LANGUAGE_ENGLISH = 0;
    public final static byte LANGUAGE_CHINESE = 48;
    public final static byte B_ESC = (byte) 0x1B;
    public static final byte FONT_24PX = (byte)0x01;
    public static final byte FONT_32PX = (byte)0x00;
    public static final byte Align_LEFT = (byte)0x30;
    public static final byte Align_CENTER = (byte)0x31;
    public static final byte Align_RIGHT = (byte)0x32;


    public static byte[] convertPrintData(String str, int offset, int length, byte languageSet, byte fontSet,byte align,byte linespace)
    {
        byte[] buffer = null;
        if (languageSet == LANGUAGE_CHINESE)
        {
            buffer = char2ByteUTF8(str, offset, length);
        } else {
            buffer = new byte[length];
            System.arraycopy(str.getBytes(), offset, buffer, 0, length);
        }

        byte[] lang = null;
//    if (languageSet != LANGUAGE_ENGLISH)
//    {
        lang = new byte[]{B_ESC, (byte)0x4B, (byte)0x31,B_ESC, (byte)0x52, languageSet};
//    }
        byte[] font = null;
        byte[] fontalign = null;
        byte[] fontlinespace = null;
        //  if (fontSet != FONT_DEFAULT)
        // {

        font = new byte[]{B_ESC, (byte)0x21, fontSet};
        fontalign = new byte[]{B_ESC,(byte)0x61,align};
        fontlinespace = new byte[]{B_ESC,(byte)0x33,linespace};

        //}
        byte[] formate = concatByteArray(lang, font, fontalign, fontlinespace);
        Log.i("formate value = ", StringUtil.toHexString(formate));
        return concatByteArray(formate, buffer);
    }

    private static byte[] char2ByteUTF8(String input, int offset, int length)
    {
        byte[] output = new byte[length * 3];

        int i = offset;
        int inEnd = offset + length;
        int outLength = 0;

        char inputChar;

        while (i < inEnd) {
            inputChar = input.charAt(i);
            if (inputChar < 0x80) {
                output[outLength++] = (byte) inputChar;
            } else if (inputChar < 0x800) {
                output[outLength++] = (byte) (0xc0 | ((inputChar >> 6) & 0x1f));
                output[outLength++] = (byte) (0x80 | (inputChar & 0x3f));
            } else {
                output[outLength++] = (byte) (0xe0 | ((inputChar >> 12)) & 0x0f);
                output[outLength++] = (byte) (0x80 | ((inputChar >> 6) & 0x3f));
                output[outLength++] = (byte) (0x80 | (inputChar & 0x3f));
            }
            i ++;
        }
        byte[] ret=new byte[outLength];
        System.arraycopy(output, 0, ret, 0, outLength);
        return ret;
    }

    private static byte[] concatByteArray(byte[] a, byte[] b)
    {
        if (a == null && b == null )
            return null;

        int aL = (a == null?0:a.length);
        int bL = (b ==null?0:b.length);

        if (bL == 0)
            return a;
        int len = aL + bL ;
        byte[] result = new byte[len];

        if (a != null)
            System.arraycopy(a, 0, result, 0, aL);
        if (b != null)
            System.arraycopy(b, 0, result, aL, bL);

        return result;
    }

    private static byte[] concatByteArray(byte[] a, byte[] b,byte c[], byte[] d)
    {



        if (a == null && b == null && c == null && d == null)
            return null;

        int aL = (a == null?0:a.length);
        int bL = (b ==null?0:b.length);
        int cL = (c == null?0:c.length);
        int dL = (d == null?0:d.length);


        Log.i("a length", aL + "");
        Log.i("b length", bL + "");
        Log.i("c length", cL + "");
        Log.i("d length", dL + "");

//    if (bL == 0)
//      return a;

        int len = aL + bL + cL + dL;
        byte[] result = new byte[len];

        Log.i("len",len+"");


        if (a != null)
            System.arraycopy(a, 0, result, 0, aL);
        if (b != null)
            System.arraycopy(b, 0, result, aL, bL);
        if(c != null)
            System.arraycopy(c, 0, result, aL+bL, cL);
        if(d != null)
            System.arraycopy(d, 0, result, aL+bL+cL, dL);

        Log.i("result ", StringUtil.toHexString(result));

        return result;
    }

}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.massky.sraum.Util;

import java.io.PrintStream;

public class MyUtil
{

    public MyUtil()
    {
    }

    public static String Bytes2HexString(byte abyte0[])
    {
        String s = "";
        for(int i = 0; i < abyte0.length; i++)
        {
            Object obj1 = Integer.toHexString(abyte0[i] & 0xff);
            Object obj = obj1;
            if(((String) (obj1)).length() == 1)
            {
                obj = new StringBuilder();
                ((StringBuilder) (obj)).append('0');
                ((StringBuilder) (obj)).append(((String) (obj1)));
                obj = ((StringBuilder) (obj)).toString();
            }
            obj1 = new StringBuilder();
            ((StringBuilder) (obj1)).append(s);
            ((StringBuilder) (obj1)).append(((String) (obj)).toUpperCase());
            s = ((StringBuilder) (obj1)).toString();
        }

        return s;
    }

    public static byte[] HexString2Bytes(String s)
    {
        byte abyte0[] = new byte[s.length() / 2];
        byte abyte1[] = s.getBytes();
        for(int i = 0; i < s.length() / 2; i++)
        {
            int j = i * 2;
            abyte0[i] = uniteBytes(abyte1[j], abyte1[j + 1]);
        }

        return abyte0;
    }

    public static void printHexString(byte abyte0[])
    {
        for(int i = 0; i < abyte0.length; i++)
        {
            Object obj1 = Integer.toHexString(abyte0[i] & 0xff);
            Object obj = obj1;
            if(((String) (obj1)).length() == 1)
            {
                obj = new StringBuilder();
                ((StringBuilder) (obj)).append('0');
                ((StringBuilder) (obj)).append(((String) (obj1)));
                obj = ((StringBuilder) (obj)).toString();
            }
            obj1 = System.out;
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("aaa");
            stringbuilder.append(((String) (obj)).toUpperCase());
            stringbuilder.append(" ");
            ((PrintStream) (obj1)).print(stringbuilder.toString());
        }

        System.out.println("");
    }

    public static byte uniteBytes(byte byte0, byte byte1)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("0x");
        stringbuilder.append(new String(new byte[] {
            byte0
        }));
        byte byte2 = (byte)(Byte.decode(stringbuilder.toString()).byteValue() << 4);
        stringbuilder = new StringBuilder();
        stringbuilder.append("0x");
        stringbuilder.append(new String(new byte[] {
            byte1
        }));
        return (byte)(byte2 ^ Byte.decode(stringbuilder.toString()).byteValue());
    }
}

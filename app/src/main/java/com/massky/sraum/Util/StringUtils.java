package com.massky.sraum.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     *正则
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean checkSame(char ac[])
    {
        int i = ac.length - 1;
        boolean flag;
        boolean flag1;
        for(flag = false; i > 0; flag = flag1)
        {
            flag1 = flag;
            if(i >= 1)
            {
                flag1 = flag;
                if(ac[i] != ac[i - 1])
                    flag1 = true;
            }
            i--;
        }

        return flag;
    }

    public static boolean checkWeakPwd(String s)
    {
        if(s == null)
            return true;
        char ac[] = s.toCharArray();
        if(!checkSame(ac))
            return false;
        if(ac.length % 2 == 0)
        {
            char ac1[] = new char[ac.length / 2];
            for(int i = 0; i < ac.length / 2; i++)
                ac1[i] = ac[i];

            char ac5[] = new char[ac.length / 2];
            for(int j = ac.length / 2; j < ac.length; j++)
                ac5[j - ac.length / 2] = ac[j];

            if(!checkSame(ac1) && !checkSame(ac5))
                return false;
        } else
        {
            char ac2[] = new char[ac.length / 2];
            for(int k = 0; k < ac.length / 2; k++)
                ac2[k] = ac[k];

            char ac6[] = new char[ac.length / 2 + 1];
            for(int l = ac.length / 2; l < ac.length; l++)
                ac6[l - ac.length / 2] = ac[l];

            if(!checkSame(ac2) && !checkSame(ac6))
                return false;
        }
        if(ac.length % 2 == 0)
        {
            char ac3[] = new char[ac.length / 2];
            char ac7[] = new char[ac.length / 2];
            int k1 = 0;
            int i2 = 0;
            int i3;
            for(int i1 = 0; k1 < ac.length; i1 = i3)
            {
                int k2;
                if(k1 % 2 == 0)
                {
                    k2 = i2;
                    i3 = i1;
                    if(i1 < ac.length / 2)
                    {
                        ac3[i1] = ac[k1];
                        i3 = i1 + 1;
                        k2 = i2;
                    }
                } else
                {
                    k2 = i2;
                    i3 = i1;
                    if(i2 < ac.length + 1)
                    {
                        ac7[i2] = ac[k1];
                        k2 = i2 + 1;
                        i3 = i1;
                    }
                }
                k1++;
                i2 = k2;
            }

            if(!checkSame(ac3) && !checkSame(ac7))
                return false;
        } else
        {
            char ac4[] = new char[ac.length / 2];
            char ac8[] = new char[ac.length / 2 + 1];
            int l1 = 0;
            int j2 = 0;
            int j3;
            for(int j1 = 0; l1 < ac.length; j1 = j3)
            {
                int l2;
                if(l1 % 2 == 0)
                {
                    l2 = j2;
                    j3 = j1;
                    if(j1 < ac.length / 2)
                    {
                        ac4[j1] = ac[l1];
                        j3 = j1 + 1;
                        l2 = j2;
                    }
                } else
                {
                    l2 = j2;
                    j3 = j1;
                    if(j2 < ac.length + 1)
                    {
                        ac8[j2] = ac[l1];
                        l2 = j2 + 1;
                        j3 = j1;
                    }
                }
                l1++;
                j2 = l2;
            }

            if(!checkSame(ac4) && !checkSame(ac8))
                return false;
        }
        return !s.equals("123456");
    }



    public static String decodeUnicode(String s)
    {
        int l = s.length();
        StringBuffer stringbuffer = new StringBuffer(l);
        for(int i = 0; i < l;)
        {
            int j = i + 1;
            char c = s.charAt(i);
            if(c == '\\')
            {
                i = j + 1;
                char c1 = s.charAt(j);
                if(c1 == 'u')
                {
                    j = i;
                    int k = 0;
                    i = 0;
                    while(k < 4)
                    {
                        char c2 = s.charAt(j);
                        switch(c2)
                        {
                            default:
                                switch(c2)
                                {
                                    default:
                                        switch(c2)
                                        {
                                            default:
                                                throw new IllegalArgumentException("Malformed      encoding.");

                                            case 97: // 'a'
                                            case 98: // 'b'
                                            case 99: // 'c'
                                            case 100: // 'd'
                                            case 101: // 'e'
                                            case 102: // 'f'
                                                i = ((i << 4) + 10 + c2) - 97;
                                                break;
                                        }
                                        break;

                                    case 65: // 'A'
                                    case 66: // 'B'
                                    case 67: // 'C'
                                    case 68: // 'D'
                                    case 69: // 'E'
                                    case 70: // 'F'
                                        i = ((i << 4) + 10 + c2) - 65;
                                        break;
                                }
                                break;

                            case 48: // '0'
                            case 49: // '1'
                            case 50: // '2'
                            case 51: // '3'
                            case 52: // '4'
                            case 53: // '5'
                            case 54: // '6'
                            case 55: // '7'
                            case 56: // '8'
                            case 57: // '9'
                                i = ((i << 4) + c2) - 48;
                                break;
                        }
                        k++;
                        j++;
                    }
                    stringbuffer.append((char)i);
                    i = j;
                } else
                {
                    if(c1 == 't')
                        c = '\t';
                    else
                    if(c1 == 'r')
                        c = '\r';
                    else
                    if(c1 == 'n')
                    {
                        c = '\n';
                    } else
                    {
                        c = c1;
                        if(c1 == 'f')
                            c = '\f';
                    }
                    stringbuffer.append(c);
                }
            } else
            {
                stringbuffer.append(c);
                i = j;
            }
        }

        return stringbuffer.toString();
    }



    public static boolean emailFormat(String s)
    {
        return s.matches("^([a-zA-Z0-9]+[_|\\-|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$");
    }



    public static String getUID(String s)
    {
        Object obj = new String();
        for(int i = 0; i < s.length();)
        {
            Object obj1 = obj;
            if(s.charAt(i) != '-')
            {
                obj1 = obj;
                if(s.charAt(i) != ' ')
                {
                    obj1 = new StringBuilder();
                    ((StringBuilder) (obj1)).append(((String) (obj)));
                    ((StringBuilder) (obj1)).append(s.charAt(i));
                    obj1 = ((StringBuilder) (obj1)).toString();
                }
            }
            i++;
            obj = obj1;
        }

        return ((String) (obj));
    }

    public static String hintEmail(String s)
    {
        if(s.indexOf("@") == -1)
        {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append(s.substring(0, 1));
            stringbuilder.append("****");
            return stringbuilder.toString();
        } else
        {
            StringBuilder stringbuilder1 = new StringBuilder();
            stringbuilder1.append(s.substring(0, 1));
            stringbuilder1.append("****");
            stringbuilder1.append(s.substring(s.indexOf("@"), s.length()));
            return stringbuilder1.toString();
        }
    }

    public static String hintPhone(String s)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(s.substring(0, 3));
        stringbuilder.append("****");
        stringbuilder.append(s.substring(8, s.length()));
        return stringbuilder.toString();
    }

    public static boolean isCheckPattern(String s)
    {
        return s.matches("^[A-Za-z0-9]+$");
    }

    public static boolean isContainsChinese(String s)
    {
        return pat.matcher(s).find();
    }

    public static boolean isEmpty(String s)
    {
        return s == null || "".equals(s) || "null".equals(s) || " ".equals(s);
    }

    public static boolean isEquals(String s, String s1)
    {
        return s.equals(s1);
    }

    public static boolean isIPCFormat(String s)
    {
        return s.matches("[8]{6}[0-9]{6}");
    }

    public static boolean isInteger(String s)
    {
        return Pattern.compile("^[-\\+]?[\\d]*$").matcher(s).matches();
    }

    public static boolean isMatchLongLength(String s)
    {
        return s.length() <= 50;
    }

    public static boolean isMatchShortLength(String s)
    {
        return s.length() >= 6;
    }

    public static boolean isPwdCorrect(String s)
    {
        return Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$").matcher(s).matches();
    }

    public static boolean isPwdLongLength(String s)
    {
        return s.length() <= 31;
    }

    public static boolean isPwdShortLength(String s)
    {
        return s.length() >= 8;
    }

    public static boolean isSpecialChar(String s)
    {
        String as[] = new String[14];
        as[0] = "!";
        as[1] = "@";
        as[2] = "#";
        as[3] = "$";
        as[4] = "%";
        as[5] = "^";
        as[6] = "&";
        as[7] = "*";
        as[8] = "(";
        as[9] = ")";
        as[10] = "<";
        as[11] = ">";
        as[12] = "/";
        as[13] = "\\";
        for(int i = 0; i < as.length; i++)
            if(s.contains(as[i]))
                return true;

        return false;
    }

    public static boolean isStringEmailCorrect(String s)
    {
        return Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$").matcher(s).matches();
    }

    public static boolean isStringFormatCorrect(String s)
    {
        return Pattern.compile("[a-zA-Z0-9_-_@_.__]+").matcher(s).matches();
    }



    public static String spitValue(String s, String s1)
    {
        String as[] = s.split(";");
        for(int i = 0; i < as.length; i++)
        {
            String s2 = as[i].trim();
            s = s2;
            if(s2.startsWith("var "))
                s = s2.substring(4, s2.length());
            if(s.startsWith(s1))
                return s.substring(s.indexOf("=") + 1);
        }

        return "-1";
    }

    public static boolean telFormat(String s)
    {
        return s.matches("[1]{1}[3,4,5,8]{1}[0-9]{9}");
    }

    public static boolean uidFormat(String s)
    {
        return s.matches("[a-zA-Z]{4}[0-9]{6}[a-zA-Z]{5}") || s.matches("[a-zA-Z]{4}[-]{1}[0-9]{6}[-]{1}[a-zA-Z]{5}");
    }

    public static boolean uidFormat2(String s)
    {
        return s.matches("[a-zA-Z]{4}[-]{1}[0-9]{6}[-]{1}[a-zA-Z]{5}");
    }

    private static Pattern pat;
    private static String regEx = "[\u4E00-\u9FA5]";

    static
    {
        pat = Pattern.compile(regEx);
    }


}

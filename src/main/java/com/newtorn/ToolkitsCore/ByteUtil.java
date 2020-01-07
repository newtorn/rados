package com.newtorn.ToolkitsCore;

/**
 * 字节操作工具类
 */
public final class ByteUtil {
    /**
     * 从字节数组指定的位置获取指定长度的字节数组
     * 
     * @param bs     源字节数组
     * @param start  起始位置
     * @param length 截取长度
     * @return
     */
    public static byte[] getByteArray(byte[] bs, int start, int length) {
        byte[] cut = new byte[length];
        for (int i = 0; i < length; i++) {
            cut[i] = bs[start + i];
        }
        return cut;
    }

    /**
     * 整型转换为n字节数组
     * 
     * @param intValue
     * @return
     */
    public static byte[] intToBytes(int intValue, int n) {
        byte[] bs = new byte[n];
        for (int i = 0; i < n; i++) {
            bs[i] = (byte) (intValue >> (8 * (n - 1 - i)) & 0xFF);
        }
        return bs;
    }

    /**
     * n字节数组转为整型
     * 
     * @param bs 要转换的字节数组
     * @return
     */
    public static int BytesToInt(byte[] bs) {
        int intValue = 0;
        for (int i = 0; i < bs.length; i++) {
            intValue += (bs[i] & 0xFF) << (8 * (bs.length - 1 - i));
        }
        return intValue;
    }

    /**
     * 断整型转为2字节数组
     * 
     * @param number 要转换的短整型数
     * @return
     */
    public static byte[] shortToTowBytes(short number) {
        int temp = number;
        byte[] bs = new byte[2];
        for (int i = 0; i < bs.length; i++) {
            bs[i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return bs;
    }

    /**
     * 2字节数组转换为短整型
     * 
     * @param bs 要转换的字节数组
     * @return
     */
    public static short twoBytesToShort(byte[] bs) {
        if (bs.length != 2) {
            throw new IllegalArgumentException("the length of byte array is not 2");
        }
        short s0 = (short) (bs[0] & 0xff);
        short s1 = (short) (bs[1] & 0xff);
        s1 <<= 8;
        return (short) (s0 | s1);
    }

    /**
     * n字节数组转换为字符串
     * 
     * @param bs 要转换的字节数组
     * @return
     */
    public static String BytesToString(byte[] bs) {
        int i;
        for (i = 0; i < bs.length; i++) {
            if (bs[i] == 0) {
                break;
            }
        }
        byte[] temp = new byte[i];
        for (int j = 0; j < i; j++) {
            temp[j] = bs[j];
        }
        return new String(temp);
    }

    /**
     * 字符串转换为n字节数组
     * 
     * @param str 要转换的字符串
     * @return
     */
    public static byte[] stringToBytes(String str, int n) {
        byte[] bs = new byte[n];
        int length = str.length();
        if (length > n) {
            throw new IllegalArgumentException("illegal length of file name of floder name");
        }
        byte[] temp = str.getBytes();
        for (int i = 0; i < length; i++) {
            bs[i] = temp[i];
        }
        for (int i = length; i < n; i++) {
            bs[i] = 0;
        }
        return bs;
    }
}
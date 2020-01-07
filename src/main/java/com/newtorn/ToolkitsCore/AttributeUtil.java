package com.newtorn.ToolkitsCore;

import com.newtorn.BeanCore.Attribute;

/**
 * 属性工具类
 */
public final class AttributeUtil {

    /**
     * byte转bits
     * 
     * @param b 要转换的字节
     * @return
     */
    public static String byteToBits(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
                + (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1)
                + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 属性转换为字节
     * @param p
     * @return
     */
    public static byte attributeToByte(Attribute p) {
        int b = 0;
        if (p.isHidden())
            b = b + (int) Math.pow(2, 6);
        if (p.isOnlyRead())
            b = b + (int) Math.pow(2, 5);
        if (p.isShare())
            b = b + (int) Math.pow(2, 4);
        if (!p.isFile())
            b = -b;
        return (byte) b;
    }
}
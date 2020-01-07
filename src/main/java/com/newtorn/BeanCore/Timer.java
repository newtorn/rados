package com.newtorn.BeanCore;

public final class Timer {
    static int second1;
    static int second2;
    static int minute1;
    static int minute2;
    static int hour1;
    static int hour2;

    static {
        second1 = 0;
        second2 = 0;
        minute1 = 0;
        minute2 = 0;
        hour1 = 0;
        hour2 = 0;
    }

    public static void add() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        second1++;
        if (second1 == 10) {
            second1 = 0;
            second2++;
        }
        if (second2 == 6) {
            second2 = 0;
            minute1++;
        }
        if (minute1 == 10) {
            minute1 = 0;
            minute2++;
        }
        if (minute2 == 6) {
            minute2 = 0;
            hour1++;
        }
        if (hour1 == 10) {
            hour1 = 0;
            hour2++;
        }
    }

    public static String getTime() {
        StringBuffer buf = new StringBuffer();
        buf.append(hour2);
        buf.append(hour1);
        buf.append(':');
        buf.append(minute2);
        buf.append(minute1);
        buf.append(':');
        buf.append(second2);
        buf.append(second1);
        return new String(buf);
    }
}

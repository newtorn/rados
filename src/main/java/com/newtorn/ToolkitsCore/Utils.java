package com.newtorn.ToolkitsCore;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * 独立工具类
 */
public final class Utils {
    /**
     * 重启应哟程序
     * @param launchClassName
     */
    public static void restartApplication(String launchClassName) {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator)
                .append("java");
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(" ");
            cmd.append(arg);
        }
        cmd.append(" ").append("-cp").append(" ").append(ManagementFactory.getRuntimeMXBean().getClassPath())
                .append(" ").append(launchClassName);
        try {
            Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> objectToList(Object src) {
        List<T> list = new ArrayList<>();
        if (list instanceof ArrayList<?>) {
            for (Object o : (List<?>) src) {
                list.add((T)o);
            }
        }
        return list;
    }
}
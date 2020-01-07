package com.newtorn.ToolkitsCore;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读取工具
 */
public final class AssetsUtil {
    private static Properties props;

    static {
        final String fileName = "/assets.properties";
        props = new Properties();
        try {
            props.load(AssetsUtil.class.getClass().getResourceAsStream(fileName));
        } catch (final IOException e) {
            e.printStackTrace();
            System.err.println("Configuration file read error");
        }
    };

    /**
     * 获取配置项
     * 
     * @param key
     * @return
     */
    public static String get(final String key) {
        String value = props.getProperty(key.trim());
        if (value == null || value.trim().isEmpty()) {
            return key;
        }
        return value;
    }

    public static class Asset {
        public String name;
        public int width, height;

        public Asset(final String name, final int height, final int width) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public void set(final String name, final int height, final int width) {
            this.name = name;
            this.width = width;
            this.height = height;
        }
    }
}
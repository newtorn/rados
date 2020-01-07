package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class FrameUtil {
    private static Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final int curWidth = System.getProperties().get("os.name").toString().toLowerCase().startsWith("mac") ? 20 : 10;
    private static final int curHeight = System.getProperties().get("os.name").toString().toLowerCase().startsWith("mac") ? 20 : 10;
    private static final int curCenter = curHeight / 2;

    public static final Cursor DefaultCursor = createCursor("DefaultCursor", 1, 0);
    public static final Cursor UDResizeCursor = createCursor("NSResizeCursor", curCenter, curCenter);
    public static final Cursor LRResizeCursor = createCursor("EWResizeCursor", curCenter, curCenter);
    public static final Cursor LDResizeCursor = createCursor("NESWResizeCursor", curCenter, curCenter);
    public static final Cursor LUResizeCursor = createCursor("NWSEResizeCursor", curCenter, curCenter);
    public static final Cursor RDResizeCursor = createCursor("NWSEResizeCursor", curCenter, curCenter);
    public static final Cursor RUResizeCursor = createCursor("NESWResizeCursor", curCenter, curCenter);
    public static final Dimension ScreenSize = toolkit.getScreenSize();
    public static final Image DefaultDesktopImage = createImage("DefaultDesktopImage");
    public static final ImageIcon ErrorDialogImage = createImageIcon("ErrorDial");
    public static final ImageIcon InputDialogImage = createImageIcon("InputDial");

    public static Cursor createCursor(String name, int x, int y) {
        Image curImg = createImage(name);
        curImg = curImg.getScaledInstance(curWidth, curHeight, Image.SCALE_SMOOTH);
        return toolkit.createCustomCursor(curImg, new Point(Math.min(x, curWidth - 1), Math.min(y, curHeight - 1)),
                name);
    }

    public static Image createImage(String name) {
        return toolkit.createImage(toolkit.getClass().getResource(AssetsUtil.get(name)));
    }

    public static ImageIcon createImageIcon(String name) {
        return new ImageIcon(createImage(name));
    }

    public static Image resizeImage(Image img, int width, int height) {
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static ImageIcon resizeIcon(String imgfile, int width, int height) {
        Image img = createImage(imgfile);
        return new ImageIcon(resizeImage(img, width, height));
    }

    public static ImageIcon resizeIcon(Icon icon, int width, int height) {
        return new ImageIcon(resizeImage(((ImageIcon)icon).getImage(), width, height));
    }

    public static final int DARK_THEME = 0;
    public static final int LIGHT_THEME = 1;

    public static Image getThemeImage(String imgprefix, int theme) {
        if (theme == DARK_THEME) {
            // 暗色主题用亮色组件
            return createImage(imgprefix + "Light");
        } else if (theme == LIGHT_THEME) {
            // 亮色主题用暗色组件
            return createImage(imgprefix + "Dark");
        }
        return null;
    }

    public static ImageIcon getThemeIcon(String imgprefix, int theme) {
        if (theme == DARK_THEME) {
            return createImageIcon(imgprefix + "Light");
        } else if (theme == LIGHT_THEME) {
            return createImageIcon(imgprefix + "Dark");
        }
        return null;
    }

    public static ImageIcon getThemeIcon(String imgprefix, int theme, int w, int h) {
        if (theme == DARK_THEME) {
            return resizeIcon(imgprefix + "Light", w, h);
        } else if (theme == LIGHT_THEME) {
            return resizeIcon(imgprefix + "Dark", w, h);
        }
        return null;
    }

    public static Color DARK_THEME_COLOR = new Color(90, 50, 60, 127);
    public static Color LIGHT_THEME_COLOR = new Color(230, 205, 210, 170);

    public static Color getThemeColor(int theme) {
        if (theme == DARK_THEME) {
            return DARK_THEME_COLOR;
        } else if (theme == LIGHT_THEME) {
            return LIGHT_THEME_COLOR;
        }
        return LIGHT_THEME_BAR_COLOR;
    }

    public static Color DARK_THEME_BAR_COLOR = new Color(70, 65, 65);
    public static Color LIGHT_THEME_BAR_COLOR = new Color(219, 219, 219);
    public static Color getThemeBarColor(int theme) {
        if (theme == DARK_THEME) {
            return DARK_THEME_BAR_COLOR;
        } else if (theme == LIGHT_THEME) {
            return LIGHT_THEME_BAR_COLOR;
        }
        return LIGHT_THEME_BAR_COLOR;
    }

    public static Color DARK_THEME_TITLE_COLOR = Color.WHITE;
    public static Color LIGHT_THEME_TITLE_COLOR = Color.BLACK;
    public static Color getThemeTitleColor(int theme) {
        if (theme == DARK_THEME) {
            return DARK_THEME_TITLE_COLOR;
        } else if (theme == LIGHT_THEME) {
            return LIGHT_THEME_TITLE_COLOR;
        }
        return LIGHT_THEME_TITLE_COLOR;
    }

    public static Color DARK_THEME_FONT_COLOR = Color.WHITE;
    public static Color LIGHT_THEME_FONT_COLOR = Color.BLACK;
    public static Color getThemeFontColor(int theme) {
        if (theme == DARK_THEME) {
            return DARK_THEME_FONT_COLOR;
        } else if (theme == LIGHT_THEME) {
            return LIGHT_THEME_FONT_COLOR;
        }
        return LIGHT_THEME_FONT_COLOR;
    }


    public static Color DARK_THEME_BACK_COLOR = new Color(57, 54, 54);
    public static Color LIGHT_THEME_BACK_COLOR =  new Color(236, 236, 236);
    public static Color getThemeBackColor(int theme) {
        if (theme == DARK_THEME) {
            return DARK_THEME_BACK_COLOR;
        } else if (theme == LIGHT_THEME) {
            return LIGHT_THEME_BACK_COLOR;
        }
        return LIGHT_THEME_BACK_COLOR;
    }

    public static String getFilePath(String name) {
        return toolkit.getClass().getResource(AssetsUtil.get(name)).getPath();
    }
}
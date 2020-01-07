package com.newtorn.ToolkitsCore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.Date;

import javax.swing.Box;
import javax.swing.Timer;

public class StatusBar extends TransparentPanel implements FrameTheme {

    private static final long serialVersionUID = 1L;

    private Timer timer;
    private int batteryLevel = 0;
    private final int barGap = 20;
    private final int btnGap = barGap / 2;
    private final int barHeight = 25;
    private final int iconHeight = 20;
    private int theme = FrameUtil.LIGHT_THEME;

    private MWFrame parentFrame = null;
    private MenuPanel syspref = new MenuPanel(barGap, btnGap);
    private GridBagConstraints gbc = new GridBagConstraints();
    private TransparentPanel barLeft = new TransparentPanel(new GridBagLayout(), null);
    private TransparentPanel barCenter = new TransparentPanel(new GridBagLayout(), null);
    private TransparentPanel barRight = new TransparentPanel(new GridBagLayout(), null);
    private TransparentButton logo = new TransparentButton(true);
    private TransparentButton date = new TransparentButton(true);
    private TransparentButton connection = new TransparentButton(true);
    private TransparentButton wifi = new TransparentButton(true);
    private TransparentButton battery = new TransparentButton(true);
    private TransparentButton keyboard = new TransparentButton(true);
    private TransparentButton search = new TransparentButton(true);
    private TransparentButton menu = new TransparentButton(true);

    public StatusBar(MWFrame parent) {
        super();
        this.parentFrame = parent;
        if (parentFrame != null) {
            setPreferredSize(new Dimension(parentFrame.getWidth(), barHeight));
            parentFrame.getContentPane().add(this, BorderLayout.NORTH);
            theme = parentFrame.getTheme();
        }

        setLayout(new BorderLayout());

        setTheme(theme);

        syspref.setSize(200, 150);
        syspref.getContentPane().add(new TransparentButton("About Local Machine"));
        syspref.getContentPane().add(new TransparentButton("System Preferences"));
        syspref.getContentPane().add(new TransparentButton("Lock Screen"));

        TransparentButton restartBtn = new TransparentButton("Restart");
        TransparentButton powerOffBtn = new TransparentButton("Power Off");
        syspref.getContentPane().add(restartBtn);
        syspref.getContentPane().add(powerOffBtn);

        restartBtn.addMouseListener(new MouseAdapter() {
            private boolean re = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!re && parentFrame != null) {
                    re = true;
                    parentFrame.restart();
                }
            }
        });

        powerOffBtn.addMouseListener(new MouseAdapter() {
            private boolean off = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!off && parentFrame != null) {
                    off = true;
                    parentFrame.powerOff();
                }
            }
        });

        logo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                syspref.setParentFrame(parentFrame);
                syspref.setRelativeLocation(0, 0);
                syspref.setVisible(!syspref.isVisible());
                syspref.toFront();
            }
        });

        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = barHeight - iconHeight;

        barLeft.add(Box.createHorizontalStrut(barGap));
        barLeft.add(logo, gbc);

        barRight.add(connection, gbc);
        barRight.add(Box.createHorizontalStrut(btnGap));
        barRight.add(wifi, gbc);
        barRight.add(Box.createHorizontalStrut(btnGap));
        barRight.add(battery, gbc);
        barRight.add(Box.createHorizontalStrut(btnGap));
        barRight.add(keyboard, gbc);
        barRight.add(Box.createHorizontalStrut(btnGap));
        barRight.add(search, gbc);
        barRight.add(Box.createHorizontalStrut(btnGap));
        barRight.add(menu, gbc);
        barRight.add(Box.createHorizontalStrut(barGap));

        add(barLeft, BorderLayout.WEST);
        add(barCenter, BorderLayout.CENTER);
        add(barRight, BorderLayout.EAST);

        int lw = barLeft.getPreferredSize().width;
        int rw = barRight.getPreferredSize().width;
        if (lw < rw) {
            barCenter.add(Box.createHorizontalStrut(rw - lw), gbc);
            barCenter.add(date, gbc);
        } else if (lw >= rw) {
            barCenter.add(date, gbc);
            barCenter.add(Box.createHorizontalStrut(lw - rw), gbc);
        }

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                date.setText(new Date().toString());
            }
        });
        timer.start();
    }

    private void update() {
        date.setForeground(FrameUtil.getThemeFontColor(theme));
        logo.setIcon(FrameUtil.getThemeIcon("Logo", theme, iconHeight, iconHeight));
        connection.setIcon(FrameUtil.getThemeIcon("Connection", theme, iconHeight, iconHeight));
        wifi.setIcon(FrameUtil.getThemeIcon("Wifi", theme, iconHeight, iconHeight));
        if (batteryLevel == -1) {
            battery.setIcon(FrameUtil.getThemeIcon("BatteryIn", theme, iconHeight, iconHeight));
        } else if (batteryLevel >= 0 && batteryLevel <= 4) {
            battery.setIcon(FrameUtil.getThemeIcon("BatteryL" + batteryLevel, theme, iconHeight, iconHeight));
        }
        keyboard.setIcon(FrameUtil.getThemeIcon("Keyboard", theme, iconHeight, iconHeight));
        search.setIcon(FrameUtil.getThemeIcon("Search", theme, iconHeight, iconHeight));
        menu.setIcon(FrameUtil.getThemeIcon("Menu", theme, iconHeight, iconHeight));
        setBackground(FrameUtil.getThemeColor(theme));
    }

    @Override
    public void setTheme(int theme) {
        this.theme = theme;
        update();
    }

    @Override
    public int getTheme() {
        return theme;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        if (batteryLevel < -1 || batteryLevel > 4) {
            return;
        }
        this.batteryLevel = batteryLevel;
        update();
    }
}
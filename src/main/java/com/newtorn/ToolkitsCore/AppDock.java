package com.newtorn.ToolkitsCore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.newtorn.ViewCore.DiskManagerApp;
import com.newtorn.ViewCore.FileManagerApp;
import com.newtorn.ViewCore.MusicApp;
import com.newtorn.ViewCore.PreferenceApp;
import com.newtorn.ViewCore.ProcessManagerApp;
import com.newtorn.ViewCore.TerminalApp;
import com.newtorn.ViewCore.TextEditorApp;
import com.newtorn.ViewCore.TrashApp;

public final class AppDock extends JComponent implements FrameTheme {

    public static void main(String[] args) {
        MWFrame mwf = new MWFrame();
        mwf.setBounds(20, 20, 800, 600);

        mwf.setDockLayout(AppDock.DOCK_LEFT);
        mwf.getAppDock().addApp(DiskManagerApp.getInstance());
        mwf.getAppDock().addApp(FileManagerApp.getInstance());
        mwf.getAppDock().addApp(ProcessManagerApp.getInstance());
        mwf.getAppDock().addApp(PreferenceApp.getInstance());
        mwf.getAppDock().addApp(TerminalApp.getInstance());
        mwf.getAppDock().addApp(TextEditorApp.getInstance());
        mwf.getAppDock().addApp(MusicApp.getInstance());

        mwf.setVisible(true);
    }

    private static final long serialVersionUID = 1L;

    public static final int DOCK_BOTTOM = 1;
    public static final int DOCK_LEFT = 2;
    public static final int DOCK_RIGHT = 3;

    private int theme = FrameUtil.LIGHT_THEME;
    private static final int dockGap = FrameUtil.ScreenSize.width / 26;
    private JComponent dockTop = new JComponent() {
        private static final long serialVersionUID = 1L;
    };
    private JComponent dockBottom = new JComponent() {
        private static final long serialVersionUID = 1L;
    };
    private GridBagConstraints gbc = new GridBagConstraints();

    private int dockLayout = DOCK_LEFT;
    private MWFrame parentFrame = null;
    private ArrayList<AppFrame> apps = new ArrayList<>();
    private TrashApp trashApp = TrashApp.getInstance();
    private TransparentButton lineSpace1 = new TransparentButton(new Color(0, 0, 0, 0), false);
    private TransparentButton lineSpace2 = new TransparentButton(new Color(0, 0, 0, 0), false);
    private TransparentPanel dockMiddle = new TransparentPanel(new GridBagLayout(), FrameUtil.getThemeColor(theme));
    private TransparentPanel dockFixed = new TransparentPanel(new VFlowLayout(FlowLayout.CENTER),
            new Color(0, 0, 0, 0));
    private TransparentPanel dockRun = new TransparentPanel(new VFlowLayout(FlowLayout.CENTER), new Color(0, 0, 0, 0));
    private TransparentPanel dockBuiltin = new TransparentPanel(new VFlowLayout(FlowLayout.CENTER),
            new Color(0, 0, 0, 0));

    public AppDock(MWFrame parent) {
        super();
        this.parentFrame = parent;
        if (parentFrame != null) {
            parentFrame.getContentPane().add(this, BorderLayout.WEST);
            theme = parentFrame.getTheme();
            trashApp.setToolTip(parent);
            bindApp(trashApp, false);
        }
        this.setOpaque(false);
        dockTop.setOpaque(false);
        dockBottom.setOpaque(false);
        dockBuiltin.add(trashApp.getRunBtn());
        setLayout(new BorderLayout());
        add(dockTop, BorderLayout.NORTH);
        add(dockMiddle, BorderLayout.CENTER);
        add(dockBottom, BorderLayout.SOUTH);
        setTheme(theme);
        setDockLayout(dockLayout);
    }

    public void addApp(AppFrame app) {
        apps.add(app);
        dockFixed.add(app.getFixedBtn());
        app.setToolTip(parentFrame);
        app.setAppDock(this);
        bindApp(app);
        updateDockLayout();
    }

    private void bindApp(AppFrame app) {
        bindApp(app, true);
    }

    private void bindApp(AppFrame app, boolean addRun) {
        app.getFixedBtn().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (app.isRunning()) {
                    return;
                }
                app.create(parentFrame);
                if (addRun) {
                    dockRun.add(app.getRunBtn());
                }
                app.getFrame().replaceCloseBtnMouseAdapter(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        app.dispose();
                    }
                });
                updateDockLayout();
            }
        });
        app.getRunBtn().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                app.display();
            }
        });
    }

    public void disposeRunApp(AppFrame app) {
        dockRun.remove(app.getRunBtn());
        updateDockLayout();
    }

    public void setDockLayout(int pos) {
        if (pos == DOCK_LEFT || pos == DOCK_RIGHT) {
            dockLayout = pos;
            remove(dockTop);
            remove(dockBottom);
            add(dockTop, BorderLayout.NORTH);
            add(dockBottom, BorderLayout.SOUTH);
            dockFixed.setLayout(new VFlowLayout(FlowLayout.CENTER));

            dockMiddle.removeAll();
            dockRun.setLayout(new VFlowLayout(FlowLayout.CENTER));
            dockBuiltin.setLayout(new VFlowLayout(FlowLayout.CENTER));

            gbc.gridx = 0;
            gbc.gridy = 0;
            dockMiddle.add(dockFixed, gbc);
            gbc.gridy = 1;
            dockMiddle.add(lineSpace1, gbc);
            gbc.gridy = 2;
            dockMiddle.add(dockRun, gbc);
            gbc.gridy = 3;
            dockMiddle.add(lineSpace2, gbc);
            gbc.gridy = 4;
            dockMiddle.add(dockBuiltin, gbc);
        } else if (pos == DOCK_BOTTOM) {
            dockLayout = pos;
            remove(dockTop);
            remove(dockBottom);
            add(dockTop, BorderLayout.WEST);
            add(dockBottom, BorderLayout.EAST);
            dockFixed.setLayout(new FlowLayout(FlowLayout.CENTER));

            dockMiddle.removeAll();
            dockRun.setLayout(new FlowLayout(FlowLayout.CENTER));
            dockBuiltin.setLayout(new FlowLayout(FlowLayout.CENTER));

            gbc.gridy = 0;
            gbc.gridx = 0;
            dockMiddle.add(dockFixed, gbc);
            gbc.gridx = 1;
            dockMiddle.add(lineSpace1, gbc);
            gbc.gridx = 2;
            dockMiddle.add(dockRun, gbc);
            gbc.gridx = 3;
            dockMiddle.add(lineSpace2, gbc);
            gbc.gridx = 4;
            dockMiddle.add(dockBuiltin, gbc);
        }
        updateDockLayout();
    }

    public int getDockLayout() {
        return dockLayout;
    }

    public void updateDockLayout() {
        if (getGap() != 0) {
            int runNum = 0;
            for (AppFrame app : apps) {
                if (app.isRunning()) {
                    runNum++;
                }
                app.getRunBtn().setPressedColor(FrameUtil.getThemeColor(theme));
                if (app.getRunBtn().getIcon() != null) {
                    app.getRunBtn().setIcon(FrameUtil.resizeIcon(app.getRunBtn().getIcon(), getGap(), getGap()));
                } else {
                    app.getRunBtn().setIcon(FrameUtil.resizeIcon(app.getAppIcon(), getGap(), getGap()));
                }
                app.getFixedBtn().setPressedColor(FrameUtil.getThemeColor(theme));
                if (app.getFixedBtn().getIcon() != null) {
                    app.getFixedBtn().setIcon(FrameUtil.resizeIcon(app.getFixedBtn().getIcon(), getGap(), getGap()));
                } else {
                    app.getFixedBtn().setIcon(FrameUtil.resizeIcon(app.getAppIcon(), getGap(), getGap()));
                }
            }
            Icon lineSpaceImg = null;
            if (dockLayout == DOCK_LEFT || dockLayout == DOCK_RIGHT) {
                lineSpaceImg = FrameUtil.resizeIcon(FrameUtil.getThemeIcon("LineSpaceX", theme), getGap(), 30);
                dockTop.setPreferredSize(new Dimension(getGap(), getGapH()));
                dockBottom.setPreferredSize(new Dimension(getGap(), getGapH()));
            } else if (dockLayout == DOCK_BOTTOM) {
                lineSpaceImg = FrameUtil.resizeIcon(FrameUtil.getThemeIcon("LineSpaceY", theme), 30, getGap());
                dockTop.setPreferredSize(new Dimension(getGapW(), getGap()));
                dockBottom.setPreferredSize(new Dimension(getGapW(), getGap()));
            }
            if (runNum != 0) {
                lineSpace1.setIcon(lineSpaceImg);
            } else {
                lineSpace1.setIcon(null);
            }
            lineSpace2.setIcon(lineSpaceImg);
            trashApp.getRunBtn().setPressedColor(FrameUtil.getThemeColor(theme));
            trashApp.setIcon(theme, getGap(), getGap());
            repaint();
        }
    }

    private int getGap() {
        int gap = dockGap;
        if (parentFrame != null) {
            gap = 30;
        }
        return gap;
    }

    private int getGapH() {
        int gapH = FrameUtil.ScreenSize.height;
        if (parentFrame != null) {
            gapH = parentFrame.getContentPaneSize().height;
        }
        int appNum = apps.size() + 1;
        for (AppFrame app : apps) {
            if (app.isRunning()) {
                appNum++;
            }
        }
        return (int) (gapH * 0.1 - (appNum * getGap()) * 0.04);
    }

    private int getGapW() {
        int gapW = FrameUtil.ScreenSize.width;
        if (parentFrame != null) {
            gapW = parentFrame.getContentPaneSize().width;
        }
        int appNum = apps.size() + 1;
        for (AppFrame app : apps) {
            if (app.isRunning()) {
                appNum++;
            }
        }
        return (int) (gapW * 0.1 - (appNum * getGap()) * 0.04);
    }

    @Override
    public void setTheme(int theme) {
        this.theme = theme;
        updateDockLayout();
    }

    @Override
    public int getTheme() {
        return theme;
    }
}
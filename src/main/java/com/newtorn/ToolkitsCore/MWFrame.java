package com.newtorn.ToolkitsCore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.newtorn.BeanCore.CPU;
import com.newtorn.BeanCore.Timer;
import com.newtorn.BeanCore.Device;
import com.newtorn.BeanCore.TaskQueue;
import com.newtorn.ViewCore.DiskManagerApp;
import com.newtorn.ViewCore.FileManagerApp;
import com.newtorn.ViewCore.MusicApp;
import com.newtorn.ViewCore.PreferenceApp;
import com.newtorn.ViewCore.ProcessManagerApp;
import com.newtorn.ViewCore.TerminalApp;
import com.newtorn.ViewCore.TextEditorApp;
import com.newtorn.ViewCore.ProcessManagerApp.ProcessPanel;

public final class MWFrame extends JFrame implements FrameTheme {

    private static final long serialVersionUID = 1L;

    private int theme = FrameUtil.DARK_THEME;

    /**
     * 静态图片资源
     */
    private static final ImageIcon _ctrlBtnCloseOver = FrameUtil.createImageIcon("CtrlBtnCloseOver");
    private static final ImageIcon _ctrlBtnMinimiseOver = FrameUtil.createImageIcon("CtrlBtnMinimiseOver");
    private static final ImageIcon _ctrlBtnMaximiseOver = FrameUtil.createImageIcon("CtrlBtnMaximiseOver");
    private static final ImageIcon _ctrlBtnCloseNormal = FrameUtil.createImageIcon("CtrlBtnCloseNormal");
    private static final ImageIcon _ctrlBtnMinimiseNormal = FrameUtil.createImageIcon("CtrlBtnMinimiseNormal");
    private static final ImageIcon _ctrlBtnMaximiseNormal = FrameUtil.createImageIcon("CtrlBtnMaximiseNormal");
    private static final ImageIcon _ctrlBtnCloseInactive = FrameUtil.createImageIcon("CtrlBtnCloseInactive");
    private static final ImageIcon _ctrlBtnMinimiseInactive = FrameUtil.createImageIcon("CtrlBtnMinimiseInactive");
    private static final ImageIcon _ctrlBtnMaximiseInactive = FrameUtil.createImageIcon("CtrlBtnMaximiseInactive");

    /**
     * 动态调整位置
     */
    private int borderGapT = 2;
    private int borderGapLRB = 1;
    private int ctrlBtnGap = 5;
    private int boderCornerOffset = 2;
    private boolean inDragSize = false;
    private Point mouseAt = new Point();
    private Direction dragDir = new Direction();

    /**
     * 子窗口
     */
    private static int mwfcnt = 0;
    LinkedHashSet<MFrame> childFrames = new LinkedHashSet<>();

    /**
     * 自定义组件
     */
    ImageContainer _contentPane = new ImageContainer();
    JDesktopPane _desktop = new JDesktopPane();
    JPanel _containPane = new JPanel();
    JPanel _ctrlArea = new JPanel();
    JPanel _titleBar = new JPanel();
    JLabel _titleLab = new JLabel();
    ImageButton _closeBtn = new ImageButton(_ctrlBtnCloseNormal);
    ImageButton _minBtn = new ImageButton(_ctrlBtnMinimiseNormal);
    ImageButton _maxBtn = new ImageButton(_ctrlBtnMaximiseNormal);

    private MScreen ms = new MScreen();
    private AppDock appDock;
    private StatusBar statusBar;
    private MenuPanel tooltip;
    private TransparentButton tiptext;
    private Image desktopImage = FrameUtil.DefaultDesktopImage;

    /**
     * MFrame创建数量
     */
    protected static LinkedHashMap<String, Integer> frames = new LinkedHashMap<>();

    public MWFrame() {
        super();
        setTitle(defaultTitle());
        customDecorate();
    }

    public MWFrame(String title) {
        super(title);
        customDecorate();
    }

    /**
     * MFrame默认标题
     */
    private String defaultTitle() {
        String title = getClass().getSimpleName();
        if (mwfcnt > 0) {
            title += String.valueOf(mwfcnt);
        }
        return title;
    }

    /**
     * 销毁所有直接子窗体
     */
    private void disposeChildFrames() {
        for (MFrame mf : childFrames) {
            mf.dispose();
        }
        childFrames.clear();
    }

    /**
     * 设置自定义装饰
     */
    private void customDecorate() {
        // 隐藏默认标题栏装饰
        setUndecorated(true);
        setCursor(FrameUtil.DefaultCursor);

        // 设置标题栏部件
        setCtrlBtnNormalIcon();
        setCtrlBtnInactiveIcon();
        setCtrlBtnRollOverIcon();

        _titleLab.setOpaque(false);
        _titleLab.setText(getTitle());
        _titleLab.setHorizontalAlignment(JLabel.CENTER);
        _closeBtn.setBounds(ctrlBtnGap, ctrlBtnGap, _closeBtn.getIcon().getIconWidth(),
                _closeBtn.getIcon().getIconHeight());
        _minBtn.setBounds(2 * ctrlBtnGap + _closeBtn.getWidth(), ctrlBtnGap, _minBtn.getIcon().getIconWidth(),
                _minBtn.getIcon().getIconHeight());
        _maxBtn.setBounds(3 * ctrlBtnGap + _closeBtn.getWidth() + _minBtn.getWidth(), ctrlBtnGap,
                _maxBtn.getIcon().getIconWidth(), _maxBtn.getIcon().getIconHeight());

        // 添加组件至标题栏
        _ctrlArea.add(_closeBtn);
        _ctrlArea.add(_minBtn);
        _ctrlArea.add(_maxBtn);
        _ctrlArea.setOpaque(false);
        _ctrlArea.setLayout(null);
        _titleBar.add(_ctrlArea);
        _titleBar.add(_titleLab);
        _titleBar.add(new JLabel());
        _titleBar.setLayout(new GridLayout(1, 3));
        _titleBar.setPreferredSize(new Dimension(getWidth(), _closeBtn.getIcon().getIconHeight() + 2 * ctrlBtnGap));
        _titleBar.setMinimumSize(new Dimension(
                2 * borderGapLRB
                        + 3 * (3 * ctrlBtnGap + _closeBtn.getWidth() + _minBtn.getWidth() + _maxBtn.getWidth()),
                _closeBtn.getIcon().getIconHeight() + 2 * ctrlBtnGap + borderGapLRB + borderGapT));

        _desktop.setOpaque(false);

        // 主题设置
        setTheme(theme);

        // 设置自定义容器面板
        _containPane.setOpaque(false);
        _containPane.setLayout(new BorderLayout());
        _contentPane.setImage(desktopImage);
        _contentPane.setLayout(new BorderLayout());
        _contentPane.add(_desktop, BorderLayout.CENTER);
        _containPane.add(_titleBar, BorderLayout.NORTH);
        _containPane.add(_contentPane, BorderLayout.CENTER);
        getRootPane().setContentPane(_containPane);

        appDock = new AppDock(this);
        statusBar = new StatusBar(this);

        tooltip = new MenuPanel(this, 0, 0);
        tiptext = new TransparentButton("");
        tiptext.setHorizontalAlignment(TransparentButton.CENTER);
        tiptext.setSize(200, 20);

        tiptext.setForeground(Color.BLACK);
        tooltip.getContentPane().add(tiptext);
        tooltip.setSize(200, 20);

        /**
         * 添加关闭按钮点击事件
         */
        _closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mwfcnt > 1) {
                    autoDispose();
                } else {
                    System.exit(0);
                }
            }
        });

        /**
         * 添加最小化按钮点击事件
         */
        _minBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setMin();
            }
        });

        /**
         * 添加最大化按钮点击事件
         */
        _maxBtn.addMouseListener(new MouseAdapter() {
            private boolean selected = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                if (selected) {
                    setMax();
                } else {
                    setFrameBorder(true);
                    setExtendedState(NORMAL);
                }
            }
        });
        
        // 添加窗体鼠标按键监听
        _titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!inDragSize) {
                    mouseAt = e.getPoint();
                }
            }
        });

        // 添加标题栏鼠标移动事件
        _titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!inDragSize) {
                    setLocationToParent(e.getXOnScreen(), e.getYOnScreen());
                }
            }
        });

        // 添加窗体鼠标移动事件
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isResizable() && inDragSize) {
                    setToDragSize(e.getLocationOnScreen());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseAt = e.getPoint();
                setCursorState(e.getPoint(), _containPane.getSize(), _containPane.getInsets());
            }
        });

        // 设置窗体焦点事件
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                setCtrlBtnNormalIcon();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                setCtrlBtnInactiveIcon();
            }
        });

        // 添加窗体鼠标按键监听
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseAt = e.getLocationOnScreen();
                setCursorState(e.getPoint(), _containPane.getSize(), _containPane.getInsets());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                inDragSize = false;
                setCursor(FrameUtil.DefaultCursor);
            }
        });
    }

    /**
     * 设置控制按钮被鼠标覆盖状态图标
     */
    private void setCtrlBtnRollOverIcon() {
        _closeBtn.setRolloverIcon(_ctrlBtnCloseOver);
        _minBtn.setRolloverIcon(_ctrlBtnMinimiseOver);
        _maxBtn.setRolloverIcon(_ctrlBtnMaximiseOver);
    }

    /**
     * 设置控制按钮正常状态图标
     */
    private void setCtrlBtnNormalIcon() {
        _closeBtn.setIcon(_ctrlBtnCloseNormal);
        _minBtn.setIcon(_ctrlBtnMinimiseNormal);
        _maxBtn.setIcon(_ctrlBtnMaximiseNormal);
    }

    /**
     * 设置控制按钮未激活状态图标
     */
    private void setCtrlBtnInactiveIcon() {
        _closeBtn.setIcon(_ctrlBtnCloseInactive);
        _minBtn.setIcon(_ctrlBtnMinimiseInactive);
        _maxBtn.setIcon(_ctrlBtnMaximiseInactive);
    }

    /**
     * 设置窗体标题
     */
    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this._titleLab.setText(title);
    }

    /**
     * 设置窗体内容面板
     */
    public void setContentPane(ImageContainer contentPane) {
        this._containPane.remove(this._contentPane);
        this._contentPane = contentPane;
        this._containPane.add(this._contentPane, BorderLayout.CENTER);
    }

    /**
     * 获取窗体内容面板
     */
    public ImageContainer getContentPane() {
        return this._contentPane;
    }

    /**
     * 获取桌面面板
     * 
     * @return
     */
    public JDesktopPane getDesktopPane() {
        return _desktop;
    }

    /**
     * 获取内容面板大小
     * 
     * @return
     */
    public Dimension getContentPaneSize() {
        int w = getWidth();
        int h = _desktop.getHeight();
        if (_titleBar.isVisible()) {
            h -= _titleBar.getMinimumSize().height;
        }
        return new Dimension(w, h);
    }

    public void setFrameBorder(boolean setBorder) {
        if (setBorder) {
            _containPane.setBorder(BorderFactory.createMatteBorder(borderGapT, borderGapLRB, borderGapLRB, borderGapLRB,
                    FrameUtil.getThemeBackColor(theme)));
        } else {
            _containPane.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    /**
     * 设置窗体最小化
     */
    public void setMin() {
        setExtendedState(ICONIFIED);
        for (MFrame c : childFrames) {
            c.setMin();
        }
        setFrameBorder(true);
    }

    /**
     * 设置窗体最大化
     * 
     * @param frame
     */
    public void setMax() {
        setExtendedState(MAXIMIZED_BOTH);
        setFrameBorder(false);
    }

    /**
     * 设置窗体位置居中
     */
    public void setLocationToCenter() {
        Dimension scsize = getToolkit().getScreenSize();
        setLocation(scsize.width / 2 - getWidth() / 2, scsize.height / 2 - getHeight() / 2);
    }

    /**
     * 设置相对位置
     * 
     * @param x
     * @param y
     */
    public void setRelativeLocation(int x, int y) {
        setLocation(x, y);
    }

    /**
     * 设置合适的尺寸大小
     */
    public void setProperSize(Dimension dm) {
        setProperSize(dm.width, dm.height);
    }

    /**
     * 设置合适的Size
     */
    public void setProperSize(int w, int h) {
        if (_titleBar.isVisible()) {
            Dimension tbdm = _titleBar.getMinimumSize();
            w = Math.max(w, tbdm.width);
            h = Math.max(h, tbdm.height);
        }
        w = Math.min(getToolkit().getScreenSize().width, w);
        h = Math.min(getToolkit().getScreenSize().height, h);
        setSize(w, h);
    }

    /**
     * 设置合适的Bounds
     */
    public void setProperBounds(int x, int y, int w, int h) {
        setProperSize(w, h);

        int px = 0, py = 0;
        int pw = getToolkit().getScreenSize().width;
        int ph = getToolkit().getScreenSize().height;

        x = Math.min(Math.max(px, x), px + pw - getWidth());
        y = Math.min(Math.max(py, y), py + ph - getHeight());
        setLocation(x, y);
    }

    /**
     * 设置鼠标状态
     * 
     * @param curPos  鼠标在组件中的位置
     * @param conSize 组件尺寸
     * @param conIns  组件边框
     */
    public void setCursorState(Point curPos, Dimension conSize, Insets conIns) {
        if (_containPane.getBorder() == null) {
            dragDir.curDir = Direction.CENTER;
            setCursor(FrameUtil.DefaultCursor);
            inDragSize = false;
            return;
        }
        inDragSize = true;
        if (curPos.x >= 0 && curPos.x <= boderCornerOffset && curPos.y >= 0 && curPos.y <= boderCornerOffset) {
            dragDir.curDir = Direction.LEFT_UP;
            setCursor(FrameUtil.LUResizeCursor);
        } else if (curPos.x >= 0 && curPos.x <= boderCornerOffset && curPos.y >= conSize.height - boderCornerOffset
                && curPos.y <= conSize.height) {
            dragDir.curDir = Direction.LEFT_DOWN;
            setCursor(FrameUtil.LDResizeCursor);
        } else if (curPos.x >= conSize.width - boderCornerOffset && curPos.x <= conSize.width && curPos.y >= 0
                && curPos.y <= boderCornerOffset) {
            dragDir.curDir = Direction.RIGHT_UP;
            setCursor(FrameUtil.RUResizeCursor);
        } else if (curPos.x >= conSize.width - boderCornerOffset && curPos.x <= conSize.width
                && curPos.y >= conSize.height - boderCornerOffset && curPos.y <= conSize.height) {
            dragDir.curDir = Direction.RIGHT_DOWN;
            setCursor(FrameUtil.RDResizeCursor);
        } else if (curPos.x >= 0 && curPos.x <= conIns.left && curPos.y >= boderCornerOffset
                && curPos.y <= conSize.height - boderCornerOffset) {
            dragDir.curDir = Direction.LEFT;
            setCursor(FrameUtil.LRResizeCursor);
        } else if (curPos.x >= conSize.width - conIns.right && curPos.x <= conSize.width
                && curPos.y >= boderCornerOffset && curPos.y <= conSize.height - boderCornerOffset) {
            dragDir.curDir = Direction.RIGHT;
            setCursor(FrameUtil.LRResizeCursor);
        } else if (curPos.x >= boderCornerOffset && curPos.x <= conSize.width - boderCornerOffset && curPos.y >= 0
                && curPos.y <= conIns.top) {
            dragDir.curDir = Direction.UP;
            setCursor(FrameUtil.UDResizeCursor);
        } else if (curPos.x >= boderCornerOffset && curPos.x <= conSize.width - boderCornerOffset
                && curPos.y >= conSize.height - conIns.bottom && curPos.y <= conSize.height) {
            dragDir.curDir = Direction.DOWN;
            setCursor(FrameUtil.UDResizeCursor);
        } else {
            dragDir.curDir = Direction.CENTER;
            setCursor(FrameUtil.DefaultCursor);
            inDragSize = false;
        }
    }

    /**
     * 自动销毁
     */
    public void autoDispose() {
        disposeChildFrames();
        dispose();
    }

    /**
     * 设置窗体标题颜色
     * 
     * @param c
     */
    public void setTitleColor(Color c) {
        this._titleLab.setForeground(c);
    }

    /**
     * 获取窗体标题颜色
     * 
     * @return
     */
    public Color getTitleColor() {
        return this._titleLab.getForeground();
    }

    /**
     * 设置窗体标题栏背景颜色
     * 
     * @param c
     */
    public void setBarBackground(Color c) {
        this._titleBar.setBackground(c);
    }

    /**
     * 获取窗体标题栏背景颜色
     * 
     * @return
     */
    public Color getBarBackground() {
        return this._titleBar.getBackground();
    }

    /**
     * 设置标题栏是否可见
     */
    public void setBarVisible(boolean b) {
        this._titleBar.setVisible(b);
    }

    /**
     * 获取关闭按钮
     * 
     * @return
     */
    public ImageButton getCloseBtn() {
        return _closeBtn;
    }

    /**
     * 获取最小化按钮
     * 
     * @return
     */
    public ImageButton getMinBtn() {
        return _minBtn;
    }

    /**
     * 获取最大化按钮
     * 
     * @return
     */
    public ImageButton getMaxBtn() {
        return _maxBtn;
    }

    /**
     * 设置窗体居中
     */
    public void toCenterPosition() {
        int x = FrameUtil.ScreenSize.width / 2 - getWidth() / 2;
        int y = FrameUtil.ScreenSize.height / 2 - getHeight() / 2;
        setLocation(x, y);
    }

    /**
     * 获取所有直接子窗体
     * 
     * @return
     */
    protected LinkedHashSet<MFrame> getChildFrames() {
        return childFrames;
    }

    /**
     * 添加直接子窗体
     * 
     * @param child
     */
    public boolean appendChildFrame(MFrame child) {
        if (child != null) {
            child.parentFrame = this;
            return this.childFrames.add(child);
        }
        return false;
    }

    /**
     * 添加直接子窗体
     * 
     * @param child
     */
    public boolean deleteChildFrame(MFrame child) {
        boolean res = this.childFrames.remove(child);
        if (res) {
            child.parentFrame = null;
        }
        return res;
    }

    /**
     * 当窗体自身移动时，设置到父窗体的容器位置
     * 
     * @param xOnScreen
     * @param yOnScreen
     */
    public void setLocationToParent(int xOnScreen, int yOnScreen) {
        Point pos = new Point(xOnScreen - mouseAt.x, yOnScreen - mouseAt.y);
        setLocation(pos);
    }

    /**
     * 设置鼠标拖动大小
     * 
     * @param screenAt 鼠标在屏幕的位置
     */
    public void setToDragSize(Point screenAt) {
        int x = getX(), y = getY();
        int w = getWidth(), h = getHeight();

        dragDir.deltax = screenAt.x - mouseAt.x;
        dragDir.deltay = screenAt.y - mouseAt.y;
        mouseAt = screenAt;

        switch (dragDir.curDir) {
        case Direction.UP: {
            if (!_titleBar.isVisible() || h - dragDir.deltay > _titleBar.getMinimumSize().height) {
                setProperBounds(x, y + dragDir.deltay, w, h - dragDir.deltay);
            }
            break;
        }
        case Direction.LEFT: {
            if (!_titleBar.isVisible() || w - dragDir.deltax > _titleBar.getMinimumSize().width) {
                setProperBounds(x + dragDir.deltax, y, w - dragDir.deltax, h);
            }
            break;
        }
        case Direction.LEFT_UP: {
            if (!_titleBar.isVisible() || w - dragDir.deltax > _titleBar.getMinimumSize().width) {
                setProperBounds(x + dragDir.deltax, y, w - dragDir.deltax, h);
                x = getX();
                w = getWidth();
            }
            if (!_titleBar.isVisible() || h - dragDir.deltay > _titleBar.getMinimumSize().height) {
                setProperBounds(x, y + dragDir.deltay, w, h - dragDir.deltay);
            }
            break;
        }
        case Direction.DOWN: {
            setProperBounds(x, y, w, h + dragDir.deltay);
            break;
        }
        case Direction.RIGHT: {
            setProperBounds(x, y, w + dragDir.deltax, h);
            break;
        }
        case Direction.RIGHT_DOWN: {
            setProperBounds(x, y, w + dragDir.deltax, h + dragDir.deltay);
            break;
        }
        case Direction.RIGHT_UP: {
            if (!_titleBar.isVisible() || h - dragDir.deltay > _titleBar.getMinimumSize().height) {
                setProperBounds(x, y + dragDir.deltay, w + dragDir.deltax, h - dragDir.deltay);
            }
            break;
        }
        case Direction.LEFT_DOWN: {
            if (!_titleBar.isVisible() || w - dragDir.deltax > _titleBar.getMinimumSize().width) {
                setProperBounds(x + dragDir.deltax, y, w - dragDir.deltax, h + dragDir.deltay);
            }
            break;
        }
        }
    }

    public AppDock getAppDock() {
        return appDock;
    }

    private int appdock_position;
    public int getDockPosition() {
        return appdock_position;
    }
    
    public void setDockLayout(int pos) {
        appdock_position = pos;
        switch (pos) {
        case AppDock.DOCK_LEFT:
            getContentPane().remove(appDock);
            getContentPane().add(appDock, BorderLayout.WEST);
            break;
        case AppDock.DOCK_RIGHT:
            getContentPane().remove(appDock);
            getContentPane().add(appDock, BorderLayout.EAST);
            break;
        case AppDock.DOCK_BOTTOM:
            getContentPane().remove(appDock);
            getContentPane().add(appDock, BorderLayout.SOUTH);
            break;
        }
        appDock.setDockLayout(pos);
    }

    @Override
    public int getTheme() {
        return theme;
    }

    @Override
    public void setTheme(int theme) {
        this.theme = theme;
        setFrameBorder(true);
        setTitleColor(FrameUtil.getThemeTitleColor(theme));
        setBarBackground(FrameUtil.getThemeBarColor(theme));
        setBackground(FrameUtil.getThemeBackColor(theme));
        if (getChildFrames() != null) {
            for (MFrame c : getChildFrames()) {
                c.setTheme(theme);
            }
        }
        if (appDock != null) {
            appDock.setTheme(theme);
        }
        if (appDock != null) {
            statusBar.setTheme(theme);
        }
    }

    private static MWFrame mwf = null;
    private static String launchClass;

    public static void boot() {
        boot(MWFrame.class.getName());
    }

    public static void boot(String launchClassName) {
        launchClass = launchClassName;
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (mwf != null) {
                    mwf.autoDispose();
                }
                mwf = new MWFrame();
                mwf.powerOn();
                mwf.setTheme(FrameUtil.DARK_THEME);
                mwf.setDockLayout(AppDock.DOCK_LEFT);
                mwf.getAppDock().addApp(DiskManagerApp.getInstance());
                mwf.getAppDock().addApp(FileManagerApp.getInstance());
                mwf.getAppDock().addApp(ProcessManagerApp.getInstance());
                mwf.getAppDock().addApp(PreferenceApp.getInstance());
                mwf.getAppDock().addApp(TerminalApp.getInstance());
                mwf.getAppDock().addApp(TextEditorApp.getInstance());
                mwf.getAppDock().addApp(MusicApp.getInstance());
            }
        });
        CPU cpu = new CPU();
        while (true) {
            cpu.run();
            ProcessPanel.setPCBfreeQueue(TaskQueue.getReadyText());
            ProcessPanel.setPCBblockQueue(TaskQueue.getBlockText());
            Device.run();
            Timer.add();
        }
    }

    public void powerOn() {
        powerOn(7000);
    }

    public void powerOn(int loadTime) {
        setVisible(true);
        setRealMax();
        new Thread() {
            @Override
            public void run() {
                try {
                    Container ctbak = getRootPane().getContentPane();

                    ImageIcon anim = FrameUtil.createImageIcon("PowerOn");
                    anim.setImage(anim.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));

                    JPanel newp = new JPanel();
                    JLabel lbl = new JLabel(anim);

                    newp.setBorder(BorderFactory.createEmptyBorder());
                    newp.setLayout(new BorderLayout());
                    newp.add(lbl, BorderLayout.CENTER);
                    getRootPane().setContentPane(newp);
                    sleep(loadTime);
                    getRootPane().setContentPane(ctbak);
                    setFrameBorder(false);
                    interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void powerOff() {
        powerOff(7000);
    }

    public void powerOff(int loadTime) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                    ImageIcon anim = FrameUtil.createImageIcon("PowerOff");
                    anim.setImage(anim.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
                    getRootPane().getContentPane().setVisible(false);
                    getRootPane().getContentPane().removeAll();
                    getRootPane().getContentPane().setLayout(new BorderLayout());
                    getRootPane().getContentPane().add(new JLabel(anim), BorderLayout.CENTER);
                    getRootPane().getContentPane().setVisible(true);
                    sleep(loadTime);
                    System.exit(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void restart() {
        restart(5000);
    }

    public void restart(int loadTime) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                    ImageIcon anim = FrameUtil.createImageIcon("PowerOff");
                    anim.setImage(anim.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
                    getRootPane().getContentPane().setVisible(false);
                    getRootPane().getContentPane().removeAll();
                    getRootPane().getContentPane().setLayout(new BorderLayout());
                    getRootPane().getContentPane().add(new JLabel(anim), BorderLayout.CENTER);
                    getRootPane().getContentPane().setVisible(true);
                    sleep(loadTime);
                    Utils.restartApplication(launchClass);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void hideTooltip() {
        tooltip.setVisible(false);
    }

    public void showTooltip(String text, int x, int y) {
        tiptext.setForeground(FrameUtil.getThemeFontColor(theme));
        tiptext.setText(text);
        tooltip.setLocation(x, y);
        tooltip.setVisible(true);
        tooltip.toFront();
    }

    public void setRealMax() {
        ms.setFullScreen(new java.awt.DisplayMode(800, 600, 16, java.awt.DisplayMode.REFRESH_RATE_UNKNOWN), this);
    }

    public void outRealMax() {
        ms.outFullScreen();
    }

    /**
     * 方向，携带x，y变化量
     */
    protected class Direction {
        public int deltax;
        public int deltay;
        public int curDir;

        public Direction() {
        }

        public Direction(int dx, int dy, int dir) {
            set(dx, dy, dir);
        }

        public void set(int dx, int dy, int dir) {
            deltax = dx;
            deltay = dy;
            curDir = dir;
        }

        static final int CENTER = 0;
        static final int UP = 1;
        static final int DOWN = 2;
        static final int LEFT = 3;
        static final int RIGHT = 4;
        static final int LEFT_UP = 5;
        static final int LEFT_DOWN = 6;
        static final int RIGHT_UP = 7;
        static final int RIGHT_DOWN = 8;
    }
}
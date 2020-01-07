package com.newtorn.ToolkitsCore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyVetoException;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * 自定义Frame窗体 改变Swing丑陋UI
 */
public class MFrame extends JInternalFrame implements FrameTheme {
    public static void main(String[] args) {
        MWFrame mwf = new MWFrame();
        mwf.setProperSize(800, 600);
        mwf.setLocationToCenter();
        mwf.setVisible(true);
        mwf.setTheme(FrameUtil.LIGHT_THEME);

        MFrame mf = new MFrame(mwf);
        JButton btn = new JButton("Lock");
        mf.getContentPane().add(btn);
        mf.setProperSize(400, 300);
        mf.setLocationToCenter();
        mf.setVisible(true);
    }

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
    private boolean isMovable = true;
    private boolean inDragSize = false;
    private Point mouseAt = new Point();
    private Direction dragDir = new Direction();
    private Dimension oldSize = new Dimension();
    private Point oldLocation = new Point();

    /**
     * 符窗口和子窗口
     */
    MWFrame parentFrame = null;

    /**
     * 自定义组件
     */
    private ImageContainer _contentPane = new ImageContainer();
    private JPanel _containPane = new JPanel();
    private JPanel _ctrlArea = new JPanel();
    private JPanel _titleBar = new JPanel();
    private JLabel _titleLab = new JLabel();
    private ImageButton _closeBtn = new ImageButton(_ctrlBtnCloseNormal);
    private ImageButton _minBtn = new ImageButton(_ctrlBtnMinimiseNormal);
    private ImageButton _maxBtn = new ImageButton(_ctrlBtnMaximiseNormal);

    private MouseAdapter _closeBtnMouseAdapter;
    private MouseAdapter _minBtnMouseAdapter;
    private MouseAdapter _maxBtnMouseAdapter;

    /**
     * MFrame创建数量
     */
    protected static LinkedHashMap<String, Integer> frames = new LinkedHashMap<>();

    public MFrame(MWFrame parent) {
        super();
        setTitle(defaultTitle());
        setParentFrame(parent);
        customDecorate();
    }

    public MFrame(String title, MWFrame parent) {
        super(title);
        setParentFrame(parent);
        customDecorate();
    }

    /**
     * MFrame默认标题
     */
    private String defaultTitle() {
        String title = getClass().getSimpleName();
        if (frames.containsKey(title)) {
            title += frames.get(title);
        } else {
            frames.put(title, 1);
        }
        return title;
    }

    /**
     * 设置自定义装饰
     */
    private void customDecorate() {
        // 隐藏默认标题栏装饰
        setOpaque(true);
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

        // 主题设置
        setTheme(theme);

        // 设置自定义容器面板
        _containPane.setOpaque(false);
        _containPane.setLayout(new BorderLayout());
        _contentPane.setLayout(new FlowLayout());
        _containPane.add(_titleBar, BorderLayout.NORTH);
        _containPane.add(_contentPane, BorderLayout.CENTER);
        getRootPane().setContentPane(_containPane);

        /**
         * 添加关闭按钮点击事件
         */
        _closeBtnMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (parentFrame == null) {
                    outMax();
                    System.exit(0);
                } else {
                    try {
                        if (getOnceCacelClose()) {
                            closeStatus = false;
                        } else {
                            outMax();
                            setClosed(true);
                        }
                    } catch (PropertyVetoException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        _closeBtn.addMouseListener(_closeBtnMouseAdapter);

        /**
         * 添加最小化按钮点击事件
         */
        _minBtnMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setMin();
            }
        };
        _minBtn.addMouseListener(_minBtnMouseAdapter);

        /**
         * 添加最大化按钮点击事件
         */
        _maxBtnMouseAdapter = new MouseAdapter() {
            private boolean selected = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                if (selected) {
                    setMax();
                } else {
                    outMax();
                }
            }
        };
        _maxBtn.addMouseListener(_maxBtnMouseAdapter);

        // 添加窗体鼠标按键监听
        _titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!inDragSize) {
                    mouseAt = e.getLocationOnScreen();
                }
            }
        });

        // 添加标题栏鼠标移动事件
        _titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!inDragSize) {
                    if (!isMovable)
                        return;
                    setLocationToParent(e.getLocationOnScreen().x, e.getLocationOnScreen().y);
                    mouseAt = e.getLocationOnScreen();
                }
            }
        });

        // 添加窗体鼠标移动事件
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isResizable() && inDragSize) {
                    setToDragSize(e.getLocationOnScreen());
                    mouseAt = e.getLocationOnScreen();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseAt = e.getLocationOnScreen();
                setCursorState(e.getPoint(), _containPane.getSize(), _containPane.getInsets());
            }
        });

        // 添加窗体移动事件
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                if (parentFrame != null) {
                    toFront();
                }
            }
        });

        // 设置窗体焦点事件
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                setCtrlBtnNormalIcon();
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
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

    private boolean closeStatus = false;
    public void setOnceCancelClose(boolean cs) {
        closeStatus = cs;
    }

    public boolean getOnceCacelClose() {
        return closeStatus;
    }

    @Override
    public void setUI(InternalFrameUI ui) {
        super.setUI(ui);
        setBorder(null);
        setResizable(true);
        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
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
     * 获取内容面板大小
     * 
     * @return
     */
    public Dimension getContentPaneSize() {
        int w = getWidth();
        int h = getHeight();
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
            _containPane.setBorder(null);
        }
    }

    /**
     * 设置窗体最小化
     */
    public void setMin() {
        if (parentFrame == null) {
           return;
        }
        isMovable = true;
        parentFrame.getAppDock().setVisible(true);
        setVisible(false);
        setFrameBorder(false);
    }

    /**
     * 设置窗体退出最小化
     */
    public void outMin() {
        if (parentFrame == null) {
            return;
        }
        setVisible(true);
        setFrameBorder(true);
        toFront();
    }

    /**
     * 设置窗体最大化
     * 
     * @param frame
     */
    public void setMax() {
        if (parentFrame == null) {
            return;
        }
        outMin();
        isMovable = false;
        oldSize.setSize(getWidth(), getHeight());
        oldLocation = getLocation();
        if (parentFrame.getDockPosition() != AppDock.DOCK_BOTTOM) {
            parentFrame.getAppDock().setVisible(false);
        }
        setLocation(0, 0);
        setSize(parentFrame.getContentPaneSize());
        setFrameBorder(false);
    }

    /**
     * 设置窗体退出最大化
     */
    public void outMax() {
        if (parentFrame == null) {
            return;
        }
        isMovable = true;
        parentFrame.getAppDock().setVisible(true);
        setLocation(oldLocation);
        setSize(oldSize);
        setFrameBorder(true);
    }

    /**
     * 设置窗体位置居中
     */
    public void setLocationToCenter() {
        int x = getX() + parentFrame.getDesktopPane().getWidth() / 2 - getWidth() / 2;
        int y = getY() + parentFrame.getDesktopPane().getHeight() / 2 - getHeight() / 2;
        setLocation(x, y);
    }

    /**
     * 设置相对位置
     * 
     * @param x
     * @param y
     */
    public void setRelativeLocation(int x, int y) {
        if (parentFrame == null)
            return;
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
        if (parentFrame != null) {
            w = Math.min(parentFrame.getContentPane().getWidth(), w);
            h = Math.min(parentFrame.getContentPane().getHeight(), h);
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
        int x, y;
        if (parentFrame == null) {
            x = FrameUtil.ScreenSize.width / 2 - getWidth() / 2;
            y = FrameUtil.ScreenSize.height / 2 - getHeight() / 2;
        } else {
            x = parentFrame.getX() + parentFrame.getWidth() / 2 - getWidth() / 2;
            y = parentFrame.getY() + parentFrame.getHeight() / 2 - getHeight() / 2;
        }
        setLocation(x, y);
    }

    @Override
    public void setTheme(int theme) {
        this.theme = theme;
        setFrameBorder(true);
        setTitleColor(FrameUtil.getThemeTitleColor(theme));
        setBarBackground(FrameUtil.getThemeBarColor(theme));
        setBackground(FrameUtil.getThemeBackColor(theme));
    }

    @Override
    public int getTheme() {
        return theme;
    }

    /**
     * 获取父窗体
     * 
     * @return
     */
    public MWFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * 设置父窗体
     */
    public boolean setParentFrame(MWFrame parent) {
        this.parentFrame = parent;
        if (parentFrame != null) {
            theme = parentFrame.getTheme();
            if (parentFrame.getDesktopPane().getIndexOf(this) == -1) {
                parentFrame.getDesktopPane().add(this);
            }
            return parent.childFrames.add(this);
        }
        return false;
    }

    /**
     * 当窗体自身移动时，设置到父窗体的容器位置
     * 
     * @param xOnScreen
     * @param yOnScreen
     */
    public void setLocationToParent(int xOnScreen, int yOnScreen) {
        int x = xOnScreen - mouseAt.x;
        int y = yOnScreen - mouseAt.y;
        x += getLocation().x;
        y += getLocation().y;
        setLocation(x, y);
    }

    /**
     * 设置鼠标拖动大小
     * 
     * @param screenAt 鼠标在屏幕的位置
     */
    public void setToDragSize(Point screenAt) {
        int x = getLocation().x;
        int y = getLocation().y;
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

    public void replaceCloseBtnMouseAdapter(MouseAdapter _closeBtnMouseAdapter) {
        _closeBtn.removeMouseListener(this._closeBtnMouseAdapter);
        this._closeBtnMouseAdapter = _closeBtnMouseAdapter;
        _closeBtn.addMouseListener(this._closeBtnMouseAdapter);
    }

    public void replaceMinBtnMouseAdapter(MouseAdapter _minBtnMouseAdapter) {
        _minBtn.removeMouseListener(this._minBtnMouseAdapter);
        this._minBtnMouseAdapter = _minBtnMouseAdapter;
        _minBtn.addMouseListener(this._minBtnMouseAdapter);
    }

    public void replaceMaxBtnMouseAdapter(MouseAdapter _maxBtnMouseAdapter) {
        _maxBtn.removeMouseListener(this._maxBtnMouseAdapter);
        this._maxBtnMouseAdapter = _maxBtnMouseAdapter;
        _maxBtn.addMouseListener(this._maxBtnMouseAdapter);
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
package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * 可调节透明度按钮
 */
public final class TransparentButton extends JLabel {
    private static final long serialVersionUID = 1L;

    private boolean isBack = false;
    private boolean isNormal = true;
    private Color normalColor = null;
    private Color pressedColor = null;

    public TransparentButton() {
        this(null, new Color(0,0,0,0), new Color(20, 115, 245));
        setForeground(Color.WHITE);
    }

    public TransparentButton(boolean isBack) {
        this();
        setBack(isBack);
    }

    public TransparentButton(String text) {
        this(text, new Color(20, 115, 245));
        setForeground(Color.WHITE);
    }

    public TransparentButton(Icon icon) {
        this(null, new Color(0,0,0,0), new Color(20, 115, 245));
        setIcon(icon);
    }

    public TransparentButton(Icon icon, boolean isBack) {
        this(null, new Color(0,0,0,0), new Color(20, 115, 245));
        setIcon(icon);
        setBack(isBack);
    }

    public TransparentButton(Icon icon, boolean isBack, int squareLen) {
        this(null, new Color(0,0,0,0), new Color(20, 115, 245));
        setIcon(FrameUtil.resizeIcon(icon, squareLen, squareLen));
        setBack(isBack);
    }

    public TransparentButton(Icon icon, Color pressedColor) {
        this(null, new Color(0,0,0,0), pressedColor);
        setIcon(icon);
    }

    public TransparentButton(Color pressedColor) {
        this(null, new Color(0,0,0,0), pressedColor);
    }

    public TransparentButton(Color pressedColor, boolean isBack) {
        this(null, new Color(0,0,0,0), pressedColor);
        setBack(isBack);
    }

    public TransparentButton(String text, Color pressedColor) {
        this(text, new Color(0,0,0,0), pressedColor);
    }

    public TransparentButton(String text, Color normalColor, Color pressedColor) {
        super(text);

        setButtonColor(normalColor, pressedColor);

        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                isNormal = !isNormal;
                updateColor();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isNormal = true;
                updateColor();
            }
        });
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        if (icon != null) {
            setText(null);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (isBack || getIcon() == null) {
            g.setColor(getBackground());
            Rectangle r = g.getClipBounds();
            g.fillRect(r.x, r.y, r.width, r.height);           
            super.paintComponent(g);
        } else {
            super.paintComponent(g);
            g.setColor(getBackground());
            Rectangle r = g.getClipBounds();
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    public void updateColor() {
        if (isNormal) {
            setBackground(normalColor);
        } else {
            setBackground(pressedColor);
        }
    }

    public void setButtonColor(Color normal, Color pressed) {
        this.normalColor = normal;
        this.pressedColor = pressed;
        updateColor();
    }

    public Color getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
        updateColor();
    }

    public Color getPressedColor() {
        return pressedColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
        updateColor();
    }

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean isBack) {
        this.isBack = isBack;
        repaint();
    }
}
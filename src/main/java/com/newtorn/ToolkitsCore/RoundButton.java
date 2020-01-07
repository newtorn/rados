package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class RoundButton extends JButton {

    public static void main(String[] args) {
        javax.swing.JFrame fr = new javax.swing.JFrame();

        RoundButton ib = new RoundButton(FrameUtil.createImage("CtrlBtnCloseNormal"));
        ib.setPreferredSize(new java.awt.Dimension(14, 14));
        fr.getContentPane().setBackground(Color.YELLOW);
        fr.getContentPane().add(ib);
        fr.setLayout(new java.awt.FlowLayout());
        fr.setSize(150, 150);
        fr.setVisible(true);
    }

    private static final long serialVersionUID = 1L;

    public RoundButton(Image icon) {
        if (icon != null) {
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setIconImage(icon);
        } else {
            throw new NullPointerException("Icon is null");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon icon = (ImageIcon) getIcon();
        g.setClip(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
        g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), this);
        if (getModel().isArmed()) {
            g.setColor(new Color(127, 127, 127, 0));
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
        }
        super.paintComponent(g);
    }

    public void setIconImage(Image icon) {
        if (icon != null) {
            setIcon(new ImageIcon(icon));
        }
    }
}
package com.newtorn.ToolkitsCore;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class RoundBorder implements Border {
    public static void main(String[] args) {
        javax.swing.JFrame fr = new javax.swing.JFrame();

        javax.swing.JLabel rb = new javax.swing.JLabel("Hello This is my RoundBorder");
        rb.setBorder(new RoundBorder(Color.RED, new Dimension(15, 15)));
        rb.setBounds(300, 300, 150, 150);

        fr.getContentPane().setBackground(Color.YELLOW);
        fr.getContentPane().add(rb);
        fr.setLayout(new java.awt.FlowLayout());
        fr.setSize(800, 600);
        fr.setVisible(true);
    }

    public RoundBorder() {
        super();
    }

    public RoundBorder(Color color) {
        super();
        borderColor = color;
    }

    public RoundBorder(Dimension arc) {
        super();
        if (arc != null) {
            this.arc = arc;
        }
    }

    public RoundBorder(Color color, Dimension arc) {
        super();
        borderColor = color;
        if (arc != null) {
            this.arc = arc;
        }
    }

    private Dimension arc = new Dimension();
    private Color borderColor = null;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (borderColor != null) {
            g.setColor(borderColor);
        }
        g.drawRoundRect(x, y, c.getSize().width-1, c.getSize().height-1, arc.width, arc.height);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    public void setArc(int width, int height) {
        arc.width = width;
        arc.height = height;
    }

    public Dimension getArc() {
        return arc;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }  
}
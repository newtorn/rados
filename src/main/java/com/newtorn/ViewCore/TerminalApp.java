package com.newtorn.ViewCore;

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import java.awt.event.KeyAdapter;

import com.newtorn.ServiceCore.DiskService;
import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.MWFrame;

public class TerminalApp extends AppFrame {

    private static AppFrame instance = null;

    private TerminalApp() {
        instance = this;
        setAppInfo("Terminal", "Terminal");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        frame.setSize(600, 500);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new TerminalPanel(this), BorderLayout.CENTER);
        frame.toCenterPosition();
    }

    public static TerminalApp getInstance() {
        if (instance == null) {
            instance = new TerminalApp();
        }
        return (TerminalApp)instance;
    }

    enum comList {
        format, quit, exit, halt, echo, clear
    }

    public class TerminalPanel extends JTextArea {
        private static final long serialVersionUID = 1L;

        public TerminalPanel(AppFrame app) {
            setLineWrap(true);
            setWrapStyleWord(true);
            
            append("➜ ");
            setBackground(Color.BLACK);
            setFont(new Font("Serif", Font.BOLD, 14));
            setForeground(Color.WHITE);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    String[] line, temp;
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_BACK_SPACE) {
                        if (getText().endsWith("➜ "))
                            e.consume();
                    }
                    if (key == KeyEvent.VK_UP) {
                        e.consume();
                    }
                    if (key == KeyEvent.VK_ENTER) {
                        e.consume();

                        if (getText().endsWith("➜ ")) {
                            append("\n" + "➜ ");
                            return;
                        }

                        line = getText().split("➜ ");

                        if (line.length == 0) {
                            append("\n" + "➜ ");
                            return;
                        }
                        
                        String tcom = line[line.length - 1];
                        if (tcom.trim().equals("")) {
                            append("\n" + "➜ ");
                            return;
                        }

                        temp = tcom.split(" ");

                        String command = temp[0];

                        try {
                            switch (comList.valueOf(command)) {
                            // 此为目录类的操作
                            case halt:
                                System.exit(0);
                                break;
                            case quit:
                            case exit:
                                app.dispose();
                                break;
                            case format:
                                new DiskService().initData();
                                break;
                            case echo:
                                append("\n" + tcom.replaceFirst("echo ", ""));
                                append("\n" + "➜ ");
                                break;
                            case clear:
                                setText("➜ ");
                            }
                        } catch (Exception ex) {
                            append("\n" + "error: command not found: " + command);
                            append("\n" + "➜ ");
                        }

                    }
                }

            });
            setCaretPosition(getText().length());
        }
    }
}
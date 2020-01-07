package com.newtorn.ViewCore;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.newtorn.BeanCore.Timer;
import com.newtorn.ToolkitsCore.AppDock;
import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.FrameUtil;
import com.newtorn.ToolkitsCore.ImageButton;
import com.newtorn.ToolkitsCore.MWFrame;
import com.newtorn.ToolkitsCore.TransparentPanel;

public class PreferenceApp extends AppFrame {

    private static AppFrame instance = null;

    private PreferenceApp() {
        instance = this;
        setAppInfo("Preference", "Preference");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);

        TransparentPanel ctrlPane = new TransparentPanel(new FlowLayout());
        ImageButton dock_to_left = new ImageButton("Dock To Left");
        ImageButton dock_to_right = new ImageButton("Dock To Right");
        ImageButton dock_to_bottom = new ImageButton("Dock To Bottom");
        ImageButton theme_to_dark = new ImageButton("Theme To Dark");
        ImageButton theme_to_light = new ImageButton("Theme To Light");
        ctrlPane.add(dock_to_left);
        ctrlPane.add(dock_to_right);
        ctrlPane.add(dock_to_bottom);
        ctrlPane.add(theme_to_dark);
        ctrlPane.add(theme_to_light);

        dock_to_left.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                parent.setDockLayout(AppDock.DOCK_LEFT);
            }
        });

        dock_to_right.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                parent.setDockLayout(AppDock.DOCK_RIGHT);
            }
        });

        dock_to_bottom.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                parent.setDockLayout(AppDock.DOCK_BOTTOM);
            }
        });

        theme_to_dark.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                parent.setTheme(FrameUtil.DARK_THEME);
            }
        });

        theme_to_light.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                parent.setTheme(FrameUtil.LIGHT_THEME);
            }
        });

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new DevicePanel(), BorderLayout.CENTER);
        frame.getContentPane().add(ctrlPane, BorderLayout.NORTH);
        frame.setSize(800, 600);
        frame.toCenterPosition();
    }

    public static PreferenceApp getInstance() {
        if (instance == null) {
            instance = new PreferenceApp();
        }
        return (PreferenceApp)instance;
    }

    public static class Display extends JPanel {
        private static final long serialVersionUID = 1L;
        private JPanel timePanel;
        private JPanel silderPanel;
        private JPanel lastPanel;
        private JSlider sb;
        public static int value;
        public static JLabel timelabel;
        private JTextArea jt;

        public Display() {
            this.setLayout(new GridLayout(3, 1));

            this.initSliderPanel();
            this.initLastPanel();

            this.add(timePanel);
            this.add(silderPanel);
            this.add(lastPanel);
        }

        static {
            value = 5;
            timelabel = new JLabel(Timer.getTime(), 2);
        }

        public void initLastPanel() {
            lastPanel = new JPanel();
        }

        public void initSliderPanel() {
            silderPanel = new JPanel();
            jt = new JTextArea(1, 5);
            jt.setEditable(false);
            jt.setText("       " + 5);
            silderPanel.setBorder(new TitledBorder("Time slice"));
            sb = new JSlider(3, 7, 5);
            sb.addChangeListener(new ChangEvent());
            silderPanel.add(sb);
            silderPanel.add(new JLabel("time slice is           "));
            silderPanel.add(new JScrollPane(jt));
        }

        class ChangEvent implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                Display.value = sb.getValue();
                jt.setText("       " + Display.value);
            }
        }
    }

    public static class DevicePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private JPanel[] panel = new JPanel[6];
        private JLabel[] namelabel = new JLabel[6];
        private JLabel[] picturelabel = new JLabel[6];
        static JTextArea[] pswtext = new JTextArea[6];

        public DevicePanel() {
            this.setLayout(new GridLayout(2, 3));
            for (int i = 0; i < 6; i++) {
                if (i < 3) {
                    panel[i] = new JPanel();
                    panel[i].setBorder(new TitledBorder(""));
                    panel[i].setLayout(new GridLayout(3, 1));

                    namelabel[i] = new JLabel("Device A");
                    picturelabel[i] = new JLabel("Printer");

                    panel[i].add(namelabel[i]);
                    panel[i].add(picturelabel[i]);
                    panel[i].add(new JScrollPane(pswtext[i]));

                    this.add(panel[i]);
                }
                if (i >= 3 && i < 5) {
                    panel[i] = new JPanel();
                    panel[i].setBorder(new TitledBorder(""));
                    panel[i].setLayout(new GridLayout(3, 1));

                    namelabel[i] = new JLabel("Device B");
                    picturelabel[i] = new JLabel("CD Disk");

                    panel[i].add(namelabel[i]);
                    panel[i].add(picturelabel[i]);
                    panel[i].add(new JScrollPane(pswtext[i]));

                    this.add(panel[i]);
                }
                if (i == 5) {
                    panel[i] = new JPanel();
                    panel[i].setBorder(new TitledBorder(""));
                    panel[i].setLayout(new GridLayout(3, 1));

                    namelabel[i] = new JLabel("Divece C");
                    picturelabel[i] = new JLabel("Display");

                    panel[i].add(namelabel[i]);
                    panel[i].add(picturelabel[i]);
                    panel[i].add(new JScrollPane(pswtext[i]));

                    this.add(panel[i]);
                }
            }
        }

        static {
            for (int i = 0; i < 6; i++) {
                pswtext[i] = new JTextArea("Free");
                pswtext[i].setEditable(false);
            }
        }

        public static void setText(int index, String text) {
            pswtext[index].setText(text);
        }

        public static void setText(int index, String text, int time) {
            pswtext[index].setText("Process " + text + "\n" + "Time " + time + " seconds");
        }
    }
}
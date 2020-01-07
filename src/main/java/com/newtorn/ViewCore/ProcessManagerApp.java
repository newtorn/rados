package com.newtorn.ViewCore;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JButton;

import com.newtorn.ToolkitsCore.AppFrame;
import com.newtorn.ToolkitsCore.MWFrame;

public class ProcessManagerApp extends AppFrame {

    private static AppFrame instance = null;
    private static ProcessPanel procPanel = new ProcessPanel();
    private static MemoryPanel memPanel = new MemoryPanel();

    private ProcessManagerApp() {
        instance = this;
        setAppInfo("ProcessManager", "ProcessManager");
    }

    @Override
    public void create(MWFrame parent) {
        super.create(parent);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(procPanel, BorderLayout.WEST);
        frame.getContentPane().add(memPanel, BorderLayout.EAST);
        frame.setSize(730, 600);
        frame.toCenterPosition();
    }

    public static ProcessManagerApp getInstance() {
        if (instance == null) {
            instance = new ProcessManagerApp();
        }
        return (ProcessManagerApp)instance;
    }

    public static class ProcessPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private JPanel freeJP;
        private JPanel centerJP;
        private JPanel blockJP;

        static JTextArea freePCB;
        static JTextArea blockPCB;

        static JTextArea PCBname;
        static JTextArea sentence;
        static JTextArea center;
        static JTextArea timeCellDisplay;

        public ProcessPanel() {
            this.setBorder(new TitledBorder("Process View"));
            this.setLayout(new GridLayout(1, 3));
            this.initFreePanel();
            this.add(freeJP);

            this.initCenterJPPanel();
            this.add(centerJP);

            this.initBlockPanel();
            this.add(blockJP);
        }

        static {
            freePCB = new JTextArea();
            PCBname = new JTextArea();
            sentence = new JTextArea();
            center = new JTextArea();
            blockPCB = new JTextArea();
            timeCellDisplay = new JTextArea();
        }

        public void initFreePanel() {
            freeJP = new JPanel();
            freeJP.setBorder(new TitledBorder("Ready Queue"));
            freeJP.setLayout(new BorderLayout());
            freePCB.setEditable(false);
            freeJP.add(new JScrollPane(freePCB));
        }

        public void initBlockPanel() {
            blockJP = new JPanel();
            blockJP.setBorder(new TitledBorder("Block Queue"));
            blockJP.setLayout(new BorderLayout());
            blockPCB.setEditable(false);
            blockJP.add(new JScrollPane(blockPCB));
        }

        public void initCenterJPPanel() {
            centerJP = new JPanel();
            centerJP.setBorder(new TitledBorder("Process View"));
            centerJP.setLayout(new GridLayout(8, 1));
            centerJP.add(new JLabel("Process Name"));
            PCBname.setEditable(false);
            centerJP.add(new JScrollPane(PCBname));
            centerJP.add(new JLabel("Current Instruction"));
            sentence.setEditable(false);
            centerJP.add(new JScrollPane(sentence));
            centerJP.add(new JLabel("Intermediate Result"));
            center.setEditable(false);
            centerJP.add(new JScrollPane(center));
            centerJP.add(new JLabel("Time Slice"));
            timeCellDisplay.setEditable(false);
            centerJP.add(new JScrollPane(timeCellDisplay));
        }

        public static void setPCBfreeQueue(String freeQueue) {
            freePCB.setText(freeQueue);
        }

        public static void setPCBblockQueue(String blockQueue) {
            blockPCB.setText(blockQueue);
        }

        public static void setPCBName(String pcbName) {
            PCBname.setText(pcbName);
        }

        public static void setPCBExeSentence(String sentenceText) {
            sentence.setText(sentenceText);
        }

        public static void setPCBCenter(String centerText) {
            center.setText(centerText);
        }

        public static void setPCBTime(int time) {
            timeCellDisplay.setText("         " + time);
        }
    }

    public static class MemoryPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        static JButton[] jb;

        public MemoryPanel() {
            initMemoryPanel();
        }

        static {
            jb = new JButton[32];
            for (int i = 0; i < 32; i++) {
                jb[i] = new JButton();
                jb[i].setBackground(Color.gray);
                jb[i].setOpaque(true);
                jb[i].setContentAreaFilled(true);
                jb[i].setSize(10, 4);
                jb[i].setEnabled(false);
            }
            jb[0].setBackground(Color.black);
        }

        public void initMemoryPanel() {
            this.setBorder(new TitledBorder("Memory"));
            this.setLayout(new GridLayout(8, 4));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    this.add(jb[i * 4 + j], i, j);
                }
            }
        }

        public static void setApplyBackColor(int num, int color) {
            if (color == -1)
                jb[num].setBackground(Color.BLACK);
            if (color == 0)
                jb[num].setBackground(Color.RED);
            if (color == 1)
                jb[num].setBackground(Color.GREEN);
        }

        public static void setFreeBackColor(int num) {
            jb[num].setBackground(Color.gray);
        }
    }
}

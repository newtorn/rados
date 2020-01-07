package com.newtorn.ToolkitsCore;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/**
 * 编译工具类
 */
public class CompileUtil {
    public static ArrayList<String> complie(MFrame frame, String text) {
        ArrayList<String> array = new ArrayList<String>();
        String[] regex = new String[4];
        regex[0] = "[a-zA-Z]=[0-9];";
        regex[1] = "[a-zA-Z][+-][+-];";
        regex[2] = "![A|B|C][0-9];";
        regex[3] = "end;";
        boolean flag = true;
        int DevA = 0;
        int DevB = 0;
        int DevC = 0;
        char variableA = 0;
        char variableB = 0;
        int endindex = 0;

        Pattern[] p = new Pattern[4];
        outer: for (int i = 0; i < 4; i++) {
            p[i] = Pattern.compile(regex[i]);
            Matcher m = p[i].matcher(text);
            while (m.find()) {
                String s = m.group();
                if (i == 0) {
                    if (variableA == 0) {
                        variableA = s.charAt(0);
                    } else if (variableB == 0 && variableA != s.charAt(0)) {
                        variableB = s.charAt(0);
                    } else if (variableA != s.charAt(0) && variableB != s.charAt(0)) {
                        flag = false;
                        showMessageDialog(frame.getParentFrame(), "Sorry, you just can set 2 variables", "Compile Error");
                        break outer;
                    }
                }

                if (i == 1) {
                    if (s.charAt(0) != variableA && s.charAt(0) != variableB) {
                        flag = false;
                        showMessageDialog(frame.getParentFrame(), "use undefined variable" + s.charAt(0), "Compile Error");
                        break outer;
                    }

                    if (Pattern.matches("[a-zA-Z][+][+];", s) == false
                            && Pattern.matches("[a-zA-Z][-][-];", s) == false) {
                        flag = false;
                        showMessageDialog(frame.getParentFrame(), "illegal code statment: " + s, "Compile Error");
                        break outer;
                    }
                }

                if (i == 2) {
                    if (s.charAt(1) == 'A') {
                        DevA++;
                        if (DevA > 3) {
                            flag = false;
                            showMessageDialog(frame.getParentFrame(), "sorry, max of device A numbers is 3, you allocate more",
                                    "Compile Error");
                            break outer;
                        }
                    }
                    if (s.charAt(1) == 'B') {
                        DevB++;
                        if (DevB > 2) {
                            flag = false;
                            showMessageDialog(frame.getParentFrame(), "sorry, max of device A numbers is 2, you allocate more",
                                    "Compile Error");
                            break outer;
                        }
                    }
                    if (s.charAt(1) == 'C') {
                        DevC++;
                        if (DevC > 1) {
                            flag = false;
                            showMessageDialog(frame.getParentFrame(), "only 1 device C", "Compile Error");
                            break outer;
                        }
                    }
                }

                if (i == 3) {
                    endindex++;
                    if (endindex > 1) {
                        showMessageDialog(frame.getParentFrame(), "more end statement", "Compile Error");
                        break outer;
                    }
                }

                // array.add(s);
            }
        }

        // for(String e : array) System.out.println(e);

        if (flag == true && endindex == 1) {
            String[] ms = text.split(";");
            for (String s : ms) {
                array.add(s + ";");
            }
            return array;
        } else if (endindex == 0) {
            showMessageDialog(frame.getParentFrame(), "lost end statement", "Compile Error");
            return null;
        } else
            return null;
    }

    private static void showMessageDialog(MWFrame frame, String msg, String title) {
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, FrameUtil.ErrorDialogImage);
    }
}

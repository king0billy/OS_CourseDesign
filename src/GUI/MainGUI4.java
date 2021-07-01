/*
 * Created by JFormDesigner on Sat Jun 27 14:12:17 CST 2020
 */

package GUI;

//import SchedulingSystem.Block;
import SchedulingSystem.JOB;
import SchedulingSystem.Scheduling;
import Tool.ToolForSch;
import info.clearthought.layout.*;
import sun.awt.WindowClosingListener;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.*;


public class MainGUI4 extends JFrame {
    public MainGUI4() {
        initComponents();
        renewAll();
        textField9.setText(((Integer)ToolForSch.unitOfTime).toString());
        scheduling.clock.addPropertyChangeListener2(new TimerListener());
    }

    class TimerListener implements PropertyChangeListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            renewAll();
        }
    }

    private void button1ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //添加作业到作业井
        try {
            String name = textField1.getText();
            int submit = Integer.parseInt(textField2.getText());
            int serviceTime = Integer.parseInt(textField3.getText());
            int memoryNeeded = Integer.parseInt(textField4.getText());
            int tapeNeeded = Integer.parseInt(textField5.getText());
            if(submit>0&&serviceTime>0&&memoryNeeded>0&tapeNeeded>0) {
                if (submit <= scheduling.clock.getTime()) {
                    JOptionPane.showMessageDialog(this, "输入井中的预输入不能早于当前时间", "警告", JOptionPane.WARNING_MESSAGE);
                } else if(memoryNeeded>ToolForSch.memoryTol || tapeNeeded>ToolForSch.tapesTol){
                    JOptionPane.showMessageDialog(this, "所需资源超过已有资源", "警告", JOptionPane.WARNING_MESSAGE);
                }else {
                    scheduling.addWell(new JOB(name, submit, serviceTime, memoryNeeded, tapeNeeded));
                    renew2();
                }
            }else {
                JOptionPane.showMessageDialog(this, "请输入正整数", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }catch (Exception e1){
            JOptionPane.showMessageDialog(this, "输入有误", "警告", JOptionPane.WARNING_MESSAGE);
        }finally {
            textField1.setText("");
            textField2.setText("");
            textField3.setText("");
            textField4.setText("");
            textField5.setText("");
        }
    }

    private void button2ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //开始运行
        new Thread(()->{
            if(!scheduling.run && scheduling.well!=null && scheduling.well.size()!= 0) {
                button2.setEnabled(false);
                button3.setEnabled(true);
                button4.setEnabled(true);
                scheduling.run();
            }else {
                JOptionPane.showMessageDialog(this, "当前无可执行的作业", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }).start();
    }

    /**
     * 更新所有
     */
    private void renewAll(){
        renew1();
        renew2();
        renew3();
        renew4();
        renew5();
        renew6();
        renewClock();
        renewTapes();
        table1.updateUI();
        table2.updateUI();
        table3.updateUI();
        table4.updateUI();
        table5.updateUI();
        table6.updateUI();
        if(!scheduling.run){
            button2.setEnabled(true);
            button3.setEnabled(false);
            button4.setEnabled(false);
        }
    }

    /**
     * 更新块表
     */
    private void renew1(){
        DefaultTableModel tableModel = ((DefaultTableModel) table1.getModel());
        tableModel.getDataVector().clear();
/*        for (Block block : scheduling.integerBlockHashMap.values()) {
            tableModel.addRow(block.toArray());
        }*/
    }

    /**
     * 更新输入井
     */
    private void renew2(){
        List<JOB> list = scheduling.well;
        DefaultTableModel tableModel = (DefaultTableModel) table2.getModel();
        tableModel.getDataVector().clear();
        for(JOB job:list){
            tableModel.addRow(job.toArray());
        }
    }

    /**
     * 更新后备队列
     */
    private void renew3(){
        List<JOB> list = scheduling.jobs1;
        DefaultTableModel tableModel = (DefaultTableModel) table3.getModel();
        tableModel.getDataVector().clear();
        for(JOB job:list){
            if(job.getState() == 0) tableModel.addRow(job.toArray());
        }
    }

    /**
     * 更新就绪队列
     */
    private void renew4(){
        List<JOB> list = scheduling.jobs2;
        DefaultTableModel tableModel = (DefaultTableModel) table4.getModel();
        tableModel.getDataVector().clear();
        for(JOB job:list){
            tableModel.addRow(job.toArray());
        }
    }
    /**
     * 更新正在执行
     */
    private void renew5(){
        JOB job = scheduling.nowProcess;
        DefaultTableModel tableModel = (DefaultTableModel) table5.getModel();
        tableModel.getDataVector().clear();
        if(job!=null){
            tableModel.addRow(job.toArray());
        }
    }
    /**
     * 更新完成队列
     */
    private void renew6(){
        List<JOB> list = scheduling.jobs3;
        DefaultTableModel tableModel = (DefaultTableModel) table6.getModel();
        tableModel.getDataVector().clear();
        for(JOB job:list){
            tableModel.addRow(job.toArray());
        }
    }

    /**
     * 更新时钟
     */
    private void renewClock(){
        textField6.setText(((Integer)scheduling.clock.getTime()).toString());
    }

    /**
     * 更新磁带机的情况
     */
    private void renewTapes(){
        textField7.setText(((Integer)ToolForSch.tapesTol).toString());
        textField8.setText(((Integer)scheduling.tapes).toString());
    }
    private void button3ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //时间暂停
        scheduling.clock.stop();
    }

    private void button4ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //时间继续
        scheduling.clock.start();
    }

    private void button5ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //设置时间单位
        try {
            int unitTime = Integer.parseInt(textField10.getText());
            if(unitTime>=1&&unitTime<=10000) {
                ToolForSch.unitOfTime = unitTime;
                textField9.setText(((Integer)unitTime).toString());
            }else {
                JOptionPane.showMessageDialog(this, "请输入适当的毫秒数", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }catch (Exception e1){
            JOptionPane.showMessageDialog(this, "请输入数字", "警告", JOptionPane.WARNING_MESSAGE);
        }finally {
            textField10.setText("");
        }
    }

    private void button6ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //添加块
        try{
            int blockSize = Integer.parseInt(textField11.getText());
            if(blockSize>=0){
                //scheduling.addBlock(new Block(blockSize));
                renew1();
            }else {
                JOptionPane.showMessageDialog(this, "块大小应该大于0", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }catch (Exception e1){
            JOptionPane.showMessageDialog(this, "请输入整型数据", "警告", JOptionPane.WARNING_MESSAGE);
        }finally {
            textField11.setText("");
        }
    }

    private void button7ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //添加磁带机
        try{
            int tapeAdd = Integer.parseInt(textField12.getText());
            if(tapeAdd>=1){
                scheduling.tapes+=tapeAdd;
                ToolForSch.tapesTol+=tapeAdd;
                renewTapes();
            }else {
                JOptionPane.showMessageDialog(this, "磁带机应大于1", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }catch (Exception e1){
            JOptionPane.showMessageDialog(this, "请输入整型数据", "警告", JOptionPane.WARNING_MESSAGE);
        }finally {
            textField12.setText("");
        }
    }

    private void button8ActionPerformed(ActionEvent e) {
        // TODO add your code here
        //合并地址空间
        new Thread(()->{
            //scheduling.spaceToMerge();
            renew1();
        }).start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - 吴泽欣
        panel8 = new JPanel();
        label18 = new JLabel();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        label19 = new JLabel();
        scrollPane2 = new JScrollPane();
        table2 = new JTable();
        label20 = new JLabel();
        scrollPane3 = new JScrollPane();
        table3 = new JTable();
        label21 = new JLabel();
        scrollPane4 = new JScrollPane();
        table4 = new JTable();
        label22 = new JLabel();
        scrollPane5 = new JScrollPane();
        table5 = new JTable();
        label23 = new JLabel();
        scrollPane6 = new JScrollPane();
        table6 = new JTable();
        panel12 = new JPanel();
        panel13 = new JPanel();
        label8 = new JLabel();
        comboBox1 = new JComboBox<>();
        label10 = new JLabel();
        comboBox2 = new JComboBox<>();
        panel1 = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField2 = new JTextField();
        label3 = new JLabel();
        textField3 = new JTextField();
        label4 = new JLabel();
        textField4 = new JTextField();
        label5 = new JLabel();
        textField5 = new JTextField();
        label6 = new JLabel();
        button1 = new JButton();
        panel3 = new JPanel();
        label15 = new JLabel();
        textField10 = new JTextField();
        button5 = new JButton();
        label16 = new JLabel();
        textField11 = new JTextField();
        button6 = new JButton();
        label17 = new JLabel();
        textField12 = new JTextField();
        button7 = new JButton();
        panel2 = new JPanel();
        label12 = new JLabel();
        textField6 = new JTextField();
        label11 = new JLabel();
        textField7 = new JTextField();
        label13 = new JLabel();
        textField8 = new JTextField();
        label14 = new JLabel();
        textField9 = new JTextField();
        button3 = new JButton();
        button4 = new JButton();
        button8 = new JButton();
        button2 = new JButton();

        //======== this ========
        setTitle("\u591a\u9053\u6279\u5904\u7406\u7684\u4e24\u7ea7\u8c03\u5ea6");
        setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel8 ========
        {
            panel8.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax . swing. border .EmptyBorder (
            0, 0 ,0 , 0) ,  "JF\u006frmDes\u0069gner \u0045valua\u0074ion" , javax. swing .border . TitledBorder. CENTER ,javax . swing. border .TitledBorder
            . BOTTOM, new java. awt .Font ( "D\u0069alog", java .awt . Font. BOLD ,12 ) ,java . awt. Color .
            red ) ,panel8. getBorder () ) ); panel8. addPropertyChangeListener( new java. beans .PropertyChangeListener ( ){ @Override public void propertyChange (java .
            beans. PropertyChangeEvent e) { if( "\u0062order" .equals ( e. getPropertyName () ) )throw new RuntimeException( ) ;} } );
            panel8.setLayout(new BoxLayout(panel8, BoxLayout.Y_AXIS));

            //---- label18 ----
            label18.setText("\u5757\u5206\u914d\u8868");
            panel8.add(label18);

            //======== scrollPane1 ========
            {

                //---- table1 ----
                table1.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u5757ID", "\u5757\u5927\u5c0f", "\u8d77\u59cb\u5730\u5740", "\u7ec8\u6b62\u5730\u5740", "\u5269\u4f59\u91cf", "\u5206\u914d\u4f5c\u4e1a"
                    }
                ));
                scrollPane1.setViewportView(table1);
            }
            panel8.add(scrollPane1);

            //---- label19 ----
            label19.setText("\u8f93\u5165\u4e95(\u9884\u8f93\u5165)");
            panel8.add(label19);

            //======== scrollPane2 ========
            {

                //---- table2 ----
                table2.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "ID", "\u540d\u5b57", "\u63d0\u4ea4\u65f6\u95f4", "\u670d\u52a1\u65f6\u95f4", "\u8fd0\u884c\u65f6\u95f4", "\u7ed3\u675f\u65f6\u95f4", "\u5468\u8f6c\u65f6\u95f4", "\u5e26\u6743\u5468\u8f6c", "\u8fd0\u884c\u72b6\u6001", "\u5206\u914d\u72b6\u6001", "\u6240\u9700\u5757\u5927\u5c0f", "\u6240\u5728\u5757", "\u6240\u9700\u78c1\u5e26"
                    }
                ));
                scrollPane2.setViewportView(table2);
            }
            panel8.add(scrollPane2);

            //---- label20 ----
            label20.setText("\u540e\u5907\u961f\u5217");
            panel8.add(label20);

            //======== scrollPane3 ========
            {

                //---- table3 ----
                table3.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "ID", "\u540d\u5b57", "\u63d0\u4ea4\u65f6\u95f4", "\u670d\u52a1\u65f6\u95f4", "\u8fd0\u884c\u65f6\u95f4", "\u7ed3\u675f\u65f6\u95f4", "\u5468\u8f6c\u65f6\u95f4", "\u5e26\u6743\u5468\u8f6c", "\u8fd0\u884c\u72b6\u6001", "\u5206\u914d\u72b6\u6001", "\u6240\u9700\u5757\u5927\u5c0f", "\u6240\u5728\u5757", "\u6240\u9700\u78c1\u5e26"
                    }
                ));
                scrollPane3.setViewportView(table3);
            }
            panel8.add(scrollPane3);

            //---- label21 ----
            label21.setText("\u5c31\u7eea\u961f\u5217");
            panel8.add(label21);

            //======== scrollPane4 ========
            {

                //---- table4 ----
                table4.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "ID", "\u540d\u5b57", "\u63d0\u4ea4\u65f6\u95f4", "\u670d\u52a1\u65f6\u95f4", "\u8fd0\u884c\u65f6\u95f4", "\u7ed3\u675f\u65f6\u95f4", "\u5468\u8f6c\u65f6\u95f4", "\u5e26\u6743\u5468\u8f6c", "\u8fd0\u884c\u72b6\u6001", "\u5206\u914d\u72b6\u6001", "\u6240\u9700\u5757\u5927\u5c0f", "\u6240\u5728\u5757", "\u6240\u9700\u78c1\u5e26"
                    }
                ));
                scrollPane4.setViewportView(table4);
            }
            panel8.add(scrollPane4);

            //---- label22 ----
            label22.setText("\u6b63\u5728\u6267\u884c");
            panel8.add(label22);

            //======== scrollPane5 ========
            {

                //---- table5 ----
                table5.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "ID", "\u540d\u5b57", "\u63d0\u4ea4\u65f6\u95f4", "\u670d\u52a1\u65f6\u95f4", "\u8fd0\u884c\u65f6\u95f4", "\u7ed3\u675f\u65f6\u95f4", "\u5468\u8f6c\u65f6\u95f4", "\u5e26\u6743\u5468\u8f6c", "\u8fd0\u884c\u72b6\u6001", "\u5206\u914d\u72b6\u6001", "\u6240\u9700\u5757\u5927\u5c0f", "\u6240\u5728\u5757", "\u6240\u9700\u78c1\u5e26"
                    }
                ));
                scrollPane5.setViewportView(table5);
            }
            panel8.add(scrollPane5);

            //---- label23 ----
            label23.setText("\u6267\u884c\u5b8c\u6210");
            panel8.add(label23);

            //======== scrollPane6 ========
            {

                //---- table6 ----
                table6.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "ID", "\u540d\u5b57", "\u63d0\u4ea4\u65f6\u95f4", "\u670d\u52a1\u65f6\u95f4", "\u8fd0\u884c\u65f6\u95f4", "\u7ed3\u675f\u65f6\u95f4", "\u5468\u8f6c\u65f6\u95f4", "\u5e26\u6743\u5468\u8f6c", "\u8fd0\u884c\u72b6\u6001", "\u5206\u914d\u72b6\u6001", "\u6240\u9700\u5757\u5927\u5c0f", "\u6240\u5728\u5757", "\u6240\u9700\u78c1\u5e26"
                    }
                ));
                scrollPane6.setViewportView(table6);
            }
            panel8.add(scrollPane6);
        }
        contentPane.add(panel8, BorderLayout.CENTER);

        //======== panel12 ========
        {
            panel12.setLayout(new BoxLayout(panel12, BoxLayout.Y_AXIS));

            //======== panel13 ========
            {
                panel13.setLayout(new GridLayout(2, 2));

                //---- label8 ----
                label8.setText("\u4f5c\u4e1a\u8c03\u5ea6\u7b97\u6cd5");
                label8.setFont(label8.getFont().deriveFont(label8.getFont().getStyle() & ~Font.ITALIC));
                panel13.add(label8);

                //---- comboBox1 ----
                comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
                    "\u77ed\u4f5c\u4e1a\u4f18\u5148"
                }));
                panel13.add(comboBox1);

                //---- label10 ----
                label10.setText("\u8fdb\u7a0b\u8c03\u5ea6\u7b97\u6cd5(\u53ef\u62a2\u5360)");
                panel13.add(label10);

                //---- comboBox2 ----
                comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
                    "\u77ed\u8fdb\u7a0b\u4f18\u5148"
                }));
                panel13.add(comboBox2);
            }
            panel12.add(panel13);

            //======== panel1 ========
            {
                panel1.setLayout(new GridLayout(6, 2));

                //---- label1 ----
                label1.setText("\u4f5c\u4e1a\u540d\u79f0");
                panel1.add(label1);
                panel1.add(textField1);

                //---- label2 ----
                label2.setText("\u63d0\u4ea4\u65f6\u95f4");
                panel1.add(label2);
                panel1.add(textField2);

                //---- label3 ----
                label3.setText("\u4f30\u8ba1\u8fd0\u884c\u65f6\u95f4");
                panel1.add(label3);
                panel1.add(textField3);

                //---- label4 ----
                label4.setText("\u5185\u5b58\u9700\u6c42");
                panel1.add(label4);
                panel1.add(textField4);

                //---- label5 ----
                label5.setText("\u78c1\u5e26\u9700\u6c42");
                panel1.add(label5);
                panel1.add(textField5);
                panel1.add(label6);

                //---- button1 ----
                button1.setText("\u6dfb\u52a0\u4f5c\u4e1a");
                button1.addActionListener(e -> button1ActionPerformed(e));
                panel1.add(button1);
            }
            panel12.add(panel1);

            //======== panel3 ========
            {
                panel3.setLayout(new GridLayout(3, 3));

                //---- label15 ----
                label15.setText("\u8bbe\u7f6e\u5355\u4f4d\u65f6\u95f4(ms)");
                panel3.add(label15);
                panel3.add(textField10);

                //---- button5 ----
                button5.setText("\u8bbe\u7f6e");
                button5.setFont(new Font("\u5b8b\u4f53", Font.BOLD, 12));
                button5.addActionListener(e -> button5ActionPerformed(e));
                panel3.add(button5);

                //---- label16 ----
                label16.setText("\u6dfb\u52a0\u5757");
                panel3.add(label16);
                panel3.add(textField11);

                //---- button6 ----
                button6.setText("\u6dfb\u52a0");
                button6.addActionListener(e -> button6ActionPerformed(e));
                panel3.add(button6);

                //---- label17 ----
                label17.setText("\u6dfb\u52a0\u78c1\u5e26\u673a");
                panel3.add(label17);
                panel3.add(textField12);

                //---- button7 ----
                button7.setText("\u6dfb\u52a0");
                button7.addActionListener(e -> button7ActionPerformed(e));
                panel3.add(button7);
            }
            panel12.add(panel3);

            //======== panel2 ========
            {
                panel2.setLayout(new GridLayout(6, 2));

                //---- label12 ----
                label12.setText("\u65f6\u949f\u5f53\u524d\u65f6\u95f4");
                panel2.add(label12);

                //---- textField6 ----
                textField6.setEditable(false);
                panel2.add(textField6);

                //---- label11 ----
                label11.setText("\u78c1\u5e26\u673a\u603b\u6570");
                panel2.add(label11);

                //---- textField7 ----
                textField7.setEditable(false);
                panel2.add(textField7);

                //---- label13 ----
                label13.setText("\u78c1\u5e26\u673a\u5269\u4f59\u91cf");
                panel2.add(label13);

                //---- textField8 ----
                textField8.setEditable(false);
                panel2.add(textField8);

                //---- label14 ----
                label14.setText("1\u4e2a\u65f6\u95f4\u5355\u4f4d(ms)");
                panel2.add(label14);

                //---- textField9 ----
                textField9.setEditable(false);
                panel2.add(textField9);

                //---- button3 ----
                button3.setText("\u6682\u505c\u6267\u884c");
                button3.addActionListener(e -> button3ActionPerformed(e));
                panel2.add(button3);

                //---- button4 ----
                button4.setText("\u7ee7\u7eed\u6267\u884c");
                button4.addActionListener(e -> button4ActionPerformed(e));
                panel2.add(button4);

                //---- button8 ----
                button8.setText("\u5408\u5e76\u7a7a\u95f4");
                button8.addActionListener(e -> button8ActionPerformed(e));
                panel2.add(button8);

                //---- button2 ----
                button2.setText("\u5f00\u59cb\u8fd0\u884c");
                button2.addActionListener(e -> button2ActionPerformed(e));
                panel2.add(button2);
            }
            panel12.add(panel2);
        }
        contentPane.add(panel12, BorderLayout.WEST);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        try {
            UIManager.setLookAndFeel(
                    //UIManager.getCrossPlatformLookAndFeelClassName()
                    UIManager.getSystemLookAndFeelClassName()
                    //new com.sun.java.swing.plaf.motif.MotifLookAndFeel()
                    //"com.jgoodies.looks.windows.WindowsLookAndFeel"
                    //"com.jgoodies.looks.plastic.PlasticLookAndFeel"
                    //"com.jgoodies.looks.plastic.Plastic3DLookAndFeel"
                    //"com.jgoodies.looks.plastic.PlasticXPLookAndFeel"
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setUIFont();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(1500,700);
        this.setVisible(true);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - 吴泽欣
    private JPanel panel8;
    private JLabel label18;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JLabel label19;
    private JScrollPane scrollPane2;
    private JTable table2;
    private JLabel label20;
    private JScrollPane scrollPane3;
    private JTable table3;
    private JLabel label21;
    private JScrollPane scrollPane4;
    private JTable table4;
    private JLabel label22;
    private JScrollPane scrollPane5;
    private JTable table5;
    private JLabel label23;
    private JScrollPane scrollPane6;
    private JTable table6;
    private JPanel panel12;
    private JPanel panel13;
    private JLabel label8;
    private JComboBox<String> comboBox1;
    private JLabel label10;
    private JComboBox<String> comboBox2;
    private JPanel panel1;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JTextField textField2;
    private JLabel label3;
    private JTextField textField3;
    private JLabel label4;
    private JTextField textField4;
    private JLabel label5;
    private JTextField textField5;
    private JLabel label6;
    private JButton button1;
    private JPanel panel3;
    private JLabel label15;
    private JTextField textField10;
    private JButton button5;
    private JLabel label16;
    private JTextField textField11;
    private JButton button6;
    private JLabel label17;
    private JTextField textField12;
    private JButton button7;
    private JPanel panel2;
    private JLabel label12;
    private JTextField textField6;
    private JLabel label11;
    private JTextField textField7;
    private JLabel label13;
    private JTextField textField8;
    private JLabel label14;
    private JTextField textField9;
    private JButton button3;
    private JButton button4;
    private JButton button8;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private Scheduling scheduling = new Scheduling();
    public static void setUIFont(){
        // 给所有组件设置默认字体.
        Font font=new Font("黑体",Font.PLAIN,20);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, font);
        }
    }


    public static void main(String[] args) {
        new MainGUI4();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakalarka;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFrame;

/**
 *
 * @author Anry
 */
public class AnalysisFrame extends javax.swing.JFrame implements ActionListener {

    /**
     * Creates new form AnalysisFrame
     */
    public AnalysisFrame() throws IOException {
        initComponents();
        jButton1.addActionListener(this);
        /*jTextField1.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                balanceFile = new File(jTextField1.getText());
            }
        });*/

        setBounds(100, 100, 1140, 660);
        jPanel1.setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(true);
        setVisible(true);
    }

    private void setTableValues() throws IOException {
        AnalysisProcessor aP = new AnalysisProcessor(balanceFile);
        List<Triplet<String, Double, Double>> cf = aP.cashFlow();
        int i = 0;
        double cfsum = 0.0;
        double total = 0.0;
        for (Triplet<String, Double, Double> triplet : cf) {
            cfsum += (triplet.getSecond() - triplet.getThird());
            double val = (triplet.getSecond() - triplet.getThird());
            jTable1.setValueAt(triplet.getFirst(), i, 0);
            if (val >= 0) {
                jTable1.setValueAt(val, i, 2);
            } else {
                jTable1.setValueAt(val, i, 1);
            }
            i++;
            switch (i) {
                case 9:
                    jTable1.setValueAt("CF Z PREVÁDZKOVEJ ČINNOSTI", i, 0);
                    if (cfsum > 0) {
                        jTable1.setValueAt(cfsum, i, 2);
                    } else {
                        jTable1.setValueAt(cfsum, i, 1);
                    }
                    i++;
                    total += cfsum;
                    cfsum = 0;
                    break;
                case 12:
                    jTable1.setValueAt("CF Z INVESTIČNEJ ČINNOSTI", i, 0);
                    if (cfsum > 0) {
                        jTable1.setValueAt(cfsum, i, 2);
                    } else {
                        jTable1.setValueAt(cfsum, i, 1);
                    }
                    i++;
                    total += cfsum;
                    cfsum = 0;
                    break;
                case 15:
                    jTable1.setValueAt("CF Z FINANČNEJ ČINNOSTI", i, 0);
                    if (cfsum > 0) {
                        jTable1.setValueAt(cfsum, i, 2);
                    } else {
                        jTable1.setValueAt(cfsum, i, 1);
                    }
                    i++;
                    total += cfsum;
                    cfsum = 0;
                    break;
                default:
                    break;
            }
        }
        jTable1.setValueAt("KONEČNÝ STAV FINANČNÝCH PROSTRIEDKOV", i, 0);
        if (total > 0) {
            jTable1.setValueAt(total, i, 2);
        } else {
            jTable1.setValueAt(total, i, 1);
        }
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new BoldRenderer());
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new BoldRenderer());
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new BoldRenderer());
    }

    private void setSolidityTable() throws IOException {
        AnalysisProcessor aP = new AnalysisProcessor(balanceFile);
        Map<Integer, Triplet<Double, Double, Double>> map = aP.solidity();
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        int i = 3;
        for (Map.Entry<Integer, Triplet<Double, Double, Double>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            Triplet<Double, Double, Double> value = entry.getValue();
            model.addColumn(key);
            jTable2.setValueAt((double)Math.round(value.getFirst()*100.0)/100.0, 0, i);
            jTable2.setValueAt((double)Math.round(value.getSecond()*100.0)/100.0, 1, i);
            jTable2.setValueAt((double)Math.round(value.getThird()*100.0)/100.0, 2, i);
            i++;
        }
        jTable2.getColumnModel().getColumn(3).setCellRenderer(new CustomRenderer());
        jTable2.getColumnModel().getColumn(4).setCellRenderer(new CustomRenderer());
    }

    private void setIndebtednessTable() throws IOException {
        AnalysisProcessor aP = new AnalysisProcessor(balanceFile);
        Map<Integer, List<Double>> map = aP.indebtedness();
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        int i = 4;
        for (Map.Entry<Integer, List<Double>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            List<Double> value = entry.getValue();
            model.addColumn(key);
            int j = 0;
            for (Double double1 : value) {
                double1 = (double)Math.round(double1*100.0)/100.0;
                jTable3.setValueAt(double1, j, i);
                j++;
            }
            i++;
        }
        jTable3.getColumnModel().getColumn(4).setCellRenderer(new CustomRenderer());
        jTable3.getColumnModel().getColumn(5).setCellRenderer(new CustomRenderer());
    }

    private void setRentabilityTable() throws IOException {
        AnalysisProcessor aP = new AnalysisProcessor(balanceFile);
        Map<Integer, List<Double>> map = aP.rentability();
        DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
        int i = 4;
        for (Map.Entry<Integer, List<Double>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            List<Double> value = entry.getValue();
            model.addColumn(key);
            int j = 0;
            for (Double double1 : value) {
                jTable4.setValueAt((double)Math.round(double1*100.0)/100.0, j, i);
                j++;
            }
            i++;
        }
        jTable4.getColumnModel().getColumn(4).setCellRenderer(new CustomRenderer());
        jTable4.getColumnModel().getColumn(5).setCellRenderer(new CustomRenderer());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userD = System.getProperty("user.home");
        System.out.println("Button Clicked!");
        chooser = new FileChooser();
        chooser.updateUI();
        chooser.setCurrentDirectory(new java.io.File(userD + "/Desktop"));
        chooser.setDialogTitle(chooserTitle);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel dokumenty", "xls", "xlsx", "xlsm", "xltx", "xltm");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String dirr = "" + chooser.getCurrentDirectory();
            StringBuilder strB = new StringBuilder(dirr);
            for (int i = 0; i < dirr.length() - 1; i++) {
                if (dirr.charAt(i) == '\\') {
                    strB.setCharAt(i, '/');
                }
            }
            if (dirr.substring(dirr.length() - 1, dirr.length()).equals(".")) {
                dirr = dirr.substring(0, dirr.length() - 1);
                sourceFolder = "" + strB + "" + chooser.getSelectedFile().getName();
            } else {
                sourceFolder = "" + strB + "/" + chooser.getSelectedFile().getName();
            }
            System.out.println("Folder path: " + dirr + " | File Name: " + chooser.getSelectedFile().getName());
            System.out.println(sourceFolder);
            jTextField1.setText(sourceFolder);
            balanceFile = chooser.getSelectedFile();
            try {
                setTableValues();
                setSolidityTable();
                setIndebtednessTable();
                setRentabilityTable();
            } catch (IOException ex) {
                Logger.getLogger(AnalysisFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class CustomRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (table.getValueAt(row, 1).toString().isEmpty() && table.getValueAt(row, 2).toString().isEmpty()) {
                cellComponent.setForeground(Color.BLACK);
            } else if (!table.getValueAt(row, 1).toString().isEmpty() && table.getValueAt(row, 2).toString().isEmpty()) {
                if ((double) value >= Double.parseDouble(table.getValueAt(row, 1).toString())) {
                    cellComponent.setForeground(new Color(0,180,0));
                } else {
                    cellComponent.setForeground(Color.RED);
                }
            } else if (table.getValueAt(row, 1).toString().isEmpty() && !table.getValueAt(row, 2).toString().isEmpty()) {
                if ((double) value <= Double.parseDouble(table.getValueAt(row, 2).toString())) {
                    cellComponent.setForeground(new Color(0,180,0));
                } else {
                    cellComponent.setForeground(Color.RED);
                }
            } else {
                if ((double) value >= Double.parseDouble(table.getValueAt(row, 1).toString()) && (double) value <= Double.parseDouble(table.getValueAt(row, 2).toString())) {
                    cellComponent.setForeground(new Color(0,180,0));
                } else {
                    cellComponent.setForeground(Color.RED);
                }
            }
            return cellComponent;
        }
    }
    
    private class BoldRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if(row == 9 || row == 12 || row == 15 || row == 16){
                cellComponent.setFont(jTable1.getFont().deriveFont(Font.BOLD));
            }
            return cellComponent;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable()/*{
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);
                if (value >= getMode().getValueAt(row,1) && value <= getMode().getValueAt(row,2)) {
                    comp.setBackground(Color.green);
                } else {
                    comp.setBackground(Color.red);
                }
                return comp;
            }

        }*/;
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable()/*{
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);
                if (value >= getMode().getValueAt(row,1) && value <= getMode().getValueAt(row,2)) {
                    comp.setBackground(Color.green);
                } else {
                    comp.setBackground(Color.red);
                }
                return comp;
            }

        }*/;
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable()/*{
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);
                if (value >= getMode().getValueAt(row,1) && value <= getMode().getValueAt(row,2)) {
                    comp.setBackground(Color.green);
                } else {
                    comp.setBackground(Color.red);
                }
                return comp;
            }

        }*/;
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        topPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        exitLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        exitLabel1 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 10, 2, new java.awt.Color(0, 0, 0)));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "", "Úbytky", "Prírastky"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setRowHeight(26);
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(200);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(2).setMaxWidth(200);
        }

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Pohotová", "0.2", "0.6"},
                {"Bežná", "1.0", "1.5"},
                {"Celková", "2.0", "2.5"}
            },
            new String [] {
                "Typ likvidity", "Min. koef.", "Max. koef."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setRowHeight(18);
        jScrollPane3.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable2.getColumnModel().getColumn(1).setPreferredWidth(10);
            jTable2.getColumnModel().getColumn(1).setMaxWidth(50);
            jTable2.getColumnModel().getColumn(2).setPreferredWidth(10);
            jTable2.getColumnModel().getColumn(2).setMaxWidth(50);
        }

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Equity ratio", "", "", "%"},
                {"Dlhodobá zadlženosť aktív", "", "", "%"},
                {"Platobná neschopnosť", "", "1", "koef."},
                {"Úrokové krytie", "3", "", "koef."},
                {"Finančná páka", "", "", "koef."}
            },
            new String [] {
                "Typ zadĺžeností", "Min", "Max", "MJ"
            }
        ));
        jTable3.setRowHeight(18);
        jScrollPane5.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable3.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable3.getColumnModel().getColumn(1).setPreferredWidth(10);
            jTable3.getColumnModel().getColumn(1).setMaxWidth(50);
            jTable3.getColumnModel().getColumn(2).setPreferredWidth(10);
            jTable3.getColumnModel().getColumn(2).setMaxWidth(50);
            jTable3.getColumnModel().getColumn(3).setPreferredWidth(10);
            jTable3.getColumnModel().getColumn(3).setMaxWidth(50);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Výkaz cash flow nepriamou metódou:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Ukazatele likvidity:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Ukazatele zadĺženosti:");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"ROE", "25", "", "%"},
                {"ROA", "15", "", "%"},
                {"ROS", "", "", "%"},
                {"ROCE", "", "", "%"},
                {"Podiel EBITDA v tržbách", "", "", "%"}
            },
            new String [] {
                "Typ zadĺžeností", "Min", "Max", "MJ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable4.setRowHeight(18);
        jScrollPane6.setViewportView(jTable4);
        if (jTable4.getColumnModel().getColumnCount() > 0) {
            jTable4.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable4.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable4.getColumnModel().getColumn(1).setPreferredWidth(10);
            jTable4.getColumnModel().getColumn(1).setMaxWidth(50);
            jTable4.getColumnModel().getColumn(2).setPreferredWidth(10);
            jTable4.getColumnModel().getColumn(2).setMaxWidth(50);
            jTable4.getColumnModel().getColumn(3).setPreferredWidth(10);
            jTable4.getColumnModel().getColumn(3).setMaxWidth(50);
        }

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Ukazatele rentability:");

        jButton1.setText("Otvoriť");

        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField1.setText("Zadajte cieľovú adresu súvahy.");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        topPanel.setBackground(new java.awt.Color(17, 17, 17));
        topPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                topPanelMouseDragged(evt);
            }
        });
        topPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                topPanelMousePressed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 125, 0));
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel4MouseExited(evt);
            }
        });

        exitLabel.setFont(new java.awt.Font("Traditional Arabic", 1, 24)); // NOI18N
        exitLabel.setForeground(new java.awt.Color(255, 255, 255));
        exitLabel.setText("-");
        exitLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 7, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 125, 0));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel3MouseExited(evt);
            }
        });

        exitLabel1.setFont(new java.awt.Font("Traditional Arabic", 1, 24)); // NOI18N
        exitLabel1.setForeground(new java.awt.Color(255, 255, 255));
        exitLabel1.setText("X");
        exitLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitLabel1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitLabel1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitLabel1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bakalarka/Click4_def.png"))); // NOI18N
        jButton4.setBorder(null);
        jButton4.setPreferredSize(new java.awt.Dimension(73, 43));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton4MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton4MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)))
                        .addGap(34, 34, 34)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void exitLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabelMouseClicked
        setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_exitLabelMouseClicked

    private void exitLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabelMouseEntered
        exitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel4.setBackground(new Color(255, 165, 0));
    }//GEN-LAST:event_exitLabelMouseEntered

    private void exitLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabelMouseExited
        jPanel4.setBackground(new Color(255, 125, 0));
    }//GEN-LAST:event_exitLabelMouseExited

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_jPanel4MouseClicked

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered
        jPanel4.setBackground(new Color(255, 165, 0));
        jPanel4.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jPanel4MouseEntered

    private void jPanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseExited
        jPanel4.setBackground(new Color(255, 125, 0));
        jPanel4.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jPanel4MouseExited

    private void exitLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabel1MouseClicked
        dispose();
    }//GEN-LAST:event_exitLabel1MouseClicked

    private void exitLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabel1MouseEntered
        exitLabel1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel3.setBackground(new Color(255, 165, 0));
    }//GEN-LAST:event_exitLabel1MouseEntered

    private void exitLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabel1MouseExited
        jPanel3.setBackground(new Color(255, 125, 0));
    }//GEN-LAST:event_exitLabel1MouseExited

    private void jPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseClicked
        dispose();
    }//GEN-LAST:event_jPanel3MouseClicked

    private void jPanel3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseEntered
        jPanel3.setBackground(new Color(255, 165, 0));
        jPanel3.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jPanel3MouseEntered

    private void jPanel3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseExited
        jPanel3.setBackground(new Color(255, 125, 0));
        jPanel3.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jPanel3MouseExited

    private void topPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topPanelMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();

        this.setLocation(x - mouseX, y - mouseY);
    }//GEN-LAST:event_topPanelMouseDragged

    private void topPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topPanelMousePressed
        mouseX = evt.getX();
        mouseY = evt.getY();
    }//GEN-LAST:event_topPanelMousePressed

    private void jButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseEntered
        ImageIcon img = new ImageIcon(this.getClass().getResource("Click4_hover.png"));
        jButton4.setIcon(img);
        jButton4.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jButton4MouseEntered

    private void jButton4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseExited
        ImageIcon img = new ImageIcon(this.getClass().getResource("Click4_def.png"));
        jButton4.setIcon(img);
        jButton4.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jButton4MouseExited

    private void jButton4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MousePressed
        ImageIcon img = new ImageIcon(this.getClass().getResource("Click4_clicked.png"));
        jButton4.setIcon(img);
    }//GEN-LAST:event_jButton4MousePressed

    private void jButton4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseReleased
        ImageIcon img = new ImageIcon(this.getClass().getResource("Click4_hover.png"));
        jButton4.setIcon(img);
    }//GEN-LAST:event_jButton4MouseReleased

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        dispose();
    }//GEN-LAST:event_jButton4MouseClicked

                                
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnalysisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnalysisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnalysisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnalysisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new AnalysisFrame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(AnalysisFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private FileChooser chooser;
    private String sourceFolder;
    private String chooserTitle;
    private File balanceFile;
    private int mouseX, mouseY;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel exitLabel;
    private javax.swing.JLabel exitLabel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}

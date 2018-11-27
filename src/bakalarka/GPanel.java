/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakalarka;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.util.Pair;
import javax.swing.JPanel;

/**
 *
 * @author Anry
 */
public class GPanel extends JPanel {

    private Map<Date, Double> map;
    private int x, y, oy;
    private int i = 0;
    private int monthCounter = 0;
    private Map<String, List<Pair<Date, Double>>> profitMap;
    private Map<Date, Double> salesMapG;
    private Map<Date, Double> salesPlayMapG;

    public GPanel() {
        setBounds(0, 0, 800, 800);
        x = 50;
        y = (int) Math.ceil(getHeight() / 2) + 100;
        oy = y;
    }

    public void setMap(Map<Date, Double> map) {
        this.map = map;
    }

    public void setProfitMap(Map<String, List<Pair<Date, Double>>> profitMap) {
        this.profitMap = profitMap;
    }

    public Map<Date, Double> getMap() {
        return map;
    }

    public Map<String, List<Pair<Date, Double>>> getProfitMap() {
        return profitMap;
    }

    public void setSalesMaps(Map<String, Pair<Date, Double>> salesMap) {
        salesMapG = new TreeMap<>();
        salesPlayMapG = new TreeMap<>();
        for (Map.Entry<String, Pair<Date, Double>> entry : salesMap.entrySet()) {
            String key = entry.getKey();
            Pair<Date, Double> value = entry.getValue();
            if (salesMapG.containsKey(value.getKey())) {
                salesMapG.put(value.getKey(), salesMapG.get(value.getKey()) + value.getValue());
            } else {
                salesMapG.put(value.getKey(), value.getValue());
            }
            if (key.contains("C")) {
                if (salesPlayMapG.containsKey(value.getKey())) {
                    salesPlayMapG.put(value.getKey(), salesPlayMapG.get(value.getKey()) + value.getValue());
                } else {
                    salesPlayMapG.put(value.getKey(), value.getValue());
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(50, 50, 50, getHeight());
        g.drawLine(getWidth() - 100, y, 50, y);
        g.drawString("Dátum", getWidth() - 100, y + 15);

        g.fillOval(x, y, 3, 3);

    }

    public void measureBar(Graphics g, int radius, int radiusBase) {
        int stock = radiusBase / radius;
        int yZero = y;
        int howMany = (int) Math.floor(yZero / (stock * 5));
        for (int j = 1; j <= howMany; j++) {
            g.drawLine(45, yZero - j * 5 * stock, 55, yZero - j * 5 * stock);
            g.drawString(String.valueOf((j * 5)), 30, yZero - j * 5 * stock + 5);
        }
        howMany = (int) Math.floor((yZero - 200) / (stock * 5));
        for (int j = 1; j <= howMany; j++) {
            g.drawLine(45, yZero + j * 5 * stock, 55, yZero + j * 5 * stock);
            g.drawString(String.valueOf((j * (-5))), 25, yZero + j * 5 * stock + 5);
        }
    }

    public void paintCashFlow(Graphics g) {
        paint(g);
        g.drawString("CashFlow [tis €]", 60, 60);
        measureBar(g, 100, 1000);
        g.setColor(Color.RED);

        for (Map.Entry<Date, Double> entry : map.entrySet()) {
            Date key = entry.getKey();
            Double value = entry.getValue();
            Gapp(g, key, value, map.size(), 100);
        }
    }

    public void paintSales(Graphics g) {
        paint(g);
        g.drawString("Obrat [100-tis €]", 60, 60);
        measureBar(g, 5000, 100000);
        g.setColor(Color.RED);
        double salesT = 0;

        for (Map.Entry<Date, Double> entry : salesMapG.entrySet()) {
            Date key = entry.getKey();
            Double value = entry.getValue();
            salesT += value;
            Gapp(g, key, salesT, salesMapG.size(), 5000);
        }
        y = (int) Math.ceil(getHeight() / 2) + 100;
        x = 50;
        oy = y;
        g.setColor(Color.blue);
        double salesP = 0;
        for (Map.Entry<Date, Double> entry : salesPlayMapG.entrySet()) {
            Date key = entry.getKey();
            Double value = entry.getValue();
            salesP += value;
            Gapp(g, key, salesP, salesPlayMapG.size(), 5000);
        }
    }

    public void paintProfit(Graphics g) {
        paint(g);
        g.drawString("Zisk [tis €]", 60, 60);
        measureBar(g, 150, 1000);
        g.setColor(Color.RED);

        Map<Date, Double> nMap = new TreeMap<>();
        for (Map.Entry<String, List<Pair<Date, Double>>> entry : profitMap.entrySet()) {
            double profit = 0;
            Date date = null;
            String key = entry.getKey();
            List<Pair<Date, Double>> value = entry.getValue();
            date = value.get(value.size() - 1).getKey();
            for (Pair<Date, Double> pair : value) {
                profit += pair.getValue();
            }
            nMap.put(date, profit);
        }
        for (Map.Entry<Date, Double> entry : nMap.entrySet()) {
            Date key = entry.getKey();
            Double value = entry.getValue();
            histoApp(g, key, value, nMap.size(), 150);
        }
        x = 50;
        y = (int) Math.ceil(getHeight() / 2);
        double totalProfit = 0;
        for (Map.Entry<Date, Double> entry : nMap.entrySet()) {
            Date key = entry.getKey();
            Double value = entry.getValue();
            totalProfit += value;
            Gapp(g, key, totalProfit, nMap.size(), 150);
        }
    }

    private void histoApp(Graphics g, Date date, double profit, int size, int radius) {
        g.setColor(Color.blue);
        int ax = 0, by = 0;
        ax = (x + (int) ((getWidth() - 150) / size));
        x = ax;
        by = (int) (profit / radius);
        if (profit > 0) {
            g.drawRect(x, y - by, 2, by);
        } else {
            g.drawRect(x, y, 2, (-1) * by);
        }
    }

    private void Gapp(Graphics g, Date key, Double value, int size, int radius) {
        try {
            Thread.sleep(100);
            int ax = 0, by = 0, oldx = x, oldy = oy;
            ax = x + (int) ((getWidth() - 150) / size);

            if (monthCounter <= key.getMonth()) {
                Font font = new Font(null, Font.BOLD, 14);
                AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(75), 0, 0);
                Font rotatedFont = font.deriveFont(affineTransform);
                g.setFont(rotatedFont);
                g.setColor(Color.BLACK);
                Format formatter = new SimpleDateFormat("dd-MM-yyyy");
                String str = formatter.format(key);
                g.drawString(str, ax, y + 30);
                monthCounter++;
                g.setColor(Color.RED);
            }

            by = y;
            //System.out.println(pairs.get(i));
            by -= (int) (value / radius);
            /*if(by<=400){
                g.setColor(Color.GREEN);
            }*/
            g.drawLine(oldx, oldy, ax, by);
            x = ax;
            oy = by;
            i++;
        } catch (Exception e) {
            System.out.println("Error ocures!");
            i = 0;
            x = 50;
            y = (int) Math.ceil(getHeight() / 2);
            monthCounter = 0;
        }
    }

    private void measureBarTT(Graphics g, int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

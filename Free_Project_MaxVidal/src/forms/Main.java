package forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private JPanel panelMain;
    private JLabel labelTrash;
    private JLabel heart;

    public Main() {
        panelMain.setPreferredSize(new Dimension(800, 600));
        panelMain.setSize(new Dimension(800, 600));
        panelMain.setLayout(null);

        //TITLE
        JPanel panelTitle = new JPanel();
        panelTitle.setLocation(0,0);
        panelTitle.setSize(panelMain.getWidth(), 50);
        panelTitle.setBackground(Color.GRAY);

        panelMain.add(panelTitle);

        //PANEL CENETR
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(null);
        panelCenter.setLocation(0,panelTitle.getHeight());
        panelCenter.setSize(panelMain.getWidth(), panelMain.getHeight()-panelTitle.getHeight());
        panelCenter.setBackground(new Color(255,237,208));

        panelMain.add(panelCenter);


        //LABEL TRASH
        labelTrash = new JLabel();
        labelTrash.setSize(300,150);
        labelTrash.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        labelTrash.setLocation(
                panelCenter.getWidth()/2 - labelTrash.getWidth()/2,
                panelCenter.getHeight() - (labelTrash.getHeight()*2)
        );
        panelCenter.add(labelTrash);
        labelTrash.setLayout(null);

        //HEART
        heart = new JLabel();
        heart.setOpaque(true);
        heart.setSize(30,30);

        heart.setBackground(new Color(255,237,208));
        ImageIcon heartIcon = new ImageIcon("src/assets/heart.png");
        Icon icon = new ImageIcon(
                heartIcon.getImage().getScaledInstance(heart.getWidth(), heart.getHeight(), Image.SCALE_DEFAULT)
        );

        heart.setIcon(icon);

        heart.setBounds((labelTrash.getWidth()/2)-15, (labelTrash.getHeight()/2)-15, 30, 30);

        heart.setVisible(true);
        heart.setFocusable(Boolean.TRUE);
        labelTrash.add(heart);
    }

    private void configurarTeclas() {

        InputMap im = labelTrash.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = heart.getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "moverArriba");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moverAbajo");
        im.put(KeyStroke.getKeyStroke("LEFT"), "moverIzquierda");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moverDerecha");

        


        am.put("moverArriba", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mover(0, -VELOCIDAD);
            }
        });
        am.put("moverAbajo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mover(0, VELOCIDAD);
            }
        });
        am.put("moverIzquierda", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mover(-VELOCIDAD, 0);
            }
        });
        am.put("moverDerecha", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mover(VELOCIDAD, 0);
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocation(350,100);


        //Icona de ventana
        Toolkit pantalla = Toolkit.getDefaultToolkit();
        Image icon = pantalla.getImage("src/logo.png");
        frame.setIconImage(icon);


    }
}

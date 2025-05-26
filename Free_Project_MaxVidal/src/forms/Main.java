package forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Main {
    //GENERAL PANELS
    private JPanel panelMain;
    private JLayeredPane panelCenter;

    //GAME ZONE
    private JLabel box;
    private JLabel heart;

    //HUD
    private JPanel hud;
    private JLabel labelHP;
    private JLabel pointsLabel;
    private Integer points;

    //BOSS
    private JLabel boss;
    private JLayeredPane labelBossHP;
    private JLabel bossHP;

    //ATTACKS
    private JLabel leftAttack;
    private JLabel centerAttack;
    private JLabel rightAttack;
    private JLabel attackZone;
    private JLabel nextAttack;

    //CRONO
    private Integer seg;

    //GAME
    private Integer hp = 1000;
    private Integer puntuacioFinal = 0;
    private String username;

    //TIMERS
    private Timer userHP;
    private Timer lowerBossHP;
    private Timer hitTimer;
    private Timer gameTimer;
    private Timer pointsTimer;

    public Main() {
        panelMain.setPreferredSize(new Dimension(800, 600));
        panelMain.setSize(new Dimension(800, 600));
        panelMain.setLayout(null);

        //HUD
        hud = (JPanel) hud();
        panelMain.add(hud);

        //PANEL CENETR
        panelCenter = (JLayeredPane) panelCenter();
        panelMain.add(panelCenter());

        //BOX
        box = (JLabel) box();
        panelCenter.add(box);

        //HEART
        heart = (JLabel) heart();

        box.add(heart);

        box.setFocusable(true);
        box.requestFocusInWindow();

        heartMove(box,heart);

        //labelHP
        labelHP = (JLabel) labelHP();
        hud.add(labelHP);

        //BOSS
        boss = (JLabel) boss();
        panelCenter.add(boss,Integer.valueOf(0));

        //POINTS
        pointsLabel = new JLabel("0 Points");
        pointsLabel = (JLabel) pointsLabel();
        hud.add(pointsLabel);

        //AVISO DE ATAQUE
        nextAttack = new JLabel();
        box.add(nextAttack);
        nextAttack.setVisible(false);

        //ATTACKS
        leftAttack = new JLabel();
        centerAttack = new JLabel();
        rightAttack = new JLabel();
        attackZone = new JLabel();

        panelCenter.add(leftAttack(),Integer.valueOf(1));
        panelCenter.add(centerAttack(),Integer.valueOf(1));
        panelCenter.add(rightAttack(),Integer.valueOf(1));
        panelCenter.add(attackZone,Integer.valueOf(1));

        attackVibration(leftAttack);
        attackVibration(centerAttack);
        attackVibration(rightAttack);

        leftAttack.setVisible(false);
        centerAttack.setVisible(false);
        rightAttack.setVisible(false);
        attackZone.setVisible(false);


        // BOSS HP LABEL
        labelBossHP = (JLayeredPane) labelBossHP();
        panelCenter.add(labelBossHP);


        // BOSS HP COLOR LABEL
        bossHP = (JLabel) bossHP();
        labelBossHP.add(bossHP,Integer.valueOf(0));

        //GAME
        game();
        hpTimer();
        hit();
        lowBossHP();

    }

    private void finalJocLose(){
        new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //YOU LOSE

                JOptionPane.showMessageDialog(null, "YOU LOSE");
                System.exit(0);

                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    private void finalJocWin(){
        new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //FI DEL JOC
                puntuacioFinal = points + (hp*3);

                username = JOptionPane.showInputDialog("Username:");
                if (username != null) {

                    while (Database.comprovarUser(username)) {
                        JOptionPane.showMessageDialog(null, "Error. Username is already in use");
                        username = JOptionPane.showInputDialog("Username:");
                    }

                    JOptionPane.showMessageDialog(null, username+" points: "+puntuacioFinal);
                    Database.insertarUsuari(username);
                    Database.insertarPartida(username,puntuacioFinal);

                    JOptionPane.showMessageDialog(null, Database.top());
                    System.exit(0);
                }

                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    private void lowBossHP(){
        lowerBossHP = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int remainingBossHP = bossHP.getWidth();
                bossHP.setSize(remainingBossHP-2, bossHP.getHeight());

                if (remainingBossHP <= 0) {
                    userHP.stop();
                    lowerBossHP.stop();
                    hitTimer.stop();
                    gameTimer.stop();
                    pointsTimer.stop();
                    ((Timer) e.getSource()).stop();
                    box.setFocusable(false);

                    finalJocWin();
                }
            }
        });
        lowerBossHP.start();
    }

    private void hpTimer(){
        userHP = new Timer(1, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                labelHP.setText(String.valueOf(hp)+" / 1000");

                //LOSE
                if (hp <= 0){

                    labelHP.setText("0 / 1000");

                    userHP.stop();
                    lowerBossHP.stop();
                    hitTimer.stop();
                    gameTimer.stop();
                    pointsTimer.stop();
                    ((Timer) e.getSource()).stop();
                    box.setFocusable(false);

                    ImageIcon heartIcon = new ImageIcon("src/assets/death.png");
                    Icon icon = new ImageIcon(
                            heartIcon.getImage().getScaledInstance(30, 25, Image.SCALE_DEFAULT)
                    );

                    heart.setIcon(icon);

                    finalJocLose();
                }
            }
        });
        userHP.start();
    }

    private void hit(){
        hitTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JLabel user = heart;

                int userX = user.getX();
                int[] userPixels = new int[user.getWidth()];

                for (int i = 0; i < userPixels.length; i++) {
                    int pixel = userX+i;
                    userPixels[i] = pixel;
                }

                JLabel actualAttackZone = attackZone;
                int[] attackPixels = new int[actualAttackZone.getWidth()];
                for (int i = 0; i < attackPixels.length; i++) {
                    int pixel = (actualAttackZone.getX()-box.getX())+i;
                    attackPixels[i] = pixel;
                }

                for (int i : attackPixels) {
                    for (int j : userPixels) {
                        if (i == j) {
                            if (actualAttackZone.isVisible()) {
                                hp--;
                                break;
                            }
                        }
                    }
                }
            }
        });
        hitTimer.start();
    }

    private void game(){
        seg = 0;
        gameTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seg++;

                if (seg % 5 == 3){
                    attackZone = (JLabel) patronesAtaque();
                    nextAttack.setVisible(true);
                }

                if (seg % 5 == 0){
                    nextAttack.setVisible(false);
                    attackZone.setVisible(true);


                    new Timer(2000, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            leftAttack.setVisible(false);
                            centerAttack.setVisible(false);
                            rightAttack.setVisible(false);
                            ((Timer) e.getSource()).stop();
                        }
                    }).start();
                }

            }
        });
        gameTimer.start();
    }

    private Component bossHP(){
        bossHP = new JLabel();
        bossHP.setOpaque(true);
        bossHP.setBackground(new Color(89,0,153));
        bossHP.setBounds(
                0,
                0,
                labelBossHP.getWidth(),labelBossHP.getHeight()
        );
        bossHP.setVisible(true);
        bossHP.setLayout(null);

        return bossHP;
    }

    private Component labelBossHP(){
        labelBossHP = new JLayeredPane();

        labelBossHP.setOpaque(true);
        labelBossHP.setBackground(new Color(255,237,208));

        labelBossHP.setSize(boss.getWidth(),15);

        labelBossHP.setBounds(
                (boss.getX()),
                (boss.getY()-labelBossHP.getHeight())-5,
                labelBossHP.getWidth(),labelBossHP.getHeight()
        );

        labelBossHP.setVisible(true);
        labelBossHP.setLayout(null);

        //BORDER
        JLabel border = new JLabel();
        border.setOpaque(false);
        border.setBorder(BorderFactory.createLineBorder(Color.BLACK,3));
        border.setBounds(
                0,
                0,
                labelBossHP.getWidth(),labelBossHP.getHeight()
        );
        border.setVisible(true);
        border.setLayout(null);

        labelBossHP.add(border,Integer.valueOf(1));

        return labelBossHP;
    }

    private Component nextAttack(JLabel attackZone){
        nextAttack.setOpaque(true);
        nextAttack.setBackground(new Color(187,91,255,70));

        nextAttack.setLayout(null);
        nextAttack.setVisible(true);
        nextAttack.setSize((box.getWidth()/3)-10, panelCenter.getHeight()-200);
        nextAttack.setBounds(attackZone.getX()-box.getX(), 2, (box.getWidth()/3)-5, box.getHeight()-4);

        return nextAttack;
    }

    private Component patronesAtaque() {
        Random rand = new Random();
        int zone = rand.nextInt(3);

        switch (zone) {
            case 0:
                attackZone = leftAttack;
                break;
            case 1:
                attackZone = centerAttack;
                break;
            case 2:
                attackZone = rightAttack;
                break;
        }
        nextAttack = (JLabel) nextAttack(attackZone);

        return attackZone;
    }

    private static void heartMove(JLabel box, JLabel heart) {
        box.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int x = heart.getX();
                int y = heart.getY();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: x -= 4; break;
                    case KeyEvent.VK_RIGHT: x += 4; break;
                    case KeyEvent.VK_UP: y -= 4; break;
                    case KeyEvent.VK_DOWN: y += 4; break;
                }

                if (x <= 4){
                    x = 4;
                }
                if (y <= 4){
                    y = 4;
                }
                if (x >= box.getWidth()-heart.getWidth()-4){
                    x = box.getWidth()-heart.getWidth()-4;
                }
                if (y >= box.getHeight()-heart.getHeight()-4){
                    y = box.getHeight()-heart.getHeight()-4;
                }

                heart.setLocation(x, y);
            }
        });
    }

    private static void attackVibration(JLabel attack){
        final int[] count = {0}; // cuántas veces se mueve

        Timer attackCrono = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dx = (count[0] % 2 == 0) ? 1 : -1; // alternar movimiento
                attack.setLocation(attack.getX() + dx, attack.getY());
                count[0]++;
            }
        });
        attackCrono.start();
    }

    private Component rightAttack(){
        leftAttack.setOpaque(false);

        ImageIcon attackIcon = new ImageIcon("src/assets/attack.png");
        Icon icon = new ImageIcon(
                attackIcon.getImage().getScaledInstance(box.getWidth()/3, panelCenter.getHeight()-200, Image.SCALE_DEFAULT)
        );
        rightAttack.setIcon(icon);

        rightAttack.setSize(box.getWidth()/3, panelCenter.getHeight()-200);
        rightAttack.setBounds(box.getX()+(rightAttack.getWidth()*2), box.getY()-100, box.getWidth()/3, panelCenter.getHeight()-200);

        rightAttack.setLayout(null);
        rightAttack.setVisible(true);

        return rightAttack;
    }

    private Component centerAttack(){
        leftAttack.setOpaque(false);

        ImageIcon attackIcon = new ImageIcon("src/assets/attack.png");
        Icon icon = new ImageIcon(
                attackIcon.getImage().getScaledInstance(box.getWidth()/3, panelCenter.getHeight()-200, Image.SCALE_DEFAULT)
        );
        centerAttack.setIcon(icon);

        centerAttack.setSize(box.getWidth()/3, panelCenter.getHeight()-200);
        centerAttack.setBounds((box.getX()+box.getWidth()/2)-(centerAttack.getWidth()/2), box.getY()-100, box.getWidth()/3, panelCenter.getHeight()-200);

        centerAttack.setLayout(null);
        centerAttack.setVisible(true);

        return centerAttack;
    }

    private Component leftAttack(){
        leftAttack.setOpaque(false);

        ImageIcon attackIcon = new ImageIcon("src/assets/attack.png");
        Icon icon = new ImageIcon(
                attackIcon.getImage().getScaledInstance(box.getWidth()/3, panelCenter.getHeight()-200, Image.SCALE_DEFAULT)
        );
        leftAttack.setIcon(icon);

        leftAttack.setSize(box.getWidth()/3, panelCenter.getHeight()-200);
        leftAttack.setBounds(box.getX(), box.getY()-100, box.getWidth()/3, panelCenter.getHeight()-200);

        leftAttack.setLayout(null);
        leftAttack.setVisible(true);

        return leftAttack;
    }

    private Component pointsLabel() {
        points = 0;
        pointsTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                points++;
                pointsLabel.setText(String.valueOf(points)+" Points");
            }
        });
        pointsTimer.start();

        pointsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        pointsLabel.setOpaque(true);
        pointsLabel.setBackground(new Color(255,237,208));

        pointsLabel.setSize(90,40);
        pointsLabel.setBounds(
                (hud.getWidth()/2)-(pointsLabel.getWidth()/2),
                (hud.getHeight()/4)-(pointsLabel.getHeight()/2),
                pointsLabel.getWidth(),pointsLabel.getHeight()
        );

        pointsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pointsLabel.setVerticalAlignment(SwingConstants.CENTER);

        pointsLabel.setVisible(true);
        return pointsLabel;
    }

    private Component boss(){
        boss = new JLabel();
        boss.setOpaque(false);
        boss.setSize(400,200);

        ImageIcon bossIcon = new ImageIcon("src/assets/boss.png");
        Icon icon = new ImageIcon(
                bossIcon.getImage().getScaledInstance(400, 200, Image.SCALE_DEFAULT)
        );
        boss.setIcon(icon);

        boss.setBounds((box.getX()/2), (box.getY()-box.getHeight())-52, boss.getWidth(), boss.getHeight());

        boss.setLayout(null);
        boss.setVisible(true);
        return boss;
    }

    private Component hud(){
        hud = new JPanel();
        hud.setSize(200, panelMain.getHeight());
        hud.setLocation(0,0);
        hud.setBackground(new Color(181,181,181));
        hud.setLayout(null);
        return hud;
    }

    private Component labelHP(){
        JLabel labelHP = new JLabel();
        labelHP.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        labelHP.setOpaque(true);
        labelHP.setBackground(new Color(255,237,208));

        labelHP.setSize(75,75);
        labelHP.setBounds(
                (hud.getWidth()/2)-(labelHP.getWidth()/2),
                (hud.getHeight()/4)+(hud.getHeight()/4)+(hud.getHeight()/4)-(labelHP.getHeight()/2),
                labelHP.getWidth(),labelHP.getHeight()
        );

        labelHP.setText("20 / 20");

        labelHP.setHorizontalAlignment(SwingConstants.CENTER);
        labelHP.setVerticalAlignment(SwingConstants.CENTER);

        labelHP.setVisible(true);
        labelHP.setLayout(null);

        return labelHP;
    }

    private Component panelCenter(){
        panelCenter = new JLayeredPane();
        panelCenter.setOpaque(true);
        panelCenter.setLayout(null);
        panelCenter.setLocation(hud.getWidth(),0);
        panelCenter.setSize(panelMain.getWidth()-hud.getWidth(), panelMain.getHeight());
        panelCenter.setBackground(new Color(255,237,208));
        return panelCenter;
    }

    private Component box(){
        box = new JLabel();
        box.setSize(300,150);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK,4));

        box.setLocation(
                panelCenter.getWidth()/2 - box.getWidth()/2,
                panelCenter.getHeight() - (box.getHeight()*2)
        );
        box.setLayout(null);
        return box;
    }

    private Component heart(){
        heart = new JLabel();
        heart.setOpaque(false);

        ImageIcon heartIcon = new ImageIcon("src/assets/heart.png");
        Icon icon = new ImageIcon(
                heartIcon.getImage().getScaledInstance(30, 25, Image.SCALE_DEFAULT)
        );

        heart.setIcon(icon);

        heart.setBounds((box.getWidth()/2)-15, (box.getHeight()/2)-15, 30, 30);
        heart.setLayout(null);
        heart.setVisible(true);
        return heart;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Kronos: God of the time");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocation(350,100);


        //Icona de ventana
        Toolkit pantalla = Toolkit.getDefaultToolkit();
        Image icon = pantalla.getImage("src/assets/logo.png");
        frame.setIconImage(icon);


    }
}

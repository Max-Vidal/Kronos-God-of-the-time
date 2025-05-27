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

    //FUNCIÓ PERDRE LA PARTIDA
    private void finalJocLose(){
        //Un cop acabada la partida s'espera 2 segons
        new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //Mostra un missatge informant que s'ha perdut la partida i surt de l'aplicació
                JOptionPane.showMessageDialog(null, "YOU LOSE");
                System.exit(0);

                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    //FUNCIÓ GUANYAR LA PARTIDA
    private void finalJocWin(){
        //Un cop acabada la partida s'espera 2 segons
        new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Calcula la puntuació final que ha aconseguit l'usuari
                puntuacioFinal = points + (hp*3);

                //Surt una finestra per demanar el nom d'usuari
                username = JOptionPane.showInputDialog("Username:");
                if (username != null) {

                    //Comprova si el nom d'usuari ja ha estat enregistrat
                    //Si el nom d'usuari ja existeix el torna a demanar
                    while (Database.comprovarUser(username)) {
                        JOptionPane.showMessageDialog(null, "Error. Username is already in use");
                        username = JOptionPane.showInputDialog("Username:");
                    }

                    //Mostra la puntuació de l'usuari
                    JOptionPane.showMessageDialog(null, username+" points: "+puntuacioFinal);
                    Database.insertarUsuari(username);
                    Database.insertarPartida(username,puntuacioFinal);

                    //Mostra el top global de punts i surt del joc
                    JOptionPane.showMessageDialog(null, Database.top());
                    System.exit(0);
                }

                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    //FUNCIÓ DE BAIXAR-LI VIDA AL BOSS
    private void lowBossHP(){
        //TIMER CADA SEGON
        lowerBossHP = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Cada segon que pasa li baixa la barra de vida 2 pixels al boss
                int remainingBossHP = bossHP.getWidth();
                bossHP.setSize(remainingBossHP-2, bossHP.getHeight());

                //Si la barra de vida arriba a 0
                if (remainingBossHP <= 0) {
                    //S'aturen tots els timers
                    userHP.stop();
                    lowerBossHP.stop();
                    hitTimer.stop();
                    gameTimer.stop();
                    pointsTimer.stop();
                    ((Timer) e.getSource()).stop();
                    box.setFocusable(false);

                    //Crida a la funció de guanyar la partida
                    finalJocWin();
                }
            }
        });
        lowerBossHP.start();
    }

    //FUNCIÓ PER MOSTRAR LA VIDA QUE TE L'USUARI EN TOT MOMENT
    private void hpTimer(){
        //TIMER CADA 1 ms (CADA INSTANT)
        userHP = new Timer(1, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                labelHP.setText(String.valueOf(hp)+" / 1000");

                //Si la vida arriba a 0
                //LOSE
                if (hp <= 0){

                    labelHP.setText("0 / 1000");

                    //S'apagen tots els timers
                    userHP.stop();
                    lowerBossHP.stop();
                    hitTimer.stop();
                    gameTimer.stop();
                    pointsTimer.stop();
                    ((Timer) e.getSource()).stop();
                    box.setFocusable(false);

                    //es canvia l'icona de l'usuari per la de mort
                    ImageIcon heartIcon = new ImageIcon("src/assets/death.png");
                    Icon icon = new ImageIcon(
                            heartIcon.getImage().getScaledInstance(30, 25, Image.SCALE_DEFAULT)
                    );
                    heart.setIcon(icon);

                    //Crida a la funció de perdre la partida
                    finalJocLose();
                }
            }
        });
        userHP.start();
    }

    //FUNCIÓ PER SABER QUAN L'USUARI REP UN COP
    private void hit(){
        //TIMER CADA 100 ms
        hitTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JLabel user = heart;

                //guarda la posicio de l'usuari
                int userX = user.getX();
                int[] userPixels = new int[user.getWidth()];

                //bucle per guardar cada pixel que ocupa l'usuari a la pantalla i on es
                for (int i = 0; i < userPixels.length; i++) {
                    int pixel = userX+i;
                    userPixels[i] = pixel;
                }

                //guarda tots els pixels que ocupa l'atac atual
                JLabel actualAttackZone = attackZone;
                int[] attackPixels = new int[actualAttackZone.getWidth()];
                for (int i = 0; i < attackPixels.length; i++) {
                    int pixel = (actualAttackZone.getX()-box.getX())+i;
                    attackPixels[i] = pixel;
                }

                //bucle per saber si l'atac esta impatcant a l'usuari
                for (int i : attackPixels) {
                    for (int j : userPixels) {
                        if (i == j) {
                            if (actualAttackZone.isVisible()) {
                                //Si l'atac esta impactant a l'usuari se li resta 1 punt de vida
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

    //FUNCIÓ DEL JOC
    private void game(){
        seg = 0;
        //TIMER QUE FUNCIONA CADA SEGON
        gameTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seg++;

                //Cada 2 segons avans de cada 5 segons mostra on anira el següent atac
                if (seg % 5 == 3){
                    attackZone = (JLabel) patronesAtaque();
                    nextAttack.setVisible(true);
                }

                //CAda 5 segons s'elimina l'avís i es genera l'atac
                if (seg % 5 == 0){
                    nextAttack.setVisible(false);
                    attackZone.setVisible(true);

                    //Al generarse l'atac começa un contador de 2 segons que
                    // a l'acabar s'oculten els atacs
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

    //FUNCIÓ DEL LABEL DE LA VIDA DEL BOSS QUE ANIRA BAIXANT
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

    //FUNCIÓ PEL LABEL QUE MOSTRA LA VIDA RESTANT DEL BOSS
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

    //FUNCIÓ PER MOSTRAR EL SEGÜENT ATAC
    private Component nextAttack(JLabel attackZone){
        nextAttack.setOpaque(true);
        nextAttack.setBackground(new Color(187,91,255,70));

        nextAttack.setLayout(null);
        nextAttack.setVisible(true);
        nextAttack.setSize((box.getWidth()/3)-10, panelCenter.getHeight()-200);
        nextAttack.setBounds(attackZone.getX()-box.getX(), 2, (box.getWidth()/3)-5, box.getHeight()-4);

        return nextAttack;
    }

    //FUNCIÓ PER DETERMINAR ELS PATRONS D'ATAC
    private Component patronesAtaque() {
        Random rand = new Random();
        int zone = rand.nextInt(3);
        //Genera un numero aleatori entre 0 i 2

        switch (zone) {
            case 0: //Si el número és 0 l'atac següent sera el de l'esquerra
                attackZone = leftAttack;
                break;
            case 1://Si el número és 1 l'atac següent sera el del centre
                attackZone = centerAttack;
                break;
            case 2://Si el número és 2 l'atac següent sera el de la dreta
                attackZone = rightAttack;
                break;
        }
        //Es guarda l'atac següent i es retorna.
        nextAttack = (JLabel) nextAttack(attackZone);

        return attackZone;
    }

    //FUNCIÓ PEL MOVIMENT DE L'USUARI
    private static void heartMove(JLabel box, JLabel heart) {
        box.addKeyListener(new KeyAdapter() {
            //CADA COP QUE L'USUARI PREM UNA TECLA DE MOVIMENT
            public void keyPressed(KeyEvent e) {
                int x = heart.getX();
                int y = heart.getY();
                //guarda la posicio de l'usuari

                //Depenent de la tecla que l'usuari premi, es mou cap un lloc a cap un altre
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: x -= 4; break;
                    case KeyEvent.VK_RIGHT: x += 4; break;
                    case KeyEvent.VK_UP: y -= 4; break;
                    case KeyEvent.VK_DOWN: y += 4; break;
                }

                if (x <= 4){ //Si l'usuari arriba al límit esquerre no deixa avançar més
                    x = 4;
                }
                if (y <= 4){ //Si l'usuari arriba al límit superior no deixa avançar més
                    y = 4;
                }
                //Si l'usuari arriba al límit de la dreta no deixa avançar més
                if (x >= box.getWidth()-heart.getWidth()-4){
                    x = box.getWidth()-heart.getWidth()-4;
                }

                //Si l'usuari arriba al límit inferior no deixa avançar més
                if (y >= box.getHeight()-heart.getHeight()-4){
                    y = box.getHeight()-heart.getHeight()-4;
                }

                heart.setLocation(x, y);
            }
        });
    }

    //FUNCIÓ PER L'ANIMACIÓ DE L'ATAC
    private static void attackVibration(JLabel attack){
        final int[] count = {0}; // CONTADOR DE QUANTS COPS VIBRA L'ATAC

        //TIMER CADA 50 ms
        Timer attackCrono = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dx = (count[0] % 2 == 0) ? 1 : -1; //alternar posició
                attack.setLocation(attack.getX() + dx, attack.getY());
                count[0]++;
            }//Cada 50 ms la posició de l'atac es mou 1 px cap a la dreta o l'esquerra
            // sortint desde la seva posició original
        });
        attackCrono.start();
    }

    //FUNCIÓ DEL LABEL DE L'ATAC DE LA DRETA
    private Component rightAttack(){
        leftAttack.setOpaque(false);

        //LI DONEM IMATGE
        ImageIcon attackIcon = new ImageIcon("src/assets/attack.png");
        Icon icon = new ImageIcon(
                attackIcon.getImage().getScaledInstance(box.getWidth()/3, panelCenter.getHeight()-200, Image.SCALE_DEFAULT)
        );
        rightAttack.setIcon(icon);

        //LI DONEM MIDES I DEFINIM LA ZONA ON APAREIX
        rightAttack.setSize(box.getWidth()/3, panelCenter.getHeight()-200);
        rightAttack.setBounds(box.getX()+(rightAttack.getWidth()*2), box.getY()-100, box.getWidth()/3, panelCenter.getHeight()-200);

        rightAttack.setLayout(null);
        rightAttack.setVisible(true);

        return rightAttack;
    }

    //FUNCIÓ DEL LABEL DE L'ATAC DEL CENTRE
    private Component centerAttack(){
        leftAttack.setOpaque(false);

        //LI DONEM IMATGE
        ImageIcon attackIcon = new ImageIcon("src/assets/attack.png");
        Icon icon = new ImageIcon(
                attackIcon.getImage().getScaledInstance(box.getWidth()/3, panelCenter.getHeight()-200, Image.SCALE_DEFAULT)
        );
        centerAttack.setIcon(icon);

        //LI DONEM MIDES I DEFINIM LA ZONA ON APAREIX
        centerAttack.setSize(box.getWidth()/3, panelCenter.getHeight()-200);
        centerAttack.setBounds((box.getX()+box.getWidth()/2)-(centerAttack.getWidth()/2), box.getY()-100, box.getWidth()/3, panelCenter.getHeight()-200);

        centerAttack.setLayout(null);
        centerAttack.setVisible(true);

        return centerAttack;
    }

    //FUNCIÓ DEL LABEL DE L'ATAC DE L'ESQUERRA
    private Component leftAttack(){
        leftAttack.setOpaque(false);

        //LI DONEM IMATGE
        ImageIcon attackIcon = new ImageIcon("src/assets/attack.png");
        Icon icon = new ImageIcon(
                attackIcon.getImage().getScaledInstance(box.getWidth()/3, panelCenter.getHeight()-200, Image.SCALE_DEFAULT)
        );
        leftAttack.setIcon(icon);

        //LI DONEM MIDES I DEFINIM LA ZONA ON APAREIX
        leftAttack.setSize(box.getWidth()/3, panelCenter.getHeight()-200);
        leftAttack.setBounds(box.getX(), box.getY()-100, box.getWidth()/3, panelCenter.getHeight()-200);

        leftAttack.setLayout(null);
        leftAttack.setVisible(true);

        return leftAttack;
    }

    //FUNCIÓ DEL LABEL DELS PUNTS
    private Component pointsLabel() {
        points = 0;
        pointsTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                points++;
                pointsLabel.setText(String.valueOf(points)+" Points");
            }
        });
        //TIMER QUE CADA SEGON SUMA 1 PUNT A L'USUARI
        pointsTimer.start();

        //LI DONEM VORA I COLOR DE FONS AL LABEL
        pointsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pointsLabel.setOpaque(true);
        pointsLabel.setBackground(new Color(255,237,208));

        //LI DONEM MIDA I POSICIÓ ON APAREIXER
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

    //FUNCIÓ PEL BOSS DEL JOC
    private Component boss(){
        boss = new JLabel();
        boss.setOpaque(false);
        boss.setSize(400,200);
        //CREEM EL LABEL AMB LES SEVES MIDES

        //LI AFEGIM LA IMATGE
        ImageIcon bossIcon = new ImageIcon("src/assets/boss.png");
        Icon icon = new ImageIcon(
                bossIcon.getImage().getScaledInstance(400, 200, Image.SCALE_DEFAULT)
        );
        boss.setIcon(icon);

        //AFEGIM LA ZONA ON APAREIX I RETORNEM EL LABEL
        boss.setBounds((box.getX()/2), (box.getY()-box.getHeight())-52, boss.getWidth(), boss.getHeight());

        boss.setLayout(null);
        boss.setVisible(true);
        return boss;
    }

    //FUNCIÓ DEL PANELL DE L'INTERFICIÉ (HUD)
    private Component hud(){
        hud = new JPanel();
        hud.setSize(200, panelMain.getHeight());
        hud.setLocation(0,0);
        hud.setBackground(new Color(181,181,181));
        hud.setLayout(null);
        return hud;
    }

    //FUNCIÓ DEL LABEL ON SURT LA VIDA RESTANT DE L'USUARI
    private Component labelHP(){
        JLabel labelHP = new JLabel();
        labelHP.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //CREEM EL LABEL I LI DONEM VORA

        //LI DONEM OPACITAT I EL COLOR DE FONS
        labelHP.setOpaque(true);
        labelHP.setBackground(new Color(255,237,208));

        //LI DONEM MIDA I LA POSICIÓ ON APAREIX
        labelHP.setSize(75,75);
        labelHP.setBounds(
                (hud.getWidth()/2)-(labelHP.getWidth()/2),
                (hud.getHeight()/4)+(hud.getHeight()/4)+(hud.getHeight()/4)-(labelHP.getHeight()/2),
                labelHP.getWidth(),labelHP.getHeight()
        );

        //LI DONEM EL TEXT DE LA VIDA DE L'USUARI
        labelHP.setText("1000 / 1000");

        //CENTREM EL TEXT I RETORNEM EL LABEL
        labelHP.setHorizontalAlignment(SwingConstants.CENTER);
        labelHP.setVerticalAlignment(SwingConstants.CENTER);

        labelHP.setVisible(true);
        labelHP.setLayout(null);

        return labelHP;
    }

    //FUNCIÓ DEL PANELL CENTRAL
    private Component panelCenter(){
        panelCenter = new JLayeredPane();
        panelCenter.setOpaque(true);
        panelCenter.setLayout(null);
        panelCenter.setLocation(hud.getWidth(),0);
        panelCenter.setSize(panelMain.getWidth()-hud.getWidth(), panelMain.getHeight());
        panelCenter.setBackground(new Color(255,237,208));
        return panelCenter;
    }

    //FUNCIÓ DE L'OBECTE DE LA ZONA D'ACCIO DE L'USUARI
    private Component box(){
        box = new JLabel();
        box.setSize(300,150);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK,4));
        //Es crea el label, amb les seves mides i vora

        //Guardem la seva zona d'apareixer i retornem l'objecte
        box.setLocation(
                panelCenter.getWidth()/2 - box.getWidth()/2,
                panelCenter.getHeight() - (box.getHeight()*2)
        );
        box.setLayout(null);
        return box;
    }

    //FUNCIO DE L'OBJECTE QUE CONTROLA L'USUARI
    private Component heart(){
        heart = new JLabel();
        heart.setOpaque(false);
        //Es converteix el label en NO opac

        //S'assigna una imatge de fons i les seves mides
        ImageIcon heartIcon = new ImageIcon("src/assets/heart.png");
        Icon icon = new ImageIcon(
                heartIcon.getImage().getScaledInstance(30, 25, Image.SCALE_DEFAULT)
        );

        heart.setIcon(icon);

        //S'assigna la seva possició al apareixer i es retorna el label
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


        //Icona de finestra
        Toolkit pantalla = Toolkit.getDefaultToolkit();
        Image icon = pantalla.getImage("src/assets/logo.png");
        frame.setIconImage(icon);


    }
}

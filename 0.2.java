package my;

import com.sun.jdi.ShortType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.util.Vector;

/**
 * @author 楊士弘
 * @version 1.0
 */
//嘗試自己完成一下
public class TankGame extends JFrame {
    private MyPanel mp = null;   //初始化畫板

    public static void main(String[] args) {
        TankGame tankGame = new TankGame();
    }

    public TankGame() {
        mp = new MyPanel();
        Thread thread = new Thread(mp);     //開創線程
        thread.start();    //線程啟動
        this.add(mp);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(mp);
        this.setSize(1000, 750);
        this.setVisible(true);
    }
}

class MyPanel extends JPanel implements KeyListener, Runnable {
    //初始化一個自己的坦克
    Hero hero = null;
    Vector<Enemy> enemies = new Vector<>();   //用這個是因為Vector是線程安全
    int enemiesSize = 3;    //設置三個坦克

    public MyPanel() {  //構造器
        hero = new Hero(100, 100);
        hero.setSpeed(10);
        for (int i = 0; i < enemiesSize; i++) {
            Enemy enemy = new Enemy(100 * (i + 1), 0);    //創建坦克位置
            enemy.setDirect(2);      //方向
            enemies.add(enemy);       //添加到數組裡面
        }
    }

    @Override
    public void paint(Graphics g) {    //重寫paint方法
        super.paint(g);
        g.fillRect(0, 0, 1000, 750);   //初始化設定黑色
        drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 1);    //畫出自己的第一個坦克
        //畫出子彈
        if (hero.shot != null && hero.shot.isLive == true) {
            g.fill3DRect(hero.shot.x, hero.shot.y, 2, 2, false);
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            drawTank(enemy.getX(), enemy.getY(), g, enemy.getDirect(), 0);
        }
    }

    //繪製坦克方法寫這
    //5個參數   int x , int y , g , direct , type
    public void drawTank(int x, int y, Graphics g, int direct, int type) {
        switch (type) {     //先判斷類型決定顏色
            case 0:    //我們的坦克
                g.setColor(Color.blue);
                break;
            case 1:
                g.setColor(Color.white);
                break;
        }
        switch (direct) {    //再判斷方向，這裡開始畫坦克
            case 0:     //上
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y);
                break;
            case 1:      //右
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x, y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x + 60, y + 20);
                break;
            case 2:      //下
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y + 60);
                break;
            case 3:      //左
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x, y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x, y + 20);
                break;
            default:
                System.out.println("暫時沒設定");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override     //重點放在這個方法
    public void keyPressed(KeyEvent e) {
        //改變方向跟走向
        if (e.getKeyCode() == KeyEvent.VK_W) {
            hero.setDirect(0);
            hero.moveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            hero.setDirect(1);
            hero.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            hero.setDirect(2);
            hero.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            hero.setDirect(3);
            hero.moveLeft();
        }

        //這裡要監聽按鍵j來觸發子彈
        if (e.getKeyCode() == KeyEvent.VK_J) {
            System.out.println("用戶按下j");
            hero.shotEnemyTank();
        }
        this.repaint();


    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repaint();
        }
    }
}

class Enemy extends Tank {
    private int direct = 2;

    public Enemy(int x, int y) {
        super(x, y);
    }
}

class Hero extends Tank {   //自己的坦克
    public Hero(int x, int y) {
        super(x, y);
    }

    Shot shot = null;

    public void shotEnemyTank() {
        switch (getDirect()) {
            case 0:
                shot = new Shot(getX() + 20, getY(), 0);  //初始化位置
                break;
            case 1:
                shot = new Shot(getX() + 60, getY() + 20, 1);
                break;
            case 2:
                shot = new Shot(getX() + 20, getY() + 60, 2);
                break;
            case 3:
                shot = new Shot(getX(), getY() + 20, 3);
                break;
        }
        new Thread(shot).start();

    }

}


class Tank  {    //創建一個坦克類   要有座標，方向，速度
    private int x;
    private int y;
    private int speed;
    private int direct;

    //四個方法用來改變走向
    public void moveUp() {
        y -= speed;
    }

    public void moveRight() {
        x += speed;
    }

    public void moveDown() {
        y += speed;
    }

    public void moveLeft() {
        x -= speed;
    }

    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }




}

class Shot implements Runnable {
    int x;
    int y;
    int direct = 0;
    int speed = 2;
    boolean isLive = true;

    public Shot(int x, int y, int direct) {
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (direct) {
                case 0:  //上
                    y -= speed;
                    break;
                case 1:   //右
                    x += speed;
                    break;
                case 2:   //下
                    y += speed;
                    break;
                case 3:     //左
                    x -= speed;
                    break;
            }
            //測試輸出一下座標
            System.out.println("子彈x =" + x + " y=" + y);
            if (!(x >= 0 && x <= 1000 && y >= 0 && y <= 750)) {    //子彈要銷毀
                isLive = false;
                break;
            }
        }
    }
}

class EnemyTank extends Tank{

    public EnemyTank(int x, int y) {
        super(x, y);
    }
    Vector<Shot> shots = new Vector<>();   //創建子彈的集合
}
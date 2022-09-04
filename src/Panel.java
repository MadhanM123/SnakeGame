import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.awt.*;


public class Panel extends JPanel implements ActionListener{

    private static final int SCREENWIDTH = 700;
    private static final int SCREENHEIGHT = 700;
    private static final int UNIT_SIZE = 50;
    private static final int GAME_UNITS = (SCREENHEIGHT * SCREENWIDTH)/(UNIT_SIZE);
    private static final int DELAY = 70;

    private final int[] X = new int[GAME_UNITS];
    private final int[] Y = new int[GAME_UNITS];

    private int bodyParts = 3;
    private int foodConsumed;
    private int foodX_position;
    private int foodY_position;
    private char direction = 'R';
    private boolean running = false;

    private Timer timer;
    private Random rand;
 
    public Panel(){
        rand = new Random();
        this.setPreferredSize(new Dimension(SCREENWIDTH,SCREENHEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new GameKeyAdapter());
        startGame();        
    }

    public void startGame(){
        makeFood();
        this.running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if(running){
            for(int i = 0; i < SCREENHEIGHT/UNIT_SIZE; i++){
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREENHEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREENWIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.PINK);
            g.fillOval(this.foodX_position, this.foodY_position, UNIT_SIZE, UNIT_SIZE);

            for(int ind = 0; ind < bodyParts; ind++){
                if(ind == 0){
                    g.setColor(Color.GREEN);
                    g.fillRect(this.X[ind], this.Y[ind], UNIT_SIZE, UNIT_SIZE);
                }
                else{
                    g.setColor(Color.RED);
                    g.fillRect(this.X[ind], this.Y[ind], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Comic Sans",Font.ITALIC,40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + this.foodConsumed, (SCREENWIDTH - metrics.stringWidth("Score: " + this.foodConsumed))/2, g.getFont().getSize());
        }
        else{
            gameOver(g);
        }   
    }

    public void makeFood(){
        this.foodX_position = rand.nextInt(SCREENWIDTH/UNIT_SIZE) * UNIT_SIZE;
        this.foodY_position = rand.nextInt(SCREENHEIGHT/UNIT_SIZE) * UNIT_SIZE;
    }

    public void move(){
        for(int index = bodyParts; index > 0; index--){
            this.X[index] = this.X[index - 1];
            this.Y[index] = this.Y[index - 1];
        }

        if(this.direction == 'U'){
            this.Y[0] = this.Y[0] - UNIT_SIZE;
        }
        else if(this.direction == 'D'){
            this.Y[0] = this.Y[0] + UNIT_SIZE;
        }
        else if(this.direction == 'L'){
            this.X[0] = this.X[0] - UNIT_SIZE;
        }
        else if(this.direction == 'R'){
            this.X[0] = this.X[0] + UNIT_SIZE;
        }
    }

    public void checkFood(){
        if((this.X[0] == this.foodX_position) && this.Y[0] == this.foodY_position){
            bodyParts++;
            foodConsumed++;
            makeFood();
        }
    }

    public void checkCollisions(){
        //Checks if collided with itself
        for(int part = this.bodyParts; part > 0; part--){
            if((this.X[0] == this.X[part]) && (this.Y[0] == this.Y[part])){
                running = false;
            }
        }

        //Checks if colleded w/ border
        if(this.X[0] > SCREENWIDTH){
            running = false;
        }
        else if(this.X[0] < 0){
            running = false;
        }

        if(this.Y[0] < 0){
            running = false;
        }
        else if(this.Y[0] > SCREENHEIGHT){
            running = false;
        }

        if(!running) timer.stop();
    }

    public void gameOver(Graphics g){

        g.setColor(Color.red);
        g.setFont(new Font("Comic Sans",Font.ITALIC,60));
        FontMetrics metrics_one = getFontMetrics(g.getFont());
        g.drawString("Score: " + this.foodConsumed, (SCREENWIDTH - metrics_one.stringWidth("Score: " + this.foodConsumed))/2, g.getFont().getSize());

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Comic Sans", Font.BOLD,80));
        FontMetrics metrics_two = getFontMetrics(g.getFont());
        g.drawString("Game over", (SCREENWIDTH - metrics_two.stringWidth("Game over"))/2, SCREENHEIGHT/2);

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(this.running){
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
        
    }

    public class GameKeyAdapter extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_LEFT){
                if(Panel.this.direction != 'R'){
                    Panel.this.direction = 'L';
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                if(Panel.this.direction != 'L'){
                    Panel.this.direction = 'R';
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                if(Panel.this.direction != 'U'){
                    Panel.this.direction = 'D';
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_UP){
                if(Panel.this.direction != 'D'){
                    Panel.this.direction = 'U';
                }
            }
        }
        
    }
    
}

import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
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
    
    private int bodyParts;
    private int foodScore;
    private int foodHighScore;
    private int foodX_position;
    private int foodY_position;
    private char direction = 'R';
    private boolean running = false;

    private Timer timer;
    private Random rand;
    private JButton replayButton, exitButton;
    private JFrame Frame;
 
    public Panel(final JFrame frame){
        Frame = frame;

        rand = new Random();
        this.setPreferredSize(new Dimension(SCREENWIDTH,SCREENHEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new GameKeyAdapter());

        replayButton = new JButton("Click to play");
        replayButton.setBounds(120,100,200,125);
        replayButton.addActionListener(this);
        replayButton.setFocusable(false);
        replayButton.setBackground(Color.BLACK);
        replayButton.setFont(new Font("Comic Sans",Font.BOLD,25));
        replayButton.setForeground(Color.ORANGE);

        exitButton = new JButton("Click to exit");
        exitButton.setBounds(320,100,200,125);
        exitButton.addActionListener(this);
        exitButton.setFocusable(false);
        exitButton.setBackground(Color.BLACK);
        exitButton.setFont(new Font("Comic Sans",Font.BOLD,25));
        exitButton.setForeground(Color.WHITE);
        
        startGame();        
    }

    public void startGame(){
        bodyParts = 3;
        foodScore = 0;
        Arrays.fill(this.X,0);
        Arrays.fill(this.Y,0);
        this.direction = 'R';
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
            g.drawString("Score: " + this.foodScore, (SCREENWIDTH - metrics.stringWidth("Score: " + this.foodScore))/2, g.getFont().getSize());
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
            foodScore++;
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
        g.drawString("Score: " + this.foodScore, (SCREENWIDTH - metrics_one.stringWidth("Score: " + this.foodScore))/2, g.getFont().getSize());

        if(foodScore > foodHighScore) foodHighScore = foodScore;

        g.setColor(Color.CYAN);
        g.setFont(new Font("Comic Sans",Font.BOLD,70));
        FontMetrics metrics_two = getFontMetrics(g.getFont());
        g.drawString("High Score: " + this.foodHighScore, (SCREENWIDTH - metrics_two.stringWidth("High Score: " + this.foodHighScore))/2, SCREENHEIGHT - (g.getFont().getSize()));


        this.add(replayButton);
        this.add(exitButton);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Comic Sans", Font.BOLD,80));
        FontMetrics metrics_three = getFontMetrics(g.getFont());
        g.drawString("Game over", (SCREENWIDTH - metrics_three.stringWidth("Game over"))/2, SCREENHEIGHT/2);

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
        if(e.getSource() == replayButton){
            startGame();
            this.remove(replayButton);
            this.remove(exitButton);
        }
        else if(e.getSource() == exitButton){
            this.Frame.dispose();
        }
        
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

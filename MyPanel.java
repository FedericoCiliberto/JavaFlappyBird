package flappyBirdGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
/*
 * Pipe coordinates (x,y) is the BOTTOM PIPE of the pair, and x,y refer to his top left corner!
 * Bird coordinate (xBird,yBird) is the TOP LEFT corner of the circle of the bird!
 * */
public class MyPanel extends JPanel implements KeyListener{
	public static final int SCREEN_WIDTH=650;
	public static final int SCREEN_HEIGHT=650;
	public static final int TIMER_SPEED=10;
	public static final int BIRD_DIM=70;
	public static final int BIRD_SPEED=9; //IMPORTANT TO CHANGE GAMEPLAY!!
	public static final int PIPE_SPEED=10;
	public static final int JUMP_TIME=250; //IMPORTANT TO CHANGE GAMEPLAY!!
	public static final int PIPE_GAP=300;
	public static final int BETWEEN_PIPES=350;
	public static final int PIPE_START=SCREEN_WIDTH+100;
	public static final int PIPES_NUMBER=5;
	public static final int PIPE_WIDTH=150;
	public static final int MINIMUM_PIPE_HEIGHT=SCREEN_HEIGHT-100;
	public static final int MAXIMUM_PIPE_HEIGHT=350;
	public static final int TOLERANCE=10;//to make the bird image fill better the bird-circle (which really is what collides)
	
	ImageIcon pointingUpBird=new ImageIcon("pointingUpBird.png");
	ImageIcon pointingDownBird=new ImageIcon("pointingDownBird.png");
	ImageIcon background=new ImageIcon("flappybird_background.png");
	ImageIcon bottomPipe=new ImageIcon("bottomPipe.png");
	ImageIcon topPipe=new ImageIcon("topPipe.png");
	public List<Pipe> pipesList=new LinkedList<>();
	public int points=0;
	public int xBird=100;
	public int yBird=SCREEN_HEIGHT/4;
	Timer timer;
	Timer switchDirectionTimer;
	Timer pipesHandlingTimer;
	boolean gameOver=false;
	Random random=new Random();
	public char direction='D';
	
	MyPanel(){
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(new Color(0,153,204,255));
		this.setFocusable(true);
		this.setLayout(null);
		//creo i 4 tubi
		for(int i=0;i<PIPES_NUMBER;i++){
			pipesList.add(new Pipe(PIPE_START+BETWEEN_PIPES*(i+1),randInt(MAXIMUM_PIPE_HEIGHT,MINIMUM_PIPE_HEIGHT),PIPE_WIDTH,PIPE_GAP));//NOTA: Maximum comes before minimum cause actually the panel "0" is up, so the "maximum height" is the one with a smaller height-coordinate.
		}
		
		timer=new Timer(TIMER_SPEED,e->{
			handlePipes();
			move();
			gameOver=checkCollision();
			if(gameOver) {
				timer.stop();
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			repaint();
		});
		timer.start();
		switchDirectionTimer=new Timer(JUMP_TIME,e->{
			direction='D';
			switchDirectionTimer.stop();
		});
		this.addKeyListener(this);
	}
	
	public void handlePipes(){
		pipesList.stream().filter(p->p.getX()<-BETWEEN_PIPES).forEach(p->{
			p.setX(getLastPipeX()+BETWEEN_PIPES);
			p.setY(randInt(MAXIMUM_PIPE_HEIGHT,MINIMUM_PIPE_HEIGHT));
			p.setSurpassed(false);
		});
		
		//to increment points i see if the bird surpassed a pipe
		pipesList.stream().filter(p->p.getX()+PIPE_WIDTH<xBird).filter(p->!p.isSurpassed()).findAny().ifPresent(p->{
			p.setSurpassed(true);
			points++;
			});
	}
	public int getLastPipeX(){
		return	pipesList.stream().mapToInt(p->p.getX()).max().getAsInt();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d=(Graphics2D)g;
		//paint background
		g2d.drawImage(background.getImage(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
		
		
		//Paint bird
		g2d.setColor(Color.red);
	//	g2d.fillOval(xBird, yBird, BIRD_DIM,BIRD_DIM ); // DEBUG VERSION, OVAL FOR BIRD
		//IMAGE FOR BIRD, poining up or down based on direction
		if(direction=='U') {
			g2d.drawImage(pointingUpBird.getImage(), xBird-TOLERANCE, yBird-TOLERANCE, BIRD_DIM+2*TOLERANCE, BIRD_DIM+2*TOLERANCE, null); 
		}else if(direction=='D') {
			g2d.drawImage(pointingDownBird.getImage(), xBird-TOLERANCE, yBird-TOLERANCE, BIRD_DIM+2*TOLERANCE, BIRD_DIM+2*TOLERANCE, null); 
		}
		
//		//JUST FOR DEBUG: paint bird-point (x-y point marked with a black dott)
//		g2d.setColor(Color.black);
//		g2d.fillOval(xBird, yBird, 10, 10);
		
		
		//Paint pipes
		g2d.setColor(Color.green);
		pipesList.stream().forEach(p->{
//			g2d.fillRect(p.getX(), p.getY(), PIPE_WIDTH, SCREEN_WIDTH-p.getY()); //DEBUG VERSION, RECT FOR BOTTOM PIPE
//			g2d.fillRect(p.getX(),0,PIPE_WIDTH,p.getY()-PIPE_GAP);//DEBUG VERSION,RECT FOR TOP PIPE
			g2d.drawImage(bottomPipe.getImage(),p.getX(),p.getY(),PIPE_WIDTH,SCREEN_HEIGHT,null); //IMAGE FOR BOTTOM PIPE
			g2d.drawImage(topPipe.getImage(),p.getX(),-SCREEN_WIDTH+p.getY()-PIPE_GAP,PIPE_WIDTH,SCREEN_HEIGHT,null); //IMAGE FOR TOP PIPE
		});
//		
//		//JUST FOR DEBUG: paint pipe-point (x-y point marked with a black dott)
//		g2d.setColor(Color.black);
//		pipesList.stream().forEach(p->{
//			g2d.fillOval(p.getX(), p.getY(), 10, 10);
//			
//		});
		
		//Paint game over screen
		if(gameOver) {
			JButton button=new JButton("Restart");
			button.addActionListener(e->{
				JFrame topFrame=(JFrame) SwingUtilities.getWindowAncestor(this);
				topFrame.dispose();
				new MyFrame();
			});
			button.setFont(new Font("MV Bali",Font.PLAIN,20));
			button.setFocusable(false);
			button.setBackground(Color.green);
			button.setForeground(Color.white);
			button.setBounds(250,400,150,50);
			this.add(button);
			super.paint(g2d);
			g2d.setFont(new Font("MV Bali",Font.BOLD,90));
			g2d.setColor(Color.red);
			g2d.drawString("Game Over!", 80, 250);
			g2d.setFont(new Font("MV Bali",Font.BOLD,40));
			g2d.setColor(Color.white);
			g2d.drawString("Points: " + points, 250, 320);
			
		}
	}
	
	public void move() {
		switch(direction) {
		case 'D':
			yBird+=BIRD_SPEED;
			break;
		case 'U':
			yBird-=BIRD_SPEED;
			break;
		
		}
		pipesList.stream().forEach(p->{
			p.decrementX(PIPE_SPEED);
		});
		
	}
	
	public boolean checkCollision() {
		//check collision with game borders
		if(yBird<-10 || yBird>SCREEN_HEIGHT) {
			return true;
		}
		
		//check collision with pipes
		return pipesList.stream().filter(p->p.collide(xBird,yBird,BIRD_DIM)).findAny().isPresent();
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			direction='U';
			switchDirectionTimer.restart();
		}
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	public int randInt(int min, int max) {
		  int randomNum;
		  randomNum=random.nextInt((max - min) + 1) + min;
		  return randomNum;
		}
	
}





package flappyBirdGame;
import java.awt.Color;
import javax.swing.*;

public class MyFrame extends JFrame {
	MyPanel gamePanel=new MyPanel();
	ImageIcon icon=new ImageIcon("flappybird.jpg");
	
	MyFrame(){
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("FlappyBird");
		this.getContentPane().setBackground(Color.black);
		this.setIconImage(icon.getImage());
		
		this.add(gamePanel);
		this.pack();
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}

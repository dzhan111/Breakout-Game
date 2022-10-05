/*
 * File: Breakout.java
 * -------------------
 * Name: David Zhan
 * Date: 4/12/2020
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 100;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 6;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;


	public void run() {
		//add a click to play
			turns=3;
				setup();
				while(turns>0){
					
					newBall();
					countdown();
					while(gameIsOver!=true) {
						ballX=ball.getX();
						ballY=ball.getY();
						
						moveBall();
						pause(20);
						collisionCheck();
						scoreUpdate();
						checkWin();
		
					}
					lifeUpdate();
					pause(1000);
				}
				if(youWin!=null) {
	
					gameOver();	
					
				}
	
			
		
		/*end of public run*/
	}
	

	
	
	private void setup() {
		int startY = 50;
		
		/*ten rows*/
		int counter = 0;
		for(int i = 0; i<10;i++) {
			/* each individual brick */
			for(int j = 0;j<10;j++) {
				GRect brick = new GRect(((BRICK_SEP/4)+(j)*BRICK_SEP)+(j*BRICK_WIDTH),startY,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				
				/*coloring the blocks based on their row using a counter*/
				if(counter<8) {
					if(counter<6) {
						if(counter<4) {
							if(counter<2) {
								brick.setFillColor(Color.RED);
								brick.setColor(Color.RED);
							}else{ brick.setFillColor(Color.ORANGE);
							brick.setColor(Color.ORANGE);}
							
						}else{ brick.setFillColor(Color.YELLOW);
						brick.setColor(Color.YELLOW);
						}
						
					}else{brick.setFillColor(Color.GREEN);
					brick.setColor(Color.GREEN);}
				}else {
					brick.setFillColor(Color.CYAN);
					brick.setColor(Color.CYAN);
				}
			add(brick);
			}
		counter+=1;
		/*move bricks down each row by brick width*/
		startY+=(BRICK_HEIGHT+BRICK_SEP);
		}

		
		/*paddle 
		 * starting position=3/4 down the screen in the center
		 * same dimensions as a brick
		 * paddle must stay in same y coord
		 */
		paddle = new GRect((getWidth()/2.0)-BRICK_WIDTH/2.0,4.0*getHeight()/5,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle); 
		/*
		 * ball
		 */
		
		//ball velocity
		addMouseListeners();
		
		vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = 3.0;
		setBackground(Color.GRAY);
		
		scoreCounter=0;
		score = new GLabel("score:"+scoreCounter);
		score.setFont("Times New Roman-20");
		add(score,0,20);
		
		
		lives = new GLabel("lives: "+turns);
		lives.setFont("Times New Roman-20");
		add(lives,getWidth()-60,20);
	}
		/*//click to play feature
		clickPlay = new GLabel("Click to play");
		clickPlay.setFont("Times New Roman-50");
		add(clickPlay,getWidth()/5,getHeight()/2);
		
	
		
		addMouseListeners();
		
		
		
		
	}
	//click to play
	public void mouseClicked(MouseEvent e) {
		remove(clickPlay);
		gameIsOver=false;
		
	}
	*/
	
	private void scoreUpdate() {
		remove(score);
		score = new GLabel("score:"+scoreCounter);
		score.setFont("Times New Roman-20");
		add(score,0,20);
		
		
	}
	
	//if mouse was pressed on the paddle, the paddle will moved with the mouse
	public void mouseMoved(MouseEvent e) {
		
		x = e.getX();
		last = paddle.getX()+PADDLE_WIDTH/2;
		if(x>getWidth()) {
			x=getWidth();
			
		}else if(x<0) {
			x=0;
		}
		
		if(x>last) {
			paddle.move(x-last, 0);
		}else {
			paddle.move(x-last, 0);
		}
		
		
	}
	
	//moving the ball
	private void moveBall() {
		
		ball.move(vx,vy);

		
		}
		
		
	//checks if the ball hits something, then implements it based on the object
	private void collisionCheck() {
		paddleBounce();
		edgeBounce();
		brickBounce();
	}
	
	private void paddleBounce() {
		ballBottom=getElementAt(ball.getX()+BALL_RADIUS,ball.getY()+2*BALL_RADIUS+1);
		if(ballBottom==paddle) {
			vy*=-1;
		}
	}

	
	private void edgeBounce() {
		
		ballX=ball.getX();
		ballY=ball.getY();
		
		if(ballX+2*BALL_RADIUS>getWidth() && vx>0) {
			vx*=-1;
		}else if(ballX<0 && vx<0) {
			vx*=-1;
		}else if(ballY<=0 && vy<0) {
			vy*=-1;
		}else if(ballY>getHeight()) {
			lifeLost();
			
			if(turns<1) {
			gameOver();
			}
		}
		
		
	}

	private void brickBounce() {
		
		/// add a check that doesn't allow the ball to remove the life counter
		ballTopLeft=getElementAt(ballX,ballY);
		ballLeftBottom=getElementAt(ballX,ballY+2*BALL_RADIUS);
		ballRightBottom=getElementAt(ballX+(2*BALL_RADIUS),ballY+(2*BALL_RADIUS));
		ballTopRight=getElementAt(ballX+2*BALL_RADIUS,ballY);
		ballTop=getElementAt(ballX+BALL_RADIUS,ballY-1);
		ballLeft=getElementAt(ballX-1,ballY+BALL_RADIUS);
		ballRight=getElementAt(ballX+2*BALL_RADIUS+1,ballY+BALL_RADIUS);
		
		// down right
		if(vx>0 && vy>=0) {
			if(ballRightBottom!=null && ballRightBottom!=paddle  && ballRightBottom!=score && ballRightBottom!=lives){
				remove(ballRightBottom);
				vy*=-1;
				scoreCounter+=1;
				
			}else if(ballRight!=null && ballRight!=paddle && ballRight!=score && ballRight!=lives) {
				remove(ballRight);
				vx*=-1;
				scoreCounter+=1;
			}
		}
		// down left
		 if(vx<0 && vy>=0) {
		if(ballLeftBottom!=null && ballLeftBottom!=paddle  && ballLeftBottom!=score  && ballLeftBottom!=lives){
				remove(ballLeftBottom);
				vy*=-1;
				scoreCounter+=1;
				
			}else if(ballLeft!=null && ballLeft!=paddle && ballLeft!=score && ballLeft!=lives) {
				remove(ballLeft);
				vx*=-1;
				scoreCounter+=1;
				
			}
		}
		 
		//going up left
		 if(vx<0 && vy<0) {
			 if(ballTopLeft!=null && ballTopLeft!=score && ballTopLeft!=lives){
				remove(ballTopLeft);
				vy*=-1;
				scoreCounter+=1;
				
				
			}
		}
		//going up right
		 if(vx>0 && vy<0) {
			if(ballTopRight!=null && ballTopRight!=score && ballTopRight!=lives){
				remove(ballTopRight);
				vy*=-1;
				scoreCounter+=1;
				
			}
			
		}
		 
		/*//going down
		 if(vy<0) {
			 if(ballBottom!=null && ballBottom!=paddle && ballBottom!=score){
				remove(ballBottom);
				vy*=-1;
				scoreCounter+=1;
				
			}
		}
		 
		//going up
		 if(vy>0) {
			if(ballTop!=null && ballTop!=paddle){
				remove(ballTop);
				vy*=-1;
				scoreCounter+=1;
			}
			
		}
			*/
		
		 ball.move(vx, vy);
	}
	
	private void newBall() {
		gameIsOver=false;
		ball = new GOval(getWidth()/2-BALL_RADIUS/2,getHeight()/2-BALL_RADIUS/2,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
		
		
	}
	
	private void checkWin() {
		//change
		if(scoreCounter>=100) {
			remove(ball);
			remove(paddle);
			GLabel youWin= new GLabel("You Win!!");
			youWin.setFont("Times New Roman-36");
			add(youWin,getWidth()/3,getHeight()/3);
			pause(2000);
			gameIsOver=true;
			turns=0;
			
		}
	}
	private void lifeLost() {
		remove(ball);
		gameIsOver=true;
		GLabel lifeLostt = new GLabel("Life Lost.");
		lifeLostt.setFont("Times New Roman-36");
		add(lifeLostt,getWidth()/3,getHeight()/3);
		pause(2000);
		turns=turns-1;
		remove(lifeLostt);
	}
	
	private void gameOver() {
		
		gameIsOver=true;
		remove(paddle);
		
		gameOver = new GLabel("game over");
		gameOver.setFont("Times New Roman-40");
		add(gameOver,getWidth()/3,getHeight()/3);
	}
	private void lifeUpdate() {
		remove(lives);
		lives = new GLabel("lives: "+turns);
		lives.setFont("Times New Roman-20");
		add(lives,getWidth()-80,20);
	}
	
	private void countdown() {
		GLabel three = new GLabel("3");
		three.setFont("Times New Roman-80");
		add(three,getWidth()/2-20,getHeight()/3);
		pause(1000);
		remove(three);
		
		GLabel two = new GLabel("2");
		two.setFont("Times New Roman-80");
		add(two,getWidth()/2-20,getHeight()/3);
		pause(1000);
		remove(two);
		
		GLabel one = new GLabel("1!!");
		one.setFont("Times New Roman-80");
		add(one,getWidth()/2-20,getHeight()/3);
		pause(1000);
		remove(one);
	}
		
	//private instance variables
	private GRect paddle;
	private double x;
	private double last;
	private GOval ball;
	private GLabel gameOver;
	private boolean gameIsOver;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double ballX;
	private double ballY;
	private GLabel clickPlay;
	private int scoreCounter;
	private GLabel score;
	private GLabel youWin;
	private GLabel lives;
	private GLabel lifeLostt;
	private int turns=3;
	
	private GObject ballBottom;
	private GObject ballTop;
	private GObject ballLeft;
	private GObject ballRight;
	private GObject ballTopLeft;
	private GObject ballLeftBottom;
	private GObject ballRightBottom;
	private GObject ballTopRight;

	
	//end of graphics program
}

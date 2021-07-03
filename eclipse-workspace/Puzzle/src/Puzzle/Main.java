package Puzzle;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.PriorityQueue;
class Main {
	static int N = 4;
	static int[] nr = {0,1,0,-1};
	static int[] nc = {1,0,-1,0};
	static public Puzzle Dest = new Puzzle();
	static class Puzzle implements Comparable<Puzzle>{//Puzzle
		int id;
		int parent;
	    int G;//num of movement
	    int H;//heuristics
	    int F;// H + G
	    int XY;
	    int zr;
	    int zc;
	    int[][] Map = new int[N][N];
	    public Puzzle(){
	    	this.id = 0;
	    	this.parent = 0;
	        this.G = 0;
	        this.H = 0;
	        this.F = 0;
	        this.XY = 0;
	    }
	    boolean Init_Puzzle(){//initialize the puzzle status
	    	this.id = 0;
	    	this.parent = -1;
	    	this.G = 0;
	        boolean[] visit = new boolean[N*N];
	        for(int i = 0 ; i < N*N ; i++)
	            visit[i] = false;
	        for(int i = 0 ; i < N ; i++){
	            for(int j = 0 ; j < N ; j++){
	                if(this.Map[i][j] != 0){
	                    visit[this.Map[i][j]-1] = true;
	                    int cnt = 0;
	                    for(int p = 0 ; p < this.Map[i][j]-1 ; p++){
	                        if(visit[p] == false) cnt++;
	                    }
	                    this.XY += cnt;
	                    this.H += Math.abs(i - (this.Map[i][j]-1)/N);
	                    this.H += Math.abs(j - (this.Map[i][j]-1)%N);
	                } else {
	                    zr = i;
	                    zc = j;
	                    this.XY += i+1;
	                    this.H += Math.abs(i - (N-1));
	                    this.H += Math.abs(j - (N-1));
	                }
	            }
	        }
	        this.F = this.H +this.G;
	        if(this.XY%2 == 1) return false;
	        return true;
	    }
	    void Get_Puzzle(Puzzle Prev,int dir){//get the next puzzle from prev & move_dir
	    	boolean[] visit = new boolean[N*N];
	        this.G = Prev.G+1;
	        for(int i = 0 ; i < N*N ; i++)
	            visit[i] = false;
	        for(int i = 0 ; i < N ; i++){
	            for(int j = 0 ; j < N ; j++){
	                this.Map[i][j] = Prev.Map[i][j];
	                if(this.Map[i][j] == 0){
	                    zr = i;
	                    zc = j;
	                }
	            }
	        }
	        int temp = this.Map[zr][zc];
	        this.Map[zr][zc] = this.Map[zr+nr[dir]][zc+nc[dir]];
	        this.Map[zr+nr[dir]][zc+nc[dir]] = temp;
	        for(int i = 0 ; i < N ; i++){
	            for(int j = 0 ; j < N ; j++){
	                if(this.Map[i][j] != 0){//number tile
	                    visit[this.Map[i][j]-1] = true;
	                    int cnt = 0;
	                    for(int p = 0 ; p < this.Map[i][j]-1 ; p++){
	                        if(visit[p] == false) cnt++;
	                    }
	                    this.XY += cnt;
	                    this.H += Math.abs(i - (this.Map[i][j]-1)/N);
	                    this.H += Math.abs(j - (this.Map[i][j]-1)%N);
	                } else {//empty tile
	                    zr = i;
	                    zc = j;
	                    this.XY += i+1;
	                    this.H += Math.abs(i - (N-1));
	                    this.H += Math.abs(j - (N-1));
	                }
	            }
	        }
	        this.F = this.H +this.G;
	    }
	    boolean Equal(Puzzle o){//if puzzle this and puzzle o is equal return true
	        for(int i = 0 ; i < N ; i++){
	            for(int j = 0 ; j < N ; j++){
	                if(o.Map[i][j] != this.Map[i][j]) return false;
	            }
	        }
	        return true;
	    }
	    @Override
	    public int compareTo(Puzzle o){// setting comparator
	    	if(this.H == o.H) {
	    		return this.F - o.F;
	    	}
	    	return this.H - o.H;
	    }
	}
	static class Gamer{
		private static JLabel[] comp = new JLabel[N*N]; 
	    private static Timer moveTimer;
	    private static int dir,prev_dir = -1;
	    private static JButton Random_Move;
	    private static JButton Solve_Puzzle;
		public Gamer() throws IOException {
			JFrame Frame = new JFrame("Puzzle game");
			Frame.setSize(930,1000);
			Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        Frame.setLayout(null);
	        BufferedImage Origin = ImageIO.read(new File("C:/Users/dirgs/eclipse-workspace/Puzzle/src/image/Puzzle_map.jpg"));//get the image file
	        Image temp = Origin.getScaledInstance(800,800,Image.SCALE_SMOOTH);
	        Origin = new BufferedImage(800,800,BufferedImage.TYPE_INT_ARGB);
	        for(int i = 0 ; i < N ; i++){
	            for(int j = 0 ; j < N ; j++){
	            	if(i == N-1 && j == N-1) {
	            		Dest.zc = N-1;
	            		Dest.zr = N-1;
	            		Dest.Map[i][j] = 0;
	            	} else {
	            		Dest.Map[i][j] = i*N+j+1;
	            	}
	            }
	        }
	        JPanel Board_MAP = new JPanel();
	        Board_MAP.setBounds(50,50,800,800);
	        Board_MAP.setLayout(null);
	        Board_MAP.setBackground(new Color(120,0,120));
	        
	        for(int i = 0 ; i  < N ; i++) {
	        	for(int j = 0 ; j < N ; j++) {
	        		if(Dest.Map[i][j] == 0) continue;
	        		BufferedImage Sub = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
	        		Sub.getGraphics().drawImage(temp,0,0,200,200,200*((Dest.Map[i][j]-1)%N), 200*((Dest.Map[i][j]-1)/N),200*((Dest.Map[i][j]-1)%N)+200 , 200*((Dest.Map[i][j]-1)/N)+200,null);
	        		comp[((Dest.Map[i][j]-1)/N)*N+((Dest.Map[i][j]-1)%N)+1] = new JLabel(new ImageIcon(Sub));
	    	        comp[((Dest.Map[i][j]-1)/N)*N+((Dest.Map[i][j]-1)%N)+1].setBounds(200*j,200*i,200,200);
	    	        Board_MAP.add(comp[((Dest.Map[i][j]-1)/N)*N+((Dest.Map[i][j]-1)%N)+1]);
	        	}
	        }
	        JPanel p1 = new JPanel();
	        p1.add(new JLabel("Num of Move"));
	        JTextField num = new JTextField(10);
	        p1.add(num);
	        p1.setBounds(100,900,200,200);
	        Random_Move = new JButton("Random Move!");
	        Solve_Puzzle = new JButton("Solve Puzzle");
	        Solve_Puzzle.setBounds(600,900,150,50);
	        Random_Move.setBounds(350,900,150,50);
	        Random_Move.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					//num.getText()
					try {
						if(num.getText().equals("")) {
							return;
						}
						Random_Move.setEnabled(false);
						Solve_Puzzle.setEnabled(false);
						Random_Move(Integer.parseInt(num.getText()));
					} catch (NumberFormatException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	        });
	        Solve_Puzzle.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					//num.getText()
					try {
						Random_Move.setEnabled(false);
						Solve_Puzzle.setEnabled(false);
						Solve();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	        });
	        //p1.add(Random_Move);
	        Frame.getContentPane().add(p1);
	        Frame.getContentPane().add(Random_Move);
	        Frame.getContentPane().add(Solve_Puzzle);
	        Frame.getContentPane().add(Board_MAP);//Game Map complete
			Frame.setVisible(true);
		}
		private static void Random_Move(int n) throws InterruptedException {//move N times
			Random random = new Random();
			moveTimer = new Timer(15, new ActionListener() {
				private int num_of_move = 0;
	            private int count = 0;
	            private int increment = 20;
	            @Override
	            public synchronized void actionPerformed(ActionEvent e) {
	            	if(num_of_move == n) {
						Random_Move.setEnabled(true);
						Solve_Puzzle.setEnabled(true);
        				moveTimer.stop();
	            	} else {
	            		if (count == 0) {
		    				dir = random.nextInt(4);
		    				do {
		    					dir = random.nextInt(4);
		    				} while(!Valid_move(Dest.zr,Dest.zc,dir) || prev_dir == dir);
		    				prev_dir = (dir+2)%4;
	            		}
		                if (count == 200) {
		                	count = 0;
	        	        	Puzzle temp = new Puzzle();
	        	        	temp.Get_Puzzle(Dest, dir);
	        	        	Dest = temp;
		                    num_of_move++;
		                } else {
			                Point loc = comp[Dest.Map[Dest.zr + nr[dir]][Dest.zc + nc[dir]]].getLocation();// the point for moving block
			                loc.x += nc[(dir+2)%4]*increment;
			                loc.y += nr[(dir+2)%4]*increment;
			                comp[Dest.Map[Dest.zr + nr[dir]][Dest.zc + nc[dir]]].setLocation(loc);
			                count += increment;
		                }
	            	}
	            }
	        });
	        moveTimer.start();
		}
		private static void Solve() {
			PriorityQueue<Puzzle> O = new PriorityQueue<Puzzle>();
		    Vector<Puzzle> C = new Vector<Puzzle>();
		    Stack<Puzzle> Answer = new Stack<Puzzle>();
			if(!Dest.Init_Puzzle()) {
	        	System.out.println("invalid Puzzle");
	        	return;
	        } else {
		        O.offer(Dest);
		        C.add(Dest);
		        while(true) {
		        	Puzzle cur = O.poll();
		        	if(cur.H == 0) {
		        		while(true) {
		        			if(cur.parent == -1) break;
		        			Answer.add(cur);
		        			cur = C.get(cur.parent);
		        		}
		        		break;
		        	}
		        	for(int dir = 0 ; dir < 4 ; dir++) {
			        	Puzzle next = new Puzzle();
		    	        if(!Valid_move(cur.zr,cur.zc,dir)) continue;
			        	next.Get_Puzzle(cur, dir);
			        	int p = 0;
			        	for( ; p < (int)C.size() ; p++) {
			        		if(next.Equal(C.get(p))) {
			        			break;
			        		}
			        	}
			        	if(p == (int)C.size()) {
			        		next.id = (int)C.size();
			        		next.parent = cur.id;
			        		O.add(next);
			        		C.add(next);
			        	}
		        	}
		        }
		        // answer vector is made
				moveTimer = new Timer(15, new ActionListener() {
			        private int n = (int)Answer.size();
					private int num_of_move = 0;
		            private int count = 0;
		            private int increment = 20;
		            private int dirx;
		            private int diry;
		            private Puzzle next;
		            @Override
		            public synchronized void actionPerformed(ActionEvent e) {
		            	if(num_of_move == n) {
							Random_Move.setEnabled(true);
							Solve_Puzzle.setEnabled(true);
	        				moveTimer.stop();
		            	} else {
		            		if (count == 0) {
			    				next = Answer.pop();
		            			dirx = Dest.zc - next.zc;
			    				diry = Dest.zr - next.zr;
		            		}
			                if (count == 200) {
			                	count = 0;
			                	Dest = next;
			                    num_of_move++;
			                } else {
				                Point loc = comp[Dest.Map[Dest.zr - diry][Dest.zc - dirx]].getLocation();// the point for moving block
				                loc.x += dirx*increment;
				                loc.y += diry*increment;
				                comp[Dest.Map[Dest.zr - diry][Dest.zc - dirx]].setLocation(loc);
				                count += increment;
			                }
		            	}
		            }
				});
				moveTimer.start();
	        }
		}
	}
	public static boolean Valid_move(int r,int c,int dir){
	    if(nr[dir] + r >= 0 && nr[dir] + r < N && nc[dir] + c >= 0 && nc[dir] + c < N){
	        return true;
	    }
	    return false;
	}
	public static void main (String[] args) throws java.lang.Exception {
		new Gamer();
	}
}
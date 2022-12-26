package homework4;
import javax.swing.*;
import java.awt.*; 
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class Game extends JFrame implements ActionListener{
	private static final int MAX_CREATURES = 2; // 최대 움직이는 두더지 수
	private static final String SCORE_PREFIX = "Score: "; 
	private static final String TIME_PREFIX = "Time: "; 
	private static final double LENGTH_OF_GAME = 20000; // 게임 시간 10초로 설정
	private static final int TARGET_SCORE = 500; // 목표점수
	private static final int MAX_LEVEL = 3; // 단계 수  
	private static final int CREATURES_PER_LEVEL = 9; 
	private static Random rand = new Random(); 
	private static  int numOfCreatures = 9;
	private static int level = 1;
	static int score = 0;
	static int totalScore = 0;
	static JLabel scoreLabel;
	static JLabel timeLabel;
	public static int creaturesAlive; // 현재 살아있는 두더지 수
	public static int moleCnt = 0; // 두더지 총 출현 횟수 담기( max: 10)
	private static String name;
	
	Creature[] creatures; // 두더징 
	
	public static void main(String[] args) throws IOException {
		// 게임 시작 
		Game this_game = new Game(); 
		name = JOptionPane.showInputDialog("이름을 입력하세요");
		
		while(true) {
			while(level <= MAX_LEVEL) {
				if(level == 1) {
					String [] opt = {"시작하기", "기록보기"};
					int result = JOptionPane.showOptionDialog(this_game, "게임을 시작하시겠습니까?", "시작하기", 0, 0, null, opt, null);
					if(result == 0) {
						JOptionPane.showMessageDialog(this_game, "Stage " + level + "\n 게임을 시작하려면 확인 버튼을 누르세요.");
						}
					else {
						try{
				            //파일 객체 생성
				            File file = new File("moleGame-history.txt");
				            //스캐너로 파일 읽기
				            Scanner scan = new Scanner(file);
				            Vector<String> v = new Vector<String>();
				            while(scan.hasNextLine()){
				               v.add(scan.nextLine() + "\n");
				            }
				            JOptionPane.showMessageDialog(this_game, v);
				        }catch (FileNotFoundException e) {
				            // TODO: handle exception
				        }
					}
				}
				
				this_game.playGame(); 
				
				if(level <= MAX_LEVEL) {
					JOptionPane.showMessageDialog(this_game, "Stage " + level + "\n 게임을 계속하려면 확인 버튼을 누르세요.");
//					nextLevel();
					this_game.dispose();
					this_game = new Game();
				}   	
			}
	
			if(level > MAX_LEVEL) {
				totalScore += score;
				if(score >= 500) {
					JOptionPane.showMessageDialog(this_game, "게임 성공! 축하드립니다. 😀");			
				}else {
					JOptionPane.showMessageDialog(this_game, "각 스테이지에서 500점을 달성하지 못해 \n 아쉽게도 실패했어요.");
				}
			}
			
			int response = JOptionPane.showConfirmDialog(this_game, name + "님의 점수: "+ totalScore +"점.\n 게임을 다시 시작하겠습니까?", "게임 종료", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			File wfile = new File("moleGame-history.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(wfile, true));
			if(wfile.isFile() && wfile.canWrite()){
				//쓰기
				bw.write("이름: " + name + ", 총점: " + totalScore);
				bw.newLine();
				bw.close();
			}
			
			// 종료
			if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) 
				break; 
			
			resetLevel(); 
			this_game.dispose();
			this_game = new Game();
		}
		this_game.dispose(); 
		
	}
	
	public static void update_time(double timeRemaining) {
		timeLabel.setText(TIME_PREFIX + NumberFormat.getInstance().format(timeRemaining/1000)); 		
	}
	
	public static void update_score() {
		score += 100; 
		scoreLabel.setText(SCORE_PREFIX + score); 		
	}

	public Game() {
		setSize(500, 500); 
		setLayout(new BorderLayout()); 
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("두더지 잡기 게임"); 
		
		totalScore += score;
		score = 0; 
		creaturesAlive = 0; 
		
		JPanel top = intitialize_top(); 
		add(top, BorderLayout.NORTH);
		
		JPanel field = intitialize_field(); 
		add(field, BorderLayout.CENTER);
		
		this.setVisible(true); 
	}
	
	private JPanel intitialize_field() {
		JPanel field = new JPanel(); 
		field.setLayout(new GridLayout(3, 3, 5, 5)); 

		creatures = new Creature[numOfCreatures]; 
		for(int x = 0; x < creatures.length; x++) {
			creatures[x] = new Creature();
			creatures[x].addActionListener(this); 
			field.add(creatures[x]); 	
		}
		return field; 
		
	}
	
	// 상단바 (현재 스코어, 남은 시간)
	private JPanel intitialize_top() { 
		
		JPanel top = new JPanel(); 
		top.setLayout(new GridLayout(1,2)); 
		
		scoreLabel = new JLabel(); 
		timeLabel = new JLabel(); 
		scoreLabel.setText(SCORE_PREFIX);
		timeLabel.setText(TIME_PREFIX);
		top.add(scoreLabel); 
		top.add(timeLabel); 
		
		return top; 
	}
	
	// 현재단계와 두더지정보 
	private void playGame() {
		double startTime = new Date().getTime();   
		double currentTime = startTime; 
		double timeRemaining = LENGTH_OF_GAME; // 남은 시간   
		
		// stage 시작 후 3초 후에 게임 시작하기
		try {
			Thread.sleep(3000);	
		}catch(InterruptedException e) {
			
		}
		
		System.out.println();
		System.out.println("level: " + level);
		moleCnt = 0;
		
		while(( LENGTH_OF_GAME - timeRemaining) < LENGTH_OF_GAME) {
			
			long time = System.currentTimeMillis();
			
			reviveCreatures(); // 두더지 출현 시작
			updateCreatures(); // 두더지 정보 업데이트
			
			try{
				long delay = Math.max(0, 32-(System.currentTimeMillis()-time));
				Thread.sleep(delay);
				}
			catch(InterruptedException e)
			{
					
			}
			  
			currentTime = new Date().getTime(); 
			// 남은 시간 계산
			timeRemaining = LENGTH_OF_GAME - (currentTime - startTime);	
			update_time(timeRemaining); 
			if (score >= TARGET_SCORE) { // 점수 500 이상이면 다음 스테이지
				level++;
				break;
			}
			
			// 두더지 출현 횟수가 10 이상일 경우 다음 단계로 넘어가기
			if (moleCnt >= 10) {
				level++;
				break;
			}
		}
	}
	private void updateCreatures() {
		for(int x = 0; x < creatures.length; x++) {
			creatures[x].update();
		}
	}
	
	private void reviveCreatures() {
		if (creaturesAlive < MAX_CREATURES) {
			int randomCreature = rand.nextInt(numOfCreatures); 
			if(!creatures[randomCreature].getIsAlive()) {
				creatures[randomCreature].revive(level); 
				creaturesAlive++; 
			}
		}
	}
	
	// 게임 리셋
	private static void resetLevel() {
		level = 1;
		totalScore = 0;
		score = 0;
		numOfCreatures = CREATURES_PER_LEVEL; 
	}
	
	// 두더지 클릭
	public void actionPerformed(ActionEvent event) {
		Creature clickedCreature = (Creature) event.getSource();
		if(clickedCreature.getIsAlive()) {
			clickedCreature.kill();
			update_score(); 
		}
	}
}
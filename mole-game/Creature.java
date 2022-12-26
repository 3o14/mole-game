package homework4;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Creature extends JButton{
	
	private boolean isAlive; 
	private int life_count;  
	private int final_life; 
	public int moleCnt = 0;
	
	public Creature() {
		isAlive = false; 
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
		this.setBorderPainted(false);
		life_count = 0; 
		
	}
	
	public boolean getIsAlive() {
		return isAlive; 
	}
	
	public int getMoleCnt() {
		return moleCnt;
	}
	
	public void revive(int stage) {
		if(!isAlive) {
			
			// 단계별 두더지 출현 시간
			if( stage == 1) {
				final_life = 80;
			} else if (stage == 2) {
				final_life = 50;
			} else {
				final_life = 30;
			}
			
			isAlive = true;   
			
			ImageIcon moleIcon = new ImageIcon("images/mole.png");
			Image moleImg = moleIcon.getImage();
			Image moleImgSized = moleImg.getScaledInstance(100, 100, moleImg.SCALE_SMOOTH);
			ImageIcon moleIconSized = new ImageIcon(moleImgSized);
			this.setIcon(moleIconSized);
			this.setOpaque(true);
			this.setBorderPainted(false);
			Game.moleCnt++;
		}
	}
	
	// 두더지 잡았을 때 함수
	public void kill() {
			isAlive = false;
			this.setIcon(null);
			this.setBackground(Color.BLACK); 
			this.setOpaque(true);
			this.setBorderPainted(false);
			life_count = 0; 
			Game.creaturesAlive--; 
	}
	
	// 각 두더지 정보 업데이트 해주기(일정 시간이 지나면 없애기)
	public void update() {
		if(isAlive) {
			life_count++; 
			if(life_count == final_life) 
				this.kill();
		}
	}
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.uno;

/**
 *
 * @author dwingz
 */
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Game {
	private int currentPlayer;
	private String[] playerIds;
	
    private UnoDeck deck;
    private ArrayList<ArrayList<UnoCard>> playerHand; 
    private ArrayList<UnoCard> stockpile;
    
    private UnoCard.Color validColor;
    private UnoCard.Value validValue;
    
    boolean gameDirection;
    
    
    public Game(String[] pids)
    {
    	deck = new UnoDeck();
    	deck.shuffle();
    	stockpile = new ArrayList<UnoCard>();
    	
    	playerIds = pids;
    	currentPlayer = 0;
    	gameDirection = false;
    	
    	playerHand = new ArrayList<ArrayList<UnoCard>>();
    	
    	for (int i = 0; i < pids.length; i++)
    	{
    		ArrayList<UnoCard> hand = new ArrayList<UnoCard>(Arrays.asList(deck.drawCard(7)));
    		playerHand.add(hand);
    	}
    	
    	
    }
    public void start(Game game)
	{
		UnoCard card = deck.drawCard();
		validColor = card.getcolor();
		validValue = card.getvalue();
		
		
		if (card.getvalue() == UnoCard.Value.Wild)
		{
			start(game);
		}
		if (card.getvalue() == UnoCard.Value.Wild_four || card.getvalue() == UnoCard.Value.DrawTwo) {
			start(game);
		}
		if (card.getvalue() == UnoCard.Value.skip) {
			JLabel message = new JLabel(playerIds[currentPlayer] + "Was Skipped!");
			message.setFont(new Font("Arial", Font.BOLD, 48));
			JOptionPane.showMessageDialog(null, message);
		}
			if(gameDirection == false) {
				currentPlayer = (currentPlayer + 1) % playerIds.length;
			}
			else if(gameDirection == true) {
				currentPlayer = (currentPlayer - 1) % playerIds.length;
				if (currentPlayer == -1) {
					currentPlayer = playerIds.length - 1;
				}
			}
		
		if (card.getvalue() == UnoCard.Value.Reverse) {
			JLabel message1 = new JLabel(playerIds[currentPlayer]+ "The game direction changed!");
			message1.setFont(new Font("Arial", Font.BOLD, 48));
			JOptionPane.showMessageDialog(null, message1); 
			gameDirection ^= true;
			currentPlayer = playerIds.length - 1;
		}

		stockpile.add(card);
		
		
	}
    public UnoCard getTopCard() {
		return new UnoCard(validColor, validValue);
		
		
		
	}
    public ImageIcon getTopCardImage() {
    	return new ImageIcon(validColor + "-" + validValue + ".png");
    }

	public boolean isGameOver() {
    	for (String player : this.playerIds) {
    		if (hasEmptyHand(player)) {
    			return true;
    		}
    		
    	}
    	return false;
    }
	
	public String getCurrentPlayer()
	{
		return this.playerIds[this.currentPlayer];
		
	}
	public String getPreviousPlayer(int i)
	{
		int index = this.currentPlayer - i;
		if (index == - 1) {
			index = playerIds.length - 1;
		}
		return this.playerIds[index];
	}
	public String[] getPlayers()
	{
		return playerIds;
	}
	public ArrayList<UnoCard> getPlayerHand(String pid)
	{
		int index = Arrays.asList(playerIds).indexOf(pid);
		
		return playerHand.get(index);
	}
	public int getPlayerHandSize(String pid){
            
        
		return getPlayerHand(pid).size();
	}
	public UnoCard getPlayerCard(String pid, int choice)
	{
		ArrayList<UnoCard> hand = getPlayerHand(pid);
		return hand.get(choice);
	}
	public boolean hasEmptyHand(String pid){
		return getPlayerHand(pid).isEmpty();
	}

	public boolean validCardPlay(UnoCard card) {
		return card.getcolor() == validColor || card.getvalue() == validValue;
	}
	public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException{
		if (this.playerIds[this.currentPlayer] != pid){
			throw new InvalidPlayerTurnException("it is not" + pid + "'s turn", pid);
		}
	}
	public void submitDraw(String pid) throws InvalidPlayerTurnException{
		checkPlayerTurn(pid);

		if (deck.isEmpty()){
			deck.replaceDeckWith(stockpile);
			deck.shuffle();
		}
		getPlayerHand(pid).add(deck.drawCard());
		if (gameDirection == false){
			currentPlayer = (currentPlayer + 1) % playerIds.length;
		}
		else if(gameDirection == true){
			currentPlayer = (currentPlayer -1) % playerIds.length;
			if (currentPlayer == -1)
			{
				currentPlayer = playerIds.length - 1;
			}
		}
	}
	public void getCardColor(UnoCard.Color color){
		validColor = color;
	}
	public void submitPlayerCard(String pid, UnoCard card, UnoCard.Color declaredColor)
              throws  InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException{
			{
		checkPlayerTurn(pid);

			ArrayList<UnoCard> pHand = getPlayerHand(pid);

			if (!validCardPlay(card)){
				if (card.getcolor() == UnoCard.Color.Wild){
					validColor = card.getcolor();
					validValue = card.getvalue();
				}
				if (card.getcolor() != validColor){
					JLabel message = new JLabel("Invalid player move, excepted color:"+ validColor + "but got color"+ card.getcolor());
					message.setFont(new Font("Arial", Font.BOLD, 48));
					JOptionPane.showMessageDialog(null, message);
					throw new InvalidColorSubmissionException("Invalid player move, excepted color:"+ validColor + "but got color"+ card.getcolor(), card.getcolor(), validColor);
				}
				else if (card.getvalue() != validValue)
				{
					JLabel message2 = new JLabel("Invalid player move, excepted value:"+ validValue + "but got value"+ card.getvalue());
					message2.setFont(new Font("Arial", Font.BOLD, 48));
					JOptionPane.showMessageDialog(null, message2);
					throw new InvalidValueSubmissionException("Invalid player move, excepted value:"+ validValue + "but got value"+ card.getvalue(), card.getvalue(), validValue);
				}
			}
			pHand.remove(card);
			if (hasEmptyHand(this.playerIds[currentPlayer])){
				JLabel message2 = new JLabel(this.playerIds[currentPlayer]+"won the game! Thank you for playing");
				message2.setFont(new Font("Arial", Font.BOLD, 48));
				JOptionPane.showMessageDialog(null, message2);
				System.exit(0);
			}
			validColor = card.getcolor();
			validValue = card.getvalue();
			stockpile.add(card);

			if(gameDirection == false){
				currentPlayer = (currentPlayer + 1) % playerIds.length;
			}
			else if(gameDirection == true){
				currentPlayer = (currentPlayer - 1)% playerIds.length;
				if(currentPlayer == -1){
					currentPlayer = playerIds.length - 1;
				}
			}
			if (card.getcolor() == UnoCard.Color.Wild){
				validColor = declaredColor;
			}
			if (card.getvalue() == UnoCard.Value.DrawTwo){
				pid = playerIds[currentPlayer];
				getPlayerHand(pid).add(deck.drawCard());
				getPlayerHand(pid).add(deck.drawCard());
				JLabel message = new JLabel(pid + "draw 2 cards!");

			}
			if (card.getvalue() == UnoCard.Value.Wild_four){
				pid = playerIds[currentPlayer];
				getPlayerHand(pid).add(deck.drawCard());
				getPlayerHand(pid).add(deck.drawCard());
				JLabel message = new JLabel(pid + "draw 4 cards!");

			}
			if (card.getvalue() == UnoCard.Value.skip){
				JLabel message = new JLabel(playerIds[currentPlayer] + "was skipped");
				message.setFont(new Font("Arial", Font.BOLD, 48));
				JOptionPane.showMessageDialog(null, message);
				if (gameDirection == false){
					currentPlayer = (currentPlayer + 1) % playerIds.length;
				}
				else if (gameDirection == true){
					currentPlayer = (currentPlayer - 1) % playerIds.length;
					if (currentPlayer == -1){
						currentPlayer = playerIds.length;
					}
				}
			}
			if (card.getvalue() == UnoCard.Value.Reverse) {
				JLabel message = new JLabel(pid + "changed the game directin");
				message.setFont(new Font("Arial", Font.BOLD, 48));
				JOptionPane.showMessageDialog(null, message);

				gameDirection ^= true;
				if (gameDirection == true){
					currentPlayer = (currentPlayer -2) % playerIds.length;
					if (currentPlayer == -1)
					{
						currentPlayer = playerIds.length - 1;
					}
					if (currentPlayer == -2)
					{
						currentPlayer = playerIds.length - 2;
					}
				}
				else if (gameDirection == false){
					currentPlayer = (currentPlayer + 2)% playerIds.length;
				}
			}
		}
	}
}
class InvalidPlayerTurnException extends Exception
{
	String playerId;

	public InvalidPlayerTurnException(String message, String pid){
		super(message);
		playerId = pid;
	}
	public String getPid(){
		return playerId;
	}

}
class InvalidColorSubmissionException extends Exception{
	private UnoCard.Color expected;
	private UnoCard.Color actual;

	public InvalidColorSubmissionException(String message, UnoCard.Color actual, UnoCard.Color excepted){
		this.actual = actual;
		this.expected = expected;
	}
}
	class InvalidValueSubmissionException extends Exception{
		private UnoCard.Value expected;
		private UnoCard.Value actual;

		public InvalidValueSubmissionException(String message, UnoCard.Value actual, UnoCard.Value expected){
			this.actual = actual;
			this.expected = expected;
		}

}

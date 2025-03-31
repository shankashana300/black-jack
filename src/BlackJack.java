
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.util.ArrayList; //Cards to be stored in the player's hands
import java.util.random.*; //Shuffling the deck
import javax.swing.*;

public class BlackJack {

    //Representation
    private class Card{
        String value;
        String type;

        Card(String value,String type){
            this.value=value;
            this.type=type;
        }

        public String toString(){
            return value + "-" + type;
        }

        public int getValue(){
            if("AJQK".contains(value))
            { //A J Q K
                if (value == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); //2-10
        };

        public boolean isAce(){
            return value == "A";
        }

        public String getImagePath(){
            return "./cards/"+toString()+".png";
        };
    }

    ArrayList <Card> deck;
    Random random = new Random(); //For shuffling

    //Dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //Window
    int boardWidth = 600;
    int boardHeight = boardWidth;
    // Has to be 1-to-1.4, the ratio, I mean.
    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            try
            {
                //Draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20,cardWidth, cardHeight, null);

                //Drawing the dealer hand
                for(int i=0;i<dealerHand.size();i++){
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth+25+(cardWidth+5)*i,20 ,cardWidth, cardHeight, null);
                }

                //Drawing the Player's hand
                for(int i=0;i<playerHand.size();i++)
                {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth+5)*i ,320 ,cardWidth, cardHeight, null);
                };

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum>21) {
                        message = "YOU LOSE";
                    }
                    else if (dealerSum>21) {
                        message = "YOU WIN";
                    }
                    //both dealer and player <= 21
                    else if (playerSum == dealerSum) {
                        message = "TIE";
                    }
                    else if (playerSum>dealerSum) {
                        message="YOU WIN";
                    }
                    else if (playerSum<dealerSum) {
                        message ="YOU LOSE!";
                    }
                    //Drawing on the game panel.
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                };

            } catch(Exception e){
                e.printStackTrace();
            }
        };
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Check");

    BlackJack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                //"reducePlayerAce()" will demo, ACE as 10/1 by sub if total cards in hand is more than 21.
                if (reducePlayerAce() > 21) {
                  hitButton.setEnabled(false);  
                };
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while(dealerSum < 17)
                {
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                };
                gamePanel.repaint();
            }
        });
        gamePanel.repaint();
    }

    //Create the deck, shuffle, assign 2 cards to player and dealer
    public void startGame(){
        //build deck
        buildDeck();
        //Shuffle
        shuffleDeck();

        //Dealer
        dealerHand = new ArrayList<Card>();
        dealerSum=0;
        dealerAceCount=0;

        hiddenCard = deck.remove(deck.size()-1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER: ");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        //Player
        playerHand = new ArrayList<Card>();
        playerSum=0;
        playerAceCount=0;

        for(int i=0;i<2;i++)
        {
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        };
        System.out.println("PLAYER: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    public void buildDeck(){
        deck = new ArrayList<Card>();    
        String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] types = {"C","D","H","S"};

        //For each value loop through each type
        //Creating the deck
        for(int i=0; i<types.length; i++){
            for(int j=0;j<values.length;j++){
                Card card = new Card(values[j], types[i]);
                deck.add(card);

                
            }
        }
        System.out.println("BUILD DECK: ");
        System.out.println(deck);
    }
  
    public void shuffleDeck(){
        for (int i = 0; i < deck.size();i++) 
        {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);

            //Swapping the cards
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
        System.out.println("AFTER SHUFFLE: ");
        System.out.println(deck);
    }

    //Below 2 are for special circumstance involving ACE(S).
    public int reducePlayerAce(){
        while(playerSum>21 && playerAceCount>0)
        {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    };

    public int reduceDealerAce(){
        while(dealerSum>21 && dealerAceCount>0)
        {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    };
}

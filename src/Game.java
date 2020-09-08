import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;

public class Game {

    private Player p = new Player();
    private Deck cards = new Deck();
    private boolean test;
    // you'll probably need some more here


    public Game(String[] testHand) {
        this.p = p;
        this.cards = cards;
        test = true;

        String[] suitString = new String[5];
        String[] rankString = new String[5];
        int[] suitInt = new int[5];
        int[] rankInt = new int[5];

        for(int i=0; i<5; i++) {
            suitString[i] = testHand[i].substring(0, 1);
            if(suitString[i].equals("s")) {
                suitInt[i] = 1;
            } else if(suitString[i].equals("h")) {
                suitInt[i] = 2;
            } else if(suitString[i].equals("c")) {
                suitInt[i] = 3;
            } else {
                suitInt[i] = 4;
            }

            if(testHand[i].length() == 2) {
                rankString[i] = "" + testHand[i].charAt(1);
                rankInt[i] = Integer.parseInt(rankString[i]);
                p.addCard(new Card(suitInt[i], rankInt[i]));
            } else {
                rankString[i] = "" + testHand[i].charAt(1) + testHand[i].charAt(2);
                rankInt[i] = Integer.parseInt(rankString[i]);
                p.addCard(new Card(suitInt[i], rankInt[i]));
            }

        }

        // This constructor is to help test your code.
        // use the contents of testHand to
        // make a hand for the player
        // use the following encoding for cards
        // c = clubs
        // d = diamonds
        // h = hearts
        // s = spades
        // 1-13 correspond to ace-king
        // example: s1 = ace of spades
        // example: testhand = {s1, s13, s12, s11, s10} = royal flush


    }

    public Game() {
        this.p = p;
        this.cards = cards;
        test = false;

        System.out.println("Welcome to Video Poker!");
        System.out.println("Your starting bankroll is " + p.getBankroll()+".");

        cards.shuffle();
        Scanner s = new Scanner(System.in);
        System.out.print("How many tokens would you like to use?");
        System.out.println(" " + "(Enter integer between 1 and 5):");
        int tokens = s.nextInt();
        p.bets(tokens);

        p.addCard(cards.deal());
        p.addCard(cards.deal());
        p.addCard(cards.deal());
        p.addCard(cards.deal());
        p.addCard(cards.deal());

        System.out.print("Here are your cards.");
        System.out.print(" " + "Would you like to replace any of them? (Enter");
        System.out.print(" " + "corresponding index number to replace, any");
        System.out.println(" " + "other number to stop)");

        for(int i=0; i<5; i++) {
            System.out.println(p.getHand().get(i).toString() + " - Index: "+i);
        }
        boolean removeNext = true;
        while(removeNext) {
            int discarded = s.nextInt();
            if(discarded == 0) {
                p.getHand().get(0).setRank(0);
                p.addCard(cards.deal());
            } else if(discarded == 1) {
                p.getHand().get(1).setRank(0);
                p.addCard(cards.deal());
            } else if(discarded == 2) {
                p.getHand().get(2).setRank(0);
                p.addCard(cards.deal());
            } else if(discarded == 3) {
                p.getHand().get(3).setRank(0);
                p.addCard(cards.deal());
            } else if(discarded == 4) {
                p.getHand().get(4).setRank(0);
                p.addCard(cards.deal());
            } else {
                break;
            }
        }
        for(int i=0; i<5; i++) {
            if(p.getHand().get(i).getRank() == 0) {
                p.removeCard(p.getHand().get(i));
            }
        }

        // This no-argument constructor is to actually play a normal game
    }

    public void play() {
        // this method should play the game

        System.out.println("Your hand is: ");
        for(int i=0; i<5; i++) {
            System.out.println(p.getHand().get(i).toString());
        }

        double payout = 0;
        if(checkHand(p.getHand()).equals("Royal Flush")) {
            payout = 250;
            System.out.println("You have a royal flush! Your payout is 250.");
        } else if(checkHand(p.getHand()).equals("Straight Flush")) {
            payout = 50;
            System.out.println("You have a straight flush! Your payout is 50.");
        } else if(checkHand(p.getHand()).equals("Four of a kind")) {
            payout = 25;
            System.out.println("You have four of a kind! Your payout is 25.");
        } else if(checkHand(p.getHand()).equals("Full House")) {
            payout = 6;
            System.out.println("You have a full house! Your payout is 6.");
        } else if(checkHand(p.getHand()).equals("Flush")) {
            payout = 5;
            System.out.println("You have a flush! Your payout is 5.");
        } else if(checkHand(p.getHand()).equals("Straight")) {
            payout = 4;
            System.out.println("You have a straight! Your payout is 4.");
        } else if(checkHand(p.getHand()).equals("Three of a kind")) {
            payout = 3;
            System.out.println("You have three of a kind! Your payout is 3.");
        } else if(checkHand(p.getHand()).equals("Two pair")) {
            payout = 2;
            System.out.println("You have a two pair! Your payout is 2.");
        } else if(checkHand(p.getHand()).equals("Pair")) {
            payout = 1;
            System.out.println("You have a pair! Your payout is 1.");
        } else if(checkHand(p.getHand()).equals("High Card")) {
            payout = 0;
            System.out.println("You have a high card. Your payout is 0.");
        }

        p.winnings(payout);

        if(test == false) {
            System.out.println("Your bankroll is " + p.getBankroll());
        }

    }

    public String checkHand(ArrayList<Card> hand) {
        Collections.sort(hand);

        String result = "";
        if(checkRoyalFlush(hand)) {
            result = "Royal Flush";
            return result;
        } else if(checkStraightFlush(hand)) {
            result = "Straight Flush";
            return result;
        } else if(checkFourOfKind(hand)) {
            result = "Four of a kind";
            return result;
        } else if(checkFullHouse(hand)) {
            result = "Full House";
            return result;
        } else if(checkFlush(hand)) {
            result = "Flush";
            return result;
        } else if(checkStraight(hand)) {
            result = "Straight";
            return result;
        } else if(checkThreeOfKind(hand)) {
            result = "Three of a kind";
            return result;
        } else if(checkTwoPair(hand)) {
            result = "Two pair";
            return result;
        } else if(checkPair(hand)) {
            result = "Pair";
            return result;
        } else {
            result = "High Card";
            return result;
        }
        // this method should take an ArrayList of cards
        // as input and then determine what evaluates to and
        // return that as a String

    }

    public boolean checkRoyalFlush(ArrayList<Card> hand) {

        boolean straight = checkStraight(hand);
        boolean flush = checkFlush(hand);
        boolean kingLast = hand.get(4).getRank() == 13;
        if(straight && flush && kingLast) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkStraightFlush(ArrayList<Card> hand) {
        if(checkStraight(hand) && checkFlush(hand)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFourOfKind(ArrayList<Card> hand) {

        boolean firstCombo = false;
        boolean secondCombo = false;

        boolean b = hand.get(0).getRank() == hand.get(1).getRank();
        boolean b1 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean b2 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean b3 = hand.get(3).getRank() == hand.get(4).getRank();


        if(b && b1 && b2) {
            firstCombo = true;
        }
        if(b1 && b2 && b3) {
            secondCombo = true;
        }

        if(firstCombo || secondCombo) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFullHouse(ArrayList<Card> hand) {
        boolean firstCombo = false;
        boolean secondCombo = false;

        boolean b = hand.get(0).getRank() == hand.get(1).getRank();
        boolean b1 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean b2 = hand.get(3).getRank() == hand.get(4).getRank();
        boolean b3 = hand.get(2).getRank() == hand.get(3).getRank();

        if(b && b1 && b2) {
            firstCombo = true;
        }
        if(b && b3 && b2) {
            secondCombo = true;
        }
        if(firstCombo || secondCombo) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFlush(ArrayList<Card> hand) {

        boolean b = hand.get(0).getSuit() == hand.get(1).getSuit();
        boolean b1 = hand.get(1).getSuit() == hand.get(2).getSuit();
        boolean b2 = hand.get(2).getSuit() == hand.get(3).getSuit();
        boolean b3 = hand.get(3).getSuit() == hand.get(4).getSuit();

        if(b && b1 && b2 && b3) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkStraight(ArrayList<Card> hand) {
        boolean firstCombo = false;
        boolean secondCombo = false;

        boolean b = hand.get(1).getRank() == 10;
        boolean b1 = hand.get(2).getRank() == 11;
        boolean b2 = hand.get(3).getRank() == 12;
        boolean b3 = hand.get(4).getRank() == 13;
        boolean b4 = hand.get(1).getRank() == 2;
        boolean b5 = hand.get(2).getRank() == 3;
        boolean b6 = hand.get(3).getRank() == 4;
        boolean b7 = hand.get(4).getRank() == 5;

        if(hand.get(0).getRank() == 1) { // checks to see whether there is an ace
            if(b && b1 && b2 && b3) {
                firstCombo = true;
            }
            if(b4 && b5 && b6 && b7) {
                secondCombo = true;
            }
            if(firstCombo || secondCombo) {
                return true;
            }

        } else { // checks general increasing order in absence of ace
            int nextNumber = hand.get(0).getRank() + 1;
            for(int i=1; i<5; i++) {
                if(hand.get(i).getRank() != nextNumber) {
                    return false;
                }
                nextNumber++;
            }
            return true;
        }
        return false;
    }

    public boolean checkThreeOfKind(ArrayList<Card> hand) {
        boolean firstCombo = false;
        boolean secondCombo = false;
        boolean thirdCombo = false;

        boolean b = hand.get(0).getRank() == hand.get(1).getRank();
        boolean b1 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean b2 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean b3 = hand.get(3).getRank() == hand.get(4).getRank();

        if(b && b1) {
            firstCombo = true;
        }

        if(b1 && b2) {
            secondCombo = true;
        }

        if(b2 && b3) {
            thirdCombo = true;
        }

        if(firstCombo || secondCombo || thirdCombo) {
            return true;
        } else {
            return false;
        }

    }

    public boolean checkTwoPair(ArrayList<Card> hand) {
        boolean firstCombo = false;
        boolean secondCombo = false;
        boolean thirdCombo = false;

        boolean b = hand.get(0).getRank() == hand.get(1).getRank();
        boolean b1 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean b2 = hand.get(3).getRank() == hand.get(4).getRank();
        boolean b3 = hand.get(1).getRank() == hand.get(2).getRank();

        if(b && b1) {
            firstCombo = true;
        }

        if(b && b2) {
            secondCombo = true;
        }

        if(b3 && b2) {
            thirdCombo = true;
        }

        if(firstCombo || secondCombo || thirdCombo) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPair(ArrayList<Card> hand) {
        boolean firstCombo = false;
        boolean secondCombo = false;
        boolean thirdCombo = false;
        boolean fourthCombo = false;

        if(hand.get(0).getRank() == hand.get(1).getRank()) {
            firstCombo = true;
        }

        if(hand.get(1).getRank() == hand.get(2).getRank()) {
            secondCombo = true;
        }

        if(hand.get(2).getRank() == hand.get(3).getRank()) {
            thirdCombo = true;
        }

        if(hand.get(3).getRank() == hand.get(4).getRank()) {
            fourthCombo = true;
        }

        if(firstCombo || secondCombo || thirdCombo || fourthCombo) {
            return true;
        } else {
            return false;
        }

    }
}
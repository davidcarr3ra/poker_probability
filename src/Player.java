import java.util.ArrayList;

public class Player {

    private ArrayList<Card> hand = new ArrayList<Card>(); // the player's cards
    private double bankroll; // bankroll = amount in wallet
    private double bet;

    private boolean dealer;
    private boolean small_blind;
    private boolean big_blind;
    private boolean still_in_round;
    private boolean bankrupt;
    private boolean check;
    private double last_move; // -1=fold, 0=check, positive=bet, -2 = start of game.
    private boolean last_to_raise;

    // you may choose to use more instance variables

    public Player() {
        this.bankroll = 1000;
        this.bet = bet;
        this.hand = hand;
        this.dealer = dealer;
        this.small_blind = small_blind;
        this.big_blind = big_blind;
        this.still_in_round = true;
        this.check = false;
        this.last_move = -2;
        this.last_to_raise = false;
    }

    public void setRaise(boolean b) {
        this.last_to_raise = b;
    }

    public boolean raisedLast() {
        return last_to_raise;
    }

    public boolean inRound() {
        return this.still_in_round;
    }

    public void setBankrupt() {
        this.bankrupt = true;
        this.still_in_round = false;
    }

    public void setLastMove(double num) {
        this.last_move = num;
    }

    public double getLastMove() {
        return this.last_move;
    }

    public void check() { // no need to use setLastMove()
        this.check = true;
        this.last_move = 0;
    }

    public void fold() { // no need to use setLastMove()
        this.still_in_round = false;
        this.last_move = -1;
    }

    public String getButton() {
        if(this.dealer == true) {
            return "dealer";
        } else if(this.big_blind == true) {
            return "big";
        } else if(this.small_blind == true) {
            return "small";
        } else {
            return "none";
        }
    }
    public void setDealer() {
        this.dealer = true;
    }

    public void setSmallBlind() {
        this.small_blind = true;
    }

    public void setBigBlind() {
        this.big_blind = true;
    }

    public void addCard(Card c) {
        // add the card c to the player's hand
        hand.add(c);
    }

    public void removeCard(Card c) {
        // remove the card c from the player's hand
        hand.remove(hand.indexOf(c));
    }

    public void bets(double amount) { // note: update setLastMove alongside using this method
        // player makes a bet
        // number of tokens
        bet = amount;
        bankroll = bankroll - amount;
    }

    public void winnings(double odds) {
        //	adjust bankroll if player wins
        bankroll = bankroll + bet*odds;
    }

    public double getBankroll() {
        // return current balance of bankroll
        return bankroll;
    }

    public void setBankroll(double amount) {
        this.bankroll = amount;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

}

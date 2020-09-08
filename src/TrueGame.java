import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

public class TrueGame {
    // Max players = 4
    Player[] players = new Player[4];
    Player p = new Player();
    private Deck cards = new Deck();
    private boolean test;
    private Card[] board = new Card[5]; // spaces for table cards
    private Card[] burns = new Card[3]; // spaces for burns
    private double pot;
    private double prev_bet;
    private double last_bet;
    private int current_position = 0;
    private boolean preflop;
    private boolean checking_royal;
    private boolean checking_sf;
    private int dealer_pos;
    private int total_players;
    private boolean bets_outstanding;
    private int players_remaining;

    public TrueGame(String[] testHand) {
        this.test = true;

        String[] suitString = new String[7];
        String[] rankString = new String[7];
        int[] suitInt = new int[7];
        int[] rankInt = new int[7];

        for (int i = 0; i < 7; i++) {
            suitString[i] = testHand[i].substring(0, 1);
            if (suitString[i].equals("s")) {
                suitInt[i] = 1;
            } else if (suitString[i].equals("h")) {
                suitInt[i] = 2;
            } else if (suitString[i].equals("c")) {
                suitInt[i] = 3;
            } else {
                suitInt[i] = 4;
            }

            if (testHand[i].length() == 2) {
                rankString[i] = "" + testHand[i].charAt(1);
                rankInt[i] = Integer.parseInt(rankString[i]);
                p.addCard(new Card(suitInt[i], rankInt[i]));
            } else {
                rankString[i] = "" + testHand[i].charAt(1) + testHand[i].charAt(2);
                rankInt[i] = Integer.parseInt(rankString[i]);
                p.addCard(new Card(suitInt[i], rankInt[i]));
            }
        }

        System.out.println("You have a " + checkHand(p.getHand()));

    }

    public TrueGame() {
        this.pot = 0;
        this.preflop = true;
        this.test = false;
        this.total_players = 4;
        this.players_remaining = total_players;

        Player p0 = new Player();
        Player p1 = new Player();
        Player p2 = new Player();
        Player p3 = new Player();
        this.players[0] = p0;
        this.players[1] = p1;
        this.players[2] = p2;
        this.players[3] = p3;

        System.out.println("Welcome to Texas Hold'em! There are " + total_players + " other players at your table. You are the dealer, Player 0.");
        Scanner in = new Scanner(System.in);
        System.out.println("How much would you like to buy in?"); // others have 1000 by default
        double buy_in = in.nextDouble();
        players[0].setBankroll(buy_in);
        System.out.println("Your starting bankroll is " + players[0].getBankroll() + ".");

        cards.shuffle();

        // set dealer and blinds. For now, p0 = dealer to begin with. Later: deal 4 cards + determine + reset
        this.dealer_pos = 0;
        players[0].setDealer();
        players[1].setSmallBlind();
        players[2].setBigBlind();

        // dealing pocket cards
        dealPockets(dealer_pos + 1);

        System.out.println("Here are your pocket cards:");
        for (int i = 0; i < 2; i++) {
            System.out.println(players[0].getHand().get(i).toString());
        }

    }

    public void dealPockets(int small_blind_position) {
        for(int i=small_blind_position; i<small_blind_position+8; i++) {
            players[i%4].addCard(cards.deal());
        }
    }

    public void play() {
        // this method should play the game

        for(int i=0; i<players.length; i++) {
            if(players[i].getButton().equals("dealer")) {
                System.out.println("Dealer at position " + i);
            } else if(players[i].getButton().equals("small")) {
                System.out.println("Small blind at position " + i);
            } else if(players[i].getButton().equals("big")) {
                System.out.println("Big blind at position " + i);
            }
        }

        // add blinds to pot -- ACCOUNT FOR BANKRUPTCY HERE?
        for (Player p : players) {
            if(p.getButton().equals("small")) {
                p.bets(25);
                pot += 25;
                last_bet = 25;
                p.setLastMove(25);
            } else if(p.getButton().equals("big")) {
                p.bets(50);
                pot += 50;
                prev_bet = last_bet;
                last_bet = 50;
                p.setLastMove(50);
            }
        }

        // bets oustanding = false if all players' last moves are the same (excluding folds and bankruptcies)
        // every time someone raises, bets outstanding should be checked at the end of that cycle.
        // exception: pre-flop, big blind gets to check assuming no one raised the blind.

        // cycle ends at the position before the last raise. So, loop should re-start every time there is a new bet.
        // upon every raise, continue.

        // pre-flop
        this.current_position = 2; // (under the gun follows)
        tableCycle();
        preflop = false;
        resetLastBets();

        // post-flop -- small blind (position 1) always starts. So, simply cycle starting at position one until no bets outstanding.

        System.out.println("No more bets outstanding. Here is the flop.");
        // burn and reveal flop
        burns[0] = cards.deal();
        for(int i=0; i<3; i++) {
            board[i] = cards.deal();
            System.out.println(board[i].toString());
        }

        current_position = 0; // so that it goes to small blind first in the table cycle
        tableCycle();
        resetLastBets();

        System.out.println("No more bets outstanding. Here is the turn.");
        // burn and reveal turn
        burns[1] = cards.deal();
        board[3] = cards.deal();
        System.out.println(board[3].toString());

        this.current_position = 0; // so that it goes to small blind first in the table cycle
        tableCycle();
        resetLastBets();

        System.out.println("No more bets outstanding. Here is the river.");
        // burn and reveal river
        burns[2] = cards.deal();
        board[4] = cards.deal();
        System.out.println(board[4].toString());

        this.current_position = 0; // so that it goes to small blind first in the table cycle
        tableCycle();
        resetLastBets();

        // determine winner, distribute winnings. (for now, dw about side pots. also dw about order of showdown).
        System.out.println("No more outstanding bets. Show your cards.");


        for(int i=0; i<players.length; i++) {
            System.out.println("Player " + i + "'s cards are: ");
            for(int j = 0; j<2; j++) {
                System.out.println(players[i].getHand().get(j).toString());
            }
        }

        // identify who has what hands. print this.
        // 7 cards for each player.

        System.out.println("-----------------------------------------");
        for(int i=0; i<players.length; i++) {
            String hand = checkHand(players[i].getHand());
            System.out.println("Player " + i + " has a " + hand);
        }

        // determine winner + payout! (see 1004)

    }

    public void tableCycle() { // reset table cycle if there was a raise. Not a raise: call when last turn folded.
        bets_outstanding = true;
        table_cycle: while(bets_outstanding) {
            if(players_remaining == 1) {
                System.out.println("No more players in this round, skipping to end.");
                bets_outstanding = false;
                break;
            }
            this.current_position = (current_position + 1)%4;
            System.out.println("Turn: position " + current_position);
            giveTurnTo(current_position);
            // raise def: current player's bet > prev bet
//            if(players[current_position].getLastMove() > -1 && (players[current_position].getLastMove() > players[((current_position - 1) + 4)%4].getLastMove())) { // if raised
//                System.out.println("Resetting table cycle....");
//                setLastRaise(current_position);
//                continue table_cycle;
//            }
            if(players[current_position].getLastMove() > prev_bet) { // if raised
                System.out.println("Resetting table cycle....");
                setLastRaise(current_position);
                continue table_cycle;
            }

            if(players_remaining == 1) {
                System.out.println("No more players in this round, skipping to end.");
                bets_outstanding = false;
                break;
            }
            this.current_position = (current_position + 1)%4;
            System.out.println("Turn: position " + current_position);
            giveTurnTo(current_position);
            if(players[current_position].getLastMove() > prev_bet) { // if raised
                System.out.println("Resetting table cycle....");
                setLastRaise(current_position);
                continue table_cycle;
            }


            if(players_remaining == 1) {
                System.out.println("No more players in this round, skipping to end.");
                bets_outstanding = false;
                break;
            }
            current_position = (current_position + 1)%4;
            System.out.println("Turn: position " + current_position);
            giveTurnTo(current_position);
            if(players[current_position].getLastMove() > prev_bet) { // if raised
                System.out.println("Resetting table cycle....");
                setLastRaise(current_position);
                continue table_cycle;
            }


            if(players_remaining == 1) {
                System.out.println("No more players in this round, skipping to end.");
                bets_outstanding = false;
                break;
            }
            current_position = (current_position + 1)%4;
            System.out.println("Turn: position " + current_position);
            giveTurnTo(current_position);
            if(players[current_position].getLastMove() > prev_bet) { // if raised
                System.out.println("Resetting table cycle....");
                setLastRaise(current_position);
                continue table_cycle;
            }

            bets_outstanding = false;

        }
    }

    public void setLastRaise(int position) {
        // set all last_to_raise to false except for player at this position
        for(int i=0; i<players.length; i++) {
            if(i == position) {
                players[i].setRaise(true);
            } else {
                players[i].setRaise(false);
            }
        }
    }

    public void resetLastBets() {
        for(Player p : players) {
            if(p.inRound()) {
                p.setLastMove(-2);
                p.setRaise(false);
            }
        }
        this.last_bet = 0;
        this.prev_bet = 0;
    }

    public void giveTurnTo(int position) {
        if(players[position].raisedLast()) {
            System.out.println("Reached player who raised. Stopping cycle...");
            return;
        }

        if(!players[position].inRound()) {
            System.out.println("Skipping player " + position + ".......");
            return; // give turn to next player if already folded / went broke
        }

        if(position == 0) { // my turn
            myMove();
        } else if(position == 2 && this.preflop) { // big blind pre-flop
            bigBlindMove();
        } else { // bot turn
            randomMove(players[position]);
        }
    }

    public void bigBlindMove() {
        // if no one has raised beyond big blind -- check. otherwise, randommove.
        if(this.last_bet == 50) {
            players[2].check();
            System.out.println("Player 2 checks.");
        } else {
            randomMove(players[2]);
        }
    }

    public void myMove() { // add logic: not in round means you have less than the small blind.
                            // can be in round but unable to play until small blind if you have between small blind and big blind
        if(players[0].getBankroll() == 0) {
            System.out.println("Your turn has passed as your bankroll is zero.");
        } else if(!players[0].inRound()) {
            System.out.println("Skipping you, since you folded this round.");
        } else {
            System.out.print("What would you like to do? -1 to fold, 0 to check, positive to call/raise");
            Scanner in = new Scanner(System.in);

            boolean invalid = true;
            while (invalid) {
                double move = in.nextDouble();
                if (move == -1) { // fold
                    System.out.println("You just folded.");
                    players[0].fold();
                    players_remaining--;
                    invalid = false;
                } else if (move == 0) { // check
                    if (this.last_bet != 0) {
                        System.out.println("There's an outstanding bet on the table. You cannot check.");
                    } else {
                        players[0].check();
                        last_bet = 0;
                        prev_bet = 0;
                        System.out.println("You just checked.");
                        invalid = false;
                    }
                } else if (move > 0) { // call / raise
                    if (players[0].getLastMove() <= 0) { // if we have not yet bet this round
                        if (move < last_bet && last_bet <= players[0].getBankroll()) { // we can afford it but put less
                            System.out.println("You must at least match the last bet, which is " + last_bet);
                        } else if (move < players[0].getBankroll()) { // call/raise
                            players[0].bets(move);
                            players[0].setLastMove(move);
                            pot += move;
                            if(move == last_bet) { // CALL
                                System.out.println("You just called " + move);
                                // last bet doesn't change
                                prev_bet = last_bet;
                            } else { // RAISE
                                System.out.println("You just raised to " + move);
                                prev_bet = last_bet;
                                last_bet = move;
                                // continue table_cycle;
                            }
                            invalid = false;
                        } else if (move == players[0].getBankroll()) { // call/raise (all in)
                            players[0].bets(move);
                            players[0].setLastMove(move);
                            pot += move;
                            players[0].setBankrupt();
                            if(move == last_bet) { // CALL, ALL IN
                                System.out.println("You just called " + move + " and ran out of chips");
                                // last bet doesn't change
                                prev_bet = last_bet;
                            } else { // RAISE, ALL IN
                                System.out.println("You just went all in and raised to " + move);
                                prev_bet = last_bet;
                                last_bet = move;
                            }
                            players_remaining--;
                            invalid = false;
                        } else {
                            System.out.println("Insufficient chips. Try again.");
                        }
                    } else { // if we have already bet this round. Bankruptcy already accounted for in outer conditional.
                        if (move < last_bet - players[0].getLastMove() && last_bet - players[0].getLastMove() <= players[0].getBankroll()) { // we can afford it but put less
                            System.out.println("You must at least match the last bet, which is " + last_bet);
                        } else if (move <= players[0].getBankroll()) {
                            players[0].bets(move);
                            this.pot += move;
                            if (move == last_bet - players[0].getLastMove()) { // CALL
                                // last bet doesn't change
                                prev_bet = last_bet;
                                if(move == players[0].getBankroll()) { // go broke
                                    players[0].setBankrupt();
                                    System.out.println("You just called " + last_bet + " and ran out of chips");
                                    players_remaining--;
                                } else {
                                    System.out.println("You just called " + last_bet);
                                }
                                players[0].setLastMove(players[0].getLastMove() + move);
                                invalid = false;
                            } else { // RAISE
                                prev_bet = last_bet;
                                last_bet = players[0].getLastMove() + move;
                                players[0].setLastMove(last_bet);
                                if(move == players[0].getBankroll()) { // all in
                                    players[0].setBankrupt();
                                    players_remaining--;
                                    System.out.println("You just went all in and raised to " + last_bet);
                                } else {
                                    System.out.println("You just raised to " + last_bet);
                                }
                                // continue table_cycle;
                                invalid = false;
                            }
                        } else {
                            System.out.println("Insufficient chips. Try again.");
                        }
                    }
                } else {
                    System.out.println("Invalid output. Try again.");
                }
            }
        }
    }

    public void randomMove(Player p) {
        Random r = new Random();
        int num = r.nextInt(100);
        if(num < 20) { // fold
            p.fold();
            players_remaining--;
            System.out.println("Player " + current_position + " folds.");
        } else if(this.last_bet == 0 && num >= 20 && num <= 80) { // check
            p.check();
            last_bet = 0;
            prev_bet = 0;
            System.out.println("Player " + current_position + " checks.");
        } else { // (here, possibilities for num: between 20 and 100. split into call and raise.)
            if(p.getLastMove() <= 0) { // if we have not yet bet this round
                if(p.getBankroll() <= last_bet) { // CALL AND GO BROKE
                    p.setLastMove(p.getBankroll());
                    prev_bet = last_bet;
                    pot += p.getBankroll();
                    p.bets(p.getBankroll()); // call whatever I have left.
                    p.setBankrupt();
                    players_remaining--;
                    System.out.println("Player " + current_position + " calls and goes broke.");
                } else if(num >= 20 && num <= 80) { // CALL
                    p.setLastMove(last_bet);
                    prev_bet = last_bet;
                    p.bets(last_bet);
                    pot += last_bet;
                    System.out.println("Player " + current_position + " calls.");
                } else { // 80-100
                    if (p.getBankroll() <= (last_bet + 50)) { // RAISE AND GO BROKE
                        p.setLastMove(p.getBankroll());
                        prev_bet = last_bet;
                        last_bet = p.getBankroll();
                        pot += p.getBankroll();
                        p.bets(p.getBankroll());
                        p.setBankrupt();
                        players_remaining--;
                        System.out.println("Player " + current_position + " raises to " + last_bet + " and goes broke.");
                    } else { // RAISE
                        p.setLastMove(last_bet + 50);
                        prev_bet = last_bet;
                        last_bet += 50;
                        pot += (last_bet);
                        p.bets(last_bet);
                        System.out.println("Player " + current_position + " raises to " + last_bet);
                    }
                }
            } else { // if we have already bet this round.
                if(p.getBankroll() == 0) {
                    return; // it's possible I went broke in last betting round. In this case, pass turn to next player.
                } else if((last_bet - p.getLastMove()) >= p.getBankroll()) { // CALL AND GO BROKE
                    p.setLastMove(p.getLastMove() + p.getBankroll());
                    prev_bet = last_bet;
                    this.pot += p.getBankroll();
                    p.bets(p.getBankroll()); // call whatever I have left
                    p.setBankrupt();
                    players_remaining--;
                    System.out.println("Player " + current_position + " calls and goes broke.");
                    // this.last_bet stays the same
                } else if(num >= 20 && num <= 80) { // CALL
                    prev_bet = last_bet;
                    this.pot += (last_bet - p.getLastMove());
                    p.bets(last_bet - p.getLastMove()); // call by matching raise
                    p.setLastMove(last_bet);
                    // last bet stays the same
                    System.out.println("Player " + current_position + " calls the raise.");
                } else { // 80-100
                    if ((last_bet - p.getLastMove() + 50) >= p.getBankroll()) { // RAISE THE RAISE AND GO BROKE
                        p.setLastMove(p.getLastMove() + p.getBankroll());
                        prev_bet = last_bet;
                        last_bet = p.getLastMove();
                        pot += p.getBankroll();
                        p.bets(p.getBankroll());
                        p.setBankrupt();
                        players_remaining--;
                        System.out.println("Player " + current_position + " calls and goes broke.");
                    } else { // RAISE THE RAISE
                        p.bets(last_bet - p.getLastMove() + 50);
                        pot += (last_bet - p.getLastMove() + 50);
                        p.setLastMove(last_bet + 50);
                        prev_bet = last_bet;
                        last_bet = p.getLastMove();
                        System.out.println("Player " + current_position + " raises to " + last_bet);
                    }
                }
            }

        }
    }

    public String checkHandNew(ArrayList<Card> hand) {

        // 7 card hand! Player's 2 pocket cards passed in, add 5 table cards.
        if(this.test == false) {
            for(Card c : this.board) {
                hand.add(c);
            }
        }

        Collections.sort(hand);

        // check type of hand first, then determine exactly how high it is? -- later

        // build sub-hands
        ArrayList<Card> sub_hand_1 = new ArrayList<Card>();
        ArrayList<Card> sub_hand_2 = new ArrayList<Card>();
        ArrayList<Card> sub_hand_3 = new ArrayList<Card>();

        for(int i=0; i<3; i++) {

            for(int j=i; j<i+5; j++) {
                if(i == 0) {
                    sub_hand_1.add(hand.get(j));
                } else if(i == 1) {
                    sub_hand_2.add(hand.get(j));
                } else {
                    sub_hand_3.add(hand.get(j));
                }
            }
        }

        String result = "";

        // 5 card hands

        // Royal Flush
        this.checking_royal = true;
//        if(checkRoyalFlushOld(sub_hand_1)) {
//            result = "Royal Flush";
//            return result;
//        }
//        if(checkRoyalFlushOld(sub_hand_2)) {
//            result = "Royal Flush";
//            return result;
//        }
//        if(checkRoyalFlushOld(sub_hand_3)) {
//            result = "Royal Flush";
//            return result;
//        }
        this.checking_royal = false;

        // Straight Flush
        this.checking_sf = true;
        if (checkStraightFlushOld(sub_hand_1)) {
            result = "Straight Flush";
            return result;
        }
        if (checkStraightFlushOld(sub_hand_2)) {
            result = "Straight Flush";
            return result;
        }
        if (checkStraightFlushOld(sub_hand_3)) {
            result = "Straight Flush";
            return result;
        }
        this.checking_sf = false;


        // 7 card hands
        if(checkFourOfKind(hand)) {
            result = "Four of a kind";
            return result;
        }

        if(checkFullHouse(hand)) {
            result = "Full House";
            return result;
        }


        // 5 card hands

        // Flush
        if(checkFlushOld(sub_hand_1)) {
            result = "Flush";
            return result;
        }
        if(checkFlushOld(sub_hand_2)) {
            result = "Flush";
            return result;
        }
        if(checkFlushOld(sub_hand_3)) {
            result = "Flush";
            return result;
        }

        // Straight
        if(checkStraightOld(sub_hand_1)) {
            result = "Straight";
            return result;
        }
        if(checkStraightOld(sub_hand_2)) {
            result = "Straight";
            return result;
        }
        if(checkStraightOld(sub_hand_3)) {
            result = "Straight";
            return result;
        }


        // 7 card hands
        if (checkThreeOfKind(hand)) {
            result = "Three of a kind";
            return result;
        }

        if (checkTwoPair(hand)) {
            result = "Two pair";
            return result;
        }

        if (checkPair(hand)) {
            result = "Pair";
            return result;
        }

        result = "High Card";
        return result;

    }

    public String checkHand(ArrayList<Card> hand) {

        // 7 card hand! Player's 2 pocket cards passed in, add 5 table cards.
        if(this.test == false) {
            for(Card c : this.board) {
                hand.add(c);
            }
        }

        Collections.sort(hand);

        // check type of hand first, then determine exactly how high it is? -- later

        String result = "";
        if (checkRoyalFlush(hand)) {
            result = "Royal Flush";
            this.checking_royal = false;
            return result;
        } else if (checkStraightFlush(hand)) {
            this.checking_sf = false;
            result = "Straight Flush";
            return result;
        } else if (checkFourOfKind(hand)) {
            result = "Four of a kind";
            return result;
        } else if (checkFullHouse(hand)) {
            result = "Full House";
            return result;
        } else if (checkFlush(hand)) {
            result = "Flush";
            return result;
        } else if (checkStraight(hand)) {
            result = "Straight";
            return result;
        } else if (checkThreeOfKind(hand)) {
            result = "Three of a kind";
            return result;
        } else if (checkTwoPair(hand)) {
            result = "Two pair";
            return result;
        } else if (checkPair(hand)) {
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

    public boolean checkRoyalFlush(ArrayList<Card> hand) { // faulty!
        this.checking_royal = true;
        boolean king1 = hand.get(4).getRank() == 13;
        boolean king2 = hand.get(5).getRank() == 13;
        boolean king3 = hand.get(6).getRank() == 13;
        boolean king_last = king1 || king2 || king3;

        if(checkStraight(hand) && checkFlush(hand) && king_last) {
            return true;
        }

        return false;

        // mark the location of the straight. if there is a flush between those positions, true. otherwise false.
        // if you have a straight AND (the first card is a 1 and the fifth card is a 13) -- AND you have a flush
    }

    public boolean checkStraightFlushOld(ArrayList<Card> hand) {
        if(checkFlushOld(hand) && checkStraightOld(hand)) {
            return true;
        }
        return false;
    }

    public boolean checkStraightFlush(ArrayList<Card> hand) { // faulty!
        this.checking_sf = true;
        if(checkStraight(hand) && checkFlush(hand)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFourOfKind(ArrayList<Card> hand) {

        boolean pair1 = hand.get(0).getRank() == hand.get(1).getRank();
        boolean pair2 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean pair3 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean pair4 = hand.get(3).getRank() == hand.get(4).getRank();
        boolean pair5 = hand.get(4).getRank() == hand.get(5).getRank();
        boolean pair6 = hand.get(5).getRank() == hand.get(6).getRank();

        if(pair1 && pair2 && pair3) { // transitive
            return true;
        }

        if(pair2 && pair3 && pair4) {
            return true;
        }

        if(pair3 && pair4 && pair5) {
            return true;
        }

        if(pair4 && pair5 && pair6) {
            return true;
        }

        return false;
    }

    public boolean checkFullHouse(ArrayList<Card> hand) {

        boolean pair1 = hand.get(0).getRank() == hand.get(1).getRank();
        boolean pair2 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean pair3 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean pair4 = hand.get(3).getRank() == hand.get(4).getRank();
        boolean pair5 = hand.get(4).getRank() == hand.get(5).getRank();
        boolean pair6 = hand.get(5).getRank() == hand.get(6).getRank();

        boolean trio1 = pair1 && pair2; // transitive
        boolean trio2 = pair2 && pair3;
        boolean trio3 = pair3 && pair4;
        boolean trio4 = pair4 && pair5;
        boolean trio5 = pair5 && pair6;


        if(trio1 && (pair4 || pair5 || pair6)) {
            return true;
        }

        if(trio2 && (pair5 || pair6)) {
            return true;
        }

        if(trio3 && (pair1 || pair6)) {
            return true;
        }

        if(trio4 && (pair1 || pair2)) {
            return true;
        }

        if(trio5 && (pair1 || pair2 || pair3)) {
            return true;
        }

        return false;
    }

    public boolean checkFlushOld(ArrayList<Card> hand) { // 5 card version
        // if we're checking for a royal / straight flush, leave sorted by rank. if just checking for flush, sort by suit.
        if(!(checking_royal || checking_sf)) {
            SuitComparator suit_comp = new SuitComparator();
            Collections.sort(hand, suit_comp); // sort by suit
        }

        boolean pair1 = hand.get(0).getSuit() == hand.get(1).getSuit();
        boolean pair2 = hand.get(1).getSuit() == hand.get(2).getSuit();
        boolean pair3 = hand.get(2).getSuit() == hand.get(3).getSuit();
        boolean pair4 = hand.get(3).getSuit() == hand.get(4).getSuit();

        Collections.sort(hand); // leave rank-sorted order

        if(pair1 && pair2 && pair3 && pair4) {
            return true;
        }

        return false;
    }

    public boolean checkFlush(ArrayList<Card> hand) {

        // if at least 5 of the 7 cards are the same suit, true. else false.

        SuitComparator suit_comp = new SuitComparator();
        Collections.sort(hand, suit_comp); // sort by suit

        boolean pair1 = hand.get(0).getSuit() == hand.get(1).getSuit();
        boolean pair2 = hand.get(1).getSuit() == hand.get(2).getSuit();
        boolean pair3 = hand.get(2).getSuit() == hand.get(3).getSuit();
        boolean pair4 = hand.get(3).getSuit() == hand.get(4).getSuit();
        boolean pair5 = hand.get(4).getSuit() == hand.get(5).getSuit();
        boolean pair6 = hand.get(5).getSuit() == hand.get(6).getSuit();

        Collections.sort(hand); // leave rank-sorted order

        if(pair1 && pair2 && pair3 && pair4) { // transitive
            return true;
        }

        if(pair2 && pair3 && pair4 && pair5) { // transitive
            return true;
        }

        if(pair3 && pair4 && pair5 && pair6) { // transitive
            return true;
        }

        return false;
    }

    public boolean checkStraightOld(ArrayList<Card> hand) { // 5 card version
        boolean b1 = hand.get(1).getRank() == 10;
        boolean b2 = hand.get(2).getRank() == 11;
        boolean b3 = hand.get(3).getRank() == 12;
        boolean b4 = hand.get(4).getRank() == 13;
        boolean b5 = hand.get(1).getRank() == 2;
        boolean b6 = hand.get(2).getRank() == 3;
        boolean b7 = hand.get(3).getRank() == 4;
        boolean b8 = hand.get(4).getRank() == 5;

        // check straight if there is an ace
        if(hand.get(0).getRank() == 1) {
            if(b1 && b2 && b3 && b4) {
                return true;
            }
            if(b5 && b6 && b7 && b8) {
                return true;
            }
        }

        // check general increasing order in absence of an ace
        int nextRank = hand.get(0).getRank() + 1;
        for(int i=1; i<5; i++) {
            if(hand.get(i).getRank() != nextRank) {
                return false;
            }
            nextRank++;
        }
        return true;

    }

    public boolean checkStraight(ArrayList<Card> hand) {

        // simplification: loop
        // loop from 0 to 4 while moving forward until last index is 6
        // valid straights: 1-5,2-6, ...., 9-13, 10-1
        // invalid straights: 11-2, 12-3, 13-4

        boolean b1 = hand.contains(new Card(1, 10));
        boolean b2 = hand.contains(new Card(1, 11));
        boolean b3 = hand.contains(new Card(1, 12));
        boolean b4 = hand.contains(new Card(1, 13));
        boolean b5 = hand.contains(new Card(1, 2));
        boolean b6 = hand.contains(new Card(1, 3));
        boolean b7 = hand.contains(new Card(1, 4));
        boolean b8 = hand.contains(new Card(1, 5));

        // if there is an ace
        if(hand.contains(new Card(1, 1))) { // testing out contains method
            if(b1 && b2 && b3 && b4) {
                return true;
            }
            if(b5 && b6 && b7 && b8) {
                return true;
            }
        }

        for(int i=0; i<3; i++) {
            boolean straight = true;
            for (int j = i; j < i + 4; j++) {
                if ((hand.get(j).getRank() + 1) % 13 != hand.get(j + 1).getRank()) {
                    straight = false;
                }
            }
            if(straight) {
                // account for invalid straights -- (can delete this now as we accounted for aces above)
                for (int k = 0; k < 3; k++) {
                    if ((hand.get(k).getRank() == 11 && hand.get(k + 4).getRank() == 2) ||
                            (hand.get(k).getRank() == 12 && hand.get(k + 4).getRank() == 3) ||
                            (hand.get(k).getRank() == 13 && hand.get(k + 4).getRank() == 4)) {
                        return false;
                    }
                }
                return true;
            }

        }
        return false;
    }

    public boolean checkThreeOfKind(ArrayList<Card> hand) {

        boolean pair1 = hand.get(0).getRank() == hand.get(1).getRank();
        boolean pair2 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean pair3 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean pair4 = hand.get(3).getRank() == hand.get(4).getRank();
        boolean pair5 = hand.get(4).getRank() == hand.get(5).getRank();
        boolean pair6 = hand.get(5).getRank() == hand.get(6).getRank();

        if(pair1 && pair2) { // transitive
            return true;
        }

        if(pair2 && pair3) {
            return true;
        }

        if(pair3 && pair4) {
            return true;
        }

        if(pair4 && pair5) {
            return true;
        }

        if(pair5 && pair6) {
            return true;
        }

        return false;

    }

    public boolean checkTwoPair(ArrayList<Card> hand) {

        // alternative idea: loop through hand. if two consecutive cards are the same rank, pairs++, record that rank
        // at end of loop: if pairs >= 2: two pair.
        boolean combo1 = false;
        boolean combo2 = false;
        boolean combo3 = false;
        boolean combo4 = false;
        boolean combo5 = false;
        boolean combo6 = false;
        boolean combo7 = false;
        boolean combo8 = false;
        boolean combo9 = false;
        boolean combo10 = false;

        boolean pair1 = hand.get(0).getRank() == hand.get(1).getRank();
        boolean pair2 = hand.get(2).getRank() == hand.get(3).getRank();
        boolean pair3 = hand.get(4).getRank() == hand.get(5).getRank();
        boolean pair4 = hand.get(5).getRank() == hand.get(6).getRank();
        boolean pair5 = hand.get(1).getRank() == hand.get(2).getRank();
        boolean pair6 = hand.get(3).getRank() == hand.get(4).getRank();

        if(pair1 && pair2) {
            combo1 = true;
        }

        if(pair1 && pair3) {
            combo2 = true;
        }

        if(pair1 && pair4) {
            combo3 = true;
        }

        if(pair1 && pair6) {
            combo4 = true;
        }

        if(pair2 && pair3) {
            combo5 = true;
        }

        if(pair2 && pair4) {
            combo6 = true;
        }

        if(pair3 && pair5) {
            combo7 = true;
        }

        if(pair4 && pair5) {
            combo8 = true;
        }

        if(pair4 && pair6) {
            combo9 = true;
        }

        if(pair5 && pair6) {
            combo10 = true;
        }


        if(combo1 || combo2 || combo3 || combo4 || combo5 || combo6 || combo7 || combo8 || combo9 || combo10) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPair(ArrayList<Card> hand) { // made more efficient.

        if(hand.get(0).getRank() == hand.get(1).getRank()) {
            return true;
        }

        if(hand.get(1).getRank() == hand.get(2).getRank()) {
            return true;
        }

        if(hand.get(2).getRank() == hand.get(3).getRank()) {
            return true;
        }

        if(hand.get(3).getRank() == hand.get(4).getRank()) {
            return true;
        }

        if(hand.get(4).getRank() == hand.get(5).getRank()) {
            return true;
        }

        if(hand.get(5).getRank() == hand.get(6).getRank()) {
            return true;
        }

        return false;

    }
}
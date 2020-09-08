public class Card implements Comparable<Card> {

    private int suit; // use integers 1-4 to encode the suit
    private int rank; // use integers 1-13 to encode the rank

    public Card(int s, int r) {
        //make a card with suit s and value v
        suit = s;
        rank = r;
    }

    public int compareTo(Card c) {
        // use this method to compare cards so they
        // may be easily sorted
        return rank - c.rank;
    }

    public boolean equals(Object obj) {
        Card c = (Card)obj;
        if(this.rank != c.rank) {
            return false;
        }

        return true;
    }

    public String toString() {
        // use this method to easily print a Card object
        String stringRank = "";
        String stringSuit = "";
        if(suit==1) {
            stringSuit = "spades";
        } else if(suit==2) {
            stringSuit = "hearts";
        } else if(suit==3) {
            stringSuit = "clubs";
        } else {
            stringSuit = "diamonds";
        }
        if(rank == 1) {
            stringRank = "Ace";
        } else if(rank == 2) {
            stringRank = "Two";
        } else if(rank == 3) {
            stringRank = "Three";
        } else if(rank == 4) {
            stringRank = "Four";
        } else if(rank == 5) {
            stringRank = "Five";
        } else if(rank == 6) {
            stringRank = "Six";
        } else if(rank == 7) {
            stringRank = "Seven";
        } else if(rank == 8) {
            stringRank = "Eight";
        } else if(rank == 9) {
            stringRank = "Nine";
        } else if(rank == 10) {
            stringRank = "Ten";
        } else if(rank == 11) {
            stringRank = "Jack";
        } else if(rank == 12) {
            stringRank = "Queen";
        } else {
            stringRank = "King";
        }
        return stringRank + " of " + stringSuit;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int r) {
        rank = r;
    }

    public int getSuit() {
        return suit;
    }
}

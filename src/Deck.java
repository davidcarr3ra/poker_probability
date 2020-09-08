import java.util.Random;

public class Deck {

    private Card[] cards = new Card[52];
    private int top = 51; // the index of the top of the deck

    // add more instance variables if needed

    public Deck() {
        // make a 52 card deck here
        int count = 0;
        for(int i=1; i<5; i++) {
            for(int j=1; j<14; j++) {
                cards[count] = new Card(i, j);
                count++;
            }
        }
    }

    public void shuffle() {
        // shuffle the deck here
        Random r = new Random();
        Card placeholder;
        for(int i=0; i<cards.length; i++) {
            placeholder = cards[i];
            int randomNumber = r.nextInt(51);
            cards[i] = cards[randomNumber];
            cards[randomNumber] = placeholder;
        }

    }

    public Card deal() {
        // deal the top card in the deck
        top--;
        return cards[top];
    }
}

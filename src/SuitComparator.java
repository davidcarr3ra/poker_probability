import java.util.Comparator;

public class SuitComparator implements Comparator<Card> {

    public int compare(Card firstcard, Card secondcard) {
        return (firstcard.getSuit() - secondcard.getSuit());
    }

}

public class PokerTest {

    public static void main(String[] args) {
        if(args.length<1) {
            TrueGame g = new TrueGame();
            g.play();
        } else {
            TrueGame g = new TrueGame(args);
        }
    }

}

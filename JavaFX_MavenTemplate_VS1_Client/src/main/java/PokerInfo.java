import java.io.Serializable;

class PokerInfo implements Serializable {
    int ante;
    int pairPlus;
    int cash;
    int play;
    int winningsThisRound;

    boolean hang = true;
    boolean playOver = false;
    boolean playerWon = false;
    boolean newRound = false;

    int buttonPressed;
        //0 - DEAL
        //1 - PLAY
        //2 - FOLD

    String card1, card2, card3,
           dCard1, dCard2, dCard3;

    int winner;
        //0 - TIE
        //1 - DEALER
        //2 - PLAYER
    
    String pHandVal, dHandVal;

    PokerInfo() {
        ante = 0;
        pairPlus = 0;
        cash = 100;
    }

    PokerInfo(int a, int pp, int c) {
        ante = a;
        pairPlus = pp;
        cash = c;
    }
}
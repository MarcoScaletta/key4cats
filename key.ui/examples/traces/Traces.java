class Traces{

    public static int x;


    public static void removeOne(boolean sync){
            x = x - 1;
        return;
    }

    public static void removeOneWithCond(boolean sync){
        if(x > 0) {
            x = x - 1;
        }
        return;
    }

    public static void removeTwo(boolean sync){
        x = x - 2;
        return;
    }

    public static void removeThree(boolean sync){
        Traces.removeOne(true);
        Traces.removeTwo(true);
        return;
    }
    public static void m1(boolean sync){
        x = x + 1;
        return;
//        int y;
//        y = y + 1;
//        return;
    }


    //    CASINO CASE STUDY
    public static int bet,amountToBet,wallet, guess, coinSide;
    public static void placeBet(boolean sync){
        bet = amountToBet;
        wallet = wallet - bet;
        return;
    }

    public static void decideBet(boolean sync){
        if(coinSide == guess){
            wallet += 2*bet;
        }
        bet = 0;
        return;
    }

    public static void callPlaceBetOnce(boolean sync){
        Traces.placeBet(true);
        return;
    }


    public static void callDecideBetAndPlaceBet(boolean sync){
        Traces.decideBet(true);
        Traces.placeBet(true);
        return;
    }

    public static void callRemoveOneAndPlaceBetOnce(boolean sync){
        Traces.removeOne(true);
        Traces.placeBet(true);
        return;
    }

    public static void callPlaceBetTwice(boolean sync){
        Traces.placeBet(true);
        Traces.placeBet(true);
        return;
    }
}
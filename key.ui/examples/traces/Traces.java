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
    public static int bet,amountToBet,wallet;
    public static void placeBet(boolean sync){
        bet = amountToBet;
        wallet = wallet - bet;
        return;
    }
}
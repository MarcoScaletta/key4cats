class Developing{

    //    CASINO CASE STUDY
    public static int bet,amountToBet,wallet, guess, coinSide;
    public static void placeBet(boolean sync){ //USED
        bet = amountToBet;
        wallet = wallet - bet;
        return;
    }

    public static void decideBet(boolean sync){ //USED
        if(coinSide == guess){
            wallet += 2*bet;
        }
        bet = 0;
        return;
    }

    public static void conditional(boolean sync){ //USED
//        if(coinSide == guess){    
//            bet = 1;
//        }
        bet = 0;
        return;
    }

    public static void casinoCaseStudySimple(boolean sync){
        wallet = 1;
        amountToBet = 1;
        Developing.placeBet(true);
        Developing.decideBet(true);
        return;
    }

//    public static void dummyPlaceBet(boolean sync){return;}

//    public static void callPlaceDecidePlaceBet(boolean sync){
//        wallet = 1;
//        amountToBet = 1;
//        TestsCATs.placeBet(true);
//        TestsCATs.decideBet(true);
//        wallet = 1;
//        amountToBet = 1;
//        TestsCATs.placeBet(true);
//        return;
//    }
}
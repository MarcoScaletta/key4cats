class TestsCATs{

    public static int x;

    public static void removeOne(boolean sync){ //USED
            x = x - 1;
        return;
    }

    public static void removeOneWithCond(boolean sync){ //USED
        if(x > 0) {
            x = x - 1;
        }
        return;
    }

    public static void removeTwo(boolean sync){ //USED
        x = x - 2;
        return;
    }

    public static void removeThree(boolean sync){ //USED
        TestsCATs.removeOne(true);
        TestsCATs.removeTwo(true);
        return;
    }


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

    public static void callPlaceBetOnce(boolean sync){//USED
        TestsCATs.placeBet(true);
        return;
    }

    public static void callDecideBetAndPlaceBet(boolean sync){ //USED
        TestsCATs.decideBet(true);
        TestsCATs.placeBet(true);
        return;
    }

    public static void callRemoveOneAndPlaceBetOnce(boolean sync){//USED
        TestsCATs.removeOne(true);
        TestsCATs.placeBet(true);
        return;
    }

    public static void callPlaceBetTwice(boolean sync){ //USED
        TestsCATs.placeBet(true);
        TestsCATs.placeBet(true);
        return;
    }



    public static void callPlaceBetOnceSetVarsToOne(boolean sync){ //USED
        wallet = 1;
        amountToBet = 1;
        TestsCATs.placeBet(true);
        return;
    }



    public static void dummyProc1(boolean sync){return;}//USED
    public static void dummyProc2(boolean sync){return;}//USED
    public static void dummyProc3(boolean sync){return;}//USED

    public static void callDummyProc1(boolean sync){//USED
        TestsCATs.dummyProc1(true);
        return;
    }

    public static void callDummyProc2And1(boolean sync){//USED
        TestsCATs.dummyProc2(true);
        TestsCATs.dummyProc1(true);
        return;
    }

    public static void callDummyProc1And2And3(boolean sync){ //USED
        TestsCATs.dummyProc1(true);
        TestsCATs.dummyProc2(true);
        TestsCATs.dummyProc3(true);
        return;
    }


    public static void casinoCaseStudySingleBetAndDecision(boolean sync){
        wallet = 1;
        amountToBet = 1;
        TestsCATs.placeBet(true);
        TestsCATs.decideBet(true);
        return;
    }

    public static void removeOneOnce(boolean sync){ //USED
        TestsCATs.removeOne(true);
        return;
    }


    public static void removeOneTwice(boolean sync){ //USED
        TestsCATs.removeOne(true);
        TestsCATs.removeOne(true);
        return;
    }

    public static void twoRounds(boolean sync){
        wallet = 1;
        amountToBet = 1;
        TestsCATs.placeBet(true);
        TestsCATs.decideBet(true);
        wallet = 1;
        amountToBet = 1;
        TestsCATs.placeBet(true);
        TestsCATs.decideBet(true);
        return;
    }

}
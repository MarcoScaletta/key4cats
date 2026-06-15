class Casino {

    public static int bet,amount,wallet, guess, coin, someVar;
    public static void placeBet(boolean sync){
        bet = amount;
        wallet = wallet - bet;
        return;
    }
    public static void decideBet(boolean sync){
        if(coin == guess){
            wallet = wallet + bet;
            wallet = wallet + bet;
        }
        bet = 0;
        return;
    }
    public static void oneRound(boolean sync){
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
        Casino.decideBet(true);
        return;
    }

    public static void twoRounds(boolean sync){
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
        Casino.decideBet(true);
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
        Casino.decideBet(true);
        return;
    }

    public static void threeRounds(boolean sync){
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
        Casino.decideBet(true);
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
        Casino.decideBet(true);
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
        Casino.decideBet(true);
        return;
    }
    public static void wrongRound(boolean sync){
        wallet = 1;
        amount = 1;
        Casino.placeBet(true);
//        Casino.decideBet(true);
        return;
    }
}


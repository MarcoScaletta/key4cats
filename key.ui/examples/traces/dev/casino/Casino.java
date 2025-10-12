class Casino {

    public static int bet,amount,wallet, guess, coin;
    public static void placeBet(boolean sync){
        bet = amount;
        wallet = wallet - bet;
        return;
    }
    public static void decideBet(boolean sync){
        if(coin == guess){
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
}
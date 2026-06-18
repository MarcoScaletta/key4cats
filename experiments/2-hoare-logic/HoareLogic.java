class HoareLogic{

    public static int x;

    // directly decrease x by one;
    public static void removeOne(boolean sync){
        x = x - 1;
        return;
    }

    // decrease x by 2 delegating the operation to remove one twice
    public static void removeTwo(boolean sync){
        HoareLogic.removeOne(true);
        HoareLogic.removeOne(true);
        return;
    }

    // decrease x by 3 delegating the operation to removeOne and removeTwo
    public static void removeThree(boolean sync){
        HoareLogic.removeOne(true);
        HoareLogic.removeTwo(true);
        return;
    }

    // decrease x by 10 delegating the operation to
    //  - removeOne (2 times)
    //  - removeTwo (1 time)
    //  - removeThree (2 times)
    public static void removeTen(boolean sync){
        HoareLogic.removeOne(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeThree(true);
        HoareLogic.removeOne(true);
        HoareLogic.removeThree(true);
        return;
    }

    public static void removeTwenty(boolean sync){
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        HoareLogic.removeTwo(true);
        return;
    }

    public static void rmOne(boolean sync){
        HoareLogic.removeOne(true);
        return;
    }
    public static void rmTwo(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }
    public static void rmThree(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }
    public static void rmFour(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }
    public static void rmFive(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }
    public static void rmSix(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }

    public static void rmSeven(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }
    public static void rmEight(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }

    public static void rmNine(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }

    public static void rmTen(boolean sync){
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        HoareLogic.rmOne(true);
        return;
    }
}
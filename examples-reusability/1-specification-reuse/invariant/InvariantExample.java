class InvariantExample {

    public static int x;

    public static void addOne(boolean sync){
        x = x + 1;
        return;
    }

    public static void removeOne(boolean sync){
        x = x - 1;
        return;
    }

}


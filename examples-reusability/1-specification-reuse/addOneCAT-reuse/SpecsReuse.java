class SpecsReuse {

    public static int x;

    public static void addOne(boolean sync){
        x = x + 1;
        return;
    }

    public static void addTwo(boolean sync){
        x = x + 2;
        return;
    }

    public static void doNothing(boolean sync){
        return;
    }

}


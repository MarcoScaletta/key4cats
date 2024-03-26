class Traces{

    public static int x;

    public static void m(boolean sync){
        Traces.m1(true);
        Traces.m1(true);
//        x= x + 1;
        return;
    }


    public static void m2(boolean sync){

        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);
        Traces.m1(true);

        return;
    }

    public static void m1(boolean sync){
        x = x + 1;
        return;
//        int y;
//        y = y + 1;
//        return;
    }
}
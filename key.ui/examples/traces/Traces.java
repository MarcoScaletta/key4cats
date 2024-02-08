class Traces{

    public static int x;

    public static void m(boolean sync){
        x = x + 1;
        return;
//        int y;
//        y = y + 1;
//        return;
    }

    private void m1(){
        x += 1;
    }
}
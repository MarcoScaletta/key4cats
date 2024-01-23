class Traces{

    int x;
    static int y;

    private static void m(boolean sync){
        y += 1;
    }

    private void m1(){
        x += 1;
    }
}
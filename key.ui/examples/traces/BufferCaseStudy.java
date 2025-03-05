class BufferCaseStudy{

    public static int resHasNext;

    public static void hasNext(boolean sync){}
    public static void next(boolean sync){}

    public static void bufferCaseStudy(boolean sync){
        BufferCaseStudy.hasNext(true);
        if(resHasNext == 1) {
            BufferCaseStudy.next(true);
        }
        return;
    }


    // safe = i.hasNext();
    // access safe (line 12)
    // ... but no next();
    // if(safe){
    // access safe (line 13)
    // i.next();}
}
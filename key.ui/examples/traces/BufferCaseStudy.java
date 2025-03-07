class BufferCaseStudy{

    public static int resHasNext;
    public static int newVar;

    public static void hasNext(boolean sync){}
    public static void next(boolean sync){}

    public static void bufferCaseStudy(boolean sync){
        BufferCaseStudy.hasNext(true);
        // obs resHasNext = h1
        if(resHasNext == 1) {
            newVar = 1; // ~{next}~
//            resHasNext = 0;
            // obs resHasNext = h2
            // h1 == h2 && h1 == 1
            BufferCaseStudy.next(true);
        }
        return;
    }

//    // CHECK
//    public static void bufferCaseStudyUpdate(boolean sync){
//        BufferCaseStudy.hasNext(true);
////        newVar = 1;
//        if(resHasNext == 1) {
//            BufferCaseStudy.next(true);
//        }
//        return;
//    }


//    What is the contract>
//    - for remove (similar to next but just removes, no return)
//    - for size (does not modify the state)
//  Todo: CHECK
// PRIORITY
//    public static void bufferCaseStudy(boolean sync){
//        BufferCaseStudy.hasNext(true);
        // obs resHasNext = h1
//        if(resHasNext == 1) {
//            newVar = 1; // ~~
    // U . U;(resHasNext) == 1
            // remove() < ~~ | ~~ | ~~ >
    // U;U1 . U;U1(resHasNext) == 1
            // obs resHasNext = h2
            // h1 == h2 && h1 == 1
//            BufferCaseStudy.next(true);
//        }
//        return;
//    }


    // safe = i.hasNext();
    // access safe (line 12)
    // ... but no next();
    // if(safe){
    // access safe (line 13)
    // i.next();}
}
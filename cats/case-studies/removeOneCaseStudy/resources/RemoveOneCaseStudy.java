class RemoveOneCaseStudy{

    public static int x;

    public static void removeOne(boolean sync){
            x = x - 1;
        return;
    }

    public static void removeOneOnce(boolean sync){
        RemoveOneCaseStudy.removeOne(true);
        return;
    }


    public static void removeOneTwice(boolean sync){
        RemoveOneCaseStudy.removeOne(true);
        RemoveOneCaseStudy.removeOne(true);
        return;
    }

    public static void removeOneThrice(boolean sync){
        RemoveOneCaseStudy.removeOne(true);
        RemoveOneCaseStudy.removeOne(true);
        RemoveOneCaseStudy.removeOne(true);
        return;
    }

    public static void removeOneQuad(boolean sync){
        RemoveOneCaseStudy.removeOne(true);
        RemoveOneCaseStudy.removeOne(true);
        RemoveOneCaseStudy.removeOne(true);
        RemoveOneCaseStudy.removeOne(true);
        return;
    }
}
class FileWork {

    public static int x;

    public static void openFile(boolean sync){
        return;
    }


    public static void workOnFile(boolean sync){
        return;
    }

    public static void closeFile(boolean sync){
        return;
    }

    public static void onlyOpen(boolean sync){
        FileWork.openFile(true);
        return;
    }

    public static void onlyClose(boolean sync){
        FileWork.closeFile(true);
        return;
    }

    public static void openAndClose(boolean sync){
        FileWork.openFile(true);
        FileWork.closeFile(true);
        return;
    }

    public static void openAndWorkAndClose(boolean sync){
        FileWork.openFile(true);
        FileWork.workOnFile(true);
        FileWork.closeFile(true);
        return;
    }

    public static void openAndWorkTwiceAndClose(boolean sync){
        FileWork.openFile(true);
        FileWork.workOnFile(true);
        FileWork.workOnFile(true);
        FileWork.closeFile(true);
        return;
    }

    public static void noOpenAndWorkAndClose(boolean sync){
        FileWork.workOnFile(true);
        FileWork.closeFile(true);
        return;
    }

    public static void openAndWorkAndNoClose(boolean sync){
        FileWork.openFile(true);
        FileWork.workOnFile(true);
        return;
    }

    public static void openAndCloseAndWork(boolean sync){
        FileWork.openFile(true);
        FileWork.closeFile(true);
        FileWork.workOnFile(true);
        return;
    }

}
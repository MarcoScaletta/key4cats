[removeOneCAT] removeOne :
    assumes: ~~ ** x::y . `true`;
    ensures: start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1=y-1`;
    expects: ~~;


[rmOneCAT] {removeOneCAT;} rmOne :
    assumes: ~~ ** x::y . `true`;
    ensures: start(rmOne,\id) ** ~~ ** pop(rmOne,\id) ** x::y1 . `y1=y-1`;
    expects: ~~;
[rmTwoCAT]{rmOneCAT;} rmTwo :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-2`;
    expects: ~~;
[rmThreeCAT]{rmOneCAT;} rmThree :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-3`;
    expects: ~~;
[rmFourCAT]{rmOneCAT;} rmFour :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-4`;
    expects: ~~;
[rmFiveCAT]{rmOneCAT;} rmFive :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-5`;
    expects: ~~;
[rmSixCAT]{rmOneCAT;} rmSix :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-6`;
    expects: ~~;
[rmSevenCAT]{rmOneCAT;} rmSeven :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-7`;
    expects: ~~;
[rmEightCAT]{rmOneCAT;} rmEight :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-8`;
    expects: ~~;
[rmNineCAT]{rmOneCAT;} rmNine :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-9`;
    expects: ~~;
[rmTenCAT]{rmOneCAT;} rmTen :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-10`;
    expects: ~~;
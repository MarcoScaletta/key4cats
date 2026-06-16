[rmOne] rmOne :
    assumes: ~~ ** x::y . `true`;
    ensures: start(rmOne,\id) ** ~~ ** pop(rmOne,\id) ** x::y1 . `y1=y-1`;
    expects: ~~;
[rmTwo]{rmOne;} rmTwo :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-2`;
    expects: ~~;
[rmThree]{rmOne;} rmThree :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-3`;
    expects: ~~;
[rmFour]{rmOne;} rmFour :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-4`;
    expects: ~~;
[rmFive]{rmOne;} rmFive :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-5`;
    expects: ~~;
[rmSix]{rmOne;} rmSix :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-6`;
    expects: ~~;
[rmSeven]{rmOne;} rmSeven :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-7`;
    expects: ~~;
[rmEight]{rmOne;} rmEight :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-8`;
    expects: ~~;
[rmNine]{rmOne;} rmNine :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-9`;
    expects: ~~;
[rmTen]{rmOne;} rmTen :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `y1=y-10`;
    expects: ~~;
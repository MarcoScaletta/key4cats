[removeOneCAT] removeOne :
    assumes: ~~ ** x::y . `true`;
    ensures: start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1=y-1`;
    expects: ~~;

    [removeOneOnceCAT] {removeOneCAT;} removeOneOnce :

        assumes: ~~ ** x::y . `true`;
        ensures: ~~ ** start(removeOne,_) **
                 ~~ ** x::y1 . `y1=y-1` ;
        expects: ~~;

    [removeOneTwiceCAT] {removeOneCAT;} removeOneTwice :
        assumes: ~~ ** x::y . `true`;
        ensures: ~~ ** start(removeOne,_) **
                 ~~ ** start(removeOne,_) **
                 ~~  ** x::y1 . `y1=y-2` ;
        expects: ~~;

    [removeOneThriceCAT] {removeOneCAT;} removeOneThrice :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** x::y1 . `y1=y-3` ;
    expects: ~~;

    [removeOneQuadCAT] {removeOneCAT;} removeOneQuad :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** x::y1 . `y1=y-4` ;
    expects: ~~;

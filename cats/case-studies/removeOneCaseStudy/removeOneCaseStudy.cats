[removeOne] removeOne :
    assumes: ~~ ** x::y . `true`;
    ensures: start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1=y-1`;
    expects: ~~;

    [removeOneOnce] {removeOne;} removeOneOnce :

        assumes: ~~ ** x::y . `true`;
        ensures: ~~ ** start(removeOne,_) **
                 ~~ ** x::y1 . `y1=y-1` ;
        expects: ~~;

    [removeOneTwice] {removeOne;} removeOneTwice :
        assumes: ~~ ** x::y . `true`;
        ensures: ~~ ** start(removeOne,_) **
                 ~~ ** start(removeOne,_) **
                 ~~  ** x::y1 . `y1=y-2` ;
        expects: ~~;

    [removeOneThrice] {removeOne;} removeOneThrice :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** x::y1 . `y1=y-3` ;
    expects: ~~;

    [removeOneQuad] {removeOne;} removeOneQuad :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** start(removeOne,_) **
             ~~ ** x::y1 . `y1=y-4` ;
    expects: ~~;

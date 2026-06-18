[removeOneCAT] removeOne :
    assumes: ~~ ** x::y . `true`;
    ensures: start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1=y-1`;
    expects: ~~;

[removeTwoCAT]{removeOneCAT;} removeTwo :
    assumes: ~~ ** x::y . `true`;
    ensures: start(removeTwo,\id) ** ~~ ** pop(removeTwo,\id) ** x::y1 . `y1=y-2`;
    expects: ~~;

[removeThreeCAT]{removeOneCAT;removeTwoCAT;} removeThree :
    assumes: ~~ ** x::y . `y>3`;
    ensures: start(removeThree,\id) ** ~~ ** pop(removeThree,\id) ** x::y1 . `y1 > 0`;
    expects: ~~;

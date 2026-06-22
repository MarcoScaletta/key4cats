[addOneCAT] addOne:
    assumes: ~~ ** x::oldX . `true`;
    ensures: ~~ ** x::newX . `newX>oldX`;
    expects: ~~;



//replace "addOne" with "addTwo" or "doNothing" to verify that this contract holds for the latter
[greaterThanBeforeCAT] addOne:
    assumes: ~~ ** x::oldX . `true`;
    ensures: ~~ ** x::newX . `newX>oldX`;
    expects: ~~;


//The following commented CATs serve as a guide, but can be uncommented and checked running "source specs-reuse.sh"

//[greaterThanBeforeDoNothingCAT] doNothing:
//    assumes: ~~ ** x::oldX . `true`;
//    ensures: ~~ ** x::newX . `newX>oldX`;
//    expects: ~~;
//
//[greaterThanBeforeAddOneCAT] addOne:
//    assumes: ~~ ** x::oldX . `true`;
//    ensures: ~~ ** x::newX . `newX>oldX`;
//    expects: ~~;
//
//[greaterThanBeforeAddTwoCAT] addTwo:
//    assumes: ~~ ** x::oldX . `true`;
//    ensures: ~~ ** x::newX . `newX>oldX`;
//    expects: ~~;

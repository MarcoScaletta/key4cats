removeTwo;
Traces.java;
[removeTwo] removeTwo :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1=y-2` |
        ~~
    >>
[removeTwoFail] removeTwo :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1>y-2` |
        ~~
    >>
[removeThree] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y . `true`|
        start(removeThree,0) ** ~~ ** x::y1 . `y1=y-3` |
        ~~
    >>
[removeThreeNoCallsRemoveOneFail] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y .`true`| ~{removeOne}~ ** x::y1 .`true`|
        ~~
    >>
[removeThreeFail] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y . `true`|
        start(removeThree,0) ** ~~ ** x::y1 . `y1>y-3` |
        ~~
    >>
[removeOne] removeOne :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1=y-1` |
        ~~
    >>
[removeOneLT] removeOne :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1<=y-1` |
        ~~
    >>
[removeOneFail] removeOne :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1!=y-1` |
        ~~
    >>

[removeOneWithCond] removeOneWithCond :
    <<
        ~~ ** x::y . `y>0`|
        ~~ ** x::y1 . `y1=y-1 & y1 >=0` |
        ~~
    >>

[removeOneWithCondFail] removeOneWithCond :
    <<
        ~~ ** x::y . `y>0`|
        ~~ ** x::y1 . `y1 >=1` |
        ~~
    >>

[placeBetInnerSimple] placeBet : //working
    <<
        ~~ ** x::y .`true`|
        start(placeBet,0) ** ~{removeOne}~ **  pop(placeBet,0)  ** x::y1 .`true`|
        ~~
    >>

[placeBetAbsTrOther] placeBet : //working
    <<
        ~~ ** x::y2 . x::y .`true`| ~{removeOne}~ ** x::y1 .`true`|
        ~~
    >>
[placeBetAbsTrSelf] placeBet : //working
    <<
        ~~ ** x::y .`true`| ~{placeBet}~ ** x::y1 .`true`|
        ~~
    >>
[placeBet] placeBet : //working
    <<
        ~~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,0) ** ~{placeBet}~ **  pop(placeBet,0)  ** wallet::w .`w = oldW - b`|
        ~~
    >>

[placeBetWithPre] placeBet :
    <<
        ~{placeBet}~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,0) ** ~{placeBet}~ **  pop(placeBet,0)  ** wallet::w .`w = oldW - b`|
        ~~
    >>
[placeBetOnlyOnce] placeBet :
    <<
        ~{placeBet}~ ** wallet::w1 . `true`|
        start(placeBet,0) ** ~{placeBet}~ **  pop(placeBet,0)  ** wallet::w .`true`|
        ~~
    >>


[trivialMultipleObsCallee] {placeBetAbsTrOther;} callPlaceBetOnce :
    << ~~ ** x::y . `true` | ~~ ** x::y1 . `true` | ~~ >>


[casinoCaseStudyNoPre] {placeBet;} casinoCaseStudyMain :
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[callPlaceBetOnceInsufficientContractFail] {placeBetOnlyOnce;removeOne;} callRemoveOneAndPlaceBetOnce :
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[callPlaceBetOnceSufficientContract] {placeBetOnlyOnce;removeOne;} callRemoveOneAndPlaceBetOnce :
    <<
        ~{placeBet}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[callBetTwiceFail] {placeBetOnlyOnce;} callPlaceBetTwice :
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[placeBetDecidedBet] placeBet:
    <<
        ~~ ** pop(decideBet, _ ) ** wallet::w1 . `true`|
        ~~ ** wallet::w .`true`|
        ~~
    >>

[placeBetDecidedBetAndNoCalls] placeBet:
    <<
        ~~ ** pop(decideBet, _ ) ** ~{placeBet,decideBet}~ ** wallet::w1 . `true`|
        ~~ ** wallet::w .`true`|
        ~~
    >>

[decideBet] decideBet:
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[callPlaceBetDecidedBet] {decideBet;placeBetDecidedBet;} callDecideBetAndPlaceBet:
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[callPlaceBetDecidedBetAndNoCalls] {decideBet;placeBetDecidedBetAndNoCalls;} callDecideBetAndPlaceBet:
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[dummyProc1CAT]  dummyProc1:
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[dummyProc2CAT]  dummyProc2:
    <<
        ~~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[forbidDummyProc1And2PreTraceCAT] dummyProc3:

    <<
        ~{dummyProc1,dummyProc2,decideBet}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[callDummyProc1And2And3CATFail] {dummyProc1CAT;dummyProc2CAT;forbidDummyProc1And2PreTraceCAT;} callDummyProc1And2And3:
    <<
        ~{dummyProc1,dummyProc2}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[placeBetFullPreFullInner] placeBet:
    <<
        (~{placeBet}~ | ~~ ** pop(decideBet, _ )) ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,0) ** ~{placeBet}~ **  pop(placeBet,0)  ** wallet::w .`w = oldW - b`|
        ~~
    >>

[dummyProc1NoDummyProc1Before] dummyProc1:
    <<
        ~{dummyProc1}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[dummyProc1NoDummyProc1And3Before] dummyProc1:
    <<
        ~{dummyProc1,dummyProc3}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[simpleSchemTraceInclusion] {dummyProc1NoDummyProc1Before;} callDummyProc1:
    <<
        ~{dummyProc1,dummyProc2}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>


[simpleSchemTraceInclusionFail] {dummyProc1NoDummyProc1And3Before;} callDummyProc1:
    <<
        ~{dummyProc1,dummyProc2}~ ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[noDummyProc1BeforeOrMustCallDummyProc2Before] dummyProc1:
    <<
        (~{dummyProc1}~ | (~~ ** pop(dummyProc2,_))) ** x::y . `true` |
        ~~ ** x::y1 . `true` | ~~ >>

[callDummyProc1AssumeNoDummyProc1Before] {noDummyProc1BeforeOrMustCallDummyProc2Before;} callDummyProc1:
        <<
            ~{dummyProc1}~ ** x::y . `true` |
            ~~ ** x::y1 . `true` | ~~ >>

[callDummyProc1WithAssumptionsButAssumeNothingFail] {dummyProc2CAT;noDummyProc1BeforeOrMustCallDummyProc2Before;} callDummyProc1:
        <<
            ~~ ** x::y . `true` |
            ~~ ** x::y1 . `true` | ~~ >>

[callDummyProc2And1SafeLocalContextOr] {dummyProc2CAT;noDummyProc1BeforeOrMustCallDummyProc2Before;} callDummyProc2And1:
        <<
            ~~ ** x::y . `true` |
            ~~ ** x::y1 . `true` | ~~ >>

[idKeyWordTest] dummyProc1 :
    <<
        ~~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`
        | start(dummyProc1,\id) ** ~{dummyProc1}~ **  pop(dummyProc1,\id)  ** wallet::w .`true`
        | ~~ >>


//
////not working
//
////not working
//[callPlaceDecidePlaceBet] {placeBetFullPreFullInner;decideBet;} callPlaceDecidePlaceBet:
//    <<
//        ~{placeBet,decideBet}~ ** x::y . `true` |
//        ~~ ** x::y1 . `true` | ~~ >>
//
//[dummyPlaceBet] dummyPlaceBet:
//    <<
//        ~~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
//        start(dummyPlaceBet,0) ** ~~ ** wallet::w .`w = oldW - b`|
//        ~~
//    >>
//
//[callPlaceDecidePlaceBetAssume] {dummyPlaceBet;placeBet;decideBet;} callPlaceDecidePlaceBetAssume:
//    <<
//        ~{placeBet,decideBet}~ ** x::y . `true` |
//        ~~ ** x::y1 . `true` | ~~ >>

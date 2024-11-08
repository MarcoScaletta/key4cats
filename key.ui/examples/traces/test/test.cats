[callBetTwiceFail] {placeBetOnlyOnce;} callPlaceBetTwice :
    <<
        ~~ ** x::y . `true` |
        start(callPlaceBetTwice,\id) ** ~~ ** pop(callPlaceBetTwice,\id)** x::y1 . `true` | ~~ >>
[callDummyProc1And2And3CATFail] {dummyProc1CAT;dummyProc2CAT;forbidDummyProc1And2PreTraceCAT;} callDummyProc1And2And3:
    <<
        ~{dummyProc1,dummyProc2}~ ** x::y . `true` |
        start(callDummyProc1And2And3,\id) ** ~~ ** pop(callDummyProc1And2And3,\id) ** x::y1 . `true` | ~~ >>
[callDummyProc1AssumeNoDummyProc1Before] {noDummyProc1BeforeOrMustCallDummyProc2Before;} callDummyProc1:
        <<
            ~{dummyProc1}~ ** x::y . `true` |
            start(callDummyProc1,\id) ** ~~ ** pop(callDummyProc1,\id) ** x::y1 . `true` | ~~ >>
[callDummyProc1WithAssumptionsButAssumeNothingFail] {dummyProc2CAT;noDummyProc1BeforeOrMustCallDummyProc2Before;} callDummyProc1:
        <<
            ~~ ** x::y . `true` |
            start(callDummyProc1,\id) ** ~~ ** pop(callDummyProc1,\id) ** x::y1 . `true` | ~~ >>
[callDummyProc2And1SafeLocalContextOr] {dummyProc2CAT;noDummyProc1BeforeOrMustCallDummyProc2Before;} callDummyProc2And1:
        <<
            ~~ ** x::y . `true` |
            start(callDummyProc2And1,\id) ** ~~ ** pop(callDummyProc2And1,\id) ** x::y1 . `true` | ~~ >>
[callPlaceBetDecidedBet] {decideBet;placeBetDecidedBet;} callDecideBetAndPlaceBet:
    <<
        ~~ ** x::y . `true` |
        start(callDecideBetAndPlaceBet, \id) ** ~~ ** pop(callDecideBetAndPlaceBet, \id) ** x::y1 . `true` | ~~ >>
[callPlaceBetOnceInsufficientContractFail] {placeBetOnlyOnce;removeOne;} callRemoveOneAndPlaceBetOnce :
    <<
        ~~ ** x::y . `true` |
        start(callRemoveOneAndPlaceBetOnce, \id) ** ~~ ** pop(callRemoveOneAndPlaceBetOnce, \id) ** x::y1 . `true` | ~~ >>
[callPlaceBetOnceSufficientContract] {placeBetOnlyOnce;removeOne;} callRemoveOneAndPlaceBetOnce :
    <<
        ~{placeBet}~ ** x::y . `true` |
        start(callRemoveOneAndPlaceBetOnce,\id) ** ~~ ** pop(callRemoveOneAndPlaceBetOnce,\id) ** x::y1 . `true` | ~~ >>

[placeBet] placeBet : //working
    <<
        ~~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,\id) ** ~{placeBet}~ **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`|
        ~~
    >>
[placeBetAbsTrSelf] placeBet : //working
    <<
        ~~ ** x::y .`true`|
        start(placeBet,\id) ** ~{placeBet}~ ** pop(placeBet,\id) ** x::y1 .`true`|
        ~~
    >>
[placeBetInnerSimple] placeBet : //working
    <<
        ~~ ** x::y .`true`|
        start(placeBet,\id) ** ~{removeOne}~ **  pop(placeBet,\id)  ** x::y1 .`true`|
        ~~
    >>
[removeOne] removeOne :
    <<
        ~~ ** x::y . `true`|
        start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1=y-1` |
        ~~
    >>

[removeOneFail] removeOne :
    <<
        ~~ ** x::y . `true`|
        start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1!=y-1` |
        ~~
    >>
[removeOneLT] removeOne :
    <<
        ~~ ** x::y . `true`|
        start(removeOne,\id) ** ~~ ** pop(removeOne,\id) ** x::y1 . `y1<=y-1` |
        ~~
    >>
[removeOneWithCond] removeOneWithCond :
    <<
        ~~ ** x::y . `y>0`|
        start(removeOneWithCond,\id) ** ~~ ** pop(removeOneWithCond,\id) ** x::y1 . `y1=y-1 & y1 >=0` |
        ~~
    >>
[removeOneWithCondFail] removeOneWithCond :
    <<
        ~~ ** x::y . `y>0`|
        start(removeOneWithCond,\id) ** ~~ ** pop(removeOneWithCond,\id) ** x::y1 . `y1 >=1` |
        ~~
    >>
[removeThree] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y . `true`|
        start(removeThree,\id) ** ~~ ** pop(removeThree,\id)  ** x::y1 . `y1=y-3` |
        ~~
    >>
[removeThreeFail] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y . `true`|
         start(removeThree,\id) ** ~~ ** pop(removeThree,\id) ** x::y1 . `y1>y-3` |
        ~~
    >>
[removeThreeNoCallsRemoveOneFail] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y .`true`|
         start(removeThree,\id) ** ~{removeOne}~ ** pop(removeThree,\id) ** x::y1 .`true`|
        ~~
    >>
[removeTwo] removeTwo :
    <<
        ~~ ** x::y . `true`|
        start(removeTwo,\id) ** ~~ ** pop(removeTwo,\id) ** x::y1 . `y1=y-2` |
        ~~
    >>
[removeTwoFail] removeTwo :
    <<
        ~~ ** x::y . `true`|
        start(removeTwo,\id) ** ~~ ** pop(removeTwo,\id) ** x::y1 . `y1>y-2` |
        ~~
    >>
[simpleSchemTraceInclusion] {dummyProc1NoDummyProc1Before;} callDummyProc1:
    <<
        ~{dummyProc1,dummyProc2}~ ** x::y . `true` |
        start(callDummyProc1,\id) ** ~~ ** pop(callDummyProc1,\id) ** x::y1 . `true` | ~~ >>
[simpleSchemTraceInclusionFail] {dummyProc1NoDummyProc1And3Before;} callDummyProc1:
    <<
        ~{dummyProc1,dummyProc2}~ ** x::y . `true` |
        start(callDummyProc1,\id) ** ~~ ** pop(callDummyProc1,\id)  ** x::y1 . `true` | ~~ >>


[callPlaceBetOnlyOnceAssumePreTrace] {placeBet;} callPlaceBetOnce :
    <<
        ~{placeBet}~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(callPlaceBetOnce,\id) ** ~~ ** pop(callPlaceBetOnce,\id)  ** wallet::w .`true`|
        ~~
    >>

[callPlaceBetOnlyOncePreCondNotMetFail] {placeBetOnlyOnce;} callPlaceBetOnce :
    <<
        ~~ ** amountToBet::b . wallet::oldW .`true`|
        start(callPlaceBetOnce,\id) ** ~~ ** pop(callPlaceBetOnce,\id) ** wallet::w .`true`|
        ~~
    >>

[callPlaceBetOnceSetVarsToOne] {placeBet;} callPlaceBetOnceSetVarsToOne:
    <<
        ~~ ** x::y . `true` |
        start(callPlaceBetOnceSetVarsToOne,\id) ** ~~ ** pop(callPlaceBetOnceSetVarsToOne,\id) ** x::y1 . `true` | ~~ >>

[trivialMultipleObsCallee] {placeBetAbsTrOther;} callPlaceBetOnce :
    << ~~ ** x::y . `true` | start(callPlaceBetOnce,\id) ** ~~ **  pop(callPlaceBetOnce,\id) ** x::y1 . `true` | ~~ >>




//assumedButNotTested

[dummyProc1NoDummyProc1And3Before] dummyProc1:
    <<
        ~{dummyProc1,dummyProc3}~ ** x::y . `true` |
        start(dummyProc1,\id) ** ~~ **  pop(dummyProc1,\id) ** x::y1 . `true` | ~~ >>
[decideBet] decideBet:
    <<
        ~~ ** x::y . `true` |
        start(decideBet,\id) ** ~~ **  pop(decideBet,\id) ** x::y1 . `true` | ~~ >>
[placeBetDecidedBet] placeBet:
    <<
        ~~ ** pop(decideBet, _ ) ** wallet::w1 . `true`|
        start(placeBet,\id) ** ~~ **  pop(placeBet,\id) ** wallet::w .`true`|
        ~~
    >>
[placeBetOnlyOnce] placeBet :
    <<
        ~{placeBet}~ ** wallet::w1 . `true`|
        start(placeBet,\id) ** ~{placeBet}~ **  pop(placeBet,\id)  ** wallet::w .`true`|
        ~~
    >>



[dummyProc1CAT]  dummyProc1:
    <<
        ~~ ** x::y . `true` |
        start(dummyProc1,\id) ** ~~ **  pop(dummyProc1,\id) ** x::y1 . `true` | ~~ >>
[dummyProc2CAT]  dummyProc2:
    <<
        ~~ ** x::y . `true` |
        start(dummyProc2,\id) ** ~~ **  pop(dummyProc2,\id) ** x::y1 . `true` | ~~ >>
[forbidDummyProc1And2PreTraceCAT] dummyProc3:
    <<
        ~{dummyProc1,dummyProc2,decideBet}~ ** x::y . `true` |
        start(dummyProc3,\id) ** ~~ **  pop(dummyProc3,\id)  ** x::y1 . `true` | ~~ >>
[noDummyProc1BeforeOrMustCallDummyProc2Before] dummyProc1:
    <<
        (~{dummyProc1}~ | (~~ ** pop(dummyProc2,_))) ** x::y . `true` |
        start(dummyProc1,\id) ** ~~ **  pop(dummyProc1,\id)  ** x::y1 . `true` | ~~ >>
[dummyProc1NoDummyProc1Before] dummyProc1:
    <<
        ~{dummyProc1}~ ** x::y . `true` |
        start(dummyProc1,\id) ** ~~ **  pop(dummyProc1,\id)  ** x::y1 . `true` | ~~ >>


[placeBetAbsTrOther] placeBet : //working
    <<
        ~~ ** x::y2 . x::y .`true`|
        start(placeBet,\id) ** ~{removeOne}~ ** pop(placeBet,\id) ** x::y1 .`true`|
        ~~
    >>


[placeBetNeverAfter] placeBet : //working
    <<
        (~{placeBet}~ | (~~ ** pop(decideBet, _ ))) ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,\id) ** ~{placeBet}~ **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`|
        ~{placeBet}~
    >>
[decideBetNoCallsToPlaceBet]  decideBet:
    <<
        ~~ ** amountToBet::b . `true` |
        start(decideBet,\id) ** ~{placeBet}~ ** pop(decideBet,\id) ** amountToBet::b1 . `true` |
        ~~
    >>
[decideBetNoCallsToDecideBet]  decideBet:
    <<
        ~~ ** amountToBet::b . `true` |
        start(decideBet,\id) ** ~{decideBet}~ ** pop(decideBet,\id) ** amountToBet::b1 . `true` |
        ~~
    >>

[casinoCaseStudySimple] {decideBetNoCallsToPlaceBet;placeBetNeverAfter;} casinoCaseStudySingleBetAndDecision:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(casinoCaseStudySingleBetAndDecision,\id) ** ~~  ** pop(casinoCaseStudySingleBetAndDecision,\id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>

[casinoCaseStudySimpleFail] {decideBetNoCallsToDecideBet;placeBetNeverAfter;} casinoCaseStudySingleBetAndDecision:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(casinoCaseStudySingleBetAndDecision,\id) ** ~~ ** pop(casinoCaseStudySingleBetAndDecision, \id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>

[placeBetComplete] placeBet :
    <<
        (~{placeBet}~ | (~~ ** pop(decideBet, _ ))) ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,\id) ** ~{placeBet}~ **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`|
        ~{placeBet,decideBet}~ ** start(decideBet, _ ) ** ~~
    >>

[decideBetNoCallsToPlaceBetNorDecideBet]  decideBet:
    <<
        ~~ ** amountToBet::b . `true` |
        start(decideBet,\id) ** ~{placeBet,decideBet}~ ** pop(decideBet,\id) ** amountToBet::b1 . `true` |
        ~~
    >>

[casinoCaseStudySimpleCompletePlaceBet] {decideBetNoCallsToPlaceBetNorDecideBet;placeBetComplete;} casinoCaseStudySingleBetAndDecision:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(casinoCaseStudySingleBetAndDecision,\id) ** ~~ ** pop(casinoCaseStudySingleBetAndDecision,\id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>

[casinoCaseStudySimpleCompletePlaceBetFail] {decideBetNoCallsToPlaceBetNorDecideBet;placeBetComplete;} callPlaceBetOnceSetVarsToOne:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(callPlaceBetOnceSetVarsToOne,\id) ** ~~ ** pop(callPlaceBetOnceSetVarsToOne,\id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>

[noObservationsTrivialPrecondition]  placeBet:
    <<
        ~~ ** `true` |
        start(placeBet,\id) ** ~{placeBet,decideBet}~ ** pop(placeBet,\id) ** `true` |
        ~~
    >>
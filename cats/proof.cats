trivialMultipleObsCallee;
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
        ~~ ** x::y1 . `y1=y-1 && y1 >=0` |
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
        (~~ ** x::y2 . x::y .`true`| ~{removeOne}~ ** x::y1 .`true`|
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

[trivialMultipleObsCallee] {placeBetAbsTrOther;} casinoCaseStudyMain :
    << ~~ ** x::y . `true` | ~~ ** x::y1 . `true` | ~~ >>


[casinoCaseStudy] {placeBet;} casinoCaseStudyMain :
    << ~~ ** x::y . `true` | ~~ ** x::y1 . `true` | ~~ >>
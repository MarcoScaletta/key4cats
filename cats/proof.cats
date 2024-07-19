removeOneFail;
[removeTwo] removeTwo :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1=y-2` |
        ~~
    >>
[removeThree] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y . `true`|
        start(removeThree,0) ** ~~ ** x::y1 . `y1=y-3` |
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
[placeBetInnerSimple] placeBet : //working
    <<
        ~~ ** x::y .`true`|
        start(placeBet,0) ** ~{removeOne}~ **  pop(placeBet,0)  ** x::y1 .`true`|
        ~~
    >>
[placeBet] placeBet : //working
    <<
        ~~ ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,0) ** ~{placeBet}~ **  pop(placeBet,0)  ** wallet::w .`w = oldW - b`|
        ~~
    >>
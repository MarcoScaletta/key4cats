removeThree;
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
[placeBetInnerSimple] placeBet : //working
    <<
        ~~ ** x::y .`true`|
        start(placeBet,0) ** ~{removeOne}~ **  pop(placeBet,0)  ** x::y1 .`true`|
        ~~
    >>
[placeBetInner] placeBet : //working
    <<
        ~~ ** amountToBet::b . wallet::w .`b > 0 b <= w`|
        start(placeBet,0) ** ~{removeOne}~ **  pop(placeBet,0)  ** x::y1 .`true`|
        ~~
    >>
    )
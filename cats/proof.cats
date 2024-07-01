placeBetInner;
[removeTwo] removeTwo :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1=y-2` |
        ~~
    >>
[removeThree] {removeOne;removeTwo;} removeThree :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1=y-3` |
        ~~
    >>
[removeOne] removeOne :
    <<
        ~~ ** x::y . `true`|
        ~~ ** x::y1 . `y1=y-1` |
        ~~
    >>
[placeBetInner] placeBet : << ~~ ** x::y .`true`| ~{removeOne}~ **  x::y1 .`true`|~~ >>
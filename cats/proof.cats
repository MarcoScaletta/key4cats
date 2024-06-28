//[removeOne] {};removeOne :
//    <<
//        ~~ ** x::y . `true`|
//        ~~ ** x::y1 . `y1=y-1` |
//        ~~
//    >>
//[removeOneLT] {};removeOne :
//    <<
//        ~~ ** x::y . `true`|
//        ~~ ** x::y1 . `y1<y` |
//        ~~
//    >>
[placeBetInner] {}; placeBet : << ~~ ** x::y .`true`| ~{placeBet}~ **  x::y1 .`true`|~~ >>
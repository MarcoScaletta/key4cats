\all;
Developing.java;

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

[casinoCaseStudySimple] {decideBet;placeBet;} casinoCaseStudySimple:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        ~~ ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>
\single:casinoCaseStudySimpleCompletePlaceBetCompleteDecideBetNoStateFml;
Developing.java;

[placeBetComplete] placeBet :
    <<
        (~{placeBet}~ | (~~ ** pop(decideBet, _ ))) ** amountToBet::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,\id) ** ~{placeBet}~ **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`|
        ~{placeBet,decideBet}~ ** start(decideBet, _ ) ** ~~
    >>
[decideBetCompleteNoStateFml]  decideBet:
    <<
        ~~ ** pop(placeBet, _ ) ** ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(decideBet,\id) ** ~{placeBet,decideBet}~ ** pop(decideBet,\id) ** amountToBet::b1 . `true` |
        ~~
    >>

[ifThenElseTrivial]  conditional:
    <<
        ~~ ** `true` |
        start(conditional,\id) ** ~~ ** pop(conditional,\id) ** `true` |
        ~~
    >>

[casinoCaseStudySimpleCompletePlaceBetCompleteDecideBetNoStateFml] {decideBetCompleteNoStateFml;placeBetComplete;} casinoCaseStudySimple:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(casinoCaseStudySimple,\id) ** ~~ ** pop(casinoCaseStudySimple,\id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>


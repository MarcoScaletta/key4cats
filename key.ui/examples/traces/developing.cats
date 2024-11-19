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

[decideBetComplete]  decideBet:
    <<
        ~~ ** pop(placeBet, _ ) ** ~{placeBet,decideBet}~ ** bet::b . wallet::oldW .  coinSide::coin . guess::g . `true` |
        start(decideBet,\id) ** ~{placeBet,decideBet}~ ** pop(decideBet,\id) ** bet::newB . wallet::w . `(newB=0) & (((coin = g) -> (w = oldW + (2*b))) & ((coin != g) -> (w = oldW)))` |
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


[casinoCaseStudyAA] {decideBetCompleteNoStateFml;placeBetComplete;} casinoCaseStudySimple:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(casinoCaseStudySimple,\id) ** ~~ ** pop(casinoCaseStudySimple,\id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>
[casinoCaseStudyAlmostComplete] {decideBetComplete;placeBetComplete;} casinoCaseStudySimple:
    <<
        ~{placeBet,decideBet}~ ** amountToBet::b . `true` |
        start(casinoCaseStudySimple,\id) ** ~~ ** pop(casinoCaseStudySimple,\id) ** amountToBet::b1 . `true` |
        ~{placeBet,decideBet}~
    >>


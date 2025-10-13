

[placeBet] placeBet : //working
    <<
        (~{placeBet}~ | (~~ ** pop(decideBet, _ ))) **~{placeBet}~** amount::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,\id) ** ~{placeBet,decideBet}~ **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`|
        ~{placeBet}~ ** start(decideBet, _ ) ** ~~
    >>
[decideBet]  decideBet:
    <<
        (~~ ** pop(placeBet, _ )) ** ~{decideBet}~ ** wallet::oldW . bet::oldBet . `true` |
        start(decideBet,\id) ** ~{placeBet,decideBet}~ ** pop(decideBet,\id) ** wallet::w . guess::g . coin::c . bet::b . `b=0 & ((c = g) -> (w = oldW + (2*oldBet) ))` |
        ~~
    >>

[oneRound] {decideBet;placeBet;} oneRound:
    <<
        ~{placeBet,decideBet}~ ** amount::b . `true` |
        ~~ ** amount::b1 . `true` |
        ~{placeBet,decideBet}~
    >>
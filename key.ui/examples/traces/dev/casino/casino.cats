

[placeBet] placeBet : //working
    <<
        (~{placeBet}~ | (~~ ** pop(decideBet, _ ))) **~{placeBet}~** amount::b . wallet::oldW .`b > 0 & b<=oldW`|
        start(placeBet,\id) ** ~{placeBet}~ **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`|
        ~{placeBet}~
    >>
[decideBet]  decideBet:
    <<
        ~~ ** amount::b . `true` |
        start(decideBet,\id) ** ~{placeBet}~ ** pop(decideBet,\id) ** amount::b1 . `true` |
        ~~
    >>

[oneRound] {decideBet;placeBet;} oneRound:
    <<
        ~{placeBet,decideBet}~ ** amount::b . `true` |
        ~~ ** amount::b1 . `true` |
        ~{placeBet,decideBet}~
    >>
[placeBetCAT] placeBet :
    assumes: (~{placeBet}~ | (~~ ** pop(decideBet, _ )))
                **~{placeBet}~** amount::b . wallet::oldW .`b > 0 & b<=oldW`;
    ensures: start(placeBet,\id) ** ~{placeBet,decideBet}~
                **  pop(placeBet,\id)  ** wallet::w .`w = oldW - b`;
    expects: ~{placeBet}~ ** start(decideBet, _ ) ** ~~;

[decideBetCAT]  decideBet:
     assumes: (~~ ** pop(placeBet, _ )) ** ~{decideBet}~ ** wallet::oldW . bet::oldBet . `true`;
     ensures: start(decideBet,\id) ** ~{placeBet,decideBet}~ ** pop(decideBet,\id)
        ** wallet::w . guess::g . coin::c . bet::b . `b=0 & ((c = g) -> (w = oldW + (2*oldBet) ))`;
     expects:   ~~;

[oneRoundCAT] {decideBetCAT;placeBetCAT;} oneRound:
    assumes: ~{placeBet,decideBet}~ ** amount::b . `true`;
    ensures: ~~ ** amount::b1 . `true`;
    expects: ~{placeBet,decideBet}~;

// WORKING
[twoRoundsCAT] {decideBetCAT;placeBetCAT;} twoRounds:
    assumes: ~{placeBet,decideBet}~ ** amount::b . `true`;
    ensures: ~~ ** amount::b1 . `true`;
    expects: ~{placeBet,decideBet}~;

[wrongRoundCAT] {decideBetCAT;placeBetCAT;} wrongRound:
    assumes: ~{placeBet,decideBet}~ ** amount::b . `true`;
    ensures: ~~ ** amount::b1 . `true`;
    expects: ~{placeBet,decideBet}~;




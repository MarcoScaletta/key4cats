[sanityCheckExampleCAT] sanityCheckExample :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true`;
    expects: ~~;

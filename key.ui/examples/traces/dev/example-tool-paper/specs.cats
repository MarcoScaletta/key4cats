
[catOfBar]  bar:
    requires:  ~~ ** x::oldX.`true`;
    ensures: ~{bar}~**x::lastX.`lastX=oldX-1`;
    expects: ~~;

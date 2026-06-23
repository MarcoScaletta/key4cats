
[openFileCAT]  openFile :
    assumes: ~~ ** x::y . `true`;
    ensures: ~{openFile,workOnFile,closeFile}~ ** x::y1 . `true` ;
    expects: ~~;

[closeFileCAT]  closeFile :
    assumes: ~~ ** x::y . `true`;
    ensures: ~{openFile,workOnFile,closeFile}~ ** x::y1 . `true` ;
    expects: ~~;

[workOnFileCAT]  workOnFile :
    assumes: ~~ ** pop(openFile, _) ** ~{closeFile}~ ** x::y . `true`;
    ensures: ~{closeFile}~ ** x::y1 . `true` ;
    expects: ~{closeFile}~ ** start(closeFile,_) ** ~~;

[onlyOpenCAT] {openFileCAT;}  onlyOpen :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[onlyCloseCAT] {closeFileCAT;}  onlyClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[openAndWorkAndCloseCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndWorkAndClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[openAndWorkTwiceAndCloseCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndWorkTwiceAndClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[noOpenAndWorkAndCloseCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  noOpenAndWorkAndClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[openAndWorkAndNoCloseCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndWorkAndNoClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[openAndCloseAndWorkCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndCloseAndWork :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;
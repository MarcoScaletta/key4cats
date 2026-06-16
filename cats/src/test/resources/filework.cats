
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
    ensures: ~{openFile,workOnFile,closeFile}~ ** x::y1 . `true` ;
    expects: ~{closeFile}~ ** start(closeFile,_) ** ~~;

[openAndWorkAndCloseCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndWorkAndClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[openAndWorkTwiceAndCloseCAT] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndWorkTwiceAndClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[noOpenAndWorkAndCloseCATFail] {openFileCAT;workOnFileCAT;closeFileCAT;}  noOpenAndWorkAndClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

[openAndWorkAndNoCloseCATFail] {openFileCAT;workOnFileCAT;closeFileCAT;}  openAndWorkAndNoClose :
    assumes: ~~ ** x::y . `true`;
    ensures: ~~ ** x::y1 . `true` ;
    expects: ~~;

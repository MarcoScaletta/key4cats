[hasNext]  hasNext :
    <<
        ~~ ** resHasNext::h1 . `true`
        |
        start(hasNext,\id) ** ~{next}~ ** pop(hasNext,\id) ** resHasNext::h2 . `true`
        |
        ~~
    >>

[next]  next :
    <<
//        ~~  ** pop(hasNext,_) ** resHasNext::hasNext . `hasNext=1` ** ~{next}~
        ~{next}~ ** resHasNext::h1 . `true`
        |
//        ~~ ** pop(hasNext,_) ** resHasNext::h1 . `true`
//        |
        start(next,\id) ** ~~ ** pop(next,\id) ** resHasNext::h2 . `true`
        |
        ~~
    >>

[main] {next;hasNext;} bufferCaseStudy :
    <<
        ~{next}~ ** resHasNext::h1 . `true`
        |
        start(bufferCaseStudy,\id) ** ~~ ** pop(bufferCaseStudy,\id) ** resHasNext::h2 . `true`
        |
        ~~
    >>
[hasNext]  hasNext :
    <<
        ~~ ** resHasNext::h1 . `true`
        |
        start(hasNext,\id) ** ~{next}~ ** pop(hasNext,\id) ** resHasNext::h2 . `true`
        |
        ~~
    >>
//  simple example for iterator
[next]  next :
    <<
//        ~~  ** pop(hasNext,_) ** resHasNext::hasNext . `hasNext=1` ** ~{next}~
//        ~~  ** pop(hasNext,_) ** ~{next,hasNext}~` ** resHasNext::hasNext . `hasNext=1`
//        ~~  ** pop(hasNext,_) ** resHasNext::hasNext . `hasNext=1`
// requires
        ~~  ** pop(hasNext,_) ** ~{next}~ ** resHasNext::h1 . `true`
        |
//        ~~ ** pop(hasNext,_) ** resHasNext::h1 . `true`
//        |
//ensures
//      ~~
        start(next,\id) ** ~~ ** pop(next,\id) ** resHasNext::h2 . `true`
        |
//expects
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
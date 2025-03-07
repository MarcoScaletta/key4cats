[hasNext]  hasNext :
    <<
        ~~ ** resHasNext::h1 . `true`
        |
        start(hasNext,\id) ** ~{next}~ ** pop(hasNext,\id) ** resHasNext::h2 . `true`
        |
        ~~
    >>
//  a simple example for iterator:
// - 'hasNext()' must occur immediately before 'next()'
// - no change of state can occur in between
[next]  next :
    <<
// requires
        ~~  ** pop(hasNext,_) ** resHasNext::h1 . `h1=1`
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
//  a more complex example for iterator:
// - 'hasNext()' does not have to occur immediately before 'next()'
// - there is no call to 'next()' after the last call to 'hasNext()' and this call to 'next'
[nextComplex]  next :
    <<
// requires
        ~~  ** pop(hasNext,_) ** ~{next}~ ** resHasNext::h1 . `h1=1`
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

[mainComplex] {nextComplex;hasNext;} bufferCaseStudy :
    requires: ~{next}~ ** resHasNext::h1 . `true`;
    ensures:  start(bufferCaseStudy,\id) ** ~~ ** pop(bufferCaseStudy,\id) ** resHasNext::h2 . `true`;
    expects: ~~ ;


[main] {next;hasNext;} bufferCaseStudy :
    <<
        ~{next}~ ** resHasNext::mainH1 . `true`
        |
        start(bufferCaseStudy,\id) ** ~~ ** pop(bufferCaseStudy,\id) ** resHasNext::mainH2 . `true`
        |
        ~~
    >>

//[mainUpdate] {next;hasNext;} bufferCaseStudyUpdate :
//    <<
//        ~{next}~ ** resHasNext::h1 . `true`
//        |
//        start(bufferCaseStudy,\id) ** ~~ ** pop(bufferCaseStudy,\id) ** resHasNext::h2 . `true`
//        |
//        ~~
//    >>
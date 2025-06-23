#!/bin/sh

grammars=("Async")
echo "Generating parsers:" 
for grammar in "${grammars[@]}"; do
    echo \\t ">" ${grammar}
    antlr4 asynclang/grammars/${grammar}.g4 -v 4.13.2 -o  asynclang/src/main/java/parsers/${grammar} -Xexact-output-dir -package key4cats.parsers.${grammar} -visitor
done

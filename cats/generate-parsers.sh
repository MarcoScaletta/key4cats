#!/bin/sh

grammars=("CATs")
echo "Generating parsers:" 
for grammar in "${grammars[@]}"; do
    echo \\t ">" ${grammar}
    antlr4 cats/grammars/${grammar}.g4 -v 4.13.2 -o  cats/src/main/java/key4cats/parsers/${grammar} -Xexact-output-dir -package key4cats.parsers.${grammar} -visitor
done

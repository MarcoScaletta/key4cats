#!/bin/sh

grammars=("CATs")
echo "Generating parsers:" 
for grammar in "${grammars[@]}"; do
    echo \\t ">" ${grammar}
    antlr4 grammars/${grammar}.g4 -o  src/main/java/key4cats/parsers/${grammar} -Xexact-output-dir -package key4cats.parsers.${grammar} -visitor
done

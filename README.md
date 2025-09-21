# dk.brics

Reviving the Brics parser and grammar analyzer. Not sure where I found it on the internet.
This code was modified to work with Java v24.

To get it to run, use VSCode: `cd grammar.sources; code .`

Example grammars are here: https://www.brics.dk/grammar/examples.html

There are a few idiosyncrasies with this code.

* The parser only computes a parse tree if every
right-hand-side symbol of a rule is attributed, using `[ foobar ]` after the symbol. Apparently,
the code looks for explicitly named symbols.

* There is a graphical UI and a command-line interface. I found the graphical UI cumbersome to use,
so kept with the command=line interface.

* I had to change quite a bit of the code from `varN`, where `N` is some integer, into a realistic,
purposeful name so I could debug and fix the code.

* I don't think it computes multiple trees for an ambiguous parse.


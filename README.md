# dk.brics

This repo is a revival of the Brics parser and grammar analyzer, described in this paper:

Brabrand, Claus, Robert Giegerich, and Anders Møller. "Analyzing ambiguity of context-free grammars." Science of Computer Programming 75.3 (2010): 176-191.

https://doi.org/10.1016/j.scico.2009.11.002

The purpose of the revival is to study the code and the algorithms used to detect grammar ambiguity.

As far as the code goes, I'm not sure where I found it on the internet.
But, I had to modify the code to work with Java v24, and extended it in a couple of ways to debug
the code.

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

* I don't think it computes multiple trees for an ambiguous parse because only one parse tree is computed
and returned from the `parse()` function call.

* I found the `print()` method for printing an AST to be completely impossible to read. So, I wrote the routine
`myprint()` to print out the AST as an indented structure, using the names of the nodes of the nonterminals.
This output is much simpler to visualize and check the tree structure.


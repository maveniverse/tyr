# Týr

> Týr is the god of war, law, justice, and honor.

Problems:
* Problem 1: Over- and misuse of dependency management.
* Problem 2: Limiting resolution (ie. "define resolvable space")
* Problem 3: Rogue dependencies.
* Problem 4: Performance and security issues due 1, 2 and 3.

## Problem 1

TBD

## Problem 2

TBD

## Problem 3

TBD

## Problem 4

Maven project received several performance related issues and almost all of them involved
huge count of dependency management entries going back and forth between Maven and Resolver.
We tried to optimize all these, made some improvements, but what if all those massive entries
are simply gone, not there?

With Problem 1, 2 and 3 gone, this problem is solved as well, as Maven, and especially
Resolver simply does not have to "juggle" with huge memory/structs => snappier and safer builds.

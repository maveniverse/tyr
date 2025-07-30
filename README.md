# Týr

> Týr is the god of war, law, justice, and honor.

Problems:
* Problem 1: Over- and misuse of dependency management.
* Problem 2: Rogue dependencies.
* Problem 3: Performance and security issues due 1 and 2.

## Problem 1

TBD

## Problem 2

TBD

## Problem 3

Maven project received several "performance" related issues and almost all of them involved
huge count of dependency management entries going back and forth between Maven and Resolver.
We tried to optimize all these, applied some changes, but what if all those massive entries
are simply gone?

With Problem 1 and Problem 2 gone, this problem is solved as well, as Maven, and especially
Resolver simply has to "juggle" with way less memory => snappier and safer builds.

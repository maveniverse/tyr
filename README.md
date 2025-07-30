# Týr

> Týr is the god of war, law, justice, and honor.

Problems:
* Problem 1: Over- and misuse of dependency management.
* Problem 2: Augmenting resolution (ie. "define resolvable space" or "pass on extra info")
* Problem 3: Rogue dependencies.
* Problem 4: Performance and security issues due 1, 2 and 3.

## Problem 1

Today many (big) projects provide BOMs for consumers to import, and those BOMs tend to be **huge**.
The reason why these projects provide them is to "lock down" various direct and transitive 
dependency versions, as the library or project was "built with" these versions, and consumers
should "carry on" working with these versions.

On the other hand, these huge BOMs are in fact burden: imagine a consumer project that consumes 
such a huge project with 1000 modules (and even more locked versions for dependencies), but in fact
it uses only a tiny portion of the huge project. It is doomed to "carry" thousands of entries
only to utilize tiny amount of them. Moreover, how Resolver works, it make this 
work internally even more CPU and memory intensive, as dependency management list "just grows",
as collection runs and list of them is appended with entries lifted from resolved POMs and kept
distinct for each "path" in dependency graph, it basically explodes on large graphs.

Conclusion:
* Large scale (huge) BOMs are just bad: they clog Maven and Resolver for "what if it is used downstream".
  Is like shooting with a cannon onto mouse. Lumps "everything" onto consumer projects thay may want
  only tiny portion of it.
* THe **`import`** scope hack is bad, many times confuses users. Moreover, the `import` scope 
  **does not and never did** work in "Maven way" (it is first comes wins instead).

## Problem 2

Consider Maven Plugins: each one was fully resolved, just to be embedded and run within Maven
you are running. Long time ago, after the build, users would end up with multiple (half of dozen
if not tens of it) versions of `maven-core` in their local repository, as each version of each plugin it depended on was
resolved (downloaded, checksum calculated and so on) only to **be excluded** from plugin classpath
when it runs (as running Maven provides the needed bits). Later on in 3.9.x timeframe, the plugin tools were improved,
and plugins started declaring `maven-core` and other dependencies with `provided` scope, but this
is in fact a hack. Also, this delegates the knowledge from Maven Core developers to Maven Plugin developers,
to figure out what is provided and what is not provided (not everything in `/lib` is exported!).
Moreover, in case you need `maven-resolver-util` on classpath, but also you
want or need to support Maven 3.8.x and also Maven 3.9.x, one **must** declare this very one dependency
as `compile` scoped dependency (while others like `maven-resolver-api` left in `provided` scope).
These "tricks" are just bad.

Plugin Tools provides here some "faint" heuristic, that is also annoying that warns users with
dependencies in "wrong scopes". Am looking at things like `maven-archiver`.

Conclusion:
* In this case we force plugin developers to figure out things that are Maven internals, combined
  with exported packages and artifacts, that is not trivial. And this happens build time.
* Why all this is not sorted at runtime? What if Maven would be "self-aware" of its own constituents,
  and plugin developers just depend on things they need and runtime figures out what to resolve and
  and what not to resolve?

## Problem 3

You built, tested and published a library to Maven Central. Hence, you can go to sleep, right?

But someone may pick up your library, with completely different setup, configuration and environment, 
and end up with totally different outcome.

## Problem 4

Maven project received several performance related issues and almost all of them involved
huge count of dependency management entries going back and forth between Maven and Resolver.
We tried to optimize all these, made some improvements, but what if all those massive entries
are simply gone, not there?

With Problem 1, 2 and 3 gone, this problem is solved as well, as Maven, and especially
Resolver simply does not have to "juggle" with huge memory/structs => snappier and safer builds.

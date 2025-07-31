# Týr

> Týr is the god of war, law, justice, and honor.

Problems:
* Problem 1: Over- and misuse of dependency management.
* Problem 2: Augmenting resolution (i.e. "define resolvable space" or "pass on extra info")
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
  Is like shooting with a cannon onto mouse. Lumps "everything" onto consumer projects they may want
  only tiny portion of it. Reading effective POMs of projects importing huge BOMs is impossible.
* The **`import`** scope hack is bad, and too many times confuses users, even experienced ones. Moreover, 
  the `import` scope **does not and never did** work in "Maven way" (it is first comes wins).

## Problem 2

Consider Maven Plugins: originally each one was fully resolved, just to be embedded and run within Maven
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
  and what not to resolve? Why not generalize this?

## Problem 3

You built, tested and published a library to Maven Central. Hence, you can go to sleep, right?

But someone may pick up your library, with completely different setup, configuration and environment, 
and end up with totally different outcome. Maybe even some spoofed artifacts, due wrong repository
ordering?

You are rang for support and asked questions you cannot answer: is `foo-bar-1.0.jar` in their
WAR the one you built against? 

## Problem 4

Maven project received several performance related issues and almost all of them involved
huge count of dependency management entries going back and forth between Maven and Resolver.
We tried to optimize all these, made some improvements, but what if all those massive entries
are simply gone, not there?

With Problem 1, 2 and 3 gone, this problem is solved as well, as Maven, and especially
Resolver simply does not have to "juggle" with huge memory/structs => snappier and safer builds.

## What if?

What if one could:
* provide similar functionality like BOMs are today (but without the huge overhead) and import trickery?
* provide even more than BOMs, like augment resolution?
* provide even more information about artifacts, like their checksums and more?
* reuse existing BOMs even, but without overhead?

Meet Týr...

## Týr sources

Týr defines "source" that contains following:
* inventory mapping: G -> A -> V -> \[C:\]E -> data 
* management -> G -> A -> depMgt-like data

Example mapping structure (just to visualize data contents and structure):

```yaml
---
# Týr mapping file
version: 1.0

global:
  # management: G -> A -> depMgt-like info
  management:
    - junit:
      - junit:
        version: "1.0"
        scope: test
        optional: true
        exclusions:
          - org.hamcrest:hamcrest-core
          - something:*

  # inventory: G -> A -> V -> [C:]E -> data
  inventory: 
    - junit: 
      - junit: 
        - "1.0": 
          - pom:
            checksums:
              - sha1: "abcd"
          - jar:
            checksums:
              - sha1: "abcd"
          - sources:jar:
            checksums:
              - sha1: "abcd"
          - javadoc:jar:
            checksums:
              - sha1: "abcd"
        - "1.1":
          - pom:
            checksums:
              - sha1: "abcd"
          - jar:
            checksums:
              - sha1: "abcd"
          - sources:jar:
            checksums:
              - sha1: "abcd"
          - javadoc:jar:
            checksums:
              - sha1: "abcd"
    - something:
      - else:
        - "1.0":
          - pom:
            checksums:
              - sha1: "abcd"
          - jar:
            checksums:
              - sha1: "abcd"
          - sources:jar:
            checksums:
              - sha1: "abcd"
          - javadoc:jar:
            checksums:
              - sha1: "abcd"
```

The "data" for now is limited to checksums (those supported by Resolver), but may be later expanded.

Source implementations envisioned are:
* BOM-source: point it to BOM artifact, it will be resolved and "on the fly" conversion happens (only repository provided checksums are available)
* Descriptor-source: point to Tyr descriptor artifact or file, and it will be (resolved if artifact) and used; descriptor may contain stronger checksums that provided by repository

Tools:
* BOM to descriptor converter Mojo (w/ options to "decorate"; for example grab checksums or even calculate strong ones)
* Mojo that "records" build descriptor; listens to build and produces inventory, possibly even deploying it

What Tyr does:
* manages dependencies (in this case the POM will be probably unusable with vanilla Maven; lack of versions and no import BOM!)
* "non-strict" or "strict" mode where versions are forced; and/or fail the build if out of inventory artifact met
* enforces that build uses only verified dependencies from inventory
* sources checksums to Resolver TC subsystem; configurable like "fail if missing"


ClaferChocoSoo
===========

v0.3.5.15-01-2014

A backend for [ClaferMooViz](https://github.com/gsdlab/ClaferMooVizualizer) that uses [ChocoSolver](https://github.com/gsdlab/chocosolver) to solve single-objective optimization problems.
This project is a wrapper that invokes ChocoSolver in a proper way and produces the output in the same format as ClaferMoo does.

Contributors
------------

* [Alexandr Murashkin](http://gsd.uwaterloo.ca/amurashk), MMath Candidate. Main developer.
* [Jimmy Liang](http://gsd.uwaterloo.ca/jliang), MSc. Candidate. Ports to Java 1.7 and Choco3.

Getting Clafer Tools
--------------------

Binary distributions of the release 0.3.5 of Clafer Tools for Windows, Mac, and Linux, 
can be downloaded from [Clafer Tools - Binary Distributions](http://http://gsd.uwaterloo.ca/clafer-tools-binary-distributions). 
Clafer Wiki requires Haskell Platform and MinGW to run on Windows. 

In case these binaries do not work on your particular machine configuration, the tools can be built from source code, as described below.

Prerequisites for Running
-------------
* [Java 6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Running Standalone
-------------

1. Running optimization over the Clafer choco output file: 
```sh
java -jar claferchocosoo-0.3.5-jar-with-dependencies.jar <file-name.js>
```

This will produce optimal instances in a textual form.

2. Version
```sh
java -jar claferchocosoo-0.3.5-jar-with-dependencies.jar --version
```

Outputs the current version of the tool

Running as a Backend
-------------


* Install *ClaferMooVisualizer* (the *MultipleBackends* branch, from the link above).
* In the folder `Server/Backends`, create a `ChocoSingle` folder.
* Copy `claferchocosoo-0.3.5-SNAPSHOT-jar-with-dependencies.jar` from the `target` folder to `ChocoSingle` folder.
* Create a folder `clafer_choco_branch` in `ChocoSingle`.
* Build *Clafer Compiler* (the *choco* branch, getting it from the link above) to the folder `clafer_choco_branch`. The folder `clafer_choco_branch` should contain the executable of Clafer.
* Put (if exists, just make sure all paths match) the following configuration in the `Server/Backends/backends.json` :

```json
{
    "backends": [
        ....
        , 
        {
            "id": "choco_single", 
            "label": "Choco (single objective only)",
            "tool": "java",
            "args": ["-jar", "$dirname$/ChocoSingle/claferchocosoo-0.3.5-SNAPSHOT-jar-with-dependencies.jar", "$filepath$", "$dirname$/ChocoSingle/clafer_choco_branch/clafer.exe"]            
        },
        ....        
    ]   
}

```

* Make sure the arguments in the code above (`"args": [ ... ]`) point to existing files, including the Clafer executable, which may be different on Linux machines. `$dirname$` means the full path to the *Server/Backends* folder, `$filepath$` is the full path to the Clafer file being processed.
* Now the backend should be accessible in *ClaferMooVisualizer*.

### Important: Branches must correspond

All related projects are following the *simultaneous release model*. 
The branch `master` contains releases, whereas the branch `develop` contains code under development. 
When building the tools, the branches should match.
Releases from branches 'master` are guaranteed to work well together.
Development versions from branches `develop` should work well together but this might not always be the case.

Need help?
==========
* See [language's website](http://clafer.org) for news, technical reports and more
  * Check out a [Clafer tutorial](http://t3-necsis.cs.uwaterloo.ca:8091/Tutorial/Intro)
  * Try a live instance of [ClaferWiki](http://t3-necsis.cs.uwaterloo.ca:8091)
  * Try a live instance of [ClaferIDE](http://t3-necsis.cs.uwaterloo.ca:8094)
  * Try a live instance of [ClaferConfigurator](http://t3-necsis.cs.uwaterloo.ca:8093)
  * Try a live instance of [ClaferMooVisualizer](http://t3-necsis.cs.uwaterloo.ca:8092)
* Take a look at (incomplete) [Clafer wiki](https://github.com/gsdlab/clafer/wiki)
* Browse example models in the [test suite](https://github.com/gsdlab/clafer/tree/master/test/positive) and [MOO examples](https://github.com/gsdlab/clafer/tree/master/spl_configurator/dataset)
* Post questions, report bugs, suggest improvements [GSD Lab Bug Tracker](http://gsd.uwaterloo.ca:8888/questions/). Tag your entries with `clafermooviz` (so that we know what they are related to) and with `alexander-murashkin` or `michal` (so that Alex or Michał gets a notification).

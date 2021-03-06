ClaferChocoSoo
===========

v0.3.5.20-01-2014

A backend for [ClaferMooViz](https://github.com/gsdlab/ClaferMooVizualizer) that uses [ChocoSolver](https://github.com/gsdlab/chocosolver) to solve single-objective optimization problems.
This project is a wrapper that invokes ChocoSolver in a proper way and produces the output in the same format as ClaferMoo does.

Contributors
------------

* [Alexandr Murashkin](http://gsd.uwaterloo.ca/amurashk), MMath Candidate. Main developer.
* [Jimmy Liang](http://gsd.uwaterloo.ca/jliang), MSc. Candidate. Ports to Java 1.7 and Choco3.

Getting Binaries
--------------------

Binary distributions of the release 0.3.5 of Clafer Tools for Windows, Mac, and Linux, 
can be downloaded from [Clafer Tools - Binary Distributions](http://http://gsd.uwaterloo.ca/clafer-tools-binary-distributions). 

The binary for this project is called `claferchocosoo-0.3.5-jar-with-dependencies.jar`.

In case this binary does not work on your particular machine configuration, it can be built from source code, as described further.

Running
-------------

### Prerequisites

* [Java 6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* [Clafer Compiler](https://github.com/gsdlab/clafer). Required for compiling Clafer files (`.cfr`) into the Clafer Choco Javascript format (`.js`), so that they can be run using the tool.
 
### Running Standalone

First, use *Clafer Compiler*:

```sh
clafer --mode=choco <file-name.cfr>
```

This will convert the Clafer file (`.cfr`) into the Clafer Choco Javascript file (`.js`). 

Next, run the optimization:

```sh
java -jar claferchocosoo-0.3.5-jar-with-dependencies.jar <file-name.js>
```

This will run the solver and produce optimal instances in a textual form. Only Clafer files with single objective can be processed at this time. 

To get the tool version:

```sh
java -jar claferchocosoo-0.3.5-jar-with-dependencies.jar --version
```

### Running as a Backend

* Install [ClaferMooVisualizer](https://github.com/gsdlab/ClaferMooVisualizer).
* Copy the binary `claferchocosoo-0.3.5-jar-with-dependencies.jar` to the `Backends` folder. If you built the project from the source code, the binary can be found in the `target` subfolder.
* Put (if exists, just make sure all paths match) the following configuration in the `Server/Backends/backends.json` :

```json
{
    "backends": [
        ....
        , 
        {
            "id": "choco_single", 
            "label": "Choco (single objective only)",
            "tooltip": "A new Choco solver, for single objective optimization only",
            "accepted_format": "choco",               
            "tool": "java",
            "tool_args": ["-jar", "$dirname$/claferchocosoo-0.3.5-jar-with-dependencies.jar", "$filepath$"],
            "tool_version_args": ["-jar", "$dirname$/claferchocosoo-0.3.5-jar-with-dependencies.jar", "--version"]             },
        ....        
    ]   
}

```
`$dirname$` means the full path to the *Server/Backends* folder, `$filepath$` is the full path to the input JS file being processed.
* If you made any changes to the `backends.json`, then restart *ClaferMooVisualizer*.
* Now the backend should be accessible in *ClaferMooVisualizer* and listed in the `Backends` dropdown list.

Building
--------

### Prerequisites

* [Maven 2+](http://maven.apache.org/download.cgi). Required for building the projects and linking all dependencies
* [ChocoSolver](https://github.com/gsdlab/chocosolver). This is a Maven dependency for the project, so it should be installed (`mvn install`) as well.

### Procedures

* Using Maven, run: `mvn install` over the project.
* Two binaries will appear in the `target` subfolder: `claferchocosoo-0.3.5-jar-with-dependencies.jar` that is standalone and contains all the required dependencies, and `claferchocosoo-0.3.5.jar` that is not.

### Important: Branches must correspond

All related projects are following the *simultaneous release model*. 
The branch `master` contains releases, whereas the branch `develop` contains code under development. 
When building the tools, the branches should match.
Releases from branches `master` are guaranteed to work well together.
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
* Post questions, report bugs, suggest improvements [GSD Lab Bug Tracker](http://gsd.uwaterloo.ca:8888/questions/). Tag your entries with `claferchocosoo` (so that we know what they are related to) and with `alexander-murashkin` or `michal` (so that Alex or Michał gets a notification).

ClaferChocoSoo
===========

v0.3.5.15-01-2014

A backend for [ClaferMooViz](https://github.com/gsdlab/ClaferMooVizualizer) that uses [ChocoSolver](https://github.com/gsdlab/chocosolver) to solve single-objective optimization problems.
This project is simply a proxy that invokes ClaferCompiler and ChocoSolver in a proper way and produces the output in the same format as ClaferMoo does.

Contributors
------------

* [Alexandr Murashkin](http://gsd.uwaterloo.ca/amurashk), MASc. Candidate. Main developer.
* [Jimmy Liang](http://gsd.uwaterloo.ca/jliang), MSc. Candidate. Ports to Java 1.7 and Choco3.

Prerequisites
-------------
* [ClaferMooVisualizer](https://github.com/gsdlab/ClaferMooVisualizer) v0.3.5.
* [Clafer Compiler](https://github.com/gsdlab/clafer) v0.3.5.
* [Java 6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* [Maven 2+](http://maven.apache.org/) - Required for building the project.

Installation
-------------
* Install *ClaferMooVisualizer* (the *MultipleBackends* branch, from the link above).
* In the folder `Server/Backends`, create a `ChocoSingle` folder.
* Copy `claferchocosoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar` from the `target` folder to `ChocoSingle` folder.
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
            "args": ["-jar", "$dirname$/ChocoSingle/claferchocosoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "$filepath$", "$dirname$/ChocoSingle/clafer_choco_branch/clafer.exe"]            
        },
        ....        
    ]   
}

```
* Make sure the arguments in the code above (`"args": [ ... ]`) point to existing files, including the Clafer executable, which may be different on Linux machines. `$dirname$` means the full path to the *Server/Backends* folder, `$filepath$` is the full path to the Clafer file being processed.
* Now the backend should be accessible in *ClaferMooVisualizer*.

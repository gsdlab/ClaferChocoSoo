ClaferChocoSoo
===========

A backend for [ClaferMooViz](https://github.com/gsdlab/ClaferMooVizualizer) that uses [ChocoSolver](https://github.com/gsdlab/chocosolver) to solve single-objective optimization problems.
This project is simply a proxy that invokes ClaferCompiler and ChocoSolver in a proper way and produces the output in the same format as ClaferMoo does.

Prerequisites
-------------
* [ClaferMooVisualizer, MultipleBackends branch](https://github.com/gsdlab/ClaferMooVisualizer/tree/MultipleBackends).
* [Clafer compiler, Choco branch](https://github.com/gsdlab/clafer/tree/choco).
* [Java 6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* [Maven 2+](http://maven.apache.org/) - Required for building the project.

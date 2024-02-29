This is the documentation for the Peggy equality saturation engine and
compiler. Below you will find some answers to simple questions about
how to install and use Peggy.

# 0. Install Java 6.


## Windows
- Install from [Oracle Java archives](https://www.oracle.com/java/technologies/javase-java-archive-javase6-downloads.html)
(requires signup).

## Linux
?

## Mac
Use a container.
```
docker pull ubuntu:14.04.2
```

Create a container:
```
docker run -d -v /local/path/to/peggy_1.0:/container/path/to/peggy_1.0 -w /container/path/to/peggy_1.0 --name peggy -i -t ubuntu:14.04.2 /bin/bash
```

Within the container:
```
apt-get install openjdk-6-jdk
```

# 1. Installation.

No installation is necessary. If you have the peggy_1.0.jar file, then
you have a working copy of Peggy and all of the libraries it depends
upon (from the lib/ folder).


# 2. Compilation.

If you wish to compile Peggy from source, you will find all the source
code in the src/ folder. We use the Eclipse 3.1+ IDE to build Peggy.
In Eclipse, you will need to create a new Java Project, and then add
the code in the src/ folder to the project. You can do this either by
copying it into the src/ folder that Eclipse creates for your new
project, or by telling it to add the existing src/ folder to your
project's source path. Once you have built a project and added the
Peggy source code, you must add all the jars in the lib/ folder to the
project's build path. Then you can refresh the project and it should
compile correctly.


# 3. Setup.

The only setup required for Peggy is specifying the paths to a few
external tools that it relies on. The optimizers need to know the path
to the Pueblo, Minisat or GLPK solver program, depending on which you
want to use. You can specify this on the command line with the
'-minisatPath', '-puebloPath', or '-glpkPath' options.  If no path is
specified explicitly, the path is assumed to be
$COLLIDER_ROOT/scripts/minisat/Minisat (for minisat),
$COLLIDER_ROOT/scripts/pueblo/Pueblo (for Pueblo), or
/usr/bin/glpsol (for GLPK).

The optimizers also generate some temporary files, and you may specify
which folder these files are created in with the option 
'-tmpFolder <folder>'. The default is /tmp/.


# 4. Usage.

We have provided bash scripts to run the appropriate command-line
class for each optimizer/translation validator for Java/LLVM. For any
one of these scripts, you may call it without parameters to see a
description of the command-line parameters it accepts.


# 5. Quick start: Optimizing.

Here's a basic example of how to optimize a Java class with Peggy.
First, make a basic java file `Foo.java` and compile it using
`javac Foo.java` to get the class `Foo.class`.
Suppose we want to optimize a class "Foo.class", which is in the
current folder.

Step 1) Pick some axioms to use. There are several axiom files
provided in the axioms/ folder, or you can write your own. To use a
given axiom file in Peggy, simply pass the "-axioms myfile.xml"
argument.  To provide multiple files, you may either have multiple
"-axioms myfile.xml" pairs, or you may provide several files in one go
with "-axioms myfile1.xml:myfile2.xml:myfile3.xml".
         
Step 2) To optimize Java programs, the classes you wish to optimize
must be on the classpath. In the opt_java.sh script, the current path
(.) is included on the classpath. We specify that we want to optimize
Foo by adding '-O2 Foo' as a parameter to the script. This tells the
optimizer that we want to optimize at level 2 (full optimization).

Step 3) If you wish to see what axioms are applied during saturation,
add the '-displayAxioms' parameter.

Step 4) Run the script with the parameters we have determined above.
The optimized classes will be placed in the newly-created 'optimized/'
folder. (you may change the output folder with the '-o <folder>'
option.




If you have any questions/comments, please direct them to mstepp@cs.ucsd.edu.

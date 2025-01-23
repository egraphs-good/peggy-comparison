# Benchmark
## Original
```java
public class Benchmark {

    public static int branchHoistingOriginal(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            y++;
            if (n == 0) {
                x = y * 2;
            } else {
                x = y * 3;
            }
        }
        return x;
    }

    public static int branchHoistingExpected(int n) {
        int y = 0;
        int x = 0;
        while (y < 500)
            y++;

        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
        }

        return x;
    }

    public static int conditionalConstantFoldingOriginal(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public static int conditionalConstantFoldingExpected() {
        return 20;
    }

    public static int constantFoldOriginal() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }

    public static int deadLoopDeletionOriginal() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
    }

    public static int functionInliningFoo() {
        return 1;
    }

    public static int functionInliningOriginal(int x) {
        return functionInliningFoo() + 1;
    }

    public static int functionInliningExpected(int x) {
        return x + 2;
    }

    public static int ifTrueOriginal(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public static int ifTrueExpected(int x) {
        return x;
    }

    public static int infiniteEffectfulLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            System.out.println(j);
        }
        j = 2;
        return j;
    }

    public static int infiniteLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            j++;
        }
        return j;
    }

    // Modified from the paper example (see loop strength reduction)
    public static int loopBasedCodeMotionOriginal() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 50;
    }

    public static int loopBasedCodeMotionExpected() {
        int x = 0;
        while (x < 150) {
            x += 50;
        }
        return x;
    }

    public static void loopInvariantCodeMotionOriginal(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public static void loopInvariantCodeMotionExpected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
        }
    }

    public static int loopPeelingOriginal(int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public static int loopPeelingExpected(int n) {
        int x = 0;
        if (0 >= n) {
            x = 0;
        } else {
            x = 5;
            int i = 1;
            while (i < n) {
                x += 5;
                i++;
            }
        }
        return x;
    }

    public static void loopStrengthReductionOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
            i = i + 1;
            d++;
        }
    }

    public static void loopStrengthReductionExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
            d++;
        }
    }

    // Modified from the paper example, as multiplying by 5 makes
    // the optimizer just turn it into five additions.
    public static void loopStrengthReductionModifiedOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 50);
            i = i + 1;
            if (d == 150) {
                i = i + 3;
            }
            d++;
        }
    }

    public static void loopStrengthReductionModifiedExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 50;
            if (d == 150) {
                i = i + 150;
            }
            d++;
        }
    }

    public static int loopUnrollOriginal(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public static int loopUnrollExpected(int n) {
        return 1;
    }

    public static int simpleLoopUnswitchOriginal(int n) {
        int j = 0;
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            if (n < 0) {
                j = 2;
            }
            j++;
        }
        return j;
    }

    // The expected code for simple loop unswitch causes encoding
    // of the output CFG to never terminate, so it is in Failing.java.
}

```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '500'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 330
      * PEG2PEGTIME 213
      * PBTIME 99
      * ENGINETIME 8
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 885 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 914
GLPKFormulation: Number of values: 312
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 11565
      * PEG2PEGTIME 11514
      * PBTIME 10596
      * ENGINETIME 885
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 305 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 810
GLPKFormulation: Number of values: 358
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 9852
      * PEG2PEGTIME 9833
      * PBTIME 9504
      * ENGINETIME 305
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 123
      * PEG2PEGTIME 112
      * PBTIME 97
      * ENGINETIME 4
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 47
      * PEG2PEGTIME 43
      * PBTIME 35
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 50
      * PEG2PEGTIME 45
      * PBTIME 34
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 41
      * PEG2PEGTIME 39
      * PBTIME 30
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 34
      * PEG2PEGTIME 33
      * PBTIME 25
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 79
      * PEG2PEGTIME 72
      * PBTIME 64
      * ENGINETIME 2
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 53
      * PEG2PEGTIME 49
      * PBTIME 41
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 35
      * PEG2PEGTIME 33
      * PBTIME 25
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 77
      * PEG2PEGTIME 75
      * PBTIME 66
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 132 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 540
GLPKFormulation: Number of values: 392
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 381 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 391
GLPKFormulation: Number of values: 77
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 209 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 378
GLPKFormulation: Number of values: 96
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 4155
      * PEG2PEGTIME 4143
      * PBTIME 3921
      * ENGINETIME 209
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 223 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 4468
      * PEG2PEGTIME 4456
      * PBTIME 4220
      * ENGINETIME 223
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 141 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 488
GLPKFormulation: Number of values: 276
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 5867
      * PEG2PEGTIME 5836
      * PBTIME 5683
      * ENGINETIME 141
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 130 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 489
GLPKFormulation: Number of values: 277
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 5825
      * PEG2PEGTIME 5808
      * PBTIME 5666
      * ENGINETIME 130
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 377 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1305
GLPKFormulation: Number of values: 186
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 14647
      * PEG2PEGTIME 14635
      * PBTIME 14247
      * ENGINETIME 377
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 742 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1042
GLPKFormulation: Number of values: 216
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 12221
      * PEG2PEGTIME 12204
      * PBTIME 11442
      * ENGINETIME 744
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 209 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1290
GLPKFormulation: Number of values: 577
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 14984
      * PEG2PEGTIME 14969
      * PBTIME 14751
      * ENGINETIME 209
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 239 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 734
GLPKFormulation: Number of values: 355
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 8636
      * PEG2PEGTIME 8621
      * PBTIME 8372
      * ENGINETIME 239
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 140 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 582
GLPKFormulation: Number of values: 350
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 6929
      * PEG2PEGTIME 6913
      * PBTIME 6762
      * ENGINETIME 140
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 150 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 619
GLPKFormulation: Number of values: 404
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 7337
      * PEG2PEGTIME 7316
      * PBTIME 7155
      * ENGINETIME 150
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 343 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 99
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 4507
      * PEG2PEGTIME 4493
      * PBTIME 4140
      * ENGINETIME 343
   - Done processing method <Benchmark: int loopUnrollOriginal(int)>
   - Processing method <Benchmark: int loopUnrollExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 41
      * PEG2PEGTIME 36
      * PBTIME 28
      * ENGINETIME 0
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 500 after 85 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 453
GLPKFormulation: Number of values: 310
         @ Running solver
java.lang.IllegalArgumentException: Cannot create process
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:180)
	at peggy.ilp.GLPKRunner.run(GLPKRunner.java:55)
	at peggy.ilp.GLPKReversionHeuristic.chooseReversionNodes(GLPKReversionHeuristic.java:143)
	at peggy.represent.DefaultPEGExtractor.getNodeMap(DefaultPEGExtractor.java:31)
	at peggy.represent.AbstractPEGExtractor.extractPEG(AbstractPEGExtractor.java:54)
	at peggy.optimize.PEG2PEGOptimizer.optimize(PEG2PEGOptimizer.java:104)
	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:73)
	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
	at peggy.optimize.java.Main.main(Main.java:2881)
Caused by: java.io.IOException: Cannot run program "/usr/bin/glpsol": java.io.IOException: error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:494)
	at java.lang.Runtime.exec(Runtime.java:612)
	at peggy.pb.ProcessRunner.start(ProcessRunner.java:172)
	... 9 more
Caused by: java.io.IOException: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:164)
	at java.lang.ProcessImpl.start(ProcessImpl.java:81)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:476)
	... 11 more
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 5664
      * PEG2PEGTIME 5593
      * PBTIME 5499
      * ENGINETIME 85
   - Done processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Done optimizing Benchmark
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 2
   - Total methods = 27
+ Fixing bytecode of method <Benchmark: void <init>()>
+ Fixing bytecode of method <Benchmark: int branchHoistingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int branchHoistingExpected(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int conditionalConstantFoldingExpected()>
+ Fixing bytecode of method <Benchmark: int constantFoldOriginal()>
+ Fixing bytecode of method <Benchmark: int deadLoopDeletionOriginal()>
+ Fixing bytecode of method <Benchmark: int functionInliningFoo()>
+ Fixing bytecode of method <Benchmark: int functionInliningOriginal(int)>
+ Fixing bytecode of method <Benchmark: int functionInliningExpected(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueOriginal(int)>
+ Fixing bytecode of method <Benchmark: int ifTrueExpected(int)>
+ Fixing bytecode of method <Benchmark: int infiniteEffectfulLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int infiniteLoopOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionOriginal()>
+ Fixing bytecode of method <Benchmark: int loopBasedCodeMotionExpected()>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
+ Fixing bytecode of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopPeelingExpected(int)>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionExpected()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedOriginal()>
+ Fixing bytecode of method <Benchmark: void loopStrengthReductionModifiedExpected()>
+ Fixing bytecode of method <Benchmark: int loopUnrollOriginal(int)>
+ Fixing bytecode of method <Benchmark: int loopUnrollExpected(int)>
+ Fixing bytecode of method <Benchmark: int simpleLoopUnswitchOriginal(int)>
+ Writing class back to optimized/Benchmark.class
+ Total optimization time = 130523 milliseconds
```

### Optimized
```java
13:59:06.116 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    for (int i = 0;; i = i)
    {
      i += 1;
      if (i >= 500) {
        break;
      }
    }
    if (paramInt == 0) {
      i *= 2;
    }
    for (;;)
    {
      return i;
      i *= 3;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (i < 500) {
      i += 1;
    }
    if (paramInt == 0) {}
    for (paramInt = i * 2;; paramInt = i * 3) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 3) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        System.out.println(paramInt2 * paramInt1);
        paramInt2 += 1;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      while (paramInt2 < 20)
      {
        System.out.println(paramInt2 * paramInt1);
        paramInt2 += 1;
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      i += 5;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int j = 0;; j = j)
    {
      return j;
      int i = 1;
      j = 5;
      while (i < paramInt)
      {
        i += 1;
        j += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(i * 5);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (j < 300)
    {
      i += 5;
      j += 1;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int k = 0;
    int j = 0;
    if (k < 300)
    {
      int i = j + 1;
      if (k == 150) {
        i += 3;
      }
      for (;;)
      {
        System.out.println(j * 50);
        k += 1;
        j = i;
        break;
        i = i;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (k < 300)
    {
      int i = j + 50;
      if (k == 150) {
        i += 150;
      }
      for (;;)
      {
        System.out.println(j);
        j = i;
        k += 1;
        break;
        i = i;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (i < paramInt)
      {
        System.out.println(i);
        i += 1;
        j = 2 + 1;
      }
      return j;
    }
    for (;;)
    {
      if (i < paramInt)
      {
        System.out.println(i);
        i += 1;
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

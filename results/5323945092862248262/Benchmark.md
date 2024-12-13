# Benchmark
## Original
```java
public class Benchmark {

    public int branchHoistingOriginal(int n) {
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

    public int branchHoistingExpected(int n) {
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

    public int conditionalConstantFoldingOriginal(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public int conditionalConstantFoldingExpected() {
        return 20;
    }

    public int constantFoldOriginal() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }

    public int deadLoopDeletionOriginal() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
    }

    public int functionInliningFoo() {
        return 1;
    }

    public int functionInliningOriginal(int x) {
        return functionInliningFoo() + 1;
    }

    public int functionInliningExpected(int x) {
        return x + 2;
    }

    public int ifTrueOriginal(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public int ifTrueExpected(int x) {
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

    public int loopBasedCodeMotionOriginal() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 5;
    }

    public int loopBasedCodeMotionExpected() {
        int x = 0;
        while (x < 15) {
            x += 5;
        }
        return x;
    }

    public void loopInvariantCodeMotionOriginal(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public void loopInvariantCodeMotionExpected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
        }
    }

    public int loopPeelingOriginal(int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public int loopPeelingExpected(int n) {
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

    public static void loopStrengthReductionModifiedOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
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
            i = i + 5;
            if (d == 150) {
                i = i + 15;
            }
            d++;
        }
    }

    public int loopUnrollOriginal(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public int loopUnrollExpected(int n) {
        return 1;
    }

    public int simpleLoopUnswitchOriginal(int n) {
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

    // This causes the encoding of output CFG to never terminate
    // public int simpleLoopUnswitchExpected(int n) {
    // int j = 0;
    // if (n < 0) {
    // for (int i = 0; i < n; i++) {
    // System.out.println(i);
    // j = 2;
    // j++;
    // }
    // } else {
    // for (int i = 0; i < n; i++) {
    // System.out.println(i);
    // j++;
    // }
    // }
    // return j;
    // }

}

```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 32

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
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
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 748
      * PEG2PEGTIME 582
      * PBTIME 429
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 173 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 60
GLPKFormulation: Number of values: 39
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 1521
      * PEG2PEGTIME 1429
      * PBTIME 1221
      * ENGINETIME 173
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 22 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 50
GLPKFormulation: Number of values: 32
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 966
      * PEG2PEGTIME 947
      * PBTIME 891
      * ENGINETIME 23
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
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
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 447
      * PEG2PEGTIME 436
      * PBTIME 422
      * ENGINETIME 4
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 353
      * PEG2PEGTIME 348
      * PBTIME 335
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 270
      * PEG2PEGTIME 264
      * PBTIME 250
      * ENGINETIME 4
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 247
      * PEG2PEGTIME 242
      * PBTIME 229
      * ENGINETIME 1
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 262
      * PBTIME 253
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 13 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 13
GLPKFormulation: Number of values: 12
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 402
      * PEG2PEGTIME 396
      * PBTIME 383
      * ENGINETIME 2
      * Optimization ratio 3004/3004 = 1.0
      * PEG-based Optimization ratio 3004/3004 = 1.0
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
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 385
      * PEG2PEGTIME 380
      * PBTIME 363
      * ENGINETIME 5
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 356
      * PEG2PEGTIME 351
      * PBTIME 335
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 371
      * PEG2PEGTIME 366
      * PBTIME 356
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 13 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 30
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
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
      * Engine reached iteration bound of 32 after 20 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 42
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
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
      * Engine reached iteration bound of 32 after 16 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 47
GLPKFormulation: Number of values: 27
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionOriginal()> SUCCESSFUL
      * Optimization took 953
      * PEG2PEGTIME 938
      * PBTIME 909
      * ENGINETIME 16
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionOriginal()>
   - Processing method <Benchmark: int loopBasedCodeMotionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 22 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 42
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopBasedCodeMotionExpected()> SUCCESSFUL
      * Optimization took 781
      * PEG2PEGTIME 773
      * PBTIME 740
      * ENGINETIME 22
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <Benchmark: int loopBasedCodeMotionExpected()>
   - Processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 7 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 49
GLPKFormulation: Number of values: 37
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)> SUCCESSFUL
      * Optimization took 856
      * PEG2PEGTIME 835
      * PBTIME 819
      * ENGINETIME 7
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <Benchmark: void loopInvariantCodeMotionOriginal(int,int)>
   - Processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 8 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 49
GLPKFormulation: Number of values: 37
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopInvariantCodeMotionExpected(int,int)> SUCCESSFUL
      * Optimization took 818
      * PEG2PEGTIME 800
      * PBTIME 783
      * ENGINETIME 8
      * Optimization ratio 31519/31519 = 1.0
      * PEG-based Optimization ratio 31519/31519 = 1.0
   - Done processing method <Benchmark: void loopInvariantCodeMotionExpected(int,int)>
   - Processing method <Benchmark: int loopPeelingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 12 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 48
GLPKFormulation: Number of values: 29
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingOriginal(int)> SUCCESSFUL
      * Optimization took 816
      * PEG2PEGTIME 807
      * PBTIME 786
      * ENGINETIME 12
      * Optimization ratio 330/330 = 1.0
      * PEG-based Optimization ratio 330/330 = 1.0
   - Done processing method <Benchmark: int loopPeelingOriginal(int)>
   - Processing method <Benchmark: int loopPeelingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 11 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 52
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopPeelingExpected(int)> SUCCESSFUL
      * Optimization took 747
      * PEG2PEGTIME 734
      * PBTIME 699
      * ENGINETIME 11
      * Optimization ratio 335/335 = 1.0
      * PEG-based Optimization ratio 335/335 = 1.0
   - Done processing method <Benchmark: int loopPeelingExpected(int)>
   - Processing method <Benchmark: void loopStrengthReductionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 9 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 46
GLPKFormulation: Number of values: 34
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionOriginal()> SUCCESSFUL
      * Optimization took 797
      * PEG2PEGTIME 788
      * PBTIME 769
      * ENGINETIME 9
      * Optimization ratio 31490/31490 = 1.0
      * PEG-based Optimization ratio 31490/31490 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 11 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 50
GLPKFormulation: Number of values: 45
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionExpected()> SUCCESSFUL
      * Optimization took 842
      * PEG2PEGTIME 832
      * PBTIME 812
      * ENGINETIME 11
      * Optimization ratio 31430/31430 = 1.0
      * PEG-based Optimization ratio 31430/31430 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionExpected()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 6 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 58
GLPKFormulation: Number of values: 45
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedOriginal()> SUCCESSFUL
      * Optimization took 939
      * PEG2PEGTIME 915
      * PBTIME 897
      * ENGINETIME 6
      * Optimization ratio 31720/31720 = 1.0
      * PEG-based Optimization ratio 31720/31720 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedOriginal()>
   - Processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 6 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 62
GLPKFormulation: Number of values: 48
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: void loopStrengthReductionModifiedExpected()> SUCCESSFUL
      * Optimization took 1002
      * PEG2PEGTIME 950
      * PBTIME 931
      * ENGINETIME 6
      * Optimization ratio 31520/31520 = 1.0
      * PEG-based Optimization ratio 31520/31520 = 1.0
   - Done processing method <Benchmark: void loopStrengthReductionModifiedExpected()>
   - Processing method <Benchmark: int loopUnrollOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 16 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 43
GLPKFormulation: Number of values: 24
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollOriginal(int)> SUCCESSFUL
      * Optimization took 842
      * PEG2PEGTIME 806
      * PBTIME 743
      * ENGINETIME 17
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
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
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int loopUnrollExpected(int)> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 244
      * PBTIME 235
      * ENGINETIME 0
   - Done processing method <Benchmark: int loopUnrollExpected(int)>
   - Processing method <Benchmark: int simpleLoopUnswitchOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 54
GLPKFormulation: Number of values: 42
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int simpleLoopUnswitchOriginal(int)> SUCCESSFUL
      * Optimization took 904
      * PEG2PEGTIME 862
      * PBTIME 850
      * ENGINETIME 4
      * Optimization ratio 31435/31435 = 1.0
      * PEG-based Optimization ratio 31435/31435 = 1.0
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
+ Total optimization time = 19511 milliseconds
```

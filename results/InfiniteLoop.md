# InfiniteLoop
## Original
```java
public class InfiniteLoop {
    public static void main(String[] args) {
        original();
    }

     public static int original() {
        int j = 0;
        for (int i = 5; i == 5; ) {
            j++;
        }
        return j;
    }
}

```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 3 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 438
      * PEG2PEGTIME 355
      * PBTIME 276
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 247
      * PBTIME 238
      * ENGINETIME 0
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 1268 milliseconds
```

```java
19:28:33.638 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 4

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 3 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 439
      * PEG2PEGTIME 359
      * PBTIME 282
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 299
      * PEG2PEGTIME 291
      * PBTIME 281
      * ENGINETIME 0
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 1312 milliseconds
```

```java
19:28:36.680 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 8

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 435
      * PEG2PEGTIME 357
      * PBTIME 275
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 244
      * PBTIME 235
      * ENGINETIME 0
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 8 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 1315 milliseconds
```

```java
19:28:39.806 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 16

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 474
      * PEG2PEGTIME 396
      * PBTIME 313
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 288
      * PEG2PEGTIME 276
      * PBTIME 265
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 16 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 1433 milliseconds
```

```java
19:28:42.922 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 32

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 440
      * PEG2PEGTIME 361
      * PBTIME 276
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 247
      * PBTIME 237
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 28 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 1508 milliseconds
```

```java
19:28:46.207 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 64

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 439
      * PEG2PEGTIME 359
      * PBTIME 277
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 261
      * PBTIME 251
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 67 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 1849 milliseconds
```

```java
19:28:49.767 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 128

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 426
      * PEG2PEGTIME 350
      * PBTIME 268
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 247
      * PEG2PEGTIME 238
      * PBTIME 229
      * ENGINETIME 0
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 149 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 2190 milliseconds
```

```java
19:28:53.698 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 256

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 431
      * PEG2PEGTIME 351
      * PBTIME 271
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 244
      * PBTIME 235
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 277 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 2997 milliseconds
```

```java
19:28:58.465 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 512

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 432
      * PEG2PEGTIME 350
      * PBTIME 269
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 245
      * PBTIME 235
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 610 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 4739 milliseconds
```

```java
19:29:05.022 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 1024

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 432
      * PEG2PEGTIME 352
      * PBTIME 272
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 315
      * PEG2PEGTIME 308
      * PBTIME 291
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 966 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 7851 milliseconds
```

```java
19:29:14.603 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2048

Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file InfiniteLoop
+ Optimizing class InfiniteLoop
   - Processing method <InfiniteLoop: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 432
      * PEG2PEGTIME 353
      * PBTIME 274
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 9 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 244
      * PBTIME 234
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3711 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteLoop: int original()> [
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
      * Optimization of method <InfiniteLoop: int original()> FAILED
+ Done optimizing InfiniteLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteLoop: void <init>()>
+ Fixing bytecode of method <InfiniteLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteLoop: int original()>
+ Writing class back to optimized/InfiniteLoop.class
+ Total optimization time = 16588 milliseconds
```

```java
19:29:32.928 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
public class InfiniteLoop
{
  public static void main(String[] paramArrayOfString)
  {
    original();
  }
  
  public static int original()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     InfiniteLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

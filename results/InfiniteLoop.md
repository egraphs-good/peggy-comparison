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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 2

### Peggy output
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
      * Engine reached iteration bound of 2 after 4 milliseconds
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
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 435
      * PBTIME 304
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 286
      * PEG2PEGTIME 277
      * PBTIME 262
      * ENGINETIME 1
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
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 12
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
+ Total optimization time = 1616 milliseconds
```

### Optimized
```java
22:40:52.798 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 4

### Peggy output
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
      * Engine reached iteration bound of 4 after 4 milliseconds
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
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 430
      * PBTIME 301
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 264
      * PBTIME 245
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 12
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
+ Total optimization time = 1598 milliseconds
```

### Optimized
```java
22:40:56.955 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 8

### Peggy output
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
      * Engine reached iteration bound of 8 after 7 milliseconds
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
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 557
      * PEG2PEGTIME 424
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void <init>()>
   - Processing method <InfiniteLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 263
      * PBTIME 246
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 13 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 15
GLPKFormulation: Number of values: 13
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
+ Total optimization time = 1656 milliseconds
```

### Optimized
```java
22:41:01.191 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 16

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 430
      * PBTIME 302
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 274
      * PEG2PEGTIME 265
      * PBTIME 248
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 26 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 21
GLPKFormulation: Number of values: 15
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
+ Total optimization time = 1714 milliseconds
```

### Optimized
```java
22:41:05.474 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 586
      * PEG2PEGTIME 452
      * PBTIME 318
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 274
      * PEG2PEGTIME 266
      * PBTIME 247
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 46 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 42
GLPKFormulation: Number of values: 24
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
+ Total optimization time = 1978 milliseconds
```

### Optimized
```java
22:41:10.051 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 64

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 567
      * PEG2PEGTIME 446
      * PBTIME 313
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 270
      * PEG2PEGTIME 261
      * PBTIME 246
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 99 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 71
GLPKFormulation: Number of values: 33
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
+ Total optimization time = 2430 milliseconds
```

### Optimized
```java
22:41:15.056 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 128

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 438
      * PBTIME 301
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 259
      * PBTIME 244
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 311 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 118
GLPKFormulation: Number of values: 41
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
+ Total optimization time = 3096 milliseconds
```

### Optimized
```java
22:41:20.762 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 256

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 547
      * PEG2PEGTIME 425
      * PBTIME 300
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 275
      * PEG2PEGTIME 267
      * PBTIME 246
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 432 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 212
GLPKFormulation: Number of values: 55
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
+ Total optimization time = 4203 milliseconds
```

### Optimized
```java
22:41:27.574 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 512

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 555
      * PEG2PEGTIME 436
      * PBTIME 300
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 261
      * PBTIME 245
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 784 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 406
GLPKFormulation: Number of values: 80
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
+ Total optimization time = 6582 milliseconds
```

### Optimized
```java
22:41:36.758 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 1024

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 435
      * PBTIME 298
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 263
      * PBTIME 248
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1358 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 758
GLPKFormulation: Number of values: 107
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
+ Total optimization time = 10959 milliseconds
```

### Optimized
```java
22:41:50.352 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 2048

### Peggy output
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void <init>()> SUCCESSFUL
      * Optimization took 554
      * PEG2PEGTIME 435
      * PBTIME 301
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 262
      * PBTIME 247
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteLoop: void main(java.lang.String[])>
   - Processing method <InfiniteLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 5524 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1550
GLPKFormulation: Number of values: 202
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
+ Total optimization time = 23537 milliseconds
```

### Optimized
```java
22:42:16.506 INFO  jd.cli.Main - Decompiling optimized/InfiniteLoop.class
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

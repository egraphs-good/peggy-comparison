# InfiniteEffectfulLoop
## Original
```java
public class InfiniteEffectfulLoop {
    public static void main(String[] args) {
        original();
    }

     public static int original() {
        int j = 0;
        for (int i = 5; i == 5; ) {
            System.out.println(j);
        }
        j = 2;
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 435
      * PBTIME 302
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 260
      * PBTIME 245
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 19
GLPKFormulation: Number of values: 19
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 1684 milliseconds
```

### Optimized
```java
22:36:59.183 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 431
      * PBTIME 303
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 270
      * PEG2PEGTIME 262
      * PBTIME 248
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 6 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 18
GLPKFormulation: Number of values: 18
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 1638 milliseconds
```

### Optimized
```java
22:37:03.428 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 427
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 279
      * PEG2PEGTIME 263
      * PBTIME 245
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 7 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 18
GLPKFormulation: Number of values: 18
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 1678 milliseconds
```

### Optimized
```java
22:37:07.711 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 437
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 272
      * PEG2PEGTIME 264
      * PBTIME 248
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 11 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 21
GLPKFormulation: Number of values: 20
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 1707 milliseconds
```

### Optimized
```java
22:37:12.030 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 431
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 270
      * PEG2PEGTIME 263
      * PBTIME 246
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 23 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 30
GLPKFormulation: Number of values: 33
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 1845 milliseconds
```

### Optimized
```java
22:37:16.428 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 437
      * PBTIME 305
      * ENGINETIME 11
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 281
      * PEG2PEGTIME 272
      * PBTIME 247
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 53 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 53
GLPKFormulation: Number of values: 43
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 2151 milliseconds
```

### Optimized
```java
22:37:21.237 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 428
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 261
      * PBTIME 246
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 114 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 116
GLPKFormulation: Number of values: 90
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 3225 milliseconds
```

### Optimized
```java
22:37:27.049 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 431
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 263
      * PBTIME 248
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 186 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 240
GLPKFormulation: Number of values: 179
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 4846 milliseconds
```

### Optimized
```java
22:37:34.476 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 430
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 265
      * PBTIME 249
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 338 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 555
GLPKFormulation: Number of values: 402
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 9718 milliseconds
```

### Optimized
```java
22:37:46.767 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 554
      * PEG2PEGTIME 432
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 296
      * PEG2PEGTIME 287
      * PBTIME 272
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 593 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1061
GLPKFormulation: Number of values: 728
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 15836 milliseconds
```

### Optimized
```java
22:38:05.237 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
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
+ Loading class file InfiniteEffectfulLoop
+ Optimizing class InfiniteEffectfulLoop
   - Processing method <InfiniteEffectfulLoop: void <init>()>
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 430
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 281
      * PEG2PEGTIME 271
      * PBTIME 254
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 1714 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2647
GLPKFormulation: Number of values: 1934
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <InfiniteEffectfulLoop: int original()> [
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
      * Optimization of method <InfiniteEffectfulLoop: int original()> FAILED
+ Done optimizing InfiniteEffectfulLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 1
   - Total methods = 3
+ Fixing bytecode of method <InfiniteEffectfulLoop: void <init>()>
+ Fixing bytecode of method <InfiniteEffectfulLoop: void main(java.lang.String[])>
+ Fixing bytecode of method <InfiniteEffectfulLoop: int original()>
+ Writing class back to optimized/InfiniteEffectfulLoop.class
+ Total optimization time = 43022 milliseconds
```

### Optimized
```java
22:38:50.875 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
import java.io.PrintStream;

public class InfiniteEffectfulLoop
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
      System.out.println(i);
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     InfiniteEffectfulLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

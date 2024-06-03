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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2

Peggy output
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 545
      * PEG2PEGTIME 453
      * PBTIME 365
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 363
      * PEG2PEGTIME 355
      * PBTIME 344
      * ENGINETIME 0
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
+ Total optimization time = 1602 milliseconds
```

```java
19:36:11.874 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 4

Peggy output
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
      * Engine reached iteration bound of 4 after 2 milliseconds
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 472
      * PEG2PEGTIME 390
      * PBTIME 280
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 256
      * PEG2PEGTIME 248
      * PBTIME 234
      * ENGINETIME 0
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 1373 milliseconds
```

```java
19:36:15.248 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 8

Peggy output
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
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 530
      * PEG2PEGTIME 444
      * PBTIME 362
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void <init>()>
   - Processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
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
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 242
      * PBTIME 230
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 1429 milliseconds
```

```java
19:36:18.520 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 16

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 438
      * PEG2PEGTIME 359
      * PBTIME 275
      * ENGINETIME 6
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 244
      * PBTIME 233
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 5 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 1346 milliseconds
```

```java
19:36:21.740 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 32

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 461
      * PBTIME 373
      * ENGINETIME 6
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 282
      * PEG2PEGTIME 273
      * PBTIME 247
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 15 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 1826 milliseconds
```

```java
19:36:25.555 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 64

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 484
      * PEG2PEGTIME 358
      * PBTIME 270
      * ENGINETIME 5
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 242
      * PBTIME 233
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 35 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 1671 milliseconds
```

```java
19:36:29.083 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 128

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 451
      * PEG2PEGTIME 370
      * PBTIME 281
      * ENGINETIME 6
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 258
      * PEG2PEGTIME 252
      * PBTIME 241
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 75 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 2794 milliseconds
```

```java
19:36:33.972 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 256

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 487
      * PEG2PEGTIME 407
      * PBTIME 319
      * ENGINETIME 5
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 256
      * PEG2PEGTIME 248
      * PBTIME 237
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 157 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 3837 milliseconds
```

```java
19:36:39.758 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 512

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 451
      * PEG2PEGTIME 371
      * PBTIME 282
      * ENGINETIME 4
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 256
      * PEG2PEGTIME 246
      * PBTIME 237
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 346 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 7196 milliseconds
```

```java
19:36:48.978 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 1024

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 438
      * PEG2PEGTIME 358
      * PBTIME 274
      * ENGINETIME 4
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 244
      * PBTIME 234
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 396 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 11325 milliseconds
```

```java
19:37:02.300 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml, optimization_level: O2, tmp_folder: tmp, pb: glpk, eto: 2048

Peggy output
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void <init>()> SUCCESSFUL
      * Optimization took 438
      * PEG2PEGTIME 357
      * PBTIME 275
      * ENGINETIME 5
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <InfiniteEffectfulLoop: void main(java.lang.String[])> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 246
      * PBTIME 235
      * ENGINETIME 1
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <InfiniteEffectfulLoop: void main(java.lang.String[])>
   - Processing method <InfiniteEffectfulLoop: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 1263 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
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
+ Total optimization time = 29893 milliseconds
```

```java
19:37:33.995 INFO  jd.cli.Main - Decompiling optimized/InfiniteEffectfulLoop.class
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

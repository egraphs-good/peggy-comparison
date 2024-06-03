# LoopUnroll
## Original
```java
public class LoopUnroll {
    public int original(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public int expected(int n) {
        return 1;
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 465
      * PEG2PEGTIME 378
      * PBTIME 283
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 312
      * PEG2PEGTIME 289
      * PBTIME 267
      * ENGINETIME 1
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 251
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1334 milliseconds
```

```java
19:31:40.651 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 480
      * PEG2PEGTIME 394
      * PBTIME 289
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 361
      * PEG2PEGTIME 331
      * PBTIME 311
      * ENGINETIME 1
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 276
      * PEG2PEGTIME 259
      * PBTIME 223
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1448 milliseconds
```

```java
19:31:44.472 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 585
      * PEG2PEGTIME 497
      * PBTIME 396
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 12 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 459
      * PEG2PEGTIME 428
      * PBTIME 354
      * ENGINETIME 12
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 248
      * PBTIME 236
      * ENGINETIME 1
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1617 milliseconds
```

```java
19:31:48.028 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 385
      * PBTIME 286
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 21 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 511
      * PEG2PEGTIME 451
      * PBTIME 409
      * ENGINETIME 21
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 240
      * PBTIME 227
      * ENGINETIME 0
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1519 milliseconds
```

```java
19:31:51.557 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 474
      * PEG2PEGTIME 370
      * PBTIME 280
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 34 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 611
      * PEG2PEGTIME 576
      * PBTIME 525
      * ENGINETIME 34
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 241
      * PBTIME 227
      * ENGINETIME 1
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1678 milliseconds
```

```java
19:31:55.240 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 539
      * PEG2PEGTIME 449
      * PBTIME 358
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 69 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 773
      * PEG2PEGTIME 749
      * PBTIME 656
      * ENGINETIME 69
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 237
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 1848 milliseconds
```

```java
19:31:59.021 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 572
      * PEG2PEGTIME 491
      * PBTIME 280
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 114 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 1227
      * PEG2PEGTIME 1202
      * PBTIME 1067
      * ENGINETIME 115
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 239
      * PBTIME 226
      * ENGINETIME 0
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 2322 milliseconds
```

```java
19:32:03.776 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 542
      * PEG2PEGTIME 460
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 2325
      * PEG2PEGTIME 2288
      * PBTIME 1990
      * ENGINETIME 277
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 462
      * PBTIME 424
      * ENGINETIME 5
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 3646 milliseconds
```

```java
19:32:09.236 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 440
      * PEG2PEGTIME 362
      * PBTIME 269
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 607 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 3976
      * PEG2PEGTIME 3950
      * PBTIME 3317
      * ENGINETIME 608
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 256
      * PEG2PEGTIME 247
      * PBTIME 231
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 4950 milliseconds
```

```java
19:32:16.148 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 508
      * PEG2PEGTIME 375
      * PBTIME 284
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 947 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 6761
      * PEG2PEGTIME 6728
      * PBTIME 5756
      * ENGINETIME 947
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 262
      * PEG2PEGTIME 257
      * PBTIME 244
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 7815 milliseconds
```

```java
19:32:25.709 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
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
+ Loading class file LoopUnroll
+ Optimizing class LoopUnroll
   - Processing method <LoopUnroll: void <init>()>
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
      * Optimization of method <LoopUnroll: void <init>()> SUCCESSFUL
      * Optimization took 453
      * PEG2PEGTIME 371
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopUnroll: void <init>()>
   - Processing method <LoopUnroll: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2394 milliseconds
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
      * Optimization of method <LoopUnroll: int original(int)> SUCCESSFUL
      * Optimization took 13556
      * PEG2PEGTIME 13529
      * PBTIME 11109
      * ENGINETIME 2395
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopUnroll: int original(int)>
   - Processing method <LoopUnroll: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopUnroll: int expected(int)> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 252
      * PBTIME 237
      * ENGINETIME 2
   - Done processing method <LoopUnroll: int expected(int)>
+ Done optimizing LoopUnroll
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopUnroll: void <init>()>
+ Fixing bytecode of method <LoopUnroll: int original(int)>
+ Fixing bytecode of method <LoopUnroll: int expected(int)>
+ Writing class back to optimized/LoopUnroll.class
+ Total optimization time = 14542 milliseconds
```

```java
19:32:42.189 INFO  jd.cli.Main - Decompiling optimized/LoopUnroll.class
public class LoopUnroll
{
  public int original(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return 1;
  }
}

/* Location:
 * Qualified Name:     LoopUnroll
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

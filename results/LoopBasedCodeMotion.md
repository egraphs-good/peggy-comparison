# LoopBasedCodeMotion
## Original
```java
public class LoopBasedCodeMotion {
    public int original() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 500;
    }

    public int expected() {
        int x = 0;
        while (x < 3) {
            x += 5;
        }
        return x;
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 604
      * PEG2PEGTIME 490
      * PBTIME 347
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 545
      * PEG2PEGTIME 501
      * PBTIME 476
      * ENGINETIME 0
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 306
      * PEG2PEGTIME 298
      * PBTIME 284
      * ENGINETIME 0
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 1882 milliseconds
```

```java
19:30:11.088 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 482
      * PEG2PEGTIME 386
      * PBTIME 283
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 331
      * PEG2PEGTIME 301
      * PBTIME 277
      * ENGINETIME 1
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 281
      * PEG2PEGTIME 272
      * PBTIME 259
      * ENGINETIME 0
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 1483 milliseconds
```

```java
19:30:14.337 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 3 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 465
      * PEG2PEGTIME 372
      * PBTIME 282
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 9 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 396
      * PEG2PEGTIME 370
      * PBTIME 344
      * ENGINETIME 9
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 322
      * PEG2PEGTIME 312
      * PBTIME 299
      * ENGINETIME 2
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 1570 milliseconds
```

```java
19:30:18.098 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; this < 3; this = 1 + this) {}
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 463
      * PBTIME 280
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 13 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 446
      * PBTIME 412
      * ENGINETIME 14
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 9 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 406
      * PEG2PEGTIME 395
      * PBTIME 373
      * ENGINETIME 9
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 1752 milliseconds
```

```java
19:30:22.032 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (3 > this) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 477
      * PEG2PEGTIME 385
      * PBTIME 280
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 29 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 717
      * PEG2PEGTIME 692
      * PBTIME 642
      * ENGINETIME 29
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 20 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 527
      * PEG2PEGTIME 518
      * PBTIME 486
      * ENGINETIME 20
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 1994 milliseconds
```

```java
19:30:25.807 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    for (this = 0; 3 > this; this = 5 + this) {}
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 516
      * PEG2PEGTIME 413
      * PBTIME 294
      * ENGINETIME 13
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 56 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 926
      * PEG2PEGTIME 900
      * PBTIME 821
      * ENGINETIME 57
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 32 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 734
      * PEG2PEGTIME 711
      * PBTIME 667
      * ENGINETIME 33
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 2536 milliseconds
```

```java
19:30:30.208 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return 500 * this;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 618
      * PEG2PEGTIME 431
      * PBTIME 320
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 231 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 1497
      * PEG2PEGTIME 1471
      * PBTIME 1213
      * ENGINETIME 232
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 95 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 1212
      * PEG2PEGTIME 1204
      * PBTIME 1088
      * ENGINETIME 96
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 3624 milliseconds
```

```java
19:30:35.858 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; this < 3; this = 1 + this) {}
    return 500 * this;
  }
  
  public int expected()
  {
    for (this = 0; this < 3; this = 5 + this) {}
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 584
      * PEG2PEGTIME 453
      * PBTIME 308
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 346 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 2393
      * PEG2PEGTIME 2362
      * PBTIME 1981
      * ENGINETIME 347
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 319 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 2064
      * PEG2PEGTIME 2056
      * PBTIME 1685
      * ENGINETIME 320
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 5456 milliseconds
```

```java
19:30:43.535 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 520
      * PEG2PEGTIME 440
      * PBTIME 341
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 487 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 3665
      * PEG2PEGTIME 3641
      * PBTIME 3131
      * ENGINETIME 488
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 263 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 3503
      * PEG2PEGTIME 3496
      * PBTIME 3216
      * ENGINETIME 263
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 7988 milliseconds
```

```java
19:30:53.297 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; 3 > this; this = 1 + this) {}
    return 500 * this;
  }
  
  public int expected()
  {
    for (this = 0; this < 3; this = 5 + this) {}
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 459
      * PEG2PEGTIME 379
      * PBTIME 276
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 755 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 6020
      * PEG2PEGTIME 5983
      * PBTIME 5207
      * ENGINETIME 755
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 594 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 6200
      * PEG2PEGTIME 6169
      * PBTIME 5557
      * ENGINETIME 595
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 13023 milliseconds
```

```java
19:31:08.444 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 485
      * PEG2PEGTIME 398
      * PBTIME 304
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 1479 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 11107
      * PEG2PEGTIME 11079
      * PBTIME 9582
      * ENGINETIME 1479
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2691 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 14212
      * PEG2PEGTIME 14201
      * PBTIME 11478
      * ENGINETIME 2694
      * Optimization ratio 190/190 = 1.0
      * PEG-based Optimization ratio 190/190 = 1.0
   - Done processing method <LoopBasedCodeMotion: int expected()>
+ Done optimizing LoopBasedCodeMotion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <LoopBasedCodeMotion: void <init>()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int original()>
+ Fixing bytecode of method <LoopBasedCodeMotion: int expected()>
+ Writing class back to optimized/LoopBasedCodeMotion.class
+ Total optimization time = 26102 milliseconds
```

```java
19:31:36.505 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (this < 3) {
      this += 1;
    }
    return this * 500;
  }
  
  public int expected()
  {
    this = 0;
    while (this < 3) {
      this += 5;
    }
    return this;
  }
}

/* Location:
 * Qualified Name:     LoopBasedCodeMotion
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

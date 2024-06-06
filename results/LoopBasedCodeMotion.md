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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 565
      * PEG2PEGTIME 443
      * PBTIME 298
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 14
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 366
      * PEG2PEGTIME 332
      * PBTIME 303
      * ENGINETIME 1
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 12
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 321
      * PEG2PEGTIME 309
      * PBTIME 290
      * ENGINETIME 1
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
+ Total optimization time = 1672 milliseconds
```

### Optimized
```java
22:43:07.548 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 566
      * PEG2PEGTIME 441
      * PBTIME 301
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 14
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 374
      * PEG2PEGTIME 343
      * PBTIME 311
      * ENGINETIME 1
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 328
      * PEG2PEGTIME 317
      * PBTIME 298
      * ENGINETIME 1
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
+ Total optimization time = 1724 milliseconds
```

### Optimized
```java
22:43:11.928 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
+ Loading class file LoopBasedCodeMotion
+ Optimizing class LoopBasedCodeMotion
   - Processing method <LoopBasedCodeMotion: void <init>()>
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 550
      * PEG2PEGTIME 429
      * PBTIME 297
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 14 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 22
GLPKFormulation: Number of values: 18
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 475
      * PEG2PEGTIME 444
      * PBTIME 397
      * ENGINETIME 15
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 5 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 16
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 411
      * PEG2PEGTIME 404
      * PBTIME 381
      * ENGINETIME 5
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
+ Total optimization time = 1871 milliseconds
```

### Optimized
```java
22:43:16.361 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; 3 > this; this = 1 + this) {}
    return this * 500;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 572
      * PEG2PEGTIME 438
      * PBTIME 300
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 23 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 26
GLPKFormulation: Number of values: 19
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 530
      * PEG2PEGTIME 497
      * PBTIME 445
      * ENGINETIME 23
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 13 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 26
GLPKFormulation: Number of values: 18
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 491
      * PEG2PEGTIME 474
      * PBTIME 442
      * ENGINETIME 13
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
+ Total optimization time = 2013 milliseconds
```

### Optimized
```java
22:43:20.965 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 440
      * PBTIME 300
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 48 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 804
      * PEG2PEGTIME 772
      * PBTIME 692
      * ENGINETIME 48
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 26 milliseconds
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
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 659
      * PEG2PEGTIME 646
      * PBTIME 602
      * ENGINETIME 26
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
+ Total optimization time = 2440 milliseconds
```

### Optimized
```java
22:43:25.991 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 617
      * PEG2PEGTIME 459
      * PBTIME 303
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 87 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 72
GLPKFormulation: Number of values: 34
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 1100
      * PEG2PEGTIME 1066
      * PBTIME 944
      * ENGINETIME 87
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 53 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 70
GLPKFormulation: Number of values: 35
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 993
      * PEG2PEGTIME 985
      * PBTIME 912
      * ENGINETIME 53
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
+ Total optimization time = 3135 milliseconds
```

### Optimized
```java
22:43:31.732 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    this = 0;
    while (3 > this) {
      this += 1;
    }
    return this * 500;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 568
      * PEG2PEGTIME 443
      * PBTIME 300
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 174 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 129
GLPKFormulation: Number of values: 50
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 1833
      * PEG2PEGTIME 1792
      * PBTIME 1581
      * ENGINETIME 174
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 123 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 116
GLPKFormulation: Number of values: 46
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 1594
      * PEG2PEGTIME 1583
      * PBTIME 1441
      * ENGINETIME 123
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
+ Total optimization time = 4416 milliseconds
```

### Optimized
```java
22:43:38.734 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; this < 3; this = 1 + this) {}
    return 500 * this;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 573
      * PEG2PEGTIME 450
      * PBTIME 309
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 357 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 235
GLPKFormulation: Number of values: 79
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 3177
      * PEG2PEGTIME 3145
      * PBTIME 2759
      * ENGINETIME 357
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 283 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 220
GLPKFormulation: Number of values: 71
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 2861
      * PEG2PEGTIME 2852
      * PBTIME 2548
      * ENGINETIME 284
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
+ Total optimization time = 7043 milliseconds
```

### Optimized
```java
22:43:48.362 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; this < 3; this = 1 + this) {}
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 571
      * PEG2PEGTIME 447
      * PBTIME 308
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 737 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 397
GLPKFormulation: Number of values: 105
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 5207
      * PEG2PEGTIME 5171
      * PBTIME 4406
      * ENGINETIME 737
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 381 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 422
GLPKFormulation: Number of values: 112
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 4987
      * PEG2PEGTIME 4977
      * PBTIME 4575
      * ENGINETIME 381
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
+ Total optimization time = 11185 milliseconds
```

### Optimized
```java
22:44:02.117 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; 3 > this; this = 1 + this) {}
    return 500 * this;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 433
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1058 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 685
GLPKFormulation: Number of values: 127
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 8585
      * PEG2PEGTIME 8551
      * PBTIME 7463
      * ENGINETIME 1058
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 963 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 745
GLPKFormulation: Number of values: 127
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 8871
      * PEG2PEGTIME 8861
      * PBTIME 7877
      * ENGINETIME 963
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
+ Total optimization time = 18440 milliseconds
```

### Optimized
```java
22:44:23.161 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 563
      * PEG2PEGTIME 442
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2180 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1269
GLPKFormulation: Number of values: 176
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 16055
      * PEG2PEGTIME 16017
      * PBTIME 13807
      * ENGINETIME 2180
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 3354 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1439
GLPKFormulation: Number of values: 180
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 19009
      * PEG2PEGTIME 18997
      * PBTIME 15616
      * ENGINETIME 3357
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
+ Total optimization time = 36082 milliseconds
```

### Optimized
```java
22:45:01.892 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; 3 > this; this = 1 + this) {}
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

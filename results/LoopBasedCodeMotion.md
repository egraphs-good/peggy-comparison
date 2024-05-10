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
      * Engine reached iteration bound of 2 after 7 milliseconds
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
      * Optimization took 1540
      * PEG2PEGTIME 1251
      * PBTIME 815
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
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
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 779
      * PEG2PEGTIME 698
      * PBTIME 636
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 624
      * PEG2PEGTIME 566
      * PBTIME 525
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
+ Total optimization time = 4323 milliseconds
```

```java
21:18:53.402 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Engine reached iteration bound of 4 after 8 milliseconds
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
      * Optimization took 990
      * PEG2PEGTIME 799
      * PBTIME 477
      * ENGINETIME 8
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int original()> SUCCESSFUL
      * Optimization took 679
      * PEG2PEGTIME 622
      * PBTIME 560
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
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 496
      * PEG2PEGTIME 475
      * PBTIME 440
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
+ Total optimization time = 3152 milliseconds
```

```java
21:19:01.699 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization of method <LoopBasedCodeMotion: void <init>()> SUCCESSFUL
      * Optimization took 1005
      * PEG2PEGTIME 802
      * PBTIME 546
      * ENGINETIME 13
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 22 milliseconds
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
      * Optimization took 826
      * PEG2PEGTIME 772
      * PBTIME 688
      * ENGINETIME 23
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <LoopBasedCodeMotion: int expected()> SUCCESSFUL
      * Optimization took 643
      * PEG2PEGTIME 618
      * PBTIME 568
      * ENGINETIME 8
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
+ Total optimization time = 3260 milliseconds
```

```java
21:19:09.313 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization took 979
      * PEG2PEGTIME 782
      * PBTIME 508
      * ENGINETIME 31
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 37 milliseconds
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
      * Optimization took 819
      * PEG2PEGTIME 762
      * PBTIME 654
      * ENGINETIME 37
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 19 milliseconds
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
      * Optimization took 726
      * PEG2PEGTIME 703
      * PBTIME 649
      * ENGINETIME 19
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
+ Total optimization time = 3384 milliseconds
```

```java
21:19:17.195 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization took 1364
      * PEG2PEGTIME 1097
      * PBTIME 605
      * ENGINETIME 13
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 87 milliseconds
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
      * Optimization took 1329
      * PEG2PEGTIME 1253
      * PBTIME 1112
      * ENGINETIME 87
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 57 milliseconds
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
      * Optimization took 1172
      * PEG2PEGTIME 1152
      * PBTIME 1060
      * ENGINETIME 58
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
+ Total optimization time = 4721 milliseconds
```

```java
21:19:26.205 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; this < 3; this = 1 + this) {}
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
      * Optimization took 1914
      * PEG2PEGTIME 1638
      * PBTIME 1321
      * ENGINETIME 41
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 171 milliseconds
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
      * Optimization took 2165
      * PEG2PEGTIME 2083
      * PBTIME 1849
      * ENGINETIME 172
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 215 milliseconds
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
      * Optimization took 2143
      * PEG2PEGTIME 2079
      * PBTIME 1815
      * ENGINETIME 215
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
+ Total optimization time = 7197 milliseconds
```

```java
21:19:37.918 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
public class LoopBasedCodeMotion
{
  public int original()
  {
    for (this = 0; 3 > this; this = 1 + this) {}
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
      * Optimization took 2018
      * PEG2PEGTIME 1665
      * PBTIME 749
      * ENGINETIME 24
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 372 milliseconds
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
      * Optimization took 3529
      * PEG2PEGTIME 3459
      * PBTIME 2974
      * ENGINETIME 373
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 228 milliseconds
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
      * Optimization took 2867
      * PEG2PEGTIME 2840
      * PBTIME 2549
      * ENGINETIME 228
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
+ Total optimization time = 9911 milliseconds
```

```java
21:19:55.958 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization took 1418
      * PEG2PEGTIME 1113
      * PBTIME 692
      * ENGINETIME 14
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 972 milliseconds
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
      * Optimization took 6146
      * PEG2PEGTIME 6017
      * PBTIME 4911
      * ENGINETIME 972
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 714 milliseconds
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
      * Optimization took 5361
      * PEG2PEGTIME 5342
      * PBTIME 4575
      * ENGINETIME 715
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
+ Total optimization time = 13810 milliseconds
```

```java
21:20:15.397 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization took 1660
      * PEG2PEGTIME 1316
      * PBTIME 938
      * ENGINETIME 25
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 1854 milliseconds
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
      * Optimization took 17035
      * PEG2PEGTIME 16917
      * PBTIME 14984
      * ENGINETIME 1855
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 1160 milliseconds
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
      * Optimization took 12238
      * PEG2PEGTIME 12212
      * PBTIME 10932
      * ENGINETIME 1163
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
+ Total optimization time = 32151 milliseconds
```

```java
21:20:54.304 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization took 1007
      * PEG2PEGTIME 755
      * PBTIME 490
      * ENGINETIME 13
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 2773 milliseconds
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
      * Optimization took 19009
      * PEG2PEGTIME 18933
      * PBTIME 16079
      * ENGINETIME 2774
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 2645 milliseconds
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
      * Optimization took 15739
      * PEG2PEGTIME 15718
      * PBTIME 13021
      * ENGINETIME 2646
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
+ Total optimization time = 37361 milliseconds
```

```java
21:21:37.816 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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
      * Optimization took 1647
      * PEG2PEGTIME 1347
      * PBTIME 928
      * ENGINETIME 40
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <LoopBasedCodeMotion: void <init>()>
   - Processing method <LoopBasedCodeMotion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 5874 milliseconds
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
      * Optimization took 42562
      * PEG2PEGTIME 42467
      * PBTIME 36528
      * ENGINETIME 5875
      * Optimization ratio 210/210 = 1.0
      * PEG-based Optimization ratio 210/210 = 1.0
   - Done processing method <LoopBasedCodeMotion: int original()>
   - Processing method <LoopBasedCodeMotion: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 11632 milliseconds
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
      * Optimization took 64116
      * PEG2PEGTIME 64079
      * PBTIME 52392
      * ENGINETIME 11636
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
+ Total optimization time = 109657 milliseconds
```

```java
21:23:35.274 INFO  jd.cli.Main - Decompiling optimized/LoopBasedCodeMotion.class
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

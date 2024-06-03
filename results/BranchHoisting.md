# BranchHoisting
## Original
```java
public class BranchHoisting {
    public int original(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            if (n == 0) {
               x = y * 2;
            } else {
                x = y * 3;
            }
            y++;
        }
        return x;
    }

    public int expected(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) y++;

        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 495
      * PEG2PEGTIME 399
      * PBTIME 288
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 398
      * PEG2PEGTIME 361
      * PBTIME 331
      * ENGINETIME 0
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 452
      * PEG2PEGTIME 439
      * PBTIME 429
      * ENGINETIME 0
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1678 milliseconds
```

```java
19:38:13.067 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (paramInt = j * 2;; paramInt = j * 3) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 447
      * PEG2PEGTIME 366
      * PBTIME 280
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 381
      * PEG2PEGTIME 346
      * PBTIME 317
      * ENGINETIME 1
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 329
      * PEG2PEGTIME 319
      * PBTIME 311
      * ENGINETIME 0
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1441 milliseconds
```

```java
19:38:16.345 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (paramInt = j * 2;; paramInt = j * 3) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 5 milliseconds
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 437
      * PEG2PEGTIME 359
      * PBTIME 276
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 7 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 496
      * PEG2PEGTIME 459
      * PBTIME 428
      * ENGINETIME 7
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 374
      * PEG2PEGTIME 363
      * PBTIME 351
      * ENGINETIME 2
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1594 milliseconds
```

```java
19:38:19.616 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = 1 + i;
      if (this >= 500) {
        break;
      }
    }
    int k;
    if (paramInt == 0) {
      j *= 2;
    }
    for (;;)
    {
      return k;
      k *= 3;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; this < 500; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 439
      * PEG2PEGTIME 359
      * PBTIME 270
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 10 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 525
      * PEG2PEGTIME 486
      * PBTIME 454
      * ENGINETIME 10
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 6 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 466
      * PEG2PEGTIME 437
      * PBTIME 421
      * ENGINETIME 6
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1730 milliseconds
```

```java
19:38:23.107 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (this = 0;; this = localBranchHoisting)
    {
      localBranchHoisting = 1 + this;
      if (localBranchHoisting >= 500) {
        break;
      }
    }
    if (paramInt == 0) {}
    for (paramInt = 2 * this;; paramInt = this * 3) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; 500 > this; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = 3 * this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 522
      * PEG2PEGTIME 428
      * PBTIME 275
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 31 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 661
      * PEG2PEGTIME 625
      * PBTIME 570
      * ENGINETIME 31
      * Optimization ratio 695/695 = 1.0
      * PEG-based Optimization ratio 695/695 = 1.0
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 13 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 622
      * PEG2PEGTIME 609
      * PBTIME 586
      * ENGINETIME 13
      * Optimization ratio 235/235 = 1.0
      * PEG-based Optimization ratio 235/235 = 1.0
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 2106 milliseconds
```

```java
19:38:27.096 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = 1 + i;
      if (this >= 500) {
        break;
      }
    }
    int k;
    if (paramInt == 0) {
      j *= 2;
    }
    for (;;)
    {
      return k;
      k *= 3;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (500 > this) {
      this += 1;
    }
    if (paramInt == 0) {}
    for (paramInt = this * 2;; paramInt = this * 3) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 462
      * PEG2PEGTIME 374
      * PBTIME 272
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 66 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 1069
      * PEG2PEGTIME 1031
      * PBTIME 941
      * ENGINETIME 66
      * Optimization ratio 575/695 = 0.8273381294964028
      * PEG-based Optimization ratio 575/695 = 0.8273381294964028
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 1063
      * PEG2PEGTIME 1046
      * PBTIME 1002
      * ENGINETIME 32
      * Optimization ratio 219/235 = 0.9319148936170213
      * PEG-based Optimization ratio 219/235 = 0.9319148936170213
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 2985 milliseconds
```

```java
19:38:32.155 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    if (paramInt == 0)
    {
      this = 0;
      for (paramInt = 0;; paramInt = 2 + paramInt)
      {
        this += 1;
        if (500 <= this) {
          break;
        }
        this = this;
      }
    }
    for (paramInt = paramInt;; paramInt = paramInt)
    {
      return paramInt;
      this = 0;
      paramInt = 0;
      for (;;)
      {
        this += 1;
        if (500 <= this) {
          break;
        }
        this = this;
        paramInt += 3;
      }
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; 500 > this; this = 1 + this) {}
    if (paramInt == 0) {}
    for (paramInt = this + this;; paramInt = 3 * this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 453
      * PEG2PEGTIME 373
      * PBTIME 284
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 131 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 1869
      * PEG2PEGTIME 1840
      * PBTIME 1688
      * ENGINETIME 131
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 71 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 1789
      * PEG2PEGTIME 1773
      * PBTIME 1693
      * ENGINETIME 71
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 4429 milliseconds
```

```java
19:38:38.430 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = i + 1;
      if (this >= 500) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    BranchHoisting localBranchHoisting = 0;
    while (500 > localBranchHoisting) {
      localBranchHoisting += 1;
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 450
      * PEG2PEGTIME 368
      * PBTIME 270
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 242 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 4033
      * PEG2PEGTIME 4003
      * PBTIME 3737
      * ENGINETIME 242
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 150 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 3720
      * PEG2PEGTIME 3703
      * PBTIME 3541
      * ENGINETIME 151
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 8507 milliseconds
```

```java
19:38:48.801 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = 1 + i;
      if (500 <= this) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 445
      * PEG2PEGTIME 362
      * PBTIME 267
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 522 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 18298
      * PEG2PEGTIME 18258
      * PBTIME 17713
      * ENGINETIME 522
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 216 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 7361
      * PEG2PEGTIME 7349
      * PBTIME 7104
      * ENGINETIME 216
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 26415 milliseconds
```

```java
19:39:17.134 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = 1 + i;
      if (500 <= this) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (this < 500) {
      this += 1;
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 507
      * PEG2PEGTIME 381
      * PBTIME 283
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 877 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 19883
      * PEG2PEGTIME 19838
      * PBTIME 18935
      * ENGINETIME 877
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 615 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 1016772
      * PEG2PEGTIME 1016751
      * PBTIME 1016121
      * ENGINETIME 615
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 1037500 milliseconds
```

```java
19:56:36.409 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    BranchHoisting localBranchHoisting;
    for (int i = 0;; localBranchHoisting = this)
    {
      this = 1 + i;
      if (this >= 500) {
        break;
      }
    }
    this = localBranchHoisting + localBranchHoisting;
    if (paramInt == 0) {}
    for (paramInt = this;; paramInt = localBranchHoisting + this) {
      return paramInt;
    }
  }
  
  public int expected(int paramInt)
  {
    for (this = 0; this < 500; this = 1 + this) {}
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    int i;
    for (localBranchHoisting = localBranchHoisting;; i = this + localBranchHoisting) {
      return localBranchHoisting;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
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
+ Loading class file BranchHoisting
+ Optimizing class BranchHoisting
   - Processing method <BranchHoisting: void <init>()>
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
      * Optimization of method <BranchHoisting: void <init>()> SUCCESSFUL
      * Optimization took 523
      * PEG2PEGTIME 437
      * PBTIME 281
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <BranchHoisting: void <init>()>
   - Processing method <BranchHoisting: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2959 milliseconds
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
      * Optimization of method <BranchHoisting: int original(int)> SUCCESSFUL
      * Optimization took 44719
      * PEG2PEGTIME 44676
      * PBTIME 41689
      * ENGINETIME 2959
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <BranchHoisting: int original(int)>
   - Processing method <BranchHoisting: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 1254 milliseconds
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
      * Optimization of method <BranchHoisting: int expected(int)> SUCCESSFUL
      * Optimization took 37084
      * PEG2PEGTIME 37069
      * PBTIME 35794
      * ENGINETIME 1254
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <BranchHoisting: int expected(int)>
+ Done optimizing BranchHoisting
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <BranchHoisting: void <init>()>
+ Fixing bytecode of method <BranchHoisting: int original(int)>
+ Fixing bytecode of method <BranchHoisting: int expected(int)>
+ Writing class back to optimized/BranchHoisting.class
+ Total optimization time = 82746 milliseconds
```

```java
19:58:01.185 INFO  jd.cli.Main - Decompiling optimized/BranchHoisting.class
public class BranchHoisting
{
  public int original(int paramInt)
  {
    int j;
    for (int i = 0;; j = this)
    {
      this = 1 + i;
      if (this >= 500) {
        break;
      }
    }
    this = j + j;
    if (paramInt == 0) {}
    int k;
    for (BranchHoisting localBranchHoisting = this;; k = this + localBranchHoisting) {
      return localBranchHoisting;
    }
  }
  
  public int expected(int paramInt)
  {
    this = 0;
    while (500 > this) {
      this += 1;
    }
    BranchHoisting localBranchHoisting = this + this;
    if (paramInt == 0) {}
    for (paramInt = localBranchHoisting;; paramInt = this + localBranchHoisting) {
      return paramInt;
    }
  }
}

/* Location:
 * Qualified Name:     BranchHoisting
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

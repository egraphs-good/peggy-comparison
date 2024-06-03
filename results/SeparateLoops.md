# SeparateLoops
## Original
```java
public class SeparateLoops {
    public int interrelated(int n) {
        int accum = 0;
        for (int i = 0; i < n; i++) {
            accum += i;
        }

        for (int j = 0; j < n + 1; j++) {
            accum += j;
        }

        return accum;
    }

    public void unrelated(int n) {
        int accumi = 0;
        for (int i = 0; i < n; i++) {
            accumi += i;
        }
        System.out.println(accumi);

        int accumj = 0;
        for (int j = 0; j < n; j++) {
            accumj += j;
        }
        System.out.println(accumj);
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 2 milliseconds
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 464
      * PEG2PEGTIME 376
      * PBTIME 285
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 394
      * PEG2PEGTIME 365
      * PBTIME 336
      * ENGINETIME 0
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 394
      * PEG2PEGTIME 378
      * PBTIME 364
      * ENGINETIME 1
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 1606 milliseconds
```

```java
19:32:46.493 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    SeparateLoops localSeparateLoops1 = 0;
    while (this < paramInt)
    {
      this += 1;
      localSeparateLoops1 += this;
    }
    paramInt += 1;
    this = 0;
    SeparateLoops localSeparateLoops2 = localSeparateLoops2;
    int i;
    while (this < paramInt)
    {
      this += 1;
      localSeparateLoops2 += this;
    }
    return i;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this += i;
      i += 1;
    }
    System.out.println(this);
    System.out.println(this);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 449
      * PEG2PEGTIME 357
      * PBTIME 277
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 415
      * PEG2PEGTIME 379
      * PBTIME 350
      * ENGINETIME 0
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 401
      * PEG2PEGTIME 385
      * PBTIME 370
      * ENGINETIME 1
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 1579 milliseconds
```

```java
19:32:50.060 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    SeparateLoops localSeparateLoops1 = 0;
    while (this < paramInt)
    {
      this += 1;
      localSeparateLoops1 += this;
    }
    paramInt += 1;
    this = 0;
    SeparateLoops localSeparateLoops2 = localSeparateLoops2;
    int i;
    while (this < paramInt)
    {
      this += 1;
      localSeparateLoops2 += this;
    }
    return i;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this += i;
      i += 1;
    }
    System.out.println(this);
    System.out.println(this);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 505
      * PEG2PEGTIME 420
      * PBTIME 277
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 11 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 447
      * PEG2PEGTIME 418
      * PBTIME 379
      * ENGINETIME 11
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 401
      * PEG2PEGTIME 385
      * PBTIME 375
      * ENGINETIME 0
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 1660 milliseconds
```

```java
19:32:53.521 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      this += i;
    }
    paramInt += 1;
    this = this;
    for (i = 0; i < paramInt; i = 1 + i) {
      this += i;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    int i = 0;
    this = 0;
    while (i < paramInt)
    {
      i += 1;
      this += i;
    }
    System.out.println(this);
    System.out.println(this);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 449
      * PEG2PEGTIME 367
      * PBTIME 283
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 18 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 612
      * PEG2PEGTIME 578
      * PBTIME 535
      * ENGINETIME 19
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 490
      * PEG2PEGTIME 471
      * PBTIME 454
      * ENGINETIME 5
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 1888 milliseconds
```

```java
19:32:57.386 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    int i = 0;
    for (this = 0; i < paramInt; this = i + this) {
      i = 1 + i;
    }
    paramInt += 1;
    i = 0;
    this = this;
    while (i < paramInt)
    {
      i = 1 + i;
      this += i;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops = 0;
    this = 0;
    int i;
    while (paramInt > this)
    {
      i = this + localSeparateLoops;
      this += 1;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 458
      * PEG2PEGTIME 377
      * PBTIME 285
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 824
      * PEG2PEGTIME 789
      * PBTIME 735
      * ENGINETIME 31
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 11 milliseconds
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 530
      * PEG2PEGTIME 512
      * PBTIME 489
      * ENGINETIME 11
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 2144 milliseconds
```

```java
19:33:01.422 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    for (int i = 0; paramInt > i; i = 1 + i) {
      this += i;
    }
    i = 0;
    paramInt += 1;
    for (this = this; paramInt > i; this = i + this) {
      i = 1 + i;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    SeparateLoops localSeparateLoops = 0;
    int i;
    while (paramInt > this)
    {
      this = 1 + this;
      localSeparateLoops += this;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 445
      * PEG2PEGTIME 361
      * PBTIME 277
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
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
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 1245
      * PEG2PEGTIME 1214
      * PBTIME 1116
      * ENGINETIME 67
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 979
      * PEG2PEGTIME 961
      * PBTIME 908
      * ENGINETIME 33
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 2978 milliseconds
```

```java
19:33:06.252 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (paramInt > i)
    {
      this += i;
      i += 1;
    }
    this = this;
    paramInt = 1 + paramInt;
    i = 0;
    while (paramInt > i)
    {
      this += i;
      i += 1;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    SeparateLoops localSeparateLoops = 0;
    int i;
    while (paramInt > this)
    {
      this = 1 + this;
      localSeparateLoops += this;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 446
      * PEG2PEGTIME 365
      * PBTIME 283
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 220 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 2130
      * PEG2PEGTIME 2095
      * PBTIME 1848
      * ENGINETIME 220
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 78 milliseconds
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 1947
      * PEG2PEGTIME 1930
      * PBTIME 1839
      * ENGINETIME 78
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 4852 milliseconds
```

```java
19:33:12.909 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops1 = 0;
    this = 0;
    while (paramInt > this)
    {
      localSeparateLoops1 += this;
      this += 1;
    }
    paramInt = 1 + paramInt;
    this = 0;
    int i;
    for (SeparateLoops localSeparateLoops2 = localSeparateLoops2; paramInt > this; i = this + localSeparateLoops2) {
      this += 1;
    }
    return i;
  }
  
  public void unrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops = 0;
    int i;
    for (this = 0; paramInt > this; this = 1 + this) {
      i = this + localSeparateLoops;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 476
      * PEG2PEGTIME 389
      * PBTIME 304
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 300 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 4681
      * PEG2PEGTIME 4642
      * PBTIME 4312
      * ENGINETIME 300
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 149 milliseconds
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 4299
      * PEG2PEGTIME 4282
      * PBTIME 4115
      * ENGINETIME 149
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 9770 milliseconds
```

```java
19:33:24.468 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops1 = 0;
    this = 0;
    while (this < paramInt)
    {
      localSeparateLoops1 += this;
      this += 1;
    }
    SeparateLoops localSeparateLoops2 = localSeparateLoops2;
    paramInt = 1 + paramInt;
    this = 0;
    int i;
    while (paramInt > this)
    {
      localSeparateLoops2 += this;
      this += 1;
    }
    return i;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      this += i;
    }
    System.out.println(this);
    System.out.println(this);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 450
      * PEG2PEGTIME 369
      * PBTIME 280
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 477 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 8735
      * PEG2PEGTIME 8703
      * PBTIME 8198
      * ENGINETIME 477
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 265 milliseconds
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 8384
      * PEG2PEGTIME 8365
      * PBTIME 8086
      * ENGINETIME 265
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 17932 milliseconds
```

```java
19:33:44.284 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this = i + this;
      i += 1;
    }
    paramInt = 1 + paramInt;
    this = this;
    i = 0;
    while (i < paramInt)
    {
      this = i + this;
      i += 1;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops = 0;
    int i;
    for (this = 0; this < paramInt; this = 1 + this) {
      localSeparateLoops += this;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 525
      * PEG2PEGTIME 442
      * PBTIME 363
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 898 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 20371
      * PEG2PEGTIME 20339
      * PBTIME 19419
      * ENGINETIME 899
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1021 milliseconds
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 19145
      * PEG2PEGTIME 19065
      * PBTIME 18025
      * ENGINETIME 1021
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 40371 milliseconds
```

```java
19:34:26.587 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (paramInt > i)
    {
      this = i + this;
      i += 1;
    }
    this = this;
    i = 0;
    paramInt += 1;
    while (i < paramInt)
    {
      this += i;
      i += 1;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    SeparateLoops localSeparateLoops = 0;
    int i;
    while (paramInt > this)
    {
      this += 1;
      localSeparateLoops += this;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 471
      * PEG2PEGTIME 370
      * PBTIME 274
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 10039 milliseconds
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
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 49634
      * PEG2PEGTIME 49531
      * PBTIME 39440
      * ENGINETIME 10062
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 6226 milliseconds
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
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 48154
      * PEG2PEGTIME 48031
      * PBTIME 41785
      * ENGINETIME 6227
      * Optimization ratio 6530/6530 = 1.0
      * PEG-based Optimization ratio 6530/6530 = 1.0
   - Done processing method <SeparateLoops: void unrelated(int)>
+ Done optimizing SeparateLoops
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <SeparateLoops: void <init>()>
+ Fixing bytecode of method <SeparateLoops: int interrelated(int)>
+ Fixing bytecode of method <SeparateLoops: void unrelated(int)>
+ Writing class back to optimized/SeparateLoops.class
+ Total optimization time = 98697 milliseconds
```

```java
19:36:07.277 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops1 = 0;
    for (this = 0; paramInt > this; this = 1 + this) {
      localSeparateLoops1 += this;
    }
    SeparateLoops localSeparateLoops2 = localSeparateLoops2;
    paramInt += 1;
    int i;
    for (this = 0; paramInt > this; this = 1 + this) {
      localSeparateLoops2 += this;
    }
    return i;
  }
  
  public void unrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops = 0;
    int i;
    for (this = 0; this < paramInt; this = 1 + this) {
      i = this + localSeparateLoops;
    }
    System.out.println(i);
    System.out.println(i);
  }
}

/* Location:
 * Qualified Name:     SeparateLoops
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

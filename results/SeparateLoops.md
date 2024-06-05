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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 434
      * PBTIME 304
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 20
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 465
      * PEG2PEGTIME 419
      * PBTIME 389
      * ENGINETIME 0
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 28
GLPKFormulation: Number of values: 28
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 519
      * PEG2PEGTIME 498
      * PBTIME 475
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
+ Total optimization time = 2017 milliseconds
```

### Optimized
```java
22:46:35.265 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 555
      * PEG2PEGTIME 434
      * PBTIME 311
      * ENGINETIME 4
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
GLPKFormulation: Number of nodes: 20
GLPKFormulation: Number of values: 20
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 481
      * PEG2PEGTIME 428
      * PBTIME 388
      * ENGINETIME 0
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 28
GLPKFormulation: Number of values: 28
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 598
      * PEG2PEGTIME 579
      * PBTIME 555
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
+ Total optimization time = 2106 milliseconds
```

### Optimized
```java
22:46:40.080 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    int i = 0;
    this = 0;
    while (i < paramInt)
    {
      i += 1;
      this += i;
    }
    i = 0;
    this = this;
    paramInt += 1;
    while (i < paramInt)
    {
      i += 1;
      this += i;
    }
    return this;
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
+ Loading class file SeparateLoops
+ Optimizing class SeparateLoops
   - Processing method <SeparateLoops: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 6 milliseconds
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
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 427
      * PBTIME 301
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 20 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 25
GLPKFormulation: Number of values: 31
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 508
      * PBTIME 451
      * ENGINETIME 21
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 28
GLPKFormulation: Number of values: 28
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 506
      * PEG2PEGTIME 484
      * PBTIME 466
      * ENGINETIME 2
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
+ Total optimization time = 2088 milliseconds
```

### Optimized
```java
22:46:44.832 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    int i = 0;
    this = 0;
    while (i < paramInt)
    {
      i = 1 + i;
      this += i;
    }
    this = this;
    i = 0;
    paramInt += 1;
    while (i < paramInt)
    {
      this += i;
      i = 1 + i;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 563
      * PEG2PEGTIME 443
      * PBTIME 309
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 25 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 34
GLPKFormulation: Number of values: 35
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 666
      * PEG2PEGTIME 620
      * PBTIME 557
      * ENGINETIME 25
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 9 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 33
GLPKFormulation: Number of values: 39
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 565
      * PEG2PEGTIME 543
      * PBTIME 514
      * ENGINETIME 9
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
+ Total optimization time = 2297 milliseconds
```

### Optimized
```java
22:46:49.888 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
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
    SeparateLoops localSeparateLoops3 = localSeparateLoops2;
    SeparateLoops localSeparateLoops2 = 1 + paramInt;
    this = 0;
    int i;
    while (this < localSeparateLoops2)
    {
      localSeparateLoops3 += this;
      this += 1;
    }
    return i;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 555
      * PEG2PEGTIME 433
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 44 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 49
GLPKFormulation: Number of values: 49
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 857
      * PEG2PEGTIME 812
      * PBTIME 734
      * ENGINETIME 44
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 19 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 43
GLPKFormulation: Number of values: 43
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 681
      * PEG2PEGTIME 659
      * PBTIME 624
      * ENGINETIME 19
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
+ Total optimization time = 2556 milliseconds
```

### Optimized
```java
22:46:55.111 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
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
    paramInt = 1 + paramInt;
    SeparateLoops localSeparateLoops2 = localSeparateLoops2;
    this = 0;
    int i;
    while (this < paramInt)
    {
      localSeparateLoops2 += this;
      this += 1;
    }
    return i;
  }
  
  public void unrelated(int paramInt)
  {
    int i = 0;
    this = 0;
    while (i < paramInt)
    {
      i = 1 + i;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 684
      * PEG2PEGTIME 556
      * PBTIME 427
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 127 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 108
GLPKFormulation: Number of values: 81
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 1756
      * PEG2PEGTIME 1712
      * PBTIME 1533
      * ENGINETIME 127
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 50 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 95
GLPKFormulation: Number of values: 77
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 1421
      * PEG2PEGTIME 1399
      * PBTIME 1333
      * ENGINETIME 50
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
+ Total optimization time = 4371 milliseconds
```

### Optimized
```java
22:47:03.312 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    SeparateLoops localSeparateLoops1 = 0;
    for (this = 0; this < paramInt; this = 1 + this) {
      localSeparateLoops2 = this + localSeparateLoops1;
    }
    paramInt = 1 + paramInt;
    SeparateLoops localSeparateLoops2 = localSeparateLoops2;
    int i;
    for (this = 0; this < paramInt; this = 1 + this) {
      i = this + localSeparateLoops2;
    }
    return i;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 428
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 197 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 213
GLPKFormulation: Number of values: 127
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 3133
      * PEG2PEGTIME 3087
      * PBTIME 2857
      * ENGINETIME 197
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 111 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 205
GLPKFormulation: Number of values: 120
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 2740
      * PEG2PEGTIME 2718
      * PBTIME 2586
      * ENGINETIME 112
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
+ Total optimization time = 6899 milliseconds
```

### Optimized
```java
22:47:12.983 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    for (int i = 0; paramInt > i; i = 1 + i) {
      this = i + this;
    }
    this = this;
    paramInt += 1;
    for (i = 0; paramInt > i; i = 1 + i) {
      this = i + this;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 571
      * PEG2PEGTIME 447
      * PBTIME 302
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 457 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 484
GLPKFormulation: Number of values: 250
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 6364
      * PEG2PEGTIME 6319
      * PBTIME 5820
      * ENGINETIME 457
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 204 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 447
GLPKFormulation: Number of values: 227
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 5518
      * PEG2PEGTIME 5493
      * PBTIME 5266
      * ENGINETIME 205
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
+ Total optimization time = 12937 milliseconds
```

### Optimized
```java
22:47:28.642 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    int i = 0;
    for (this = 0; paramInt > i; this = i + this) {
      i = 1 + i;
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
    SeparateLoops localSeparateLoops = 0;
    this = 0;
    int i;
    while (paramInt > this)
    {
      localSeparateLoops += this;
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 549
      * PEG2PEGTIME 429
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 788 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1002
GLPKFormulation: Number of values: 419
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 12776
      * PEG2PEGTIME 12733
      * PBTIME 11897
      * ENGINETIME 788
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 351 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 989
GLPKFormulation: Number of values: 366
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 11881
      * PEG2PEGTIME 11858
      * PBTIME 11479
      * ENGINETIME 352
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
+ Total optimization time = 25675 milliseconds
```

### Optimized
```java
22:47:57.006 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    int i = 0;
    for (this = 0; i < paramInt; this = i + this) {
      i += 1;
    }
    i = 0;
    paramInt = 1 + paramInt;
    this = this;
    while (i < paramInt)
    {
      i += 1;
      this += i;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    SeparateLoops localSeparateLoops = 0;
    int i;
    while (this < paramInt)
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 439
      * PBTIME 306
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1398 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2230
GLPKFormulation: Number of values: 745
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 27022
      * PEG2PEGTIME 26975
      * PBTIME 25538
      * ENGINETIME 1398
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 1328 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 2210
GLPKFormulation: Number of values: 674
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 27150
      * PEG2PEGTIME 27122
      * PBTIME 25775
      * ENGINETIME 1329
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
+ Total optimization time = 55213 milliseconds
```

### Optimized
```java
22:48:55.042 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      this = i + this;
    }
    i = 0;
    this = this;
    paramInt = 1 + paramInt;
    while (i < paramInt)
    {
      i = 1 + i;
      this = i + this;
    }
    return this;
  }
  
  public void unrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (paramInt > i)
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 433
      * PBTIME 302
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <SeparateLoops: void <init>()>
   - Processing method <SeparateLoops: int interrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 9822 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 4790
GLPKFormulation: Number of values: 1326
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: int interrelated(int)> SUCCESSFUL
      * Optimization took 68228
      * PEG2PEGTIME 68176
      * PBTIME 58319
      * ENGINETIME 9823
      * Optimization ratio 524/524 = 1.0
      * PEG-based Optimization ratio 524/524 = 1.0
   - Done processing method <SeparateLoops: int interrelated(int)>
   - Processing method <SeparateLoops: void unrelated(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 10547 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 4780
GLPKFormulation: Number of values: 1184
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <SeparateLoops: void unrelated(int)> SUCCESSFUL
      * Optimization took 66425
      * PEG2PEGTIME 66394
      * PBTIME 55822
      * ENGINETIME 10549
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
+ Total optimization time = 135699 milliseconds
```

### Optimized
```java
22:51:13.520 INFO  jd.cli.Main - Decompiling optimized/SeparateLoops.class
import java.io.PrintStream;

public class SeparateLoops
{
  public int interrelated(int paramInt)
  {
    this = 0;
    int i = 0;
    while (i < paramInt)
    {
      this += i;
      i += 1;
    }
    paramInt += 1;
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

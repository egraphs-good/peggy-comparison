# MultiVariableLoop
## Original
```java
public class MultiVariableLoop {
    public int original(int n) {
        for (int i = 0, j = 4; i < 10 & j < 2; i++, j++) {
            System.out.println(i);
            System.out.println(j);
            System.out.println(i + j);
            n += 2;
        }
        return n;
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 433
      * PEG2PEGTIME 350
      * PBTIME 269
      * ENGINETIME 2
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 617
      * PEG2PEGTIME 564
      * PBTIME 537
      * ENGINETIME 1
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1353 milliseconds
```

```java
19:58:05.210 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int j = paramInt;
    int i = 0;
    MultiVariableLoop localMultiVariableLoop = 4;
    if (localMultiVariableLoop < 2) {}
    for (this = 1;; this = 0)
    {
      if (i < 10) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if ((paramInt & this) != 0)
        {
          paramInt = i;
          this = localMultiVariableLoop;
          j += 2;
          i += 1;
          localMultiVariableLoop += 1;
          System.out.println(paramInt);
          System.out.println(this);
          System.out.println(paramInt + this);
          break;
        }
        return j;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 440
      * PEG2PEGTIME 356
      * PBTIME 272
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 648
      * PEG2PEGTIME 599
      * PBTIME 573
      * ENGINETIME 1
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1398 milliseconds
```

```java
19:58:08.401 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = 0;
    int i = 4;
    int k = paramInt;
    if (i < 2) {}
    for (paramInt = 1;; paramInt = 0)
    {
      if (this < 10) {}
      for (int j = 1;; j = 0)
      {
        if ((j & paramInt) != 0)
        {
          paramInt = this;
          j = i;
          this += 1;
          i += 1;
          System.out.println(paramInt);
          System.out.println(j);
          System.out.println(paramInt + j);
          k += 2;
          break;
        }
        return k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 429
      * PEG2PEGTIME 349
      * PBTIME 269
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 628
      * PEG2PEGTIME 579
      * PBTIME 546
      * ENGINETIME 7
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1360 milliseconds
```

```java
19:58:11.598 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = paramInt;
    int j = 0;
    int k = 4;
    if (j < 10) {}
    for (int i = 1;; i = 0)
    {
      if (k < 2) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if ((i & paramInt) != 0)
        {
          i = j;
          paramInt = k;
          this = 2 + this;
          j += 1;
          k += 1;
          System.out.println(i);
          System.out.println(paramInt);
          System.out.println(i + paramInt);
          break;
        }
        return this;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 448
      * PEG2PEGTIME 367
      * PBTIME 279
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 807
      * PEG2PEGTIME 748
      * PBTIME 711
      * ENGINETIME 13
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1569 milliseconds
```

```java
19:58:14.921 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    MultiVariableLoop localMultiVariableLoop = 4;
    int j = paramInt;
    int i = 0;
    if (2 > localMultiVariableLoop) {}
    for (this = 1;; this = 0)
    {
      if (i < 10) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if ((paramInt & this) != 0)
        {
          this = localMultiVariableLoop;
          paramInt = i;
          localMultiVariableLoop += 1;
          j += 2;
          i = 1 + i;
          System.out.println(paramInt);
          System.out.println(this);
          System.out.println(paramInt + this);
          break;
        }
        return j;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 440
      * PEG2PEGTIME 360
      * PBTIME 276
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 32 after 16 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 772
      * PEG2PEGTIME 718
      * PBTIME 673
      * ENGINETIME 16
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1532 milliseconds
```

```java
19:58:18.302 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = 0;
    int j = paramInt;
    paramInt = 4;
    if (2 > paramInt) {}
    int m;
    for (int k = 1;; m = 0)
    {
      if (this < 10) {}
      for (int i = 1;; i = 0)
      {
        if ((i & k) != 0)
        {
          m = this;
          this = 1 + this;
          System.out.println(m);
          System.out.println(paramInt);
          System.out.println(m + paramInt);
          j += 2;
          paramInt += 1;
          break;
        }
        return j;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 486
      * PEG2PEGTIME 405
      * PBTIME 277
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 64 after 41 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 1050
      * PEG2PEGTIME 1003
      * PBTIME 935
      * ENGINETIME 41
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1834 milliseconds
```

```java
19:58:22.034 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    MultiVariableLoop localMultiVariableLoop3 = 4;
    paramInt = paramInt;
    MultiVariableLoop localMultiVariableLoop1 = 0;
    if (localMultiVariableLoop3 < 2) {}
    int i;
    for (MultiVariableLoop localMultiVariableLoop2 = 1;; i = 0)
    {
      if (localMultiVariableLoop1 < 10) {}
      for (this = 1;; this = 0)
      {
        if ((localMultiVariableLoop2 & this) != 0)
        {
          localMultiVariableLoop2 = localMultiVariableLoop3;
          this = localMultiVariableLoop1;
          localMultiVariableLoop3 = 1 + localMultiVariableLoop3;
          paramInt = 2 + paramInt;
          localMultiVariableLoop1 = 1 + localMultiVariableLoop1;
          System.out.println(this);
          System.out.println(localMultiVariableLoop2);
          System.out.println(this + localMultiVariableLoop2);
          break;
        }
        return paramInt;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 512
      * PEG2PEGTIME 373
      * PBTIME 276
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 128 after 108 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 1779
      * PEG2PEGTIME 1732
      * PBTIME 1595
      * ENGINETIME 108
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 2616 milliseconds
```

```java
19:58:26.592 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int m = 0;
    this = 4;
    paramInt = paramInt;
    if (2 > this) {}
    int k;
    for (int i = 1;; k = 0)
    {
      if (10 > m) {}
      int i1;
      for (int n = 1;; i1 = 0)
      {
        if ((i & n) != 0)
        {
          n = m;
          int j = this;
          m = 1 + m;
          this = 1 + this;
          System.out.println(n);
          System.out.println(j);
          System.out.println(n + j);
          paramInt += 2;
          break;
        }
        return paramInt;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 508
      * PEG2PEGTIME 359
      * PBTIME 274
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 256 after 197 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 2866
      * PEG2PEGTIME 2818
      * PBTIME 2598
      * ENGINETIME 197
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 3689 milliseconds
```

```java
19:58:32.163 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int k = paramInt;
    int i = 0;
    int j = 4;
    if (10 > i) {}
    for (paramInt = 1;; paramInt = 0)
    {
      if (2 > j) {}
      for (this = 1;; this = 0)
      {
        if ((paramInt & this) != 0)
        {
          paramInt = i;
          k += 2;
          i += 1;
          System.out.println(paramInt);
          System.out.println(j);
          System.out.println(paramInt + j);
          j = 1 + j;
          break;
        }
        return k;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 431
      * PEG2PEGTIME 349
      * PBTIME 266
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 512 after 466 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 6058
      * PEG2PEGTIME 6005
      * PBTIME 5517
      * ENGINETIME 466
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 6794 milliseconds
```

```java
19:58:40.819 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    this = paramInt;
    int i = 4;
    int k = 0;
    if (k < 10) {}
    for (int j = 1;; j = 0)
    {
      if (i < 2) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if ((paramInt & j) != 0)
        {
          j = i;
          this = 2 + this;
          i += 1;
          System.out.println(k);
          System.out.println(j);
          System.out.println(k + j);
          k = 1 + k;
          break;
        }
        return this;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 481
      * PEG2PEGTIME 353
      * PBTIME 266
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1024 after 665 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 11954
      * PEG2PEGTIME 11891
      * PBTIME 11193
      * ENGINETIME 665
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 12738 milliseconds
```

```java
19:58:55.402 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    int j = paramInt;
    int i = 4;
    paramInt = 0;
    if (i < 2) {}
    int k;
    for (MultiVariableLoop localMultiVariableLoop = 1;; k = 0)
    {
      if (paramInt < 10) {}
      for (this = 1;; this = 0)
      {
        if ((localMultiVariableLoop & this) != 0)
        {
          System.out.println(paramInt);
          System.out.println(i);
          System.out.println(i + paramInt);
          j += 2;
          i = 1 + i;
          paramInt += 1;
          break;
        }
        return j;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
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
+ Loading class file MultiVariableLoop
+ Optimizing class MultiVariableLoop
   - Processing method <MultiVariableLoop: void <init>()>
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
      * Optimization of method <MultiVariableLoop: void <init>()> SUCCESSFUL
      * Optimization took 516
      * PEG2PEGTIME 359
      * PBTIME 277
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <MultiVariableLoop: void <init>()>
   - Processing method <MultiVariableLoop: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2048 after 2208 milliseconds
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
      * Optimization of method <MultiVariableLoop: int original(int)> SUCCESSFUL
      * Optimization took 1051835
      * PEG2PEGTIME 1051779
      * PBTIME 1049543
      * ENGINETIME 2208
      * Optimization ratio 93750/93750 = 1.0
      * PEG-based Optimization ratio 93750/93750 = 1.0
   - Done processing method <MultiVariableLoop: int original(int)>
+ Done optimizing MultiVariableLoop
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <MultiVariableLoop: void <init>()>
+ Fixing bytecode of method <MultiVariableLoop: int original(int)>
+ Writing class back to optimized/MultiVariableLoop.class
+ Total optimization time = 1052668 milliseconds
```

```java
20:16:29.846 INFO  jd.cli.Main - Decompiling optimized/MultiVariableLoop.class
import java.io.PrintStream;

public class MultiVariableLoop
{
  public int original(int paramInt)
  {
    MultiVariableLoop localMultiVariableLoop2 = 4;
    int j = paramInt;
    paramInt = 0;
    if (paramInt < 10) {}
    for (this = 1;; this = 0)
    {
      if (localMultiVariableLoop2 < 2) {}
      int i;
      for (MultiVariableLoop localMultiVariableLoop1 = 1;; i = 0)
      {
        if ((this & localMultiVariableLoop1) != 0)
        {
          localMultiVariableLoop1 = localMultiVariableLoop2;
          localMultiVariableLoop2 = 1 + localMultiVariableLoop2;
          System.out.println(paramInt);
          System.out.println(localMultiVariableLoop1);
          System.out.println(localMultiVariableLoop1 + paramInt);
          j = 2 + j;
          paramInt = 1 + paramInt;
          break;
        }
        return j;
      }
    }
  }
}

/* Location:
 * Qualified Name:     MultiVariableLoop
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

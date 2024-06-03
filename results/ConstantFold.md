# ConstantFold
## Original
```java
public class ConstantFold {
    public int original() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 505
      * PEG2PEGTIME 420
      * PBTIME 327
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 244
      * PBTIME 232
      * ENGINETIME 0
      * Optimization ratio 24/24 = 1.0
      * PEG-based Optimization ratio 24/24 = 1.0
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1017 milliseconds
```

```java
19:37:37.477 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 467
      * PBTIME 278
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 247
      * PBTIME 232
      * ENGINETIME 1
      * Optimization ratio 24/24 = 1.0
      * PEG-based Optimization ratio 24/24 = 1.0
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1140 milliseconds
```

```java
19:37:41.956 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 466
      * PEG2PEGTIME 379
      * PBTIME 277
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 250
      * PBTIME 231
      * ENGINETIME 9
      * Optimization ratio 4/24 = 0.16666666666666666
      * PEG-based Optimization ratio 4/24 = 0.16666666666666666
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 993 milliseconds
```

```java
19:37:44.910 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 479
      * PEG2PEGTIME 389
      * PBTIME 289
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 361
      * PEG2PEGTIME 351
      * PBTIME 330
      * ENGINETIME 11
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1118 milliseconds
```

```java
19:37:47.765 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 597
      * PEG2PEGTIME 501
      * PBTIME 312
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 246
      * PBTIME 225
      * ENGINETIME 11
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1363 milliseconds
```

```java
19:37:51.739 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 498
      * PEG2PEGTIME 412
      * PBTIME 309
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 248
      * PBTIME 226
      * ENGINETIME 12
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1193 milliseconds
```

```java
19:37:54.851 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 451
      * PEG2PEGTIME 370
      * PBTIME 275
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 147
      * PEG2PEGTIME 141
      * PBTIME 120
      * ENGINETIME 11
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 841 milliseconds
```

```java
19:37:57.562 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 440
      * PEG2PEGTIME 357
      * PBTIME 273
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 249
      * PBTIME 228
      * ENGINETIME 11
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 946 milliseconds
```

```java
19:38:00.214 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 503
      * PEG2PEGTIME 424
      * PBTIME 275
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 244
      * PBTIME 224
      * ENGINETIME 11
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1015 milliseconds
```

```java
19:38:02.839 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 444
      * PEG2PEGTIME 358
      * PBTIME 271
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 246
      * PBTIME 223
      * ENGINETIME 12
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 950 milliseconds
```

```java
19:38:05.663 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
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
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
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
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 496
      * PEG2PEGTIME 405
      * PBTIME 291
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
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
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 165
      * PEG2PEGTIME 147
      * PBTIME 120
      * ENGINETIME 10
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 957 milliseconds
```

```java
19:38:08.676 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
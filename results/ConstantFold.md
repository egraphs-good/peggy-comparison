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
      * Engine reached iteration bound of 2 after 4 milliseconds
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
      * Optimization took 583
      * PEG2PEGTIME 454
      * PBTIME 309
      * ENGINETIME 4
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
      * Optimization took 272
      * PEG2PEGTIME 260
      * PBTIME 245
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
+ Total optimization time = 1274 milliseconds
```

```java
20:26:18.850 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 572
      * PEG2PEGTIME 441
      * PBTIME 304
      * ENGINETIME 4
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
      * Optimization took 268
      * PEG2PEGTIME 259
      * PBTIME 244
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
+ Total optimization time = 1220 milliseconds
```

```java
20:26:22.630 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 560
      * PEG2PEGTIME 437
      * PBTIME 304
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 15 milliseconds
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
      * Optimization took 282
      * PEG2PEGTIME 274
      * PBTIME 242
      * ENGINETIME 15
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
+ Total optimization time = 1240 milliseconds
```

```java
20:26:26.455 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 595
      * PEG2PEGTIME 469
      * PBTIME 321
      * ENGINETIME 8
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
      * Optimization took 274
      * PEG2PEGTIME 265
      * PBTIME 233
      * ENGINETIME 17
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1268 milliseconds
```

```java
20:26:30.382 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 598
      * PEG2PEGTIME 475
      * PBTIME 319
      * ENGINETIME 7
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
      * Optimization took 285
      * PEG2PEGTIME 275
      * PBTIME 239
      * ENGINETIME 20
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1274 milliseconds
```

```java
20:26:34.336 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 590
      * PEG2PEGTIME 453
      * PBTIME 301
      * ENGINETIME 7
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
      * Optimization took 383
      * PEG2PEGTIME 371
      * PBTIME 340
      * ENGINETIME 16
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1370 milliseconds
```

```java
20:26:38.351 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 584
      * PEG2PEGTIME 458
      * PBTIME 312
      * ENGINETIME 8
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
      * Optimization took 276
      * PEG2PEGTIME 268
      * PBTIME 236
      * ENGINETIME 16
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1322 milliseconds
```

```java
20:26:42.700 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 570
      * PEG2PEGTIME 448
      * PBTIME 311
      * ENGINETIME 7
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
      * Optimization took 301
      * PEG2PEGTIME 292
      * PBTIME 250
      * ENGINETIME 18
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1273 milliseconds
```

```java
20:26:46.838 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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
      * Optimization took 604
      * PEG2PEGTIME 475
      * PBTIME 323
      * ENGINETIME 8
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
      * Optimization took 292
      * PEG2PEGTIME 281
      * PBTIME 239
      * ENGINETIME 20
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1373 milliseconds
```

```java
20:26:51.087 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
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

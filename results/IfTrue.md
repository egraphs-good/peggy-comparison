# IfTrue
## Original
```java
public class IfTrue {
    public int original(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public int expected(int x) {
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 461
      * PEG2PEGTIME 375
      * PBTIME 285
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 229
      * PBTIME 220
      * ENGINETIME 1
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 241
      * PEG2PEGTIME 238
      * PBTIME 221
      * ENGINETIME 1
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1213 milliseconds
```

```java
19:29:36.700 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 444
      * PEG2PEGTIME 362
      * PBTIME 273
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 138
      * PEG2PEGTIME 132
      * PBTIME 121
      * ENGINETIME 2
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 243
      * PEG2PEGTIME 239
      * PBTIME 218
      * ENGINETIME 0
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1188 milliseconds
```

```java
19:29:39.609 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 442
      * PEG2PEGTIME 362
      * PBTIME 276
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 135
      * PEG2PEGTIME 130
      * PBTIME 121
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 133
      * PEG2PEGTIME 130
      * PBTIME 116
      * ENGINETIME 1
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 967 milliseconds
```

```java
19:29:42.389 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 448
      * PEG2PEGTIME 369
      * PBTIME 281
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 238
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 242
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1195 milliseconds
```

```java
19:29:45.245 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 458
      * PEG2PEGTIME 372
      * PBTIME 280
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 133
      * PEG2PEGTIME 128
      * PBTIME 118
      * ENGINETIME 1
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 136
      * PEG2PEGTIME 135
      * PBTIME 119
      * ENGINETIME 1
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 990 milliseconds
```

```java
19:29:48.034 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 470
      * PEG2PEGTIME 383
      * PBTIME 279
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 246
      * PEG2PEGTIME 240
      * PBTIME 225
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 219
      * ENGINETIME 0
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1207 milliseconds
```

```java
19:29:51.065 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 460
      * PEG2PEGTIME 381
      * PBTIME 291
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 137
      * PEG2PEGTIME 129
      * PBTIME 120
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 133
      * PEG2PEGTIME 131
      * PBTIME 118
      * ENGINETIME 1
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 975 milliseconds
```

```java
19:29:53.708 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 530
      * PEG2PEGTIME 448
      * PBTIME 299
      * ENGINETIME 10
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 232
      * PBTIME 221
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 235
      * PBTIME 218
      * ENGINETIME 0
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1276 milliseconds
```

```java
19:29:57.167 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 550
      * PEG2PEGTIME 463
      * PBTIME 289
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 233
      * PBTIME 222
      * ENGINETIME 1
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 242
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1344 milliseconds
```

```java
19:30:00.432 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 442
      * PEG2PEGTIME 362
      * PBTIME 270
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 231
      * PBTIME 221
      * ENGINETIME 1
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 235
      * PBTIME 221
      * ENGINETIME 0
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1179 milliseconds
```

```java
19:30:03.479 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 462
      * PEG2PEGTIME 367
      * PBTIME 278
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
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
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 233
      * PBTIME 223
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
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
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 234
      * PBTIME 218
      * ENGINETIME 0
   - Done processing method <IfTrue: int expected(int)>
+ Done optimizing IfTrue
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <IfTrue: void <init>()>
+ Fixing bytecode of method <IfTrue: int original(int)>
+ Fixing bytecode of method <IfTrue: int expected(int)>
+ Writing class back to optimized/IfTrue.class
+ Total optimization time = 1196 milliseconds
```

```java
19:30:06.403 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
public class IfTrue
{
  public int original(int paramInt)
  {
    return paramInt;
  }
  
  public int expected(int paramInt)
  {
    return paramInt;
  }
}

/* Location:
 * Qualified Name:     IfTrue
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

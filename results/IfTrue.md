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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 573
      * PEG2PEGTIME 449
      * PBTIME 316
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 247
      * PBTIME 231
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 1 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 250
      * PBTIME 229
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
+ Total optimization time = 1472 milliseconds
```

### Optimized
```java
22:42:21.740 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 575
      * PEG2PEGTIME 453
      * PBTIME 309
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <IfTrue: void <init>()>
   - Processing method <IfTrue: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 246
      * PBTIME 232
      * ENGINETIME 0
   - Done processing method <IfTrue: int original(int)>
   - Processing method <IfTrue: int expected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 252
      * PBTIME 229
      * ENGINETIME 2
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
+ Total optimization time = 1460 milliseconds
```

### Optimized
```java
22:42:25.750 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
+ Loading class file IfTrue
+ Optimizing class IfTrue
   - Processing method <IfTrue: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 8 milliseconds
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
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 578
      * PEG2PEGTIME 445
      * PBTIME 306
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 243
      * PBTIME 226
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 249
      * PBTIME 227
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
+ Total optimization time = 1480 milliseconds
```

### Optimized
```java
22:42:29.795 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 614
      * PEG2PEGTIME 480
      * PBTIME 340
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 260
      * PEG2PEGTIME 252
      * PBTIME 237
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 249
      * PBTIME 227
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
+ Total optimization time = 1512 milliseconds
```

### Optimized
```java
22:42:33.895 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 569
      * PEG2PEGTIME 448
      * PBTIME 301
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 266
      * PEG2PEGTIME 257
      * PBTIME 241
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 253
      * PBTIME 232
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
+ Total optimization time = 1483 milliseconds
```

### Optimized
```java
22:42:37.975 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 443
      * PBTIME 314
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 249
      * PBTIME 231
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 258
      * PEG2PEGTIME 256
      * PBTIME 232
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
+ Total optimization time = 1459 milliseconds
```

### Optimized
```java
22:42:41.998 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 440
      * PBTIME 309
      * ENGINETIME 6
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 244
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 268
      * PEG2PEGTIME 265
      * PBTIME 240
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
+ Total optimization time = 1470 milliseconds
```

### Optimized
```java
22:42:46.002 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 578
      * PEG2PEGTIME 451
      * PBTIME 301
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 248
      * PEG2PEGTIME 241
      * PBTIME 227
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 298
      * PEG2PEGTIME 294
      * PBTIME 267
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
+ Total optimization time = 1516 milliseconds
```

### Optimized
```java
22:42:50.108 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 566
      * PEG2PEGTIME 443
      * PBTIME 300
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 259
      * PEG2PEGTIME 249
      * PBTIME 235
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 247
      * PEG2PEGTIME 245
      * PBTIME 225
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
+ Total optimization time = 1458 milliseconds
```

### Optimized
```java
22:42:54.135 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 565
      * PEG2PEGTIME 443
      * PBTIME 304
      * ENGINETIME 7
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 248
      * PEG2PEGTIME 238
      * PBTIME 224
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 250
      * PBTIME 227
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
+ Total optimization time = 1454 milliseconds
```

### Optimized
```java
22:42:58.159 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 437
      * PBTIME 299
      * ENGINETIME 8
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int original(int)> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 241
      * PBTIME 226
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
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <IfTrue: int expected(int)> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 245
      * PBTIME 225
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
+ Total optimization time = 1451 milliseconds
```

### Optimized
```java
22:43:02.170 INFO  jd.cli.Main - Decompiling optimized/IfTrue.class
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

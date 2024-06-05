# ConditionalConstantFolding
## Original
```java
public class ConditionalConstantFolding {
    public int original(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public int expected() {
        return 20;
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 572
      * PEG2PEGTIME 449
      * PBTIME 307
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 355
      * PEG2PEGTIME 312
      * PBTIME 278
      * ENGINETIME 0
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 244
      * PEG2PEGTIME 240
      * PBTIME 227
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1626 milliseconds
```

### Optimized
```java
22:58:31.252 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 437
      * PBTIME 301
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 354
      * PEG2PEGTIME 311
      * PBTIME 280
      * ENGINETIME 1
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 3 milliseconds
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 245
      * PBTIME 227
      * ENGINETIME 3
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1581 milliseconds
```

### Optimized
```java
22:58:35.441 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 552
      * PEG2PEGTIME 430
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 2 milliseconds
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
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 350
      * PEG2PEGTIME 309
      * PBTIME 277
      * ENGINETIME 3
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 260
      * PEG2PEGTIME 256
      * PBTIME 238
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1571 milliseconds
```

### Optimized
```java
22:58:39.592 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 441
      * PBTIME 307
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 16 after 14 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 370
      * PEG2PEGTIME 331
      * PBTIME 288
      * ENGINETIME 14
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 263
      * PBTIME 240
      * ENGINETIME 0
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1606 milliseconds
```

### Optimized
```java
22:58:43.739 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 567
      * PEG2PEGTIME 446
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 395
      * PEG2PEGTIME 340
      * PBTIME 292
      * ENGINETIME 15
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 252
      * PBTIME 229
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1625 milliseconds
```

### Optimized
```java
22:58:47.946 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 441
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 378
      * PEG2PEGTIME 334
      * PBTIME 295
      * ENGINETIME 15
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 247
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1598 milliseconds
```

### Optimized
```java
22:58:52.100 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 569
      * PEG2PEGTIME 447
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 389
      * PEG2PEGTIME 343
      * PBTIME 290
      * ENGINETIME 16
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 248
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1638 milliseconds
```

### Optimized
```java
22:58:56.304 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 580
      * PEG2PEGTIME 444
      * PBTIME 298
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 376
      * PEG2PEGTIME 333
      * PBTIME 287
      * ENGINETIME 16
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 248
      * PEG2PEGTIME 245
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1621 milliseconds
```

### Optimized
```java
22:59:00.520 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 560
      * PEG2PEGTIME 438
      * PBTIME 299
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 381
      * PEG2PEGTIME 341
      * PBTIME 295
      * ENGINETIME 15
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 248
      * PEG2PEGTIME 245
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1599 milliseconds
```

### Optimized
```java
22:59:04.676 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 597
      * PEG2PEGTIME 475
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 399
      * PEG2PEGTIME 354
      * PBTIME 304
      * ENGINETIME 16
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 247
      * PBTIME 226
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1663 milliseconds
```

### Optimized
```java
22:59:08.948 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
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
+ Loading class file ConditionalConstantFolding
+ Optimizing class ConditionalConstantFolding
   - Processing method <ConditionalConstantFolding: void <init>()>
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
      * Optimization of method <ConditionalConstantFolding: void <init>()> SUCCESSFUL
      * Optimization took 579
      * PEG2PEGTIME 447
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConditionalConstantFolding: void <init>()>
   - Processing method <ConditionalConstantFolding: int original(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConditionalConstantFolding: int original(int)> SUCCESSFUL
      * Optimization took 381
      * PEG2PEGTIME 339
      * PBTIME 295
      * ENGINETIME 15
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <ConditionalConstantFolding: int original(int)>
   - Processing method <ConditionalConstantFolding: int expected()>
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
      * Optimization of method <ConditionalConstantFolding: int expected()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 248
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <ConditionalConstantFolding: int expected()>
+ Done optimizing ConditionalConstantFolding
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 3
+ Fixing bytecode of method <ConditionalConstantFolding: void <init>()>
+ Fixing bytecode of method <ConditionalConstantFolding: int original(int)>
+ Fixing bytecode of method <ConditionalConstantFolding: int expected()>
+ Writing class back to optimized/ConditionalConstantFolding.class
+ Total optimization time = 1621 milliseconds
```

### Optimized
```java
22:59:13.157 INFO  jd.cli.Main - Decompiling optimized/ConditionalConstantFolding.class
public class ConditionalConstantFolding
{
  public int original(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public int expected()
  {
    return 20;
  }
}

/* Location:
 * Qualified Name:     ConditionalConstantFolding
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```

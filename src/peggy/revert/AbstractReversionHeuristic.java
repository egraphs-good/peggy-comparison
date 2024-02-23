package peggy.revert;

import peggy.AbstractLoggable;

/**
 * This implementation just adds logging capability.
 */
public abstract class AbstractReversionHeuristic<O,P,R,N extends Number> 
extends AbstractLoggable implements ReversionHeuristic<O,P,R,N> {
}

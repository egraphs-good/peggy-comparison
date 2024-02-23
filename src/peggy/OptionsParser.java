package peggy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.pair.Pair;

/**
 * This class is a parser that can parse command line options according to 
 * an Option description.
 * It can also contain action-based options, which are ones that take no
 * parameters.
 */
public class OptionsParser {
	protected final Map<String,Pair<String,Runnable>> actionParameters = 
		new HashMap<String,Pair<String,Runnable>>();
	protected final Options options;
	
	public OptionsParser(Options _options) {
		this.options = _options;
	}
	
	public boolean hasCommand(String key) {
		return actionParameters.containsKey(key);
	}
	public Set<String> getCommandKeys() {
		return Collections.unmodifiableSet(actionParameters.keySet());
	}
	
	public void registerCommand(String key, String description, Runnable action) {
		if (hasCommand(key) || options.hasKey(key))
			throw new IllegalArgumentException();
		actionParameters.put(key, new Pair<String,Runnable>(description, action));
	}
	
	public String getCommandDescription(String key) {
		if (actionParameters.containsKey(key)) {
			return actionParameters.get(key).getFirst();
		}
		else
			throw new IllegalArgumentException();
	}
	
	public void parse(String args[]) {
		int index = 0;
		while (index < args.length) {
			if (!args[index].startsWith("-"))
				throw new OptionParsingException("Unrecognized option: " + args[index]);
			String option = args[index].substring(1);
			index++;
			
			if (options.hasKey(option)) {
				// value option
				int numTokens = options.getNumTokens(option);
				if (index+numTokens > args.length)
					throw new OptionParsingException("Not enough tokens for " + option);
				String[] tokens = new String[numTokens];
				for (int i = 0; i < tokens.length; i++)
					tokens[i] = args[index+i];
				options.setValue(option, tokens);
				index += numTokens;
			} else if (hasCommand(option)) {
				// command option
				Runnable command = actionParameters.get(option).getSecond();
				command.run();
			} else {
				throw new OptionParsingException("Unrecognized option: " + option);
			}
		}
	}
}

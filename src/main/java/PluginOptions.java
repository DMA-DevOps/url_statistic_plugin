import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * 
 * @author Maik Fröbe
 *
 */
public final class PluginOptions
{
	private PluginOptions()
	{
		// hide utility class constructor
	}
	
	public static final Options getPluginOptions()
	{
	    return new Options()
	    	.addOption(new Option("h", "help", false, "Show this message"))
	    	.addOption(inputDirOption())
	    	.addOption(outputDirOption())
	    	.addOption(numReducersOption());
	}
	
	private static Option numReducersOption()
	{
		return OptionBuilder
			.withArgName("numReducers")
		    .withDescription("Optional number of reduce jobs to use. Defaults to 1")
		    .hasArgs()
		    .create("numReducers");
	}
	
	private static Option outputDirOption()
	{
		return OptionBuilder
			.withArgName("outputDir")
		    .isRequired()
		    .withDescription("Output directory where results should be dumped")
		    .hasArgs()
		    .create("outputDir");
	}
	
	private static Option inputDirOption()
	{
		return OptionBuilder
			.withArgName("inputDirs")
		    .isRequired()
		    .withDescription("Comma separated list of crawl directories (e.g., \"./crawl1,./crawl2\")")
		    .hasArgs()
		    .create("inputDirs");
	}
}

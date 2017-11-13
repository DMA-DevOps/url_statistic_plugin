import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.nutch.util.TimingUtil;
import org.apache.nutch.util.NutchConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlStats extends Configured implements Tool
{
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final Options OPTIONS = PluginOptions.getPluginOptions();

	private static final String JOB_NAME = "UrlStats";

	@Override
	public int run(String[] args) throws Exception
	{
		CommandLine cli = parseArgs(args);

		if(cli == null)
		{
			return 1;
		}

		if(cli.hasOption("help"))
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(JOB_NAME, OPTIONS, true);
			return 1;
		}

		String inputDir = cli.getOptionValue("inputDirs");
		String outputDir = cli.getOptionValue("outputDir");

		int numOfReducers = 1;

		if(cli.hasOption("numReducers"))
		{
			numOfReducers = Integer.parseInt(args[3]);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long start = System.currentTimeMillis();
		LOG.info(JOB_NAME + ": starting at {}", sdf.format(start));

		Configuration conf = getConf();
		conf.setInt("domain.statistics.mode", 1);
		conf.setBoolean("mapreduce.fileoutputcommitter.marksuccessfuljobs", false);

		Job job = Job.getInstance(conf, JOB_NAME);
		job.setJarByClass(UrlStats.class);

		String[] inputDirsSpecs = inputDir.split(",");

		for(int i = 0; i < inputDirsSpecs.length; i++)
		{
			File completeInputPath = new File(new File(inputDirsSpecs[i]), "crawldb/current");
			FileInputFormat.addInputPath(job, new Path(completeInputPath.toString()));
		}

		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(outputDir));
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		job.setMapperClass(UrlStatsMapper.class);
		job.setReducerClass(UrlStatsReducer.class);
		job.setNumReduceTasks(numOfReducers);

		try
		{
			boolean success = job.waitForCompletion(true);
			if(!success)
			{
				String message = JOB_NAME + " job did not succeed, job status: " + job.getStatus().getState()
						+ ", reason: " + job.getStatus().getFailureInfo();
				LOG.error(message);
				// throw exception so that calling routine can exit with error
				throw new RuntimeException(message);
			}
		}
		catch(IOException | InterruptedException | ClassNotFoundException e)
		{
			LOG.error(JOB_NAME + " job failed");
			throw e;
		}

	    long end = System.currentTimeMillis();
	    LOG.info("CrawlCompletionStats: finished at {}, elapsed: {}",
	      sdf.format(end), TimingUtil.elapsedTime(start, end));
		
		return 0;
	}

	private static CommandLine parseArgs(String[] args) throws Exception
	{
		try
		{
			return new GnuParser().parse(OPTIONS, args);
		}
		catch(MissingOptionException e)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("UrlStats", OPTIONS, true);

			return null;
		}
	}

	public static void main(String[] args) throws Exception
	{
		ToolRunner.run(NutchConfiguration.create(), new UrlStats(), args);
	}
}

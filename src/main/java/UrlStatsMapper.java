import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.nutch.crawl.CrawlDatum;

/**
 * 
 * @author Maik Fröbe
 *
 */
public class UrlStatsMapper extends Mapper<Text, CrawlDatum, Text, LongWritable>
{
	public void map(Text urlText, CrawlDatum datum, Context context) throws IOException, InterruptedException
	{
		for(String urlComponent : StatisticGenerator.getUrlStatisticMap(urlText.toString()).keySet())
		{
			context.write(new Text(urlComponent), new LongWritable(1));
		}
	}
}

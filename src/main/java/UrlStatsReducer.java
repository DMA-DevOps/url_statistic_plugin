import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * @author Maik Fröbe
 *
 */
public class UrlStatsReducer extends Reducer<Text, LongWritable, LongWritable, Text>
{
	public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException
	{
		long total = StreamSupport.stream(values.spliterator(), false)
				.collect(Collectors.summarizingLong(a -> (a).get())).getSum();
		
		if(total > 200l)
		{
			context.write(new LongWritable(total), key);
		}
	}
}

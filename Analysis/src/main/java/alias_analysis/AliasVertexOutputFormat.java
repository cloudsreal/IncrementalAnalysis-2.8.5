package alias_analysis;

import alias_data.Grammar;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import data.Fact;
import alias_data.AliasVertexValue;
import alias_data.Pegraph;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AliasVertexOutputFormat extends TextVertexOutputFormat<IntWritable, AliasVertexValue, NullWritable> {

    public Grammar grammar = null;

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        grammar = new Grammar();
        readGrammar();
        return new LabelPropagationTextVertexLineWriter();
    }

    public void readGrammar() {
        try {
            BufferedReader br = new BufferedReader(readHDFS(grammarPath));
            grammar.loadGrammar(br);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStreamReader readHDFS(String path) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(path), conf);
        FSDataInputStream hdfsInStream = fs.open(new Path(path));
        return new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
    }


    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, AliasVertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            Fact fact = vertex.getValue().getFact();
            stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");
            int sum = 0;
            if (fact != null) {
                sum = ((Pegraph)fact).getNumEdges();
                stringBuilder.append(sum);
                stringBuilder.append("\t").append(((Pegraph) fact).getAliasNumEdges(grammar));
            }
            else{
                stringBuilder.append("0");
            }
            return new Text(stringBuilder.toString());
        }
    }
}

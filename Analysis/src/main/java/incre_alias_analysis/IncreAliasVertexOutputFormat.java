package incre_alias_analysis;

import alias_data.AliasVertexValue;
import alias_data.Grammar;
import alias_data.Pegraph;
import incre_data.Fact;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

//import incre_data.Tool;
//import alias_data.AliasTool;

public class IncreAliasVertexOutputFormat extends TextVertexOutputFormat<IntWritable, AliasVertexValue, NullWritable> {
    public Grammar grammar = null;

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        grammar = new Grammar();
        readGrammar();
        return new LabelPropagationTextVertexLineWriter();
    }

    public void readGrammar() {
        try {
            /// BufferedReader br = new BufferedReader(readHDFS(grammarPath));
            // BufferedReader br = new BufferedReader(readHDFS(
            // "hdfs://master-1-1.c-b0a67c86e8bdeaf7.cn-hangzhou.emr.aliyuncs.com:9000/grammar"));

            // /// g6, region H, core27
            // BufferedReader br = new BufferedReader(readHDFS(
            // "hdfs://master-1-1.c-6c696d6821822669.cn-hangzhou.emr.aliyuncs.com:9000/grammar"));

            /// g7, region J, core120
            BufferedReader br = new BufferedReader(readHDFS(
                    "hdfs://master-1-1.c-18efa09a29555ff2.cn-hangzhou.emr.aliyuncs.com:9000/grammar"));

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
        protected Text convertVertexToLine(Vertex<IntWritable, AliasVertexValue, NullWritable> vertex) {
            StringBuilder stringBuilder = new StringBuilder();
            // stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");
            stringBuilder.append(vertex.getId()).append("\t");
            AliasVertexValue value = (AliasVertexValue) vertex.getValue();
            Fact fact = vertex.getValue().getFact();
            int sum = 0;
            if (fact != null) {

                // /// AliasTool tool = (AliasTool)(vertex.getValue().getTool());
                // /// Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
//                AliasTool tool = (AliasTool) (vertex.getValue().getTool());
//                if (tool != null)
//                    fact = tool.transfer(vertex.getValue().getStmtList(), fact);

                sum = ((Pegraph) fact).getNumEdges();
                stringBuilder.append(sum);
                stringBuilder.append("\t").append(((Pegraph) fact).getAliasNumEdges(grammar));
            } else {
                stringBuilder.append("0");
            }
            return new Text(stringBuilder.toString());

            // stringBuilder.append(value.gstoretoString()); // GS
            // stringBuilder.append(value.pegtoString()); // S
            // return new Text(stringBuilder.toString());
        }
    }
}

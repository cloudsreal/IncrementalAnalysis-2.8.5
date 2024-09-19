package alias_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import data.Fact;
import data.Tool;
import alias_data.AliasVertexValue;
import alias_data.Pegraph;
import alias_data.Grammar;

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
            /// BufferedReader br = new BufferedReader(readHDFS(grammarPath));
//            BufferedReader br = new BufferedReader(readHDFS(
//                    "hdfs://master-1-1.c-5d52d5e1d3ac3468.cn-hangzhou.emr.aliyuncs.com:9000/grammar"));
            // 116
            BufferedReader br = new BufferedReader(readHDFS(
                    "hdfs://master-1-1.c-9056493f92112533.cn-hangzhou.emr.aliyuncs.com:9000/grammar"));
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
            stringBuilder.append(vertex.getId()).append("\t");

            AliasVertexValue value = (AliasVertexValue) vertex.getValue();

//            Fact fact = vertex.getValue().getFact();
//            int sum = 0;
//            if (fact != null) {
//                sum = ((Pegraph) fact).getNumEdges();
//                stringBuilder.append(sum);
//                stringBuilder.append("\t").append(((Pegraph)fact).getAliasNumEdges(grammar));
//            } else {
//                stringBuilder.append("0");
//            }
            // stringBuilder.append(value.stmtstoString());

             stringBuilder.append(value.gstoretoString());
             stringBuilder.append(value.pegtoString());

            // Fact fact = vertex.getValue().getFact();
            // int sum = 0;
            // if (fact != null) {
            // /// sum = ((Pegraph)fact).getNumEdges();
            // /// stringBuilder.append(sum);

            // /// if(vertex.getValue().getTool() != null){
            // /// AliasTool tool = (AliasTool)(vertex.getValue().getTool());
            // /// Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
            // /// sum = ((Pegraph)out_fact).getNumEdges();
            // /// stringBuilder.append(sum);
            // /// }
            // /// else{
            // /// sum = ((Pegraph)fact).getNumEdges();
            // /// stringBuilder.append(sum);
            // /// }
            // stringBuilder.append("1\t");
            // sum = ((Pegraph)fact).getNumEdges();
            // stringBuilder.append(sum);
            // }
            // else{
            // stringBuilder.append("0");
            // }

            return new Text(stringBuilder.toString());
        }
    }
}

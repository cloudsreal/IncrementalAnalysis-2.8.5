package incre_alias_analysis;

import alias_data.Grammar;
import alias_data.Singletons;
import incre_analysis.MyWorkerContext;
import org.apache.giraph.worker.WorkerContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class IncreAliasWorkerContext extends MyWorkerContext {
    public Singletons singletons;
    public Grammar grammar;

    public InputStreamReader readHDFS(String path) throws IOException
    {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(path), conf);
        FSDataInputStream hdfsInStream = fs.open(new Path(path));
        return new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
    }

    @Override
    public void preApplication() {
//-----------------------------------------------------read from local----------------------------------------------------------------
//        File singletonFile = new File("/Downloads/hadoop-2.5.1/share/hadoop/common/var_singleton_info.txt");
//        File grammarFile = new File("/Downloads/hadoop-2.5.1/share/hadoop/common/rules_pointsto.txt");
//        BufferedReader singletonReader;
//        BufferedReader grammarReader;
//        singletons = new Singletons();
//        grammar = new Grammar();
//        try {
//            singletonReader = new BufferedReader(new FileReader(singletonFile));
//            String s;
//            while((s = singletonReader.readLine()) != null)
//            {
//                singletons.addOneSingleton(Integer.parseInt(s));
//            }
//            singletonReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            grammarReader = new BufferedReader(new FileReader(grammarFile));
//            grammar.loadGrammar(grammarReader);
//            grammar.test();
//            grammarReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//-----------------------------------------------------read from HDFS----------------------------------------------------------------
        String singletonPath = null;
        String grammarPath = null;
        try
        {
//             BufferedReader pa = new BufferedReader(readHDFS("hdfs://localhost:8000/analysis/start"));
            BufferedReader pa = new BufferedReader(readHDFS("hdfs://localhost:8000/client/analysis_conf"));
            /// @szw, configuration according to Ali EMR
//            BufferedReader pa = new BufferedReader(readHDFS("hdfs://xxx.cn-hangzhou.emr.aliyuncs.com:9000/client/analysis_conf"));
//            pa.readLine();
            singletonPath = pa.readLine();
            grammarPath = pa.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        singletons = new Singletons();
        grammar = new Grammar();
        readSingletons(singletonPath, singletons);
        readGrammar(grammarPath, grammar);
        super.preApplication();
    }

    public void readSingletons(String singletonPath, Singletons singletons)
    {
        try {
            BufferedReader br = new BufferedReader(readHDFS(singletonPath));
            String s;
            while((s = br.readLine()) != null)
            {
                singletons.addOneSingleton(Integer.parseInt(s));
            }
            br.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void readGrammar(String grammarPath, Grammar grammar) {
        try {
            BufferedReader br = new BufferedReader(readHDFS(grammarPath));
            grammar.loadGrammar(br);
            // grammar.test();
            br.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

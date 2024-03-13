package incre_analysis;

import org.apache.giraph.master.MasterCompute;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import data.SetWritable;

public class MasterBroadcast extends MasterCompute
{
    // public static String entry = "hdfs://localhost:8000/cache_entrys/entry";
    public static String conf_path = "hdfs://localhost:8000/client/analysis_conf";

    public InputStreamReader readHDFS(String path) throws IOException
    {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(path), conf);
        FSDataInputStream hdfsInStream = fs.open(new Path(path));
        return new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
    }

    public void readEntrys(String entryPath, SetWritable entrys, SetWritable worklist)
    {
        try {
            BufferedReader br = new BufferedReader(readHDFS(entryPath));
            String s;
            while((s = br.readLine())!=null)
            {
                String[] parts = s.split("\t");
                if (parts[1].equals("0")){
                    entrys.addEntry(Integer.parseInt(parts[0]));
                }
                else{
                    worklist.addEntry(Integer.parseInt(parts[0]));
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void compute() {
        // MasterCompute body
        if (getSuperstep() == 0)
        {
            SetWritable entrys = new SetWritable();
            SetWritable worklist = new SetWritable();
            try {
                // BufferedReader start = new BufferedReader(readHDFS("hdfs://localhost:8000/analysis/start"));
                // BufferedReader start = new BufferedReader(readHDFS("hdfs://emr-header-1.cluster-289320:9000/analysis/start"));
                BufferedReader start = new BufferedReader(readHDFS(conf_path));
                String entryPath = start.readLine();
                start.close();
//                readEntrys(entryPath, entrys);
                readEntrys(entryPath, entrys, worklist);
            } catch (IOException e) {
                e.printStackTrace();
            }
            broadcast("entry1", entrys);
//            broadcast("entry2", worklist);
        }
    }

    private void readEntrysLocal(File file, SetWritable entrys) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine())!=null)
            {
                entrys.addEntry(Integer.parseInt(s));
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFields(DataInput arg0) {
        // To deserialize this class fields (global variables) if any
    }

    public void write(DataOutput arg0) {
        // To serialize this class fields (global variables) if any
    }
}


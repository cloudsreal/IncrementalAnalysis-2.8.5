package data_reach;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CommonWrite{
  public  static String file = "/Users/zhangyujin/Desktop/jar/test.txt";
  
  public static void method2(String conent) {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
      out.write(conent+"\r\n");
    } 
    catch (Exception e) {
      e.printStackTrace();
    } 
    finally {
      try {
        out.close();
      } 
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

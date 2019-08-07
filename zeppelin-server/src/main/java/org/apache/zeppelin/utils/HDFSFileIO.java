package org.apache.zeppelin.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zeppelin.notebook.socket.Message;

public class HDFSFileIO {

    public static void writeToHDFS(Message message) throws IOException {
        Configuration conf = new Configuration();
        conf.set("hadoop.job.ugi", "hdfs");

        String fileName = (String) message.get("fileName");
        String userName = message.principal;

        FileSystem fs = FileSystem.get(conf);
        Path outFile;
        // Hadoop DFS Path - & Output file
        if(fileName != null && !fileName.isEmpty())
        {
            outFile = new Path("/user/root/zeppelin/" + fileName);
        } else {
            outFile = new Path("/user/root/zeppelin/testFile" + UUID.randomUUID() + ".txt");
        }

        // Verification
        if (fs.exists(outFile)) {
            System.out.println("Output file already exists");
            throw new IOException("Output file already exists");
        }

        // Create file to write
        FSDataOutputStream out = fs.create(outFile);
        InputStream in = new ByteArrayInputStream(message.data.toString().getBytes());

        byte buffer[] = new byte[message.data.toString().getBytes().length];
        try {
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Error while copying file");
        } finally {
            in.close();
            out.close();
        }
    }
}

package org.apache.zeppelin.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.notebook.socket.Message;
import org.apache.zeppelin.socket.NotebookServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSFileIO {

    private static final Logger LOG = LoggerFactory.getLogger(NotebookServer.class);

    private static ZeppelinConfiguration zConf =  ZeppelinConfiguration.create();

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
            outFile = new Path(zConf.getHDFSPath() + userName + "/" + fileName);
        } else {
            outFile = new Path(zConf.getHDFSPath() + userName + "/testFile" + UUID.randomUUID() + ".txt");
        }

        // Verification
        if (fs.exists(outFile)) {
            LOG.error("Output file already exists");
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
            LOG.error("Error while copying file");
        } finally {
            in.close();
            out.close();
        }
    }

    public static void deleteFromHDFS(Message message) throws IOException {
        Configuration conf = new Configuration();
        conf.set("hadoop.job.ugi", "hdfs");

        // fileName should include path
        String fileName = (String) message.get("fileName");
        String userName = message.principal;

        FileSystem fs = FileSystem.get(conf);

        Path filePath = new Path(fileName);

        // Verification
        if (!fs.exists(filePath)) {
            LOG.error("Tried to delete a file that doesn't exist: " + fileName);
            throw new IOException("Tried to delete a file that doesn't exist: " + fileName);
        }

        try {
            fs.delete(filePath, false);
        } catch (IOException e) {
            System.out.println("Error while deleting file");
        }
    }

    public static HashSet<String> getUploadedFilesForUser() throws IOException {
        Configuration conf = new Configuration();
        conf.set("hadoop.job.ugi", "hdfs");
        final String userName = SecurityUtils.getPrincipal();

        FileSystem fs = FileSystem.get(conf);

        Path hdfsPath = new Path(zConf.getHDFSPath() + userName + "/");

        RemoteIterator<LocatedFileStatus> fileStatusListIterator = fs.listFiles( hdfsPath, false);

        HashSet<String> fileList = new HashSet<>();

        while(fileStatusListIterator.hasNext()){
            LocatedFileStatus fileStatus = fileStatusListIterator.next();
            fileList.add(fileStatus.getPath().toString());
        }

        return fileList;
    }
}

package kr.hs.dgsw.network.test01.n2202.server;

import java.io.*;

public class GetFile {

    DataInputStream dis;
    DataOutputStream dos;

    GetFile(OutputStream os, InputStream is) {
        this.dis = new DataInputStream(new BufferedInputStream(is));
        this.dos = new DataOutputStream(new BufferedOutputStream(os));
    }
    public int reFile(String dir) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(this.dis);
        DataInputStream dis = new DataInputStream(bufferedInputStream);
        try {
            String fileName = dis.readUTF();
            long receiveFileSize = dis.readLong();
            FileOutputStream fos = new FileOutputStream(dir + '/' + fileName);
            byte[] bytes = new byte[8192];
            int readSize = 0;
            int fileSize = 0;
            while (true) {
                readSize = dis.read(bytes);
                fos.write(bytes, 0, readSize);
                fileSize = fileSize + readSize;
                if (fileSize == receiveFileSize) {
                    break;
                }
            }
            fos.close();
            return fileSize;
        } catch (IOException exception) {
            return -1;
        }
    }
    public long receive(String dir) {
        try {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            FileOutputStream fos = new FileOutputStream(dir + fileName);
            int readSize = 0;
            byte[] bytes = new byte[8192];
            while (true) {
                readSize = receiveSize(this.dis, bytes);
                if(readSize == -1) {
                    dos.writeUTF("오류");
                    System.out.println("오류");
                    dos.flush();
                }
                else {
                    dos.writeUTF("OK");
                    dos.flush();
                    fileSize = fileSize - readSize;
                    fos.write(bytes, 0, readSize);
                    if (fileSize == 0) {
                        break;
                    }
                    else if(fileSize < 0) {
                        System.out.println("오류 " + fileSize);
                    }
                }
            }
            return 0;
        } catch (IOException exception) {
            return -1;
        }
    }
     private int receiveSize(DataInputStream dis, byte[] bytes) {
        try {
            int size = dis.readInt();
            int realSize = dis.read(bytes);
            return size == realSize ? size : -1;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
}

package kr.hs.dgsw.network.test01.n2202.server;

import java.io.*;

public class SendFile {
    private String fileName;
    private File file;

    DataInputStream dis;
    DataOutputStream dos;

    SendFile(String fileName, String file, InputStream is, OutputStream os) {
        this.fileName = fileName;
        this.file = new File(file);
        this.dis = new DataInputStream(new BufferedInputStream(is));
        this.dos = new DataOutputStream(new BufferedOutputStream(os));
    }
    public long send() {
        try {
            FileInputStream fis = new FileInputStream(file);
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());
            int readSize = 0;
            int fileSize = 0;
            byte[] bytes = new byte[8192];
            while (true) {
                readSize = fis.read(bytes);
                fileSize = fileSize + readSize;
                if(readSize == -1) {
                    break;
                }
                this.sendByte(bytes, readSize);
            }
            fis.close();
            return fileSize;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
    private void sendByte(byte[] bytes, int length) {
        try {
            while (true) {
                dos.writeInt(length);
                dos.write(bytes, 0, length);
                dos.flush();
                String answer = dis.readUTF();
                if(answer.equals("OK")) {
                    break;
                }
                else {
                    continue;
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

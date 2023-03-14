package kr.hs.dgsw.network.test01.n2202.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws IOException {

        String text = "";

        Scanner scanner = new Scanner(System.in);

        Socket socket = null;
        try {
            socket = new Socket("localhost",5000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream os = socket.getOutputStream();
        PrintWriter pr = new PrintWriter(os, true);
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        System.out.println(br.readLine());
        Login(pr, br);

        while(true){
            text = scanner.nextLine();
            String command = text.split(" ")[0];
            pr.println(text);

            if(command.equals("ls")){
                int rl = Integer.parseInt(br.readLine());
                System.out.println("** File List **");

                for(int i=0;i<rl;i++){
                    System.out.println(br.readLine());
                }
                System.out.printf("%d개 파일\n",rl);
            }
            else if(command.equals("upload")){
                String[] texts = text.split(" ");
                File file = new File(texts[1]);
                if(! file.exists()){
                    System.out.println("파일이 존재하지 않습니다");
                    continue;
                }
                String ret = br.readLine();
                if(ret.equals("retry")){
                    System.out.println("파일이 이미 있습니다. 덮어쓰기 하실건가요??(Y: 덮어쓰기 / N: 업로드 취소):");
                    String ok = scanner.nextLine();
                    pr.println(ok);
                    if(!(ok.equals("y") || ok.equals("Y"))){
                        continue;
                    }
                }
                sendFile(texts[1],texts.length == 2 ?
                        texts[1].split("\\\\")[texts[1].split("\\\\").length - 1]
                        : texts[texts.length - 1],os);
            }
            else if(command.equals("download")){
                if (receiveFile("D:\\download\\", os, is) == -1) { System.out.println("File Download Error"); }
                else {
                    System.out.println("파일 다운로드 완료");
                }
            }
        }

    }
    private static void sendFile(String pathName, String fileName, OutputStream os) throws IOException {
        BufferedOutputStream bor = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bor);
        File fl = new File(pathName);
        System.out.println("파일 전송 시작");
        FileInputStream fis = new FileInputStream(fl);
        dos.writeUTF(fileName);
        dos.writeLong(fl.length());
        int readSize = 0;
        byte[] bytes = new byte[8192];
        while (true) {
            readSize = fis.read(bytes);
            if(readSize == -1) {
                System.out.println(fileName + " 파일을 업로드하였습니다");
                dos.flush();
                break;
            }
            dos.write(bytes, 0, readSize);
        }
        fis.close();
    }

    private static long receiveFile(String dir, OutputStream outputStream, InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        DataInputStream dis = new DataInputStream(bufferedInputStream);
        BufferedOutputStream bor = new BufferedOutputStream(outputStream);
        DataOutputStream dos = new DataOutputStream(bor);
        try {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            FileOutputStream fos = new FileOutputStream(dir + fileName);
            int readSize = 0;
            byte[] bytes = new byte[8192];
            while (true) {
                readSize = receiveBuffer(dis, bytes);
                if(readSize == -1) {
                    dos.writeUTF("오류");
                    System.out.println("오류");
                    dos.flush();
                }
                else {
                    dos.writeUTF("OK");
                    dos.flush();
                    fileSize -= readSize;
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
    static private int receiveBuffer(DataInputStream dis, byte[] bytes) {
        try {
            int length = dis.readInt();
            int realLength = dis.read(bytes);
            return length == realLength ? length : -1;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public static void Login(PrintWriter pw, BufferedReader br) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            String id;
            String password;
            System.out.print("ID: ");
            id = scanner.nextLine();
            System.out.print("PASSWORD: ");
            password = scanner.nextLine();
            pw.println("login&" + id + "&" + password);
            String result = br.readLine();
            System.out.println(result);
            if(result.equals("** FTP 서버에 접속하였습니다 **")) {
                return;
            }
        }
    }
}

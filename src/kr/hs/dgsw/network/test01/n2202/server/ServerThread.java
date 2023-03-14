package kr.hs.dgsw.network.test01.n2202.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

public class ServerThread extends Thread{
    InputStream is = null;
    OutputStream os = null;
    BufferedReader br = null;
    PrintWriter pr = null;

    public ServerThread(Socket socket) throws IOException {

        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
        this.br = new BufferedReader(new InputStreamReader(is));
        this.pr = new PrintWriter(os, true);

        this.start();
    }

    public void run(){
        this.pr.println("** 서버에 접속하였습니다 **");
        this.Login(pr, br);
        while(true){
            String storage = ReceivedData();
            String command = storage.split(" ")[0];
            if(command.equals("ls")){
                var arr = list();

                pr.println(arr.size());

                for (String i : arr) {
                    pr.println(i);
                }

            }
            else if(command.equals("upload")){
                String[] commands = storage.split(" ");
                String filename = commands.length == 2 ?
                        commands[1].split("\\\\")[commands[1].split("\\\\").length - 1]
                        : commands[commands.length - 1];

                File file = new File("D:\\upload\\" + filename);
                System.out.println("D:\\upload\\" + filename);
                if(file.exists()){

                    pr.println("retry");
                    String rd = ReceivedData();
                    if(!(rd.equals("y") || rd.equals("Y"))){
                        continue;
                    }
                }
                else {
                    pr.println("continue");
                }

                GetFile receiveFile = new GetFile(os, is);
                //receiveFile.receive("D:\\upload\\");
                receiveFile.reFile("D:\\upload\\");
                System.out.println(filename);
            }
            else if(command.equals("download")){

                String[] commands = storage.split(" ");
                SendFile sendFile = new SendFile(commands[1],"D:\\upload\\" + commands[1], is, os );
                long size = sendFile.send();
                System.out.println(size);
            }
            else {
                System.out.println(storage);
            }
        }

    }

    public boolean Login(PrintWriter pw, BufferedReader br){
            while (true) {
                String[] receives;
                try {
                    receives = br.readLine().split("&");
                } catch (IOException e) {
                    System.out.println("문자열 파싱 오류");
                    e.printStackTrace();
                    return false;
                }
                if (receives.length != 3) {
                    pw.println("입력이 잘못됐습니다");
                    continue;
                }
                String command = receives[0];
                if (command.equals("login")) {
                    if (receives[1].equals("admin") && receives[2].equals("1234")) {
                        pw.println("** FTP 서버에 접속하였습니다 **");
                        return true;
                    } else pw.println("** ID 또는 PASSWORD가 틀렸습니다 ** ");
                } else pw.println("** 로그인을 먼저 해 주세요 **");
            }
    }


    public ArrayList<String> list(){

        ArrayList<String> array = new ArrayList<>();

        File file = new File("D:\\upload");

        File[] fileName = file.listFiles();

        BasicFileAttributes Attributes; //파일 생성 날짜는 경로 에서 BasicFileAttributes 를 읽어 액세스

        try {

            for(File filename : fileName) {

                Attributes = Files.readAttributes(filename.toPath(), BasicFileAttributes.class);
                FileTime time = Attributes.creationTime();

                if(filename.isDirectory()) {
                    String dir = "<DIR>";
                    System.out.printf("%s %s      %s\n", dir, filename.getName());
                }

                else {

                    String[] byteIni = new String[5]; // byteIni : 바이트 단위 첫 글자

                    byteIni[0] = " ";
                    byteIni[1] = "K";
                    byteIni[2] = "M";
                    byteIni[3] = "G";
                    byteIni[4] = "T";

                    long byteUnit = Files.size(filename.toPath()); // byteUnit : 바이트 단위

                    String by = byteIni[0];

                    if(byteUnit/1024 > 0) {

                        byteUnit /= 1024;
                        by = byteIni[1]; //KB

                        if(byteUnit/1024 > 0) {

                            byteUnit /= 1024;
                            by = byteIni[2]; //MB

                            if(byteUnit/1024 > 0) {

                                byteUnit /= 1024;
                                by = byteIni[3]; //GB

                                if(byteUnit/1024 > 0) {

                                    byteUnit /= 1024;
                                    by = byteIni[4]; //TB

                                }

                            }
                        }
                    }

                    array.add(String.format("%s     %s%sB", filename.getName(), byteUnit, by));

                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }

    public String ReceivedData(){
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

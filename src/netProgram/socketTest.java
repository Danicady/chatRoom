package netProgram;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

public class socketTest {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Socket s = null;
		try {
			s = new Socket("100.0.101.15", 12345);
			ReadThread reader = new ReadThread(s);
			reader.start();
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			String word = sc.nextLine();
			while (!word.equals("quit")) {
				out.println(word);
				word = sc.nextLine();
			}
			reader.stopRun();
			out.println(word);
			reader.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null)
					s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
class ReadThread extends Thread{
	Socket c;
	boolean flag = true;
	
	public ReadThread(Socket c) {
		this.c = c;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(), "utf-8"));
			while(flag){
				System.out.println(in.readLine());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopRun() {
		flag = false;
	}
}

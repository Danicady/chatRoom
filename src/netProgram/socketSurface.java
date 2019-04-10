package netProgram;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class socketSurface extends JFrame{
	
	private JTextField jtf=new JTextField(20);
	private JButton btn=new JButton("����");
	private JButton connectbtn=new JButton("����");
	private JTextArea jta=new JTextArea(30, 40);
	private JScrollPane jsp=new JScrollPane();
	private JPanel jp=new JPanel();
	
	public socketSurface(){
		surfaceInit();
		mainInfo();
	}
	
	private void surfaceInit(){
		
		jsp.add(jta);
		jp.add(jsp);
		jp.add(jtf);
		jp.add(btn);
		jp.add(connectbtn);
		
		this.setContentPane(jp);
		
		this.setTitle("�ͻ��˽���");
		this.setSize(600,600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		jsp.setSize(400,400);
		jsp.setLocation(10, 10);
		
		jtf.setSize(300,50);
		jtf.setLocation(0, 400);
		
		btn.setSize(100, 50);
		btn.setLocation(310, 400);
		
		connectbtn.setSize(100, 50);
		connectbtn.setLocation(420, 400);
		
		jta.setSize(400,400);
		jta.setLocation(10,10);
		
		jp.setSize(550,550);
		jp.setLocation(0, 0);
	}
	
	private void mainInfo(){
		
		//Scanner sc=new Scanner(System.in);
		Socket s = null;
		readThread2 rt = null;
		try {
			//����ָ����ַ��IP+PORT��
			s=new Socket("100.0.101.15",12345);
			//����������һ��������Ϣ���߳�
			rt=new readThread2(s);
			rt.start();
			//�����������ȡ���ͻ���socket�������
			final PrintWriter out=new PrintWriter(s.getOutputStream(),true);
			//String word=sc.nextLine();
			jta.append(rt.brString()+"\n");
			final String word=jtf.getText();
			btn.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					while (!word.equals("quit")){
				//���Ϊ������socket������
				out.println(word);
				//word = sc.nextLine();
					}
					out.println(word);
				}});
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					if (out!=null) {
						out.println("quit");
					}
				}
			});
			rt.stopRun();
			rt.join();//��֤�޸ĵ�flag��ֵ
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(s!=null)
					s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		socketSurface s=new socketSurface();
		s.setVisible(true);
	}
}

class readThread2 extends Thread{
	
	boolean flag=true;
	Socket cSocket;
	String word;
	
	
	public readThread2(Socket cSocket) {
		this.cSocket=cSocket;
	}
	
	@Override
	public void run() {
		super.run();
		//��ȡ�ͻ������ݣ�����̨д��
		BufferedReader br;
		try {
			br=new BufferedReader(new InputStreamReader(cSocket.getInputStream(),"utf-8"));
			while(flag){
//			System.out.println(br.readLine());
			word=br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String brString(){
		return word;
	}
	
	public void stopRun() {
		flag = false;
	}
}

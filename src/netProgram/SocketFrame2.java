package netProgram;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SocketFrame2 extends JFrame {
	private JTextArea jtaMes = new JTextArea();
	private JScrollPane jspMes = new JScrollPane(jtaMes);
	private JButton btnSend = new JButton("Send");
	private JButton btnConnect = new JButton("Connect");
	private JTextField jtfNewMes = new JTextField(20);
	private JPanel panSend = new JPanel();
	private Font font = new Font("ËÎÌו", Font.PLAIN, 20);
	private Socket socket;
	private ObjectOutputStream out;
	private ReadThread reader;

	public SocketFrame2() {
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
		getContentPane().add(jspMes);
		getContentPane().add(panSend, BorderLayout.SOUTH);
	}

	private void init() {
		panSend.add(jtfNewMes);
		panSend.add(btnSend);
		panSend.add(btnConnect);
		jtaMes.setEditable(false);
		jtaMes.setFont(font);
		jtfNewMes.setFont(font);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String str = jtfNewMes.getText().trim();
				
				if (str != null && str.length() > 0 && socket != null) {
					MyMessage mes = new MyMessage(MyMessage.MES_TYPE_PLAIN,true);
					mes.setContent(str);
					sendMes(mes);
				}
			}
		});
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					socket = new Socket("100.0.101.15", 12345);
					reader = new ReadThread(socket);
					reader.start();
					out = new ObjectOutputStream(socket.getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				if (out != null) {
					MyMessage mes = new MyMessage(MyMessage.MES_TYPE_PLAIN,false);
					mes.setContent("quit");
					sendMes(mes);
					reader.stopRun();
				}
			}
		});
	}

	public void sendMes(MyMessage m) {
		if (out != null) {
			try {
				out.writeObject(m);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ReadThread extends Thread {
		Socket c;
		boolean flag = true;

		public ReadThread(Socket c) {
			this.c = c;
		}

		@Override
		public void run() {
			
			try  {
				ObjectInputStream in = new ObjectInputStream((c.getInputStream()));
				MyMessage newMes = (MyMessage) in.readObject();
				while (flag) {
					jtaMes.append(newMes.getContent() + "\n");
					in = new ObjectInputStream((c.getInputStream()));
					newMes = (MyMessage) in.readObject();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopRun() {
			flag = false;
		}
	}

	public static void main(String[] args) {
		new SocketFrame().setVisible(true);
	}

}

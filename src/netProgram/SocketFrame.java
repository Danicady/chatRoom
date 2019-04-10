package netProgram;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SocketFrame extends JFrame {
	JPanel panel_main;
	JButton button = null;
	JButton buttonGetConn = null;
	JTextField jtf = null;
	JTextArea jta = null;
	Socket socket = null;
	ReadThread reader = null;
	ObjectOutputStream out;
	JScrollPane jsp = null;
	//list
	JList<String> clientList=new JList<>();
	JScrollPane jspClientList=new JScrollPane(clientList);

	public SocketFrame() {
		init();
		listener();
	}

	private void init() {
		panel_main = new JPanel();
		button = new JButton("发送");
		buttonGetConn = new JButton("连接");
		jtf = new JTextField(20);
		jta = new JTextArea(18, 40);
		jsp = new JScrollPane(jta);
		jspClientList.setBounds(360,50 ,50, 300);
		jsp.setBounds(50, 50, 300, 300);
		jta.setEditable(false);

		panel_main.add(jsp, BorderLayout.NORTH);
		panel_main.add(jspClientList, BorderLayout.EAST);
		panel_main.add(jtf);
		panel_main.add(button);
		panel_main.add(buttonGetConn);

		this.setContentPane(panel_main);
		this.setSize(800, 420);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void getCon() {
		try {
			socket = new Socket("100.0.101.15", 12345);
			reader = new ReadThread(socket);
			reader.start();
			// out = new PrintWriter(socket.getOutputStream(), true);
			out=new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listener() {

		buttonGetConn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getCon();
				try {
					out = new ObjectOutputStream(socket.getOutputStream());
					reader.stopRun();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if(out!=null){
					MyMessage mes=new MyMessage(MyMessage.MES_TYPE_PLAIN,false);
					mes.setContent("quit");
					sendMes(mes);
				}
			}
			
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*try {
					String text = jtf.getText().toString();
					jtf.setText("");
					if (!text.equals("quit"))
						out.println(text);
					else {
						reader.stopRun();
						out.println(text);
						reader.join();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}*/
				String content=jtf.getText().trim();
				if (content!=null && content.length()>0 && socket!=null){
					MyMessage mes=new MyMessage(MyMessage.MES_TYPE_PLAIN,true);
					mes.setContent(content);
					sendMes(mes);
				}
			}
		});
		
		class MyDialog extends JDialog{
			//发送按钮和聊天框
			JTextArea jTextArea=new JTextArea();
			JScrollPane jScrollPane=new JScrollPane(jTextArea);
			JButton jButton=new JButton("发送");
			JTextField jTextField=new JTextField(20);
			JPanel jPanel=new JPanel();
			String ipString;
			public MyDialog(String ipString) {
				this.ipString=ipString;
				this.setTitle(ipString);
				this.setSize(400, 300);
				this.setLocationRelativeTo(null);
				this.add(jPanel,BorderLayout.SOUTH);
				this.add(jScrollPane);
				smallinit();
				
			}
			private void smallinit() {
				jTextArea.setEditable(false);
				jPanel.add(jTextField);
				jPanel.add(jButton);
				jButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String str=jTextField.getText().trim();
						if (str!=null&&str.length()>0&&socket!=null) {
							MyMessage mes=new MyMessage(MyMessage.MES_TYPE_PLAIN,false);
							mes.setContent(str);
							sendMes(mes);
						}
						
					}
				});
			}
		}
		
		clientList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				if (arg0.getClickCount()==2) {
					new MyDialog(clientList.getSelectedValue());
					
				}
			}
			
		});
	}
	
	public void sendMes(MyMessage ms) {
		if(out!=null){
		try {
			//不能自动刷新
			out.writeObject(ms);
			//调用方法，刷新
			out.flush();
//			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		}
	}
	
	public void updateListClient(ArrayList list) {
		clientList.setModel(new ClientListModel(list));
	}
	
	class ClientListModel extends AbstractListModel{

		ArrayList list;
		
		public ClientListModel(ArrayList list) {
			this.list=list;
		}
		
		@Override
		public Object getElementAt(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getSize() {
			return list.size();
		}
		
	}
		
	
	
	public static void main(String[] args) {
		SocketFrame entry = new SocketFrame();
		entry.setVisible(true);
	}

	class ReadThread extends Thread {
		Socket c;
		boolean flag = true;

		public ReadThread(Socket c) {
			this.c = c;
		}

		@Override
		public void run() {
			ObjectInputStream in;
			try {
				in=new ObjectInputStream(c.getInputStream());
				MyMessage mes=(MyMessage) in.readObject();
				while (flag) {
					in=new ObjectInputStream(c.getInputStream());
					switch (mes.getMesType()) {
					case MyMessage.MES_TYPE_PLAIN:
						jta.append(mes.getContent()+"\n");
						break;
					case MyMessage.MES_TYPE_UPDATE_CLIENTLIST:
						updateListClient(mes.getClientList());
						break;
					default:
						break;
					}
					mes=(MyMessage) in.readObject();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void stopRun() {
			flag = false;
		}
	}
}

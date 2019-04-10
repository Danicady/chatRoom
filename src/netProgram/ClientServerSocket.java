package netProgram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Iterator;

public class ClientServerSocket {

	HashSet<Socket> clientSet=new HashSet<>();//存放引用
	
	int count;
	ServerSocket serverSocket;
	
	public ClientServerSocket(){
		try {
			serverSocket=new ServerSocket(12345);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void work() {
		while (true){
			try {
				Socket sc=serverSocket.accept();
				clientSet.add(sc);
				++count;
				new Mythread(sc,count).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//群发消息方法
	public void massMes(MyMessage mes){
		//迭代器，提供快速遍历的方法
		Iterator<Socket> it=clientSet.iterator();
		while (it.hasNext()) {
			/*try {
				OutputStream outputStream=it.next().getOutputStream();
				PrintWriter out = new PrintWriter(outputStream,true);
				out.println(mes);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			sendMes(it.next(), mes);
		}
	}
	
	public void sendMes(Socket socket,MyMessage ms) {
		try (ObjectOutputStream objout=new ObjectOutputStream(socket.getOutputStream());){
			//不能自动刷新
			objout.writeObject(ms);
			//调用方法，刷新
			objout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public String[] getClientList(){
		/*获取到客户端IP地址，保存在clientArray数组中*/
		String[] clientArray=null;
		if(clientSet.size()>1){
			clientArray=new String[clientSet.size()];
			Iterator<Socket> it=clientSet.iterator();
			int i=0;
			while (it.hasNext()) {
				clientArray[i++]=it.next().getRemoteSocketAddress().toString();
			}
		}
		return clientArray;
	}
	
	public Socket findClientSocket(String ip) {
		Iterator<Socket> it=clientSet.iterator();
		Socket cSocket=null;
		int index=0;
		while(it.hasNext()){
			cSocket=it.next();
			if (ip.equals(cSocket.getRemoteSocketAddress().toString())) {
				return cSocket;
			}
		}
		return cSocket;
	}
	
	class Mythread extends Thread{
		Socket s;
		int count;
		public Mythread(Socket s,int count) {
			this.s=s;
			this.count=count;
		}
		@Override
		public void run() {
			super.run();
			
			try {
				SocketAddress address=s.getRemoteSocketAddress();
				String ip=address.toString().substring(1, address.toString().indexOf(":")+1);
//				PrintWriter out = new PrintWriter(s.getOutputStream(),true);
				ObjectInputStream br=new ObjectInputStream(s.getInputStream());
//				out.println("Hello! No "+count);
//				String string=br.readLine();
				MyMessage mes1=new MyMessage(MyMessage.MES_TYPE_PLAIN,true);
				mes1.setContent("welcome you are no."+count+"; Your ip : "+ip);
				MyMessage mes=(MyMessage)br.readObject(); 
				while(!mes.getContent().equals("quit")){
					switch (mes.getMesType()) {
					case MyMessage.MES_TYPE_PLAIN:
						if(mes1.isIfMass()) massMes(mes);
						else {
							String to=mes.getSendTo();
							Socket s=findClientSocket(to);
							if (s!=null) {
								sendMes(s, mes);
							}
						}
						break;

					default:
						break;
					}
					mes=(MyMessage) br.readObject();
				}
				clientSet.remove(s);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}finally{
				clientSet.remove(s);
			}
	}
}

	public static void main(String[] args) {
		ClientServerSocket cServerSocket=new ClientServerSocket();
		cServerSocket.work();
	}

}



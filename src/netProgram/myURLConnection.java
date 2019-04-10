package netProgram;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class myURLConnection extends JFrame{

	private JList list=new JList();
	private JTextField textField=new JTextField();
	private JScrollPane jsPane=new JScrollPane();
	MyListModel model;
	String url;
	
	public myURLConnection() {
		
		jsPane.setViewportView(list);
		url="ftp://ftp.cs.nsu.edu.cn/";
		model=new MyListModel(url);
//		for(int i =0;i<model.getSize() ;i++){
//			System.out.println(model.getElementAt(i).toString());
//		}
		
		list.setModel(model);
		list.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int cntClick=e.getClickCount();
				if(cntClick==2) {
					//获取双击行的末尾值
					String[] newContent=list.getSelectedValue().toString().split(" ");		
					url += newContent[newContent.length-1];
//					System.out.println(url);
					model=new MyListModel(url);
					list.setModel(model);
				}
			}});
		Init();
	}
		
	
	private void Init() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 600);
		this.setLayout(null);
		this.setLocationRelativeTo(null);
		textField.setSize(500, 30);
		this.add(textField);
		jsPane.setSize(480, 560);
		jsPane.setLocation(4,40);
		this.add(jsPane);
	}

	
	public List<String> list(String url){
		List<String> list =new ArrayList<String>();
		try {
			//构造一个URL
			URL myUrl=new URL(url);
			//连接目标
			URLConnection urlConnection=myUrl.openConnection();
//			//获取目标脚本文件数据
			InputStream inputStream=urlConnection.getInputStream();
			//读入
			BufferedReader bReader=new BufferedReader(new InputStreamReader(inputStream,"gbk"));
			//加入list
			String stringLine=new String();
			while((stringLine=bReader.readLine())!=null)
			list.add(stringLine);
			
			bReader.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
		
	}
	
	class MyListModel extends AbstractListModel{
		List<String> model;
		public MyListModel(String url) {
			model=list(url);
			
		}
		
		@Override
		public Object getElementAt(int arg0) {
			return model.get(arg0);
		}

		@Override
		public int getSize() {
			return model.size();
		}
		
	}
	
	public static void main(String[] args) {
		myURLConnection myConnection=new myURLConnection();
		myConnection.setVisible(true);
	}
}

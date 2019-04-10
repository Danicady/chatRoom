package netProgram;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;


public class cv {
	// ��Ҫcopy���ļ�
			File origin = new File("");
			
	public cv(String url) {
		this.origin=new File(url);
	}
	
		
		// ���Ƶ�ĳ���ļ�
		File desti = new File("C:/Users/DY/Desktop/");
		// ��Ĵ�С
		int blockSize = 1024 * 1024 * 10;

		public void startCopy() {
			// ����ȡ����ȷ���̸߳���
			int blockNum = (int) Math.ceil((double) origin.length() / blockSize);
			// ���߳�ִ��
			for (int i = 0; i < blockNum; i++) {
				new CopyThread(origin, desti, i).start();
			}
		}

		class CopyThread extends Thread {
			File origin, desti;
			// ����������ҪΪblockSize ���������������ڶ�ȡ��ʱ����ܻ�������
			byte[] buftemp = new byte[1024 * 512];
			// blockNoΪ�̱߳�ţ�0123...��
			long blockNo;

			public CopyThread(File origin, File desti, int blockNo) {
				this.origin = origin;
				this.desti = desti;
				this.blockNo = blockNo;
			}

			@Override
			public void run() {
				try (BufferedInputStream bufread = new BufferedInputStream(new FileInputStream(origin));
						// ����Ŀ���ļ�Ȩ��Ϊ�ɶ���д
						RandomAccessFile raf = new RandomAccessFile(desti, "rw")) {
					// ����ĳ���̣߳������ظ���д
					bufread.skip(blockNo);
					// Ѱ�����
					raf.seek(blockNo * blockSize);
					int readlength = 0, writelength = 0;
					// ÿ���߳�ֻ����������Ķ�д��key point:writelength<blockSize
					while ((readlength = bufread.read(buftemp)) > 0 && writelength < blockSize) {
						raf.write(buftemp, 0, readlength);
						writelength += readlength;
					}
					System.out.println(this.getName() + " : " + writelength);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
}

package netProgram;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;


public class cv {
	// 需要copy的文件
			File origin = new File("");
			
	public cv(String url) {
		this.origin=new File(url);
	}
	
		
		// 复制到某个文件
		File desti = new File("C:/Users/DY/Desktop/");
		// 块的大小
		int blockSize = 1024 * 1024 * 10;

		public void startCopy() {
			// 向上取整，确定线程个数
			int blockNum = (int) Math.ceil((double) origin.length() / blockSize);
			// 多线程执行
			for (int i = 0; i < blockNum; i++) {
				new CopyThread(origin, desti, i).start();
			}
		}

		class CopyThread extends Thread {
			File origin, desti;
			// 缓冲区，需要为blockSize 的整数倍，否则在读取的时候可能会多读数据
			byte[] buftemp = new byte[1024 * 512];
			// blockNo为线程编号（0123...）
			long blockNo;

			public CopyThread(File origin, File desti, int blockNo) {
				this.origin = origin;
				this.desti = desti;
				this.blockNo = blockNo;
			}

			@Override
			public void run() {
				try (BufferedInputStream bufread = new BufferedInputStream(new FileInputStream(origin));
						// 设置目标文件权限为可读可写
						RandomAccessFile raf = new RandomAccessFile(desti, "rw")) {
					// 跳过某个线程，避免重复读写
					bufread.skip(blockNo);
					// 寻找起点
					raf.seek(blockNo * blockSize);
					int readlength = 0, writelength = 0;
					// 每个线程只负责该区域块的读写，key point:writelength<blockSize
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

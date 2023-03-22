package AT;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class test1014 {
	public int[][] getImgPixel(String path) {
		File file = new File(
				"C:\\Users\\user\\Desktop\\CAMARA\\88383-eminem-music-hd-4k-minimalism-minimalist-artstation.jpg");
		BufferedImage buffImg = null; // 緩衝圖片
		try {
			buffImg = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int w = buffImg.getWidth();
		int h = buffImg.getHeight();
		// 定義二維陣列，儲存畫素點
		int[][] pixelArray = new int[w][h];
		// 讀取每個位置的畫素點
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int pixel = buffImg.getRGB(i, j); // 獲取每個位置畫素值
				pixelArray[i][j] = pixel;
			}
		}
		System.out.println(pixelArray);
		return pixelArray;
	}

	public void drawImg(String path, Graphics gr) {
		// 得到圖片路徑
		int[][] img = getImgPixel(path);
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				int pixel = img[i][j];
				// 原圖顏色不變
				Color c = new Color(pixel);
				gr.setColor(c);
				// 使用rectangle填充每一個點
				gr.fillRect(i, j, 1, 1);
			}
		}
	}
}
package AT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageProcessingTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new AT();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				// JOptionPane.showMessageDialog(null, "請先選擇一張圖片!!!", "Message",
				// JOptionPane.NO_OPTION);
			}
		});
	}
}

class AT extends JFrame {
	private BufferedImage image;
	BufferedImage image2;

	int DEFAULT_WIDTH = 500;
	int DEFAULT_HEIGHT = 500;

	public AT() { // 視窗大小設定
		setTitle("AT");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		add(new JComponent() {
			public void paintComponent(Graphics g) {
				if (image != null)
					g.drawImage(image, 0, 0, null);
			}
		});
		// GUI元件
		JMenu fileMenu = new JMenu("檔案");
		JMenuItem openItem = new JMenuItem("開啟檔案");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openFile();

			}
		});
		fileMenu.add(openItem);

		JMenuItem exitItem = new JMenuItem("離開");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
//------------------------------------------------------------------------------------------------------
		JMenu editMenu = new JMenu("編輯");
		JMenuItem blur = new JMenuItem("模糊");
		blur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				float[] elements = new float[9];
				for (int i = 0; i < 9; i++)
					elements[i] = 0.1f;// 要浮點數
				convolve(elements);
			}
		});
		editMenu.add(blur);

		JMenuItem sharp = new JMenuItem("銳利");
		sharp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				float[] elements = { 0.0f, -1.0f, 0.0f, -1.0f, 5.f, -1.0f, 0.0f, -1.0f, 0.0f };
				convolve(elements);
			}
		});
		editMenu.add(sharp);

		JMenuItem brighten = new JMenuItem("增亮");
		brighten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				float a = 1.1f;
				// float b = 20.0f;
				float b = 0;
				RescaleOp op = new RescaleOp(a, b, null);
				Filter(op);
			}
		});
		editMenu.add(brighten);

		JMenuItem negative = new JMenuItem("負片");
		negative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				short[] negative = new short[256 * 1];
				for (int i = 0; i < 256; i++)
					negative[i] = (short) (255 - i);
				ShortLookupTable table = new ShortLookupTable(0, negative);
				LookupOp op = new LookupOp(table, null);
				Filter(op);
			}
		});
		editMenu.add(negative);

		JMenuItem rotateItem = new JMenuItem("翻轉");
		rotateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (image == null)
					return;
				AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(5), image.getWidth() / 2,
						image.getHeight() / 2);
				AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
				Filter(op);
			}
		});
		editMenu.add(rotateItem);

		JMenuItem RED = new JMenuItem("強調紅色");
		RED.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				turn_red();
			}
		});
		editMenu.add(RED);

		JMenuItem GREEN = new JMenuItem("強調綠色");
		GREEN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				turn_green();
			}
		});
		editMenu.add(GREEN);
//------------------------------------------------------------------------------------------------------
		JMenu saveMenu = new JMenu("存檔");
		JMenuItem save = new JMenuItem("存檔");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				SaveFile();
			}
		});
		saveMenu.add(save);

		JMenu backMenu = new JMenu("還原");
		JMenuItem back = new JMenuItem("還原");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Back();
			}
		});
		backMenu.add(back);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(saveMenu);
		menuBar.add(backMenu);
		setJMenuBar(menuBar);
	}
//------------------------------------------------------------------------------------------------------

	public void Back() {
		for (int i = 0; i <= image2.getWidth() - 1; i++) {
			for (int j = 0; j <= image2.getHeight() - 1; j++) {
				Color origin = new Color(image2.getRGB(i, j));
				// 重畫一次
				int reset = new Color(origin.getRed(), origin.getGreen(), origin.getBlue()).getRGB();
				image.setRGB(i, j, reset);
			}
		}
		repaint();// 秀出來
	}

	public void openFile() {// 開檔案
		JFileChooser chooser = new JFileChooser();
		BufferedImage img = null;
		chooser.setCurrentDirectory(new File("."));
		String[] extensions = ImageIO.getReaderFileSuffixes();
		chooser.setFileFilter(new FileNameExtensionFilter("Image files", extensions));
		int r = chooser.showOpenDialog(this);
		if (r != JFileChooser.APPROVE_OPTION)
			return;
		try {
			img = ImageIO.read(chooser.getSelectedFile());
			image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
			image2 =this.image;
			image.getGraphics().drawImage(img, 0, 0, null);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e);
		}
		repaint();
	}

	public void turn_green() {
		for (int i = 0; i <= image.getWidth() - 1; i++) {
			for (int j = 0; j <= image.getHeight() - 1; j++) {
				Color origin = new Color(image.getRGB(i, j));
				// 原先的紅綠像素
				int ored = origin.getRed();
				int ogreen = origin.getGreen();
				int minus = 255 - ogreen;
				// 判斷後重製
				if (ogreen > 170 && ored < 200) {// 看不出來的區間
					int reset = new Color(0, (ogreen + (minus / 2)), 0).getRGB();
					image.setRGB(i, j, reset);
				}
			}
		}
		repaint();// 秀出來
	}

	public void turn_red() {
		for (int i = 0; i <= image.getWidth() - 1; i++) {
			for (int j = 0; j <= image.getHeight() - 1; j++) {
				Color origin = new Color(image.getRGB(i, j));
				// 原先的紅綠像素
				int ored = origin.getRed();
				int ogreen = origin.getGreen();
				int minus = 255 - ored;
				// 判斷後重製
				if (ored > 160 && ogreen < 200) {// 看不出來的區間
					int reset = new Color((ored + (minus / 2)), 0, 0).getRGB();
					image.setRGB(i, j, reset);
				}
			}
		}
		repaint();
	}

	private void Filter(BufferedImageOp op) {
		if (image == null)
			return;
		image = op.filter(image, null);
		repaint();
	}

	private void convolve(float[] elements) {
		Kernel kernel = new Kernel(3, 3, elements);
		ConvolveOp op = new ConvolveOp(kernel);
		Filter(op);
	}

	public void SaveFile() {// 存檔嚕~~
		try {
			BufferedImage bi = image;
			String st = JOptionPane.showInputDialog("輸入檔案名稱 ~~");
			File outputfile = new File(st + ".jpg");
			ImageIO.write(bi, "jpg", outputfile);
		} catch (IOException e) {
		}
	}
}
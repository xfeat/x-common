package cn.ocoop.framework.common.util;

import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeGenWrapper;
import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeOptions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

public class Test {


    public static void main(String[] args) {
        long x = System.currentTimeMillis();
        System.out.println(x);
        // 根据本地文件生成待logo的二维码
        try (FileInputStream logo = new FileInputStream(Test.class.getResource("/logo1.jpg").getPath());
             FileInputStream bg = new FileInputStream(Test.class.getResource("/bg.jpg").getPath());
        ) {


            BufferedImage bufferedImage = QrCodeGenWrapper.of("121212")
                    .setW(382)
                    .setPadding(1)
                    .setLogo(logo)
                    .setLogoRate(12)
                    .setLogoStyle(QrCodeOptions.LogoStyle.ROUND)
                    .setLogoBorder(true)
                    .setLogoBgColor(Color.RED)
                    .setBgImg(bg)
                    .setBgOpacity(0.8f)
                    .setBgStyle(QrCodeOptions.BgImgStyle.FILL)
                    .setBgStartX(262)
                    .setBgStartY(580)
                    .asBufferedImage();


            Graphics2D outg = bufferedImage.createGraphics();
            outg.setColor(Color.gray);
            outg.setFont(new Font("微软雅黑", Font.BOLD, 40));// 字体、字型、字号
            int strWidth = outg.getFontMetrics().stringWidth("门店名称门店名称门店名称门店名称");
            outg.drawString("门店名称门店名称门店名称门店名称", 453 - strWidth / 2, bufferedImage.getHeight() - 760);
//            outg.drawString("门店名称", 262, 570);
            outg.dispose();
            bufferedImage.flush();
            System.out.println(bufferedImage);
            ImageIO.write(bufferedImage, "png", new File(Test.class.getResource("/").getPath() + "haha.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("create qrcode error! e: " + e);
        }
        System.out.println(System.currentTimeMillis() - x);

    }
}

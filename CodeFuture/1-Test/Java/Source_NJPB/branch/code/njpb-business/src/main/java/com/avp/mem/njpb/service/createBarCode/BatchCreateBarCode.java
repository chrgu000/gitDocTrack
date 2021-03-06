/**
 * Copyright (c) 2017 Avant-Port All Rights Reserved.
 */
package com.avp.mem.njpb.service.createBarCode;


import com.avp.mem.njpb.entity.view.VwEstate;
import com.avp.mem.njpb.entity.view.VwUserEstateBarCode;
import com.avp.mem.njpb.repository.estate.ObjStationRepository;
import com.avp.mem.njpb.repository.estate.VwEstateRepository;
import com.avp.mem.njpb.util.MagicNumber;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by Amber Wang on 2017/11/27 17:28.
 */

@Service
public class BatchCreateBarCode {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchCreateBarCode.class);

    @Autowired
    BatchCreateStationBarcode batchCreateStationBarcode;

    @Value("${business.service.file.QRCodeImageZip}")
    private String urlQRCodeImageZip;

    @Value("${business.service.file.QRCodeImage}")
    private String urlQRCodeImage;

    @Value("${business.service.file.app}")
    private String urlApp;

    @Autowired
    VwEstateRepository vwEstateRepository;

    @Autowired
    ObjStationRepository objStationRepository;

    public void createBarCode(HttpServletResponse response, @RequestParam(value = "objEstates") List<VwUserEstateBarCode> objEstates) {

//        String zipFile="";
        try {
            int count = 0;
            for (VwUserEstateBarCode objEstate : objEstates) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                @SuppressWarnings("rawtypes")
                Map hints = new HashMap();

                //设置UTF-8， 防止中文乱码
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                //设置二维码四周白色区域的大小
                hints.put(EncodeHintType.MARGIN, 2);
                //设置二维码的容错性
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

                //width:图片完整的宽;height:图片完整的高
                //因为要在二维码下方附上文字，所以把图片设置为长方形（高大于宽）
                int width = MagicNumber.THREE_FIVE_TWO;
                int height = MagicNumber.FIVE_HUNDRED;
                String content = "";
                //二维码表示的内容
                if (objEstate.getEstateTypeId() == 1) {
                    content = "http://www.changxingnanjing.com?qr_code=" + objEstate.getBicycleStakeBarCode() + "|773f3c8f872bf34cc3fe97022720330e";
                } else {
                    content = "http://www.changxingnanjing.com?qr_code=" + objEstate.getBarCodeSn() + "|773f3c8f872bf34cc3fe97022720330e";
                }


                //画二维码，记得调用multiFormatWriter.encode()时最后要带上hints参数，不然上面设置无效
                BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

                //qrcFile用来存放生成的二维码图片（无logo，无文字）
                File logoFile = new File(urlApp + File.separator, "logo.jpg");

                //开始画二维码
                BufferedImage barCodeImage = MatrixToImageWriter.writeToFile(bitMatrix, "jpg");

                //在二维码中加入图片
                LogoConfig logoConfig = new LogoConfig(); //LogoConfig中设置Logo的属性
                BufferedImage image = addLogoQRCode(barCodeImage, logoFile, logoConfig);

                int font = MagicNumber.ONE_EIGHT; //字体大小
                int fontStyle = 1; //字体风格

                //用来存放带有logo+文字的二维码图片
                String newImageWithText = urlQRCodeImage + "/" + objEstate.getStationName() + objEstate.getEstateName() + ".jpg";

                //附加在图片上的文字信息
                String text = "";
                if (objEstate.getEstateTypeId() == 1) {
                    String[] temp = objEstate.getBicycleStakeBarCode().split("");
                    for (int i = 0; i < temp.length; i++) {
                        if (i < temp.length - 1) {
                            text += temp[i] + " ";
                        } else {
                            text += temp[i];
                        }
                    }
                } else {
                    String[] temp = objEstate.getBarCodeSn().split("");
                    for (int i = 0; i < temp.length; i++) {
                        if (i < temp.length - 1) {
                            text += temp[i] + " ";
                        } else {
                            text += temp[i];
                        }
                    }

                }
                String estateTypeName = "";
                String[] temp = objEstate.getEstateName().split("");
                for (int i = 0; i < temp.length; i++) {
                    if (i < temp.length - 1) {
                        estateTypeName += temp[i] + " ";
                    } else {
                        estateTypeName += temp[i];
                    }
                }


                //在二维码下方添加文字（文字居中）
                pressText(text, newImageWithText, image, fontStyle, Color.black, font, width, height, estateTypeName);

            }

            String zipFile = urlQRCodeImageZip + File.separator + "temp" + ".zip";
            zip(urlQRCodeImage, zipFile);

            InputStream fis = new BufferedInputStream(new FileInputStream(zipFile));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
          response.setContentType("application/octet-stream");
            //如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
            response.setHeader("Content-Disposition", "attachment;filename=bar.zip");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();

            File tempImage = new File(urlQRCodeImage);
            File[] tempImages = tempImage.listFiles();
            for (int i = 0; i < tempImages.length; i++) {
                tempImages[i].delete();
            }
            File tempZip = new File(zipFile);
            tempZip.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 给二维码图片添加Logo
     *
     * @param barCodeImage
     * @param logoPic
     */
    public static BufferedImage addLogoQRCode(BufferedImage barCodeImage, File logoPic, LogoConfig logoConfig) {
        try {
            if (!logoPic.isFile()) {
                System.out.print("file not find !");
                throw new IOException("file not find !");
            }

            /**
             * 读取二维码图片，并构建绘图对象
             */
            Graphics2D g = barCodeImage.createGraphics();

            /**
             * 读取Logo图片
             */
            BufferedImage logo = ImageIO.read(logoPic);

            int widthLogo = barCodeImage.getWidth() / logoConfig.getLogoPart();
            int heightLogo = barCodeImage.getWidth() / logoConfig.getLogoPart(); //保持二维码是正方形的

            // 计算图片放置位置
            int x = (barCodeImage.getWidth() - widthLogo) / 2;
            int y = (barCodeImage.getHeight() - heightLogo) / 2 - MagicNumber.FIVE_ZERO;


            //开始绘制图片
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.drawRoundRect(x, y, widthLogo, heightLogo, MagicNumber.TEN, MagicNumber.TEN);
            g.setStroke(new BasicStroke(logoConfig.getBorder()));
            g.setColor(logoConfig.getBorderColor());
            g.drawRect(x, y, widthLogo, heightLogo);

            g.dispose();
            return barCodeImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param pressText 文字
     * @param newImg    带文字的图片
     * @param image     需要添加文字的图片
     * @param fontStyle
     * @param color
     * @param fontSize
     * @param width
     * @param height
     * @为图片添加文字
     */
    public static void pressText(String pressText, String newImg, BufferedImage image, int fontStyle, Color color, int fontSize, int width, int height, String estateTypeName) {

        //计算文字开始的位置
        //x开始的位置：（图片宽度-字体大小*字的个数）/2
        int startX = (width - (fontSize * pressText.length())) - MagicNumber.TEN;
        //y开始的位置：图片高度-（图片高度-图片宽度）/2
        int startY = height - (height - width) / 2 - MagicNumber.THREE_ZERO;


        try {
//            File file = new File(targetImg);
//            Image src = ImageIO.read(file);
            int imageW = image.getWidth();
            int imageH = image.getHeight();
//            BufferedImage image = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(image, 0, 0, imageW, imageH, null);
            g.setColor(color);
            g.setFont(new Font("宋体", Font.BOLD, MagicNumber.THREE_ZERO));
            g.drawString(pressText, startX + MagicNumber.TWO_ONE, startY);
//            String[] t = estateTypeName.split("");
           g.drawString(estateTypeName, startX + MagicNumber.FIVE_ZERO, startY + MagicNumber.THREE_ZERO);
            g.dispose();


            FileOutputStream out = new FileOutputStream(newImg);

            ImageIO.write(image, "JPEG", out);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

            encoder.encode(image);

            out.close();

//            System.out.println("image press success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


    public static void zip(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            LOGGER.error("创建ZIP文件失败 {}", e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LOGGER.error("创建ZIP文件失败 {}", e);
            }
        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if (file.exists()) {
            if (file.isDirectory()) {//处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                for (File f : files) {
                    writeZip(f, parentPath, zos);
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[MagicNumber.ONE_ZERO_TWO_FOUR];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                } catch (FileNotFoundException e) {
                    LOGGER.error("创建ZIP文件失败 {}", e);
                } catch (IOException e) {
                    LOGGER.error("创建ZIP文件失败 {}", e);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        LOGGER.error("创建ZIP文件失败", e);
                    }
                }
            }
        }
    }


    public void createBarCodeFirst() {
        List<Integer> tempkk = new ArrayList<>();
        tempkk.add(2);
        tempkk.add(MagicNumber.SIX);
        tempkk.add(MagicNumber.TWO_ONE);
        List<VwEstate> objEstates = vwEstateRepository.findByEstateTypeIdIn(tempkk);

        try {
            int count = 0;
            for (VwEstate objEstate : objEstates) {
                String name = objEstate.getStationNo() + objEstate.getStationName();

                String nameCorp = objEstate.getCorpName();

                File dirYM = new File(urlQRCodeImage + "/" + nameCorp);
                if (dirYM != null && !dirYM.exists()) {
                    dirYM.mkdir();
                }
                String localRoot = dirYM.getPath() + File.separator;

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                @SuppressWarnings("rawtypes")
                Map hints = new HashMap();

                //设置UTF-8， 防止中文乱码
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                //设置二维码四周白色区域的大小
                hints.put(EncodeHintType.MARGIN, 2);
                //设置二维码的容错性
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

                //width:图片完整的宽;height:图片完整的高
                //因为要在二维码下方附上文字，所以把图片设置为长方形（高大于宽）
                int width = MagicNumber.THREE_FIVE_TWO;
                int height = MagicNumber.FIVE_HUNDRED;
                String content = "";
                //二维码表示的内容
                if (objEstate.getEstateTypeId() == 1) {
                    content = "http://www.changxingnanjing.com?qr_code=" + objEstate.getBicycleStakeBarCode() + "|773f3c8f872bf34cc3fe97022720330e";
                } else {
                    content = "http://www.changxingnanjing.com?qr_code=" + objEstate.getEstateSn() + "|773f3c8f872bf34cc3fe97022720330e";
                }


                //画二维码，记得调用multiFormatWriter.encode()时最后要带上hints参数，不然上面设置无效
                BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

                //qrcFile用来存放生成的二维码图片（无logo，无文字）
                File logoFile = new File(urlApp + File.separator, "logo.jpg");

                //开始画二维码
                BufferedImage barCodeImage = MatrixToImageWriter.writeToFile(bitMatrix, "jpg");

                //在二维码中加入图片
                LogoConfig logoConfig = new LogoConfig(); //LogoConfig中设置Logo的属性
                BufferedImage image = addLogoQRCode(barCodeImage, logoFile, logoConfig);

                int font = MagicNumber.ONE_EIGHT; //字体大小
                int fontStyle = 1; //字体风格

                //用来存放带有logo+文字的二维码图片
                String newImageWithText = localRoot + "/" + objEstate.getStationName() + objEstate.getEstateName() + ".png";

                //附加在图片上的文字信息
                String text = "";
                if (objEstate.getEstateTypeId() == 1) {
                    String[] temp = objEstate.getBicycleStakeBarCode().split("");
                    for (int i = 0; i < temp.length; i++) {
                        if (i < temp.length - 1) {
                            text += temp[i] + " ";
                        } else {
                            text += temp[i];
                        }
                    }
                } else {
                    String[] temp = objEstate.getEstateSn().split("");
                    for (int i = 0; i < temp.length; i++) {
                        if (i < temp.length - 1) {
                            text += temp[i] + " ";
                        } else {
                            text += temp[i];
                        }
                    }

                }
                String estateTypeName = "";
                String[] temp = objEstate.getEstateName().split("");
                for (int i = 0; i < temp.length; i++) {
                    if (i < temp.length - 1) {
                        estateTypeName += temp[i] + " ";
                    } else {
                        estateTypeName += temp[i];
                    }
                }


                //在二维码下方添加文字（文字居中）
                pressText(text, newImageWithText, image, fontStyle, Color.black, font, width, height, estateTypeName);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

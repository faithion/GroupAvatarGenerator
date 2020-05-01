package com.eha.wishtree.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 群头像生成器<br>
 * 无头像数量限制1~n均可
 *
 * @author Ion
 */
public class GroupAvatarGenerator {
    /**
     * 画布底色
     */
    private Color gapColor;
    /**
     * 期望的头像大小
     */
    private int baseSize;
    /**
     * 间距
     */
    private int gap;
    /**
     * 成员头像大小
     */
    private int avatarSize;

    /**
     * 一个默认参数的生成器
     */
    public final static GroupAvatarGenerator groupAvatarGenerator = new GroupAvatarGenerator(new Color(219, 223, 225), 340, 10);

    /**
     * 自定义参数生成
     *
     * @param gapColor 间隙颜色
     * @param baseSize 底片大小
     * @param gap      间隙宽
     */
    public GroupAvatarGenerator(Color gapColor, int baseSize, int gap) {
        this.gapColor = gapColor;
        this.baseSize = baseSize;
        this.gap = gap;
    }

    /**
     * 通过url图片生成
     *
     * @param avatars
     * @return 生成的群头像
     * @throws IOException
     */
    public BufferedImage generateGroupAvatarByURL(List<URL> avatars) throws IOException {
        if(avatars.isEmpty())throw new IllegalArgumentException("avatars is empty");
        //先获取所有的群成员头像
        List<BufferedImage> groupAvatars = new ArrayList<>();
        for (URL avatar : avatars) {
            groupAvatars.add(ImageIO.read(avatar));
        }
        return generateGroupAvatarByBufferedImage(groupAvatars);
    }

    /**
     * 通过bufferedImage生成
     *
     * @param avatars
     * @return 生成的群头像
     */
    public BufferedImage generateGroupAvatarByBufferedImage(List<BufferedImage> avatars) {
        if(avatars.isEmpty())throw new IllegalArgumentException("avatars is empty");
        //基础画布
        BufferedImage baseImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < baseImage.getWidth(); i++) {
            for (int j = 0; j < baseImage.getHeight(); j++) {
                baseImage.setRGB(i, j, gapColor.getRGB());
            }
        }
        int cnt = avatars.size();
        int col = (int) Math.ceil(Math.sqrt(cnt));
        avatarSize = (baseSize - (col + 1) * gap) / col;
        //调整大小
        List<BufferedImage> groupAvatars = avatars.stream().map(x -> zoomBySize(avatarSize, avatarSize, x)).collect(Collectors.toList());
        int row = (int) Math.ceil(cnt / (col / 1.));
        //上下偏移
        int py = (baseSize - ((row * avatarSize) + (row - 1) * gap)) / 2;
        int cntIndex = 0;
        //遍历每一排
        for (int i = 0; i < row; i++) {
            //填充第一排,单独处理
            if (i == 0) {
                int row1ColNum = cnt - col * (row - 1);
                int row1AvatarsWidth = row1ColNum * avatarSize + (row1ColNum - 1) * gap;
                int px = (baseSize - row1AvatarsWidth) / 2;
                for (int j = 0; j < row1ColNum; j++) {
                    fillImage(px + j * (avatarSize + gap), py, baseImage, groupAvatars.get(cntIndex++));
                }
            } else {
                //填充其余的
                for (int j = 0; j < col; j++) {
                    fillImage(gap + j * (avatarSize + gap), py + i * (avatarSize + gap), baseImage, groupAvatars.get(cntIndex++));
                }
            }
        }
        return baseImage;
    }

    /**
     * 改变大小
     */
    private static BufferedImage zoomBySize(int width, int height, Image img) {
        Image _img = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(_img, 0, 0, null);
        graphics.dispose();
        return image;
    }

    /**
     * 填充图片
     *
     * @param px        横偏移,x
     * @param py        纵偏移,y
     * @param baseImage 底片
     * @param tinyImage 要填充的图片
     */
    private static void fillImage(int px, int py, BufferedImage baseImage, BufferedImage tinyImage) {
        for (int j = py, y = 0; y < tinyImage.getHeight(); j++, y++) {
            for (int i = px, x = 0; x < tinyImage.getWidth(); i++, x++) {
                baseImage.setRGB(i, j, tinyImage.getRGB(x, y));
            }
        }
    }

}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Test{
	public static void main (String[] args) throws Exception{
		BufferedImage bi=ImageIO.read(new File("emoji.jpg"));
		List<BufferedImage> avatars=new ArrayList<>();
		for(int i=0;i<5;i++){
			avatars.add(bi);
		}
		BufferedImage groupAvatar=GroupAvatarGenerator.groupAvatarGenerator.generateGroupAvatarByBufferedImage(avatars);
		ImageIO.write(groupAvatar,"JPEG",new File("groupAvatar.jpg"));
	}
}
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.*;

public class Output {
     double[][] block;
     String name;
    int bit;//大图每行每列几个
    int smallSize;//小图压缩后单位
    int smallMounts;//仓库中所有小图数量
      double grays[];
      public Output(String bigPhoto,int smallMounts,int bit,int smallSize) throws IOException {
          name=bigPhoto;
          this.bit=bit;
          this.smallSize=smallSize;
          this.smallMounts=smallMounts;
          grays = new double[smallMounts];
          for(int i=0;i<smallMounts;i++){
              //将所有图片灰度值储存下来
              grays[i]=calculate(System.getProperty("user.dir")+"/src/smallPhotos/"+(i+1)+".jpg");
          }
          //将大图压缩
          File imageFile = new File(System.getProperty("user.dir")+"/src/bigPhotos/"+bigPhoto);
          BufferedImage image = ImageIO.read(imageFile);
          image =resize(image,bit,bit);

          int width =image.getWidth();
          int height =image.getHeight();
          block =new double[ width][height];
          System.out.println(width);
         //计算每个像素点的灰度值 并储存下来
          for(int i=0;i<width;i++){
              for(int j=0;j<height;j++){
                  int rgb=image.getRGB(i,j)& 0xFFFFFF;
                  double r=(rgb & 0xff0000)>>16;
                  double g=(rgb &0xff00)>>8;
                  double b=(rgb & 0xff);

                  double gray=(r*0.3+g*0.6+b*0.1);
                  block[i][j]=gray;


              }
          }
         finish();
      }
      //拼接图片
      private void finish() throws IOException {

          BufferedImage ImageNew = new BufferedImage(smallSize*bit, smallSize*bit, BufferedImage.TYPE_INT_RGB);
          int height_i = 0;
          int width_i = 0;
          int [] ImageArrays = new int[smallSize * smallSize];
          for (int i = 0; i < block.length; i++) {
              for(int j=block[i].length-1;j>=0;j--){
                  for(int y=0;y<grays.length;y++){
                      //找到平均灰度值与该像素点的灰度值相近的图片 替代该像素点的位置
                      if(Math.abs( grays[y]-block[i][j])<2){
                          File imageFile = new File(System.getProperty("user.dir")+"/src/smallPhotos/"+(y+1)+".jpg");
                          BufferedImage image = ImageIO.read(imageFile);
                          image =resize(image,smallSize,smallSize);
                          ImageArrays = image.getRGB(0,0,image.getWidth(),image.getHeight(),ImageArrays,0,image.getWidth());
                          break;
                      }
                  }
                  ImageNew.setRGB(width_i, height_i, smallSize, smallSize, ImageArrays, 0, smallSize);
                  width_i+=smallSize;

              }
              width_i=0;
              height_i+=smallSize;
              System.out.println(i+"ok");
          }


          ByteArrayOutputStream out = new ByteArrayOutputStream();
          ImageIO.write(ImageNew, "jpg", out);// 图片写入到输出流中

          ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
          BufferedImage newImage = ImageIO.read(in);
          File outputfile = new File(System.getProperty("user.dir")+"/src/results/"+name+"——one.jpg");
          ImageIO.write(newImage, "jpg", outputfile);


      }
      //计算平均灰度值
      private double calculate(String Photo) throws IOException {
          File imageFile = new File(Photo);
          BufferedImage image = ImageIO.read(imageFile);
          image =resize(image,smallSize,smallSize);
          double sum=0;
          image.getSource();
          int width =image.getWidth();
          int height =image.getHeight();

          for(int i=0;i<width;i++){
              for(int j=0;j<height;j++){
                  int rgb=image.getRGB(i,j)& 0xFFFFFF;
                  double r=(rgb & 0xff0000)>>16;
                  double g=(rgb & 0xff00)>>8;
                  double b=(rgb & 0xff);

                  int gray=(int)(r*0.3+g*0.6+b*0.1);
                  sum += gray * 1.0/ (width * height) ;
              }
          }

          return sum;
      }
      //压缩图片
    private static BufferedImage resize(BufferedImage source, int targetW,
                                        int targetH) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        // 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
        // 则将下面的if else语句注释即可
//        if (sx < sy) {
//            sx = sy;
//            targetW = (int) (sx * source.getWidth());
//        } else {
//            sy = sx;
//            targetH = (int) (sy * source.getHeight());
//        }
        if (type == BufferedImage.TYPE_CUSTOM) { // handmade
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
                    targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else
            target = new BufferedImage(targetW, targetH, type);
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }
    public static void main(String[] args) throws IOException {
          Output output =new Output("1.jpg",275,200,16);
          System.out.println("ok");
      }
}

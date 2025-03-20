import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Collide {
	static int width=1080, height=1080,
			 n=14,start=0,it=1440*10;
	static double k=2000,g=3,delt=0.02,visc=.99;
	static String name="circle";
	static BufferedImage image=new BufferedImage( width,height,BufferedImage.TYPE_4BYTE_ABGR);
public static void main(String[] blurps)
	{
		ArrayList<Object> object=Object.random(n,width,height);
		object.add(new Object(Object.circle,new double[] {540,540},new double[] {0,0},300));
		object.add(new Object(Object.square,new double[] {700,540},new double[] {0,0},100));
		for(int i=start;i<start+it;i++)
		{
		//	if(i%24==0)
		//	object.add(Object.random(width,height));
			System.out.println(i);
			draw(object,i); 
			
			for(Object obj: object)
			{ 
				
				//bounce off each other
				for(Object subj: object)if(subj!=obj)
				{
					obj.touch(subj);
				
				}
			}	
			for(Object obj:object)obj.moveOn();
			dampen();
		//	System.out.println(object.get(0).loc[1]);
			
		}
	}
	private static void dampen() {
		double f=0.8;
		for(int i=0;i<width;i++)for(int j=0;j<height;j++)
		{	Color c=new Color(image.getRGB(i, j));
			image.setRGB(i,j,new Color((int)(c.getRed()*f),(int)(c.getGreen()*f),(int)(c.getBlue()*f)).getRGB());
		}
		
	
	}
	private static double length(double[] r) {
		double out=0;
		for(int i=0;i<r.length;i++)out+=r[i]*r[i];
		return Math.sqrt(out);
	}
	private static void draw(ArrayList<Object> object,int t) {
		for(Object obj : object)
		{
			obj.draw(image);
			
		}
		
		File outputfile = new File(name+SolarSystem.df.format(t)+".png");
		try 
		{
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			System.out.println("IOException");System.out.println("couldn't print");
			e.printStackTrace(); 
		}
	}
}

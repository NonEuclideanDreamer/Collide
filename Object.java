import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Object {
	 String type;
	double[] loc,vel,n;
	double size,mass, or, torque;
	int color;
	boolean gravity=true;
	static int i=0,
			white=Color.white.getRGB();
	static int[] colors= {new Color(255,0,0).getRGB(),new Color(255,128,0).getRGB(),Color.yellow.getRGB(),Color.green.getRGB(),Color.cyan.getRGB(),Color.blue.getRGB(),new Color(128,0,255).getRGB()};
	static int c=colors.length;
	
	static double k=Collide.k,g=Collide.g,delt=Collide.delt,visc=Collide.visc,factor=Math.pow(visc, delt),a=3;
	static final String circle="circle",disk="disk", wall="wall", square="square";;//
	static String[]types={circle,disk,square};//square,disk,
	public Object(String t, double[]l,double[]v,double s)
	{
		loc=l.clone();
		vel=v;
		size=s;
		type=t;
		color=i;
		n=dir(0);
		i++;
	}
	public Object(String t, double[]l,double[]v,double s,double orj)
	{
		loc=l.clone();
		vel=v;
		size=s;
		type=t;
		color=i;
		i++;
		or=orj;
		n=dir(orj);
	}
	public static Object wall(int nr, int width,int height )
	{
		String t=wall;
		int[]dim= {width,height};
		double[]l=new double[2], v=new double[2];
		int or=nr%2,side=nr/2;
		double size=dim[or]/2,
				orj=(1-nr)*Math.PI/2;
		l[or]=dim[or]/2;
		l[1-or]=(1-Math.pow(-1, side))*dim[1-or]/2;	
		Object out= new Object(t,l,v,size,orj);
		out.n=dir(orj);
	//	out.addv(out.n,30);
		print(out.n);
		return out;
	}
	public void setOrientation(double orj)
	{
		or=orj;
		n=dir(orj);
	}
	
	private static void print(double[] v) {
		System.out.print("{");
		for(int i=0;i<v.length;i++)
			System.out.print(v[i]+",");
		System.out.println("}");
	}
	public static ArrayList<Object> random(int n, int width, int height)
	{
		Random rand=new Random();
		ArrayList<Object> out=new ArrayList<Object>();
		for(int i=0;i<4;i++)
			out.add(wall(i,width,height));
		for(int i=0;i<n;i++)
		{
			
			String tp=types[rand.nextInt(types.length)];
			out.add(new Object(tp,new double[] {rand.nextDouble(width),rand.nextDouble(height)},
					new double[] {rand.nextDouble(1)*0,rand.nextDouble(1)*0},rand.nextDouble()*rand.nextDouble()*rand.nextDouble()*rand.nextDouble()*rand.nextDouble()*800+rand.nextDouble()*50));
		}
		return out;
	}

	public double[] dir(Object subj) {
		double[]out=new double[loc.length];
		for(int i=0;i<out.length;i++)
			out[i]=subj.loc[i]-loc[i];
		return out;
	}
	
	public void touch(Object subj)
	{
		switch(type)
		{
		case circle:
			switch(subj.type)
			{
			case circle:
				double[]r=dir(subj);
				double d=length(r);
				double b=d-Math.abs(subj.size-size);
				if(d<Math.abs(subj.size-size)) {}
				else if(d<subj.size||d<size) {
					addv(r,strength(b)/d);
				}
				else if(d<size+subj.size) {
					b=size+subj.size-d;
					addv(r,- strength(b)/d);
				} 
				break;
			case disk:	
				r=dir(subj);
				 d=length(r);
				 if(d>size+subj.size) {}
				 else if(d<size && subj.size<size)
				 {
					 if(d>size-subj.size)
					 {
						 b=d-Math.abs(subj.size-size);
						 addv(r,strength(b)/d);
					 }
				 }
				 else if(d<size+subj.size)
				 {
					 b=size+subj.size-d;
						addv(r,- strength(b)/d);
				 }
				 break;
				 
			case square:
				
				
				double[][]ro=VG.rot(subj.or), back=VG.rot(-subj.or);
				
				r=dir(subj);
				double[]r1=VG.times(back, r);
				d=r1[0];
				b=r1[1];
				if(Math.pow(subj.size- Math.abs(b),2)+Math.pow(subj.size- Math.abs(d),2)>size*size&&(Math.abs(b)>size+subj.size||Math.abs(d)>size+subj.size||b*b+d*d>subj.size*subj.size+2*subj.size*size+size*size)) {}//fully apart
				else if(Math.pow(subj.size+ Math.abs(b),2)+Math.pow(subj.size+ Math.abs(d),2)<size*size) {}//square fully in circle
				else {
					double sgn=-1;
					if(size*size>2*subj.size*subj.size&&b*b+d*d<size*size)sgn=1;
						double excess=-Math.sqrt(Math.pow(subj.size+ Math.abs(b)*sgn,2)+Math.pow(subj.size+ Math.abs(d)*sgn,2))+size;
						
						if(Math.abs(b)<subj.size||Math.abs(d)<subj.size)
						{
							excess=subj.size+size-Math.max(Math.abs(b), Math.abs(d));
							
							double[]dir=dir(subj);
							
							if(Math.abs(b)>Math.abs(d))
							{
							//	System.out.print("flipped: ");
								if(sgn>0)
								{
									double sqrt=Math.sqrt(size*size-Math.pow(Math.abs(d)+subj.size, 2));
									excess=-Math.abs(b)-subj.size+sqrt;
								}
								
								dir=flipflop(subj.n);
								if(b<0)excess*=-1;
							}
							else
							{
								if(sgn>0)
								{
									double sqrt=Math.sqrt(size*size-Math.pow(Math.abs(b)+subj.size, 2));
									excess=-Math.abs(d)-subj.size+sqrt;
								}
								dir=subj.n;
								if(d<0)excess*=-1;
							}
							//System.out.print("case hor, excess="+excess+"dir=");print(dir);
							addv(dir,strength(-excess));
							VG.normalize(r);
							subj.addv(VG.times(r,scalar(r,dir)),strength(excess));
							subj.torque+=VG.cross(r,dir)*strength(excess)/subj.size;
						}
						else
						{
							
							double[]dir= {d-subj.size*Math.signum(d),b-subj.size*Math.signum(b)};
							dir=VG.times(ro, dir);
							VG.normalize(dir);
							//System.out.print("case diag, excess="+excess+"dir=");print(dir);
							addv(dir,strength(-excess));
							VG.normalize(r);
							subj.addv(VG.times(r,scalar(r,dir)),strength(excess));
							subj.torque+=VG.cross(r,dir)*strength(excess)/subj.size;
						}
				
				}
				break;
			case wall:
				double dis=scalar(subj.n,subj.dir(this))-size;
				if(dis<0)
				{
					//System.out.print("touching wall with n="); print(subj.n);
					addv(subj.n,-strength(dis*2));
				}
			default: ;
			}
			break;
		case disk:
			switch(subj.type)
			{
			case disk:
				double[]r=dir(subj);
				double d=length(r);
				double b;
				 if(d<size+subj.size) {
					b=size+subj.size-d;
					addv(r,- strength(b)/d);
				} 
				break;
			case circle:	
				r=dir(subj);
				 d=length(r);
				 if(d>size+subj.size) {}
				 else if(d<subj.size && subj.size>size)
				 {
					 if(d>subj.size-size)
					 {
						 b=d-Math.abs(subj.size-size);
						 addv(r,strength(b)/d);
					 }
				 }
				 else if(d<size+subj.size)
				 {
					 b=size+subj.size-d;
						addv(r,- strength(b)/d);
				 }
				 break;
		
			case square:
				double[][]ro=VG.rot(subj.or), back=VG.rot(-subj.or);
				
				r=dir(subj);
				double[]r1=VG.times(back, r);
				d=r1[0];
				b=r1[1];
				if(Math.pow(subj.size- Math.abs(b),2)+Math.pow(subj.size- Math.abs(d),2)>size*size&&(Math.abs(b)>size+subj.size||Math.abs(d)>size+subj.size||b*b+d*d>subj.size*subj.size+2*subj.size*size+size*size)) {}//fully apart
				else {
					double sgn=-1;
					
						double excess=-Math.sqrt(Math.pow(subj.size+ Math.abs(b)*sgn,2)+Math.pow(subj.size+ Math.abs(d)*sgn,2))+size;
				
						if(Math.abs(b)<subj.size||Math.abs(d)<subj.size)//Math.pow(subj.size+Math.abs(b),2)+Math.pow(subj.size+ Math.abs(d),2)<size*size||Math.pow(subj.size-Math.abs(b),2)+Math.pow(subj.size-Math.abs(d),2)>size*size)
						{
							excess=subj.size+size-Math.max(Math.abs(b), Math.abs(d));
							
							double[]dir=dir(subj);
							if(Math.abs(b)>Math.abs(d))
							{
							//	System.out.print("flipped: ");
								dir=flipflop(subj.n);
								if(b<0)excess*=-1;
							}
							else
							{
								dir=subj.n;
								if(d<0)excess*=-1;
							}
						//	System.out.print("case hor, excess="+excess+"dir=");print(dir);
							addv(dir,strength(-excess));
							VG.normalize(r);
							subj.addv(VG.times(r,scalar(r,dir)),strength(excess));
							subj.torque+=VG.cross(r,dir)*strength(excess)/subj.size;
						}
						else
						{
							
							double[]dir= {d-subj.size*Math.signum(d),b-subj.size*Math.signum(b)};
							dir=VG.times(ro, dir);
							VG.normalize(dir);
							//System.out.print("case diag, excess="+excess+"dir=");print(dir);
							addv(dir,strength(-excess));
							VG.normalize(r);
							subj.addv(VG.times(r,scalar(r,dir)),strength(excess));
							subj.torque+=VG.cross(r,dir)*strength(excess)/subj.size;
						}
				
				}
				break;
			case wall:
				double dis=scalar(subj.n,subj.dir(this))-size;
				if(dis<0)
				{
					//System.out.print("touching wall with n="); print(subj.n);
					addv(subj.n,-strength(dis));
				}
			default: ;
		}	
			break;
		case square:
			switch(subj.type)
			{
			case square:
				if(this==subj)return;
				double[]dir=dir(subj),r=dir.clone(),m=flipflop(n),M=flipflop(subj.n);
				if(dir[0]*dir[0]+dir[1]*dir[1]>2*(size*size+subj.size*subj.size))return; //apart for sure
				double da=scalar(n,dir),db=scalar(subj.n,dir),ba=scalar(m,dir),bb=scalar(M ,dir),
						nn=scalar(n,subj.n),nm=scalar(n,M),mn=scalar(m,subj.n),mm=scalar(m,M),
						dis1=size+subj.size*(Math.abs(nn)+Math.abs(nm))-da,disf1=size+subj.size*(Math.abs(mn)+Math.abs(mm))-ba,
						dis2=subj.size+size*(Math.abs(nn)+Math.abs(mn))-db, disf2=subj.size+size*(Math.abs(nm)+Math.abs(mm))-bb,
						dis=Math.min(Math.min(dis1, dis2), Math.min(disf1, disf2));
				
				if(dis1==dis) {dir=n; dis*=Math.signum(da);}
				else if(dis2==dis) {dir=subj.n; dis*=Math.signum(db);}
				else if(disf1==dis) {dir=m; dis*=Math.signum(ba);}
				else {dir=M; dis*=Math.signum(bb);}
				subj.addv(VG.times(r,scalar(r,dir)),strength(dis));
				subj.torque+=VG.cross(r,dir)*strength(dis)/subj.size;
				break;
			
			case wall:
				double d=scalar(n,subj.n)*size, b=scalar(flipflop(n),subj.n)*size;
				dis=scalar(subj.n,subj.dir(this))-Math.abs(d)-Math.abs(b);
				if(dis<0)
				{
					double[] x=new double[2],y=new double[2];
					if(-dis<2*Math.abs(d)) {x[0]=size*(-dis/d-Math.signum(d));x[1]=-size*Math.signum(b);}
					else if(-dis<2*(Math.abs(b)+Math.abs(d))) {x[1]=((-dis-2*Math.abs(d))/b-Math.signum(d))*size;x[0]=size*Math.signum(d);}
					else x[0]=1e10;
					if(-dis<2*Math.abs(b)){y[1]=size*(-dis/b-Math.signum(b));y[0]=-size*Math.signum(d);}
					else if(-dis<2*size*(Math.abs(b)+Math.abs(d))) {y[0]=((-dis-2*Math.abs(b))/d-Math.signum(b))*size;y[1]=size*Math.signum(b);}
				
					if(x[0]==1e10)addv(subj.n,-strength(dis));
					else
					{
						dis/=2;
						double[][]ro=VG.rot(or+Math.atan2(x[1], x[0])),rob=VG.rot(-or-Math.atan2(x[1], x[0]));
						double[]v=VG.times(rob, subj.n);
						addv(VG.times(ro, VG.proj(v, 0)),-dis);
						torque+=v[1]*strength(dis)/size;
						
						ro=VG.rot(or+Math.atan2(y[1], y[0]));rob=VG.rot(-or-Math.atan2(y[1], y[0]));
						v=VG.times(rob, subj.n);
						addv(VG.times(ro, VG.proj(v, 0)),-dis);
						torque+=v[1]*strength(dis)/size;
					}
				}
				break;
				
				default:;
			}
		default:;}
		
		
	}
	public double strength(double b) {
	//	return k*b*g/size*delt+Math.signum(b)*1;
		if(b<0) return  k*Math.atan(b*g/size)*delt-10;
		return k*Math.atan(b*g/size)*delt+10;
	}
	
	private double[] flipflop(double[] v) {
		// TODO Auto-generated method stub
		return new double[] {-v[1],v[0]};
	}
	private static double scalar(double[] a, double[] b) {
		double out=0;
		for(int i=0;i<a.length;i++)out+=a[i]*b[i];
		return out;
	}
	private double length(double[] r) 
	{		
		double out=0;
		for(int i=0;i<r.length;i++)out+=r[i]*r[i];
		return Math.sqrt(out);
	}

	public boolean distance(Object obj,double[]v,double[]pos)
	{
		return true;
	}

	public void addv(double[] r, double d) {
		for(int i=0;i<r.length;i++)
			vel[i]+=r[i]*d;
	}

	public void moveOn() {
		
	for(int i=0;i<loc.length;i++) {
			if(type!=wall)
			vel[i]*=factor;
		
		}	
//	print(loc);
	torque*=factor;
	if(gravity&&!(type==wall)) {int sign=1; if(type==circle)sign=-1;
		vel[1]+=a*delt*sign;
	}
		setOrientation(or-torque*delt);
		double v=norm(vel),c=1080;
	//	System.out.println("v="+v+", gamma="+1/Math.sqrt(1-v*v/c/c));
		for(int i=0;i<loc.length;i++) {
			
			loc[i]+=vel[i]/Math.sqrt(1+v*v/c/c)/2*delt;
		}
	}

	private double norm(double[] vel2) {
		double out=0;
		for(int i=0;i<vel.length;i++)out+=vel[i]*vel[i];
		
		return Math.sqrt(out);
	}
	
	static double[]dir(double angle)
	{
		return new double[] {Math.cos(angle),Math.sin(angle)};
	}
	public static Object random(int width,int height) {
		
		Random rand=new Random();
		String tp=types[rand.nextInt(types.length)];
		int sign=1;
		if(tp==disk)sign=-1;
		double s=rand.nextDouble()*rand.nextDouble()*rand.nextDouble()*rand.nextDouble()*400+50;
		return new Object(tp,new double[] {rand.nextDouble(width),height/2+sign*(height/2+s)},
				new double[] {rand.nextDouble(1)*0,rand.nextDouble(1)*0},s);
	
	}
	public void draw(BufferedImage image)
	{
		switch(type)
		{
		case circle:
			for(int i=0;i<2*Math.PI*size;i++)
				try {		image.setRGB((int)(loc[0]+size*Math.cos(i/size)),(int)(loc[1]+size*Math.sin(i/size)),colors[color%c]);
				image.setRGB(1+(int)(loc[0]+size*Math.cos(i/size)),(int)(loc[1]+size*Math.sin(i/size)),colors[color%c]);
				
				}catch (ArrayIndexOutOfBoundsException whatever) {}
			break;
		case disk:
			for(int i=(int) (-size); i<size;i++)
			{
				int s=(int) Math.sqrt(size*size-i*i);
				for(int j=-s;j<=s;j++)
					try {		image.setRGB((int)(loc[0]+i),(int)(loc[1]+j),colors[color%c]);
					}catch (ArrayIndexOutOfBoundsException whatever) {}
			}
			break;
/*		case wall:
			for(int i=(int) -size;i<size;i++)
				for(int j=0;j<2;j++)
					try {		image.setRGB((int)(loc[0]+j+n[1]*i),(int)(loc[1]+j-n[0]*i),white);
					}catch (ArrayIndexOutOfBoundsException whatever) {}
			break;*/
		case square:
			for(int i=(int) -size;i<size;i++)
				for(int j=(int) -size;j<size;j++)
					try {		image.setRGB((int)(loc[0]+i*n[0]-j*n[1]),(int)(loc[1]+j*n[0]+i*n[1]),colors[color%c]);
					}catch (ArrayIndexOutOfBoundsException whatever) {}
				break;
		}
	}
}

//tools of vector spaces on arrays
public class VG {

	static double[][]rot(double alpha)
	{
		return new double[][]{ {Math.cos(alpha),-Math.sin(alpha)},{Math.sin(alpha),Math.cos(alpha)}};
	}
	
	static double[]times(double[][]m,double[]v)
	{
		double[]out=new double[m.length];
		
		for(int i=0;i<m.length;i++)
		for(int j=0;j<v.length;j++)
			out[i]+=m[i][j]*v[j];
			
		return out;
	}
	static double[] add(double[]v,double[]w)
	{
		double[]out=new double[v.length];
		
		for(int i=0;i<v.length;i++)
			out[i]=v[i]+w[i];
		return out;
	}
	
	static void normalize(double[]v)
	{
		double norm=norm(v);
		for(int i=0;i<v.length;i++)
			v[i]/=norm;
	}
	
	static double norm(double[]r)
	{
		double out=0;
		for(int i=0;i<r.length;i++)out+=r[i]*r[i];
		return Math.sqrt(out);
	}
	
	static double angle(double[]v,double[]w)
	{
		return Math.atan2(w[1]-v[1], w[0]-v[0]);
	}

	public static double[] proj(double[] v, int i) {
		double[]out=new double[v.length];
		out[i]=v[i];
		return out;
	}

	public static double[] times(double[] r, double scalar) {
		double[] out=new double[r.length];
		for(int i=0;i<r.length;i++)out[i]=r[i]*scalar;
		return out;
	}

	public static double cross(double[] v, double[] w) {
		// TODO Auto-generated method stub
		return v[0]*w[1]-v[1]*w[0];
	}
}

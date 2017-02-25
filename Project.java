package recommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
* The Recommender System program implements an application that
* generates predictions based on the training data.
*
* @author  Sandesh Nimhan
* @version 1.0
* @date 11-28-2016
*/
public class Project {

	/**
	   * This is the main method which processes training data.
	   * @param args Unused.
	   * @return Nothing.
	   * @exception IOException On input error.
	   * @exception FileNotFoundException
	   */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str;
		int[][] ratingMatrix=new int[944][1683];
		double[][] itemSimilarityMatrix=new double[1683][1683];
		int[] averageItemRating=new int[1683];
		int[] ratingCount=new int[1683];
		try {
			
			FileReader fr=new FileReader("train_all_txt.txt");
			BufferedReader br=new BufferedReader(fr);
			FileWriter fw=new FileWriter("output.txt");
			BufferedWriter bw=new BufferedWriter(fw);
			String[] line;
			int[] data=new int [3];
			int i,j,k,l,m,u;
			/**
			 * Reading training data from file and storing into a data structure
			 */
			while((str=br.readLine())!=null)
			{
				line=str.split("\\s+");
				for(i=0;i<3;i++)
				{
					data[i]=Integer.parseInt(line[i]);
				}
				ratingCount[data[1]]++;
				ratingMatrix[data[0]][data[1]]=data[2];
			}
			/**
			 * Calculating average of all items ratings
			 */
			for(j=1;j<1683;j++)
			{
				for(i=1;i<944;i++)
				{
					averageItemRating[j]+=ratingMatrix[i][j];
				}
				if(ratingCount[j]!=0)
					averageItemRating[j]/=/*943*/ratingCount[j];
				else
					averageItemRating[j]=0;
			}
			
			/**
			 * Calculating Pearson(Correlation)-based similarity Coefficient
			 * @algorithm Item-based collaborative filtering which is a Model-based algorithm
			 * to find similarity between items
			 */
			double simNum=0,simDen1=0,simDen2=0;
			double simDen=0;
			
			for(i=1;i<1683;i++)
			{
				for(j=1;j<1683;j++)
				{
					if(i!=j)
					{
						for(u=1;u<944;u++)
						{
							if(ratingMatrix[u][i]!=0 && ratingMatrix[u][j]!=0)
							{
								simNum+=(ratingMatrix[u][i]-averageItemRating[i])*(ratingMatrix[u][j]-averageItemRating[j]);
								simDen1+=(ratingMatrix[u][i]-averageItemRating[i])*(ratingMatrix[u][i]-averageItemRating[i]);
								simDen2+=(ratingMatrix[u][j]-averageItemRating[j])*(ratingMatrix[u][j]-averageItemRating[j]);
							}
						}
						simDen=Math.sqrt(simDen1)*Math.sqrt(simDen2);
						itemSimilarityMatrix[i][j]=simNum/simDen;
					}
				}
			}
			
			/*
			 * Calculating Predictions using the idea of Weighted sum
			 * */
			double predNum=0,predDen=0;
			double pred=0;
			String prediction;
			for(u=1;u<944;u++)
			{
				for(i=1;i<1683;i++)
				{
					if(ratingMatrix[u][i]==0)
					{
						for(j=1;j<1683;j++)
						{
							if(ratingMatrix[u][j]!=0 && i!=j)
							{
									predNum+=itemSimilarityMatrix[i][j]*ratingMatrix[u][j];
									predDen+=Math.abs((itemSimilarityMatrix[i][j]));
							}
						}
						pred=predNum/predDen;
						prediction=Double.toString(Math.floor(pred));
						Double d=0.0;
						
						if(pred>4.0)
							d=5.0;//d=Math.ceil(pred);
						else if(pred<=4.0 && pred>3.0)
							d=4.0;
						else if(pred<=3.0 && pred>2.0)
							d=3.0;//d=Math.floor(pred);
						else if(pred<=2.0 && pred>1.0)
							d=2.0;
						else if(pred<=1.0)
							d=1.0;
						
						pred=0;predNum=0;predDen=0;
						ratingMatrix[u][i]=d.intValue();
					}					
				}
			}
			/**
			 * Writing output into file
			 */
			for(i=1;i<944;i++)
			{
				for(j=1;j<1683;j++)
				{
					bw.write(i+" "+j+" "+ratingMatrix[i][j]);
					//bw.write(" "+ratingMatrix[i][j]+" ");
					bw.newLine();
					bw.flush();
				}
			}
			fr.close();
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
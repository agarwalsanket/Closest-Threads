/* 
 * Closest.java 
 * 
 * Version: 
 *     $Id$
 * 
 * Revisions: Initial Version
 *    
 */
/**
 * Closest.java is a class which finds the distance between different points,
 *  number of points being provided. It calculate the minimum distance, and the pair 
 *  having the minimum distance, both using single threads and multiple threads.
 *
 * @author Sanket Agarwal
 *
 */
import java.util.ArrayList;
import java.util.Random;

public class Closest implements Runnable{
	int x;
	int y;
	int z;
	int NoOfProcessors;
	int NoOfPoints;
	int NoOfProcessorsTemp = 0;
	double minDistanceT = 1000;
	int PerProcessorJob = 0;
	long timeStarted = 0;
	long timeEnd = 0;
	long TimeAvg = 0;
	private ArrayList<ArrayList<Integer>> pointsSet = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> point;
	ArrayList<Integer> PointTempFirst = null;
	ArrayList<Integer> PointTempSec = null;

	public Closest(int NoOfProcessors, int NoOfPoints){
		this.NoOfProcessors = NoOfProcessors;
		this.NoOfPoints = NoOfPoints;
		NoOfProcessorsTemp = NoOfProcessors;
		PerProcessorJob = NoOfPoints/NoOfProcessors;
	}

	public void pointsUnique(){

		Random rand = new Random();
		for(int i=0; i < NoOfPoints; i++){
			point = new ArrayList<Integer>(3);
			x = rand.nextInt(50);
			y = rand.nextInt(50);
			z = rand.nextInt(50);
			point.add(x);
			point.add(y);
			point.add(z);
			if(!pointsSet.contains(point)){
				pointsSet.add(point);	
			}
		}
		/*for(int j=0; j < nPoints ;j++){
		System.out.println(pointsSet.get(j));
	}*/
		//shortestDistancePair();
	}

	public void shortestDistancePair(){
		double minDistance = 1000;
		double Distance = 0;
		double DistanceX = 0;
		double DistanceY = 0;
		double DistanceZ = 0;
		timeStarted = System.currentTimeMillis();
		for(int i=0; i < pointsSet.size()-1; i++){
			for(int j=i+1; j < pointsSet.size(); j++){
				DistanceX = Math.pow((pointsSet.get(i).get(0)-pointsSet.get(j).get(0)), 2);
				DistanceY = Math.pow((pointsSet.get(i).get(1)-pointsSet.get(j).get(1)), 2);
				DistanceZ = Math.pow((pointsSet.get(i).get(2)-pointsSet.get(j).get(2)), 2);
				Distance = Math.sqrt(DistanceX + DistanceY + DistanceZ);
				//System.out.println(Distance);
				if(Distance < minDistance){
					minDistance = Distance;
					PointTempFirst = pointsSet.get(i);
					PointTempSec   = pointsSet.get(j);
				}

			}
		}
		timeEnd = System.currentTimeMillis() ;
		System.out.println("The shortest distance is "+ minDistance);
		System.out.println("Single Thread: ");
		System.out.print("("+PointTempFirst.get(0)+"/"+PointTempFirst.get(1)+"/"+PointTempFirst.get(2)+")");
		System.out.print(" ");
		System.out.print("("+PointTempSec.get(0)+"/"+PointTempSec.get(1)+"/"+PointTempSec.get(2)+")");
		System.out.print(" : " +(timeEnd-timeStarted)+" ms");
	}


	public synchronized void shortestDistancePairThreads(){	
		double Distance = 0;
		//double DistanceX = 0;
		//double DistanceY = 0;
		//double DistanceZ = 0;
		int TempILimit = 0;
		int TempI = (NoOfProcessors-NoOfProcessorsTemp)*(PerProcessorJob);
		if((NoOfPoints%NoOfProcessors == 0) && NoOfProcessorsTemp == 1 ){
			TempILimit = (NoOfProcessors-NoOfProcessorsTemp+1)*(PerProcessorJob)-1;
		}
		else{
			TempILimit = (NoOfProcessors-NoOfProcessorsTemp+1)*(PerProcessorJob);
		}

		timeStarted = System.currentTimeMillis();
		for(int i=TempI; i < TempILimit; i++){
			for(int j=i+1; j < pointsSet.size(); j++){
				//DistanceX = Math.pow((pointsSet.get(i).get(0)-pointsSet.get(j).get(0)), 2);
				//DistanceY = Math.pow((pointsSet.get(i).get(1)-pointsSet.get(j).get(1)), 2);
				//DistanceZ = Math.pow((pointsSet.get(i).get(2)-pointsSet.get(j).get(2)), 2);
				Distance = Math.sqrt(Math.pow((pointsSet.get(i).get(0)-pointsSet.get(j).get(0)), 2) + Math.pow((pointsSet.get(i).get(1)-pointsSet.get(j).get(1)), 2) + Math.pow((pointsSet.get(i).get(2)-pointsSet.get(j).get(2)), 2));
				//System.out.println(Distance);
				if(Distance < minDistanceT){
					minDistanceT = Distance;
					PointTempFirst = pointsSet.get(i);
					PointTempSec   = pointsSet.get(j);
				}

			}
		}
		timeEnd = System.currentTimeMillis() ;
		TimeAvg += (timeEnd-timeStarted);
		if(NoOfProcessorsTemp==1){
			System.out.println();
			System.out.println("The shortest distance is "+ minDistanceT);
			System.out.println(NoOfProcessors+ " Threads");
			System.out.print("("+PointTempFirst.get(0)+"/"+PointTempFirst.get(1)+"/"+PointTempFirst.get(2)+")");
			System.out.print(" ");
			System.out.print("("+PointTempSec.get(0)+"/"+PointTempSec.get(1)+"/"+PointTempSec.get(2)+")");
			System.out.print(" : " +TimeAvg +" ms");
		}

		NoOfProcessorsTemp--;
	}






	@Override
	public void run() {
		shortestDistancePairThreads();

	}
	public static void main(String[] args){
		int NoOfPoints=0;
		try {
			NoOfPoints = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Argument" + args[0] + " must be an integer.");
			System.exit(1);
		}

		if(NoOfPoints < 2){
			System.out.println("At least two points needed.") ;
			System.exit(1);
		}
		Runtime r = Runtime.getRuntime();
		int noOfProcessors = r.availableProcessors();
		Closest c = new Closest(noOfProcessors,NoOfPoints);
		c.pointsUnique();
		c.shortestDistancePair();
		for(int i=0;i<noOfProcessors;i++){
			(new Thread(c)).start();
		}

	}

}


/*
 * It becomes beneficial to use more than one threads, when the job to be done
 * can be divided into multiple parts and shared with all the available threads
 * to finish the job individually by each thread.
 * 
 * In our program, we have achieved this by assigning  calculation of the minimum distance
 * between different points, in different parts and assigning them to the available processors.
 * So, the work load on a single main processor reduces, which increases the efficiency.
 * 
 */ 

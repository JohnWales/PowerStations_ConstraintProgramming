

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class EnergyData {

   private int demand;  //minimum expected energy demand
   private int numStations;  //number of power stations
   private int numRegions;  //number of geographic regions
   private int[] costs;  //daily cost (if active) for each station
   private int[] maxEnergy;  //maximum daily energy output per station
   private int[] maxActive; //max number of active stations per region
   private int[][] serve;  //a numRegions x numStations matrix of {0,1}, where
   // a 1 indicates that region is served by that station

   public EnergyData(String filename) throws IOException {
      /*
       * Assumes data is in file in the following format:
       *    demand number-of-stations  number-of-regions
       * followed by the costs per station on a new line
       * followed by the max energy output per station on a new line
       * followed by the max active stations per region on a new line
       * followed by the numRegions rows of the serve matrix
       */
      Scanner scanner = new Scanner(new File(filename));
      demand = scanner.nextInt();
      numStations = scanner.nextInt();
      numRegions = scanner.nextInt();

      costs = new int[numStations];
      for (int station = 0; station < numStations; station++) {
         costs[station] = scanner.nextInt();
      }

      maxEnergy = new int[numStations];
      for (int station = 0; station < numStations; station++) {
         maxEnergy[station] = scanner.nextInt();
      }

      maxActive = new int[numRegions];
      for (int region = 0; region < numRegions; region++) {
         maxActive[region] = scanner.nextInt();
      }

      serve = new int[numRegions][numStations];
      for (int region = 0; region < numRegions; region++) {
         for (int station=0;station<numStations;station++){
            serve[region][station] = scanner.nextInt();
         }
      }
      scanner.close();
   }

   public int getDemand() { // d is the energy demand
      return demand;
  }

   public int getNumStations() { // p is the total number of power stations
      return numStations;
  }

  public int getNumRegions() { // g is number of geographic regions
      return numRegions;
  }

  public int[] getCosts() {	// ci is then the daily cost of running power station i
      return costs;
  }

  public int[] getMaxEnergy() { // ei is the energy output from station i
      return maxEnergy;
  }

  public int[] getMaxActive() { // mj is the maximum number of stations able to be active in region j
      return maxActive;
  }

  public int[][] getServe() { // rji is then 1 is power station i is located in region j and is 0 if it is located elsewhere
      return serve;
  }

}

import java.io.IOException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.Solver;


public class Assignment1 {
	
	public static void main(String[] args) throws IOException {
		
		// Create the model
		Model model = new Model("Power Stations Problem");
		EnergyData energydata = null; // Call EnergyData.java so I can access its variables

		
		
		try {
			energydata = new EnergyData("src/energy0.txt"); // Pass name of the text file in the parenthesis
//			energydata = new EnergyData("src/energy1.txt"); // Pass name of the text file in the parenthesis
//			energydata = new EnergyData("src/energy2.txt"); // Pass name of the text file in the parenthesis


		}
		catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Cannot read in the specified file");
		}
		


		
// Constants
		int d = energydata.getDemand(); // energy demand
		int p = energydata.getNumStations(); // total number of power stations
		int g = energydata.getNumRegions(); // number of geographic regions
		int[] ci = energydata.getCosts(); // daily cost of running power station i
		int[] ei = energydata.getMaxEnergy(); // energy output from station i
		int[] mj = energydata.getMaxActive(); // maximum number of stations able to be active in region j
		int[][] serve = energydata.getServe(); // 1 is power station i is located in region j, and is 0 if it is located elsewhere
		

		
		
// Create Variables

		// Loops through each station and outputs maximum possible energy output of all stations
		// Used to create the maximum range of the totalEnergyOutput for the model
		int maxEnergyOutput = 0;
		for (int station=0; station<ei.length; station++) {
			maxEnergyOutput += ei[station];	
		}
		
		// Total energy output of all stations for the model. Ranges from 0 to the maximum possible energy output of all stations
		IntVar totalEnergyOutput = model.intVar("Total Energy Output", 0, maxEnergyOutput);

		// Loops through each station and outputs the maximum possible cost of all stations
		// Creating a max cost variable so it can be added to the model to put a limit on the cost range
		int maxCost = 0;
		for (int station=0; station<ci.length; station++) {
			maxCost += ci[station];	
		}
		
		// Total cost of all stations for the model. Ranges from 0 to the maximum possible cost
		IntVar totalCost = model.intVar("Total Cost", 0, maxCost);

		// Iterating through the matrix to by accessing each station in every region and calculating the number of active stations
		// Used to set the limit for the active stations in a region. An array by region
		int[] stationsInRegion = new int[g];
		for (int region=0; region<g; region++) {
			int stationCount = 0;
			for (int station=0; station<p; station++)  {
				if (serve[region][station] == 1) {
					stationCount += 1;
				}
			}
			stationsInRegion[region] = stationCount;
		}
//		1 0 1 0 0 0
//		0 1 0 1 0 0
//		0 0 0 0 1 1
		
		// Array of stations for the model that are either not active or active ( 0 or 1 )
		// This defines the solution for the active stations which meet the constraint criteria
		// This array is used to calculate both the cost and energy output for the solution
		IntVar[] selectedStations = model.intVarArray("Selected Stations: ", p, 0, 1);		
		
		// Count of active stations in a region
		IntVar[] countActiveStationsInRegion = new IntVar[g]; 
	
		// Active stations in the region ranges from 0 and the count of stations in the region
		for (int region=0; region<g; region++) {
			countActiveStationsInRegion[region] = model.intVar("Total Active Stations in Region: " + region, 0, stationsInRegion[region]);
		}
		
		// Sets the number of stations that can be active in a certain region for the model
		IntVar[] maxActiveStationsInRegion = model.intVarArray(mj.length, mj);
			
	
		
		
// Post Constraints		
		
		// Count of active stations in a region <= max number of stations allowed to operate in the region
		for (int region=0; region<g; region++) {
			model.arithm(countActiveStationsInRegion[region], "<=", maxActiveStationsInRegion[region]).post();
		}
		
		// Scalar which calculates the energy output of active stations by multiplying the energy output by 1 or 0 ( The selectedStations array )
		for (int station=0; station<p; station++) {
			model.scalar(selectedStations, ei, "=", totalEnergyOutput).post();
		}
		
		// Scalar which calculates the cost of active stations by multiplying the cost of operating by 1 or 0 ( The selectedStations array )
		for (int station=0; station<p; station++) {
			model.scalar(selectedStations, ci, "=", totalCost).post();
		}
		
		// Energy output must be >= to energy demand
		model.arithm(totalEnergyOutput, ">=", d).post();
		
		// Minimising the total cost
		model.setObjective(Model.MINIMIZE, totalCost);
		
		

		
// Solve the Problem
		Solver solver = model.getSolver();
		
		// Using while loop to find the optimal solution
		while (solver.solve()) {
			System.out.println("------------------------------------------");
			System.out.println("Solution Count: " + solver.getSolutionCount());
			System.out.println(totalCost);
			System.out.println(totalEnergyOutput);
			
			for (int i=0; i<selectedStations.length; i++) {
				if (selectedStations[i].getValue() == 1) {
					System.out.println("Station: " + i + " \tEnergy: " + ei[i] + " \tCost: " + ci[i]); // add power and cost 
				}
			}
		}
	}
}



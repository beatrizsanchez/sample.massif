package massif.validation;

import br.com.embraer.massif.commandevaluation.exception.MatlabPropertiesException;
import br.com.embraer.massif.commandevaluation.exception.MatlabRMIException;
import hu.bme.mit.massif.simulink.Block;
import hu.bme.mit.massif.simulink.SimulinkModel;
import hu.bme.mit.massif.simulink.api.Importer;
import hu.bme.mit.massif.simulink.api.exception.SimulinkApiException;

public class SimulinkExplorer {

	private static String matlabPath = "/Applications/MATLAB_R2017b.app/";
	
	private static String modelName = Models.AUTO_CLIMATE.name;
	private static String modelPath = Models.AUTO_CLIMATE.path;
	
	private static String exportName = "export_example";
	private static String exportPath = "out/";

	
	public static void main(String[] args) throws MatlabRMIException, MatlabPropertiesException, SimulinkApiException {
		
		System.out.println("Initiating communication with MATLAB");
		SimulinkMassifHandler simulinkMassifHandler = new SimulinkMassifHandler(matlabPath);;
		
		System.out.println("Importing");
		Importer importer = simulinkMassifHandler.importer(modelName, modelPath);
		
		System.out.println("EMF Model would be saved in " + importer.getDefaultSavePath());
		SimulinkModel model;
		try{
			model = SimulinkMassifHandler.getSimulinkModel(importer);
		} catch (SimulinkApiException e) {
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("Model FQN: " + model.getSimulinkRef().getFQN());
		System.out.println("Model Qualifier: " + model.getSimulinkRef().getQualifier());
		
		System.out.println("Printing blocks: ");
		for (Block block : model.getContains()) {
			System.out.println(block);
		}
		
		String emfFileNameWithoutExtension = "";
		try{
			//exporter = simulinkMassifHandler.exportModel(model);
			simulinkMassifHandler.exportModel(emfFileNameWithoutExtension);
		} catch (SimulinkApiException e) {
			e.printStackTrace();
			throw e;
		}
		
		//exporter.saveSimulinkModel(model.getSimulinkRef().getFQN(), model.getSimulinkRef().getQualifier());

	}
	
	private enum Models {
		AUTO_CLIMATE("sldemo_auto_climate_elec", "/Applications/MATLAB_R2017b.app/toolbox/simulink/simdemos/automotive"),
		TRAFFIC_LIGHTS("sf_traffic_light", "/Applications/MATLAB_R2017b.app/toolbox/stateflow/sfdemos");
		
		private String name;
		private String path;
		
		Models(String name, String path){
			this.name = name;
			this.path = path;
		}
	}
	
}
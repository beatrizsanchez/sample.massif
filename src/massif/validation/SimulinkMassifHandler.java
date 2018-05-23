package massif.validation;

import br.com.embraer.massif.commandevaluation.client.MatlabClient;
import br.com.embraer.massif.commandevaluation.exception.MatlabPropertiesException;
import br.com.embraer.massif.commandevaluation.exception.MatlabRMIException;
import hu.bme.mit.massif.communication.ICommandEvaluator;
import hu.bme.mit.massif.communication.command.MatlabCommandFactory;
import hu.bme.mit.massif.communication.commandevaluation.CommandEvaluatorImpl;
import hu.bme.mit.massif.communication.matlabcontrol.CommandEvaluatorMCImpl;
import hu.bme.mit.massif.simulink.SimulinkModel;
import hu.bme.mit.massif.simulink.api.Exporter;
import hu.bme.mit.massif.simulink.api.Importer;
import hu.bme.mit.massif.simulink.api.ModelObject;
import hu.bme.mit.massif.simulink.api.exception.SimulinkApiException;
import hu.bme.mit.massif.simulink.api.util.ImportMode;

public class SimulinkMassifHandler {

	private ICommandEvaluator commandEvaluator; 
	private MatlabCommandFactory commandFactory;
	
	/**
	 * CONSTRUCTORS
	 */
	SimulinkMassifHandler(String matlabPath) {
		commandEvaluator = new CommandEvaluatorMCImpl(matlabPath);
		initMatlabCommandFactory();
	}

	SimulinkMassifHandler(String hostAddress, int hostPort, String serviceName) throws MatlabRMIException {
		commandEvaluator = new CommandEvaluatorImpl(new MatlabClient(hostAddress, hostPort, serviceName));
		initMatlabCommandFactory();
	}
	
	SimulinkMassifHandler(String matlabVersion, String matlabPid) throws MatlabRMIException, MatlabPropertiesException {
		commandEvaluator= new CommandEvaluatorImpl(new MatlabClient(matlabVersion, matlabPid));
		initMatlabCommandFactory();
	}
	
	SimulinkMassifHandler(String matlabVersion, String matlabPid, String configPath) throws MatlabRMIException, MatlabPropertiesException {
		commandEvaluator= new CommandEvaluatorImpl(new MatlabClient(matlabVersion, matlabPid, configPath));
		initMatlabCommandFactory();
	}
	
	private void initMatlabCommandFactory() {
		commandFactory = new MatlabCommandFactory(commandEvaluator);
	}
	
	/**
	 * IMPORT
	 */
	
	public Importer importer(String modelName, String modelPath) {
		return importer(modelName, modelPath, modelName + "EMF");
	}
	
	public Importer importer(String modelName, String modelPath, String emfModelName) {
		ModelObject model = new ModelObject(modelName, commandEvaluator);
		model.setLoadPath(modelPath);
		Importer importer = new Importer(model);
		return importer;
	}
	
	/**
	 * EXPORT
	 */
	
	public Exporter exportModel(String emfFileNameWithoutExtension) throws SimulinkApiException {
		Exporter exporter = new Exporter();
		SimulinkModel loadedModel = exporter.loadSimulinkModel(emfFileNameWithoutExtension);
		exporter.export(loadedModel, commandFactory);
		return exporter;
	}
	
	public Exporter exportModel(SimulinkModel simulinkModel) throws SimulinkApiException {
		Exporter exporter = new Exporter();
		exporter.export(simulinkModel, commandFactory);
		return exporter;
	}
	
	/**
	 * GET SIMULINK EMF MODEL
	 */
	
	public static SimulinkModel getSimulinkModel(Importer importer, ImportMode importMode, boolean save) throws SimulinkApiException{
		importer.traverseAndCreateEMFModel(importMode);
		if (save) {
			importer.saveEMFModel();
		}
		return importer.getSimulinkModel();
	}
	
	public static SimulinkModel getSimulinkModel(Importer importer, ImportMode importMode, String importedModelName, String  defaultSavePath) throws SimulinkApiException{
		importer.traverseAndCreateEMFModel(importMode);
		importer.setDefaultSavePath(defaultSavePath);
		importer.saveEMFModel(importedModelName);
		return importer.getSimulinkModel();
	}
	
	public static SimulinkModel getSimulinkModel(Importer importer, ImportMode importMode) throws SimulinkApiException{
		return getSimulinkModel(importer, importMode, false);
	}
	
	public static SimulinkModel getSimulinkModel(Importer importer) throws SimulinkApiException{
		return getSimulinkModel(importer, ImportMode.FLATTENING, false);
	}
	
}

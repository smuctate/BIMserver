package org.bimserver.o3d;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import nl.tue.buildingsmart.express.dictionary.SchemaDefinition;

import org.bimserver.emf.IdEObject;
import org.bimserver.ifc.BimModelSerializer;
import org.bimserver.ifc.EmfSerializer;
import org.bimserver.ifc.FieldIgnoreMap;
import org.bimserver.ifc.IfcModel;
import org.bimserver.ifc.SerializerException;
import org.bimserver.ifc.file.writer.IfcStepSerializer;
import org.bimserver.ifcengine.FailSafeIfcEngine;
import org.bimserver.ifcengine.Geometry;
import org.bimserver.ifcengine.IfcEngineException;
import org.bimserver.ifcengine.IfcEngineFactory;
import org.bimserver.ifcengine.IfcEngineModel;
import org.bimserver.ifcengine.Instance;
import org.bimserver.ifcengine.SurfaceProperties;
import org.bimserver.ifcengine.jvm.IfcEngine.InstanceVisualisationProperties;
import org.bimserver.models.ifc2x3.IfcColumn;
import org.bimserver.models.ifc2x3.IfcDoor;
import org.bimserver.models.ifc2x3.IfcRailing;
import org.bimserver.models.ifc2x3.IfcRamp;
import org.bimserver.models.ifc2x3.IfcRoof;
import org.bimserver.models.ifc2x3.IfcRoot;
import org.bimserver.models.ifc2x3.IfcSlab;
import org.bimserver.models.ifc2x3.IfcSlabTypeEnum;
import org.bimserver.models.ifc2x3.IfcStair;
import org.bimserver.models.ifc2x3.IfcStairFlight;
import org.bimserver.models.ifc2x3.IfcWall;
import org.bimserver.models.ifc2x3.IfcWallStandardCase;
import org.bimserver.models.ifc2x3.IfcWindow;
import org.bimserver.models.store.Project;
import org.bimserver.models.store.User;
import org.codehaus.jettison.json.JSONException;
import org.eclipse.emf.ecore.EObject;
import org.mangosdk.spi.ProviderFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

@ProviderFor(value=EmfSerializer.class)
public class O3dJsonSerializer extends BimModelSerializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(O3dJsonSerializer.class);
	private SchemaDefinition schemaDefinition;
	private FailSafeIfcEngine ifcEngine;
	private int convertCounter;
	private Project project;
	private User user;

	public void init(Project project, User user, String fileName, IfcModel model, FieldIgnoreMap fieldIgnoreMap, SchemaDefinition schemaDefinition, IfcEngineFactory ifcEngineFactory) throws SerializerException {
		super.init(fileName, model, fieldIgnoreMap);
		this.project = project;
		this.user = user;
		this.schemaDefinition = schemaDefinition;
		try {
			this.ifcEngine = ifcEngineFactory.createFailSafeIfcEngine();
		} catch (IfcEngineException e) {
			throw new SerializerException(e);
		}
	}

	@Override
	protected void reset() {
		setMode(Mode.BODY);
	}
	
	@Override
	public boolean write(OutputStream out) {
		if (getMode() == Mode.BODY) {
			try {
				try {
					Scene scene = createScene();
					out.write(scene.toString(2).getBytes(Charsets.UTF_8)); // Doing this formatted because certain browsers don't like very long strings of JSON without new lines (Chrome)
					out.flush();
				} catch (JSONException e) {
					LOGGER.error("", e);
				}
				out.flush();
				setMode(Mode.FINISHED);
				ifcEngine.close();
				return true;
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		} else if (getMode() == Mode.FINISHED) {
			return false;
		}
		return false;
	}

	public Scene createScene() throws JSONException {
		JsonFactory jsonFactory = new JsonFactory();
		Scene scene = new Scene();
		Transformation rootTransformation = jsonFactory.createRootTransformation();
		scene.addTransformation(rootTransformation);

		Material unknownMaterial = jsonFactory.createMaterial("UnknownMaterial", new Color(0, 255, 0));
		Material roofMaterial = jsonFactory.createMaterial("RoofMaterial", new Color(255, 0, 0));
		Material wallMaterial = jsonFactory.createMaterial("WallMaterial", new Color(200, 200, 200));
		Material slabMaterial = jsonFactory.createMaterial("SlabMaterial", new Color(150, 150, 150));
		Material windowMaterial = jsonFactory.createMaterial("WindowMaterial", new Color(0, 0, 255), 0.6f);
		Material doorMaterial = jsonFactory.createMaterial("DoorMaterial", new Color(0, 0, 255));
		Material columnMaterial = jsonFactory.createMaterial("ColumnMaterial", Color.yellow);
		Material rampMaterial = jsonFactory.createMaterial("RampMaterial", Color.yellow);
		Material stairMaterial = jsonFactory.createMaterial("StairMaterial", Color.yellow);
		Material stairFlightMaterial = jsonFactory.createMaterial("StairFlightMaterial", Color.yellow);
		Material railingMaterial = jsonFactory.createMaterial("RailingMaterial", Color.yellow);
		scene.addMaterial(roofMaterial);
		scene.addMaterial(unknownMaterial);
		scene.addMaterial(wallMaterial);
		scene.addMaterial(slabMaterial);
		scene.addMaterial(windowMaterial);
		scene.addMaterial(doorMaterial);
		scene.addMaterial(columnMaterial);
		scene.addMaterial(rampMaterial);
		scene.addMaterial(stairMaterial);
		scene.addMaterial(stairFlightMaterial);
		scene.addMaterial(railingMaterial);
		@SuppressWarnings("rawtypes")
		Class[] eClasses = new Class[] { IfcSlab.class, IfcRoof.class, IfcWall.class, IfcWallStandardCase.class, IfcWindow.class, IfcDoor.class, IfcColumn.class, IfcRamp.class,
				IfcStair.class, IfcStairFlight.class, IfcRailing.class };
		try {
			for (Class<? extends EObject> eClass : eClasses) {
				for (Object object : model.getAll(eClass)) {
					IfcRoot ifcRoot = (IfcRoot) object;
					Shape shape = jsonFactory.createShape(ifcRoot.getGlobalId().getWrappedValue());
					scene.addShape(shape);
					rootTransformation.addShape(shape);
					SetGeometryResult setGeometryResult = setGeometry(ifcRoot);
					if (setGeometryResult != null) {
						int fieldId1 = jsonFactory.incCounter();
						int fieldId2 = jsonFactory.incCounter();
						int fieldId3 = jsonFactory.incCounter();
						StreamBank streamBank = jsonFactory.createStreamBank();
						streamBank.addStream(jsonFactory.createStream(1, fieldId1, 0));
						streamBank.addStream(jsonFactory.createStream(2, fieldId2, 0));
						scene.addStreamBank(streamBank);
						VertexBuffer vertexBuffer = jsonFactory.createVertexBuffer(fieldId1, fieldId2);
						scene.addVertexBuffer(vertexBuffer);
						IndexBuffer indexBuffer = jsonFactory.createIndexBuffer(fieldId3);
						scene.addIndexBuffer(indexBuffer);
						FieldData indexFieldData = jsonFactory.createFieldData(fieldId3, "o3d.UInt32Field", 1);
						FieldData vertexFieldData = jsonFactory.createFieldData(fieldId1, "o3d.FloatField", 3);
						FieldData normalFieldData = jsonFactory.createFieldData(fieldId2, "o3d.FloatField", 3);
						vertexBuffer.addFieldData(vertexFieldData);
						vertexBuffer.addFieldData(normalFieldData);
						indexBuffer.addFieldData(indexFieldData);
						Primitive primitive = jsonFactory.createPrimitive();
						primitive.setIndexBuffer(indexBuffer);
						if (object instanceof IfcRoof) {
							primitive.setMaterial(roofMaterial);
						} else if (object instanceof IfcWall) {
							primitive.setMaterial(wallMaterial);
						} else if (object instanceof IfcSlab) {
							if (((IfcSlab) object).getPredefinedType() == IfcSlabTypeEnum.ROOF) {
								primitive.setMaterial(roofMaterial);
							} else {
								primitive.setMaterial(slabMaterial);
							}
						} else if (object instanceof IfcWindow) {
							primitive.setMaterial(windowMaterial);
						} else if (object instanceof IfcDoor) {
							primitive.setMaterial(doorMaterial);
						} else if (object instanceof IfcColumn) {
							primitive.setMaterial(columnMaterial);
						} else if (object instanceof IfcStair) {
							primitive.setMaterial(stairMaterial);
						} else if (object instanceof IfcStairFlight) {
							primitive.setMaterial(stairFlightMaterial);
						} else if (object instanceof IfcRamp) {
							primitive.setMaterial(rampMaterial);
						} else if (object instanceof IfcRailing) {
							primitive.setMaterial(railingMaterial);
						} else {
							primitive.setMaterial(unknownMaterial);
						}
						primitive.setNumberPrimitives(setGeometryResult.getAddedIndices() / 3);
						primitive.setNumberVertices(setGeometryResult.getAddedVertices() / 6);
						primitive.setStartIndex(0);
						primitive.setStreamBank(streamBank);
						primitive.setOwner(shape);
						scene.addPrimitive(primitive);
						shape.setPrimitive(primitive);
						
						indexFieldData.addDataIntegers(0, setGeometryResult.getBinaryIndexBuffer().getIndices());
						vertexFieldData.addDataFloats(setGeometryResult.getBinaryVertexBuffer().getVertices());
						normalFieldData.addDataFloats(setGeometryResult.getBinaryVertexBuffer().getNormals());
					}
				}
			}
		} catch (SerializerException e) {
			LOGGER.error("", e);
		} catch (IfcEngineException e) {
			LOGGER.error("", e);
		}
		return scene;
	}

	public byte[] serialize(Scene sceneFile) throws JSONException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(baos);
		try {
			outputStreamWriter.write(sceneFile.toString());
			outputStreamWriter.close();
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return baos.toByteArray();
	}

	private SetGeometryResult setGeometry(IdEObject ifcRootObject) throws SerializerException, IfcEngineException {
		convertCounter++;
		IfcModel IfcModel = new IfcModel();
		convertToSubset(ifcRootObject.eClass(), ifcRootObject, IfcModel, new HashMap<EObject, EObject>());
		IfcStepSerializer ifcSerializer = new IfcStepSerializer(project, user, "", IfcModel, schemaDefinition);
		BinaryIndexBuffer binaryIndexBuffer = new BinaryIndexBuffer();
		BinaryVertexBuffer binaryVertexBuffer = new BinaryVertexBuffer();
		IfcEngineModel model = ifcEngine.openModel(ifcSerializer.getBytes());
		try {
			SurfaceProperties sp = model.initializeModelling();
			model.setPostProcessing(true);
			Geometry geometry = model.finalizeModelling(sp);
			int nrIndices = 0;
			if (geometry != null) {
				for (Instance instance : model.getInstances(ifcRootObject.eClass().getName().toUpperCase())) {
					InstanceVisualisationProperties instanceInModelling = instance.getVisualisationProperties();
					for (int i = instanceInModelling.getStartIndex(); i < instanceInModelling.getPrimitiveCount() * 3 + instanceInModelling.getStartIndex(); i += 3) {
						binaryIndexBuffer.addIndex(geometry.getIndex(i));
						binaryIndexBuffer.addIndex(geometry.getIndex(i + 2));
						binaryIndexBuffer.addIndex(geometry.getIndex(i + 1));
						nrIndices++;
					}
				}
				for (int i = 0; i < geometry.getNrVertices(); i += 3) {
					binaryVertexBuffer.addVertex(geometry.getVertex(i));
					binaryVertexBuffer.addVertex(geometry.getVertex(i + 1));
					binaryVertexBuffer.addVertex(geometry.getVertex(i + 2));
				}
				for (int i = 0; i < geometry.getNrNormals(); i += 3) {
					binaryVertexBuffer.addNormal(geometry.getNormal(i));
					binaryVertexBuffer.addNormal(geometry.getNormal(i + 1));
					binaryVertexBuffer.addNormal(geometry.getNormal(i + 2));
				}
				return new SetGeometryResult(nrIndices * 3, geometry.getNrVertices(), binaryIndexBuffer, binaryVertexBuffer);
			}
		} finally {
			model.close();
		}
		return null;
	}
}
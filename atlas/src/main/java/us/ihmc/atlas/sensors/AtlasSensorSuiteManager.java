package us.ihmc.atlas.sensors;

import java.io.IOException;
import java.net.URI;

import us.ihmc.atlas.parameters.AtlasSensorInformation;
import us.ihmc.avatar.drcRobot.RobotPhysicalProperties;
import us.ihmc.avatar.drcRobot.RobotTarget;
import us.ihmc.avatar.networkProcessor.lidarScanPublisher.LidarScanPublisher;
import us.ihmc.avatar.networkProcessor.lidarScanPublisher.LidarScanPublisher.ScanData;
import us.ihmc.avatar.networkProcessor.lidarScanPublisher.LidarScanPublisher.ScanTransformer;
import us.ihmc.avatar.networkProcessor.stereoPointCloudPublisher.StereoVisionPointCloudPublisher;
import us.ihmc.avatar.ros.DRCROSPPSTimestampOffsetProvider;
import us.ihmc.avatar.sensors.DRCSensorSuiteManager;
import us.ihmc.avatar.sensors.multisense.MultiSenseSensorManager;
import us.ihmc.commons.thread.ThreadTools;
import us.ihmc.communication.configuration.NetworkParameters;
import us.ihmc.communication.net.ObjectCommunicator;
import us.ihmc.communication.packetCommunicator.PacketCommunicator;
import us.ihmc.communication.packets.IMUPacket;
import us.ihmc.communication.util.NetworkPorts;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.humanoidRobotics.kryo.IHMCCommunicationKryoNetClassList;
import us.ihmc.ihmcPerception.camera.FisheyeCameraReceiver;
import us.ihmc.ihmcPerception.camera.SCSCameraDataReceiver;
import us.ihmc.ihmcPerception.depthData.CollisionBoxProvider;
import us.ihmc.robotModels.FullHumanoidRobotModel;
import us.ihmc.robotModels.FullHumanoidRobotModelFactory;
import us.ihmc.robotModels.FullRobotModel;
import us.ihmc.robotics.sensors.IMUDefinition;
import us.ihmc.sensorProcessing.communication.packets.dataobjects.RobotConfigurationData;
import us.ihmc.sensorProcessing.communication.producers.RobotConfigurationDataBuffer;
import us.ihmc.sensorProcessing.parameters.DRCRobotCameraParameters;
import us.ihmc.sensorProcessing.parameters.DRCRobotLidarParameters;
import us.ihmc.sensorProcessing.parameters.DRCRobotPointCloudParameters;
import us.ihmc.utilities.ros.RosMainNode;
import us.ihmc.wholeBodyController.DRCRobotJointMap;

public class AtlasSensorSuiteManager implements DRCSensorSuiteManager
{
   private final PacketCommunicator sensorSuitePacketCommunicator = PacketCommunicator.createIntraprocessPacketCommunicator(NetworkPorts.SENSOR_MANAGER,
                                                                                                                            new IHMCCommunicationKryoNetClassList());

   private final LidarScanPublisher lidarScanPublisher;
   private final StereoVisionPointCloudPublisher stereoVisionPointCloudPublisher;

   private final DRCROSPPSTimestampOffsetProvider ppsTimestampOffsetProvider;
   private final AtlasSensorInformation sensorInformation;
   private final RobotConfigurationDataBuffer robotConfigurationDataBuffer;
   private final FullHumanoidRobotModelFactory modelFactory;

   public AtlasSensorSuiteManager(FullHumanoidRobotModelFactory modelFactory, CollisionBoxProvider collisionBoxProvider,
                                  DRCROSPPSTimestampOffsetProvider ppsTimestampOffsetProvider, AtlasSensorInformation sensorInformation,
                                  DRCRobotJointMap jointMap, RobotPhysicalProperties physicalProperties, RobotTarget targetDeployment)
   {
      this.ppsTimestampOffsetProvider = ppsTimestampOffsetProvider;
      this.sensorInformation = sensorInformation;
      this.robotConfigurationDataBuffer = new RobotConfigurationDataBuffer();
      this.modelFactory = modelFactory;

      DRCRobotLidarParameters multisenseLidarParameters = sensorInformation.getLidarParameters(AtlasSensorInformation.MULTISENSE_LIDAR_ID);
      String sensorName = multisenseLidarParameters.getSensorNameInSdf();
      lidarScanPublisher = new LidarScanPublisher(sensorName, modelFactory, sensorSuitePacketCommunicator);
      lidarScanPublisher.setPPSTimestampOffsetProvider(ppsTimestampOffsetProvider);
      lidarScanPublisher.setCollisionBoxProvider(collisionBoxProvider);

      stereoVisionPointCloudPublisher = new StereoVisionPointCloudPublisher(modelFactory, sensorSuitePacketCommunicator);
      stereoVisionPointCloudPublisher.setPPSTimestampOffsetProvider(ppsTimestampOffsetProvider);

   }

   @Override
   public void initializeSimulatedSensors(ObjectCommunicator scsSensorsCommunicator)
   {
      sensorSuitePacketCommunicator.attachListener(RobotConfigurationData.class, robotConfigurationDataBuffer);

      SCSCameraDataReceiver cameraDataReceiver = new SCSCameraDataReceiver(sensorInformation.getCameraParameters(0).getRobotSide(), modelFactory,
                                                                           sensorInformation.getCameraParameters(0).getSensorNameInSdf(),
                                                                           robotConfigurationDataBuffer, scsSensorsCommunicator, sensorSuitePacketCommunicator,
                                                                           ppsTimestampOffsetProvider);
      cameraDataReceiver.start();

      lidarScanPublisher.receiveLidarFromSCS(scsSensorsCommunicator);
      lidarScanPublisher.setScanFrameToLidarSensorFrame();
      lidarScanPublisher.start();
   }

   @Override
   public void initializePhysicalSensors(URI rosCoreURI)
   {
      if (rosCoreURI == null)
         throw new RuntimeException(getClass().getSimpleName() + " Physical sensor requires rosURI to be set in " + NetworkParameters.defaultParameterFile);

      sensorSuitePacketCommunicator.attachListener(RobotConfigurationData.class, robotConfigurationDataBuffer);

      ForceSensorNoiseEstimator forceSensorNoiseEstimator = new ForceSensorNoiseEstimator(sensorSuitePacketCommunicator);
      sensorSuitePacketCommunicator.attachListener(RobotConfigurationData.class, forceSensorNoiseEstimator);

      RosMainNode rosMainNode = new RosMainNode(rosCoreURI, "atlas/sensorSuiteManager", true);

      DRCRobotCameraParameters multisenseLeftEyeCameraParameters = sensorInformation.getCameraParameters(AtlasSensorInformation.MULTISENSE_SL_LEFT_CAMERA_ID);
      DRCRobotLidarParameters multisenseLidarParameters = sensorInformation.getLidarParameters(AtlasSensorInformation.MULTISENSE_LIDAR_ID);
      DRCRobotPointCloudParameters multisenseStereoParameters = sensorInformation.getPointCloudParameters(AtlasSensorInformation.MULTISENSE_STEREO_ID);

      lidarScanPublisher.receiveLidarFromROSAsPointCloud2WithSource(multisenseLidarParameters.getRosTopic(), rosMainNode);
      lidarScanPublisher.setScanFrameToWorldFrame();
      lidarScanPublisher.setCustomScanTransformer(createCustomScanTransformer());

      stereoVisionPointCloudPublisher.receiveStereoPointCloudFromROS(multisenseStereoParameters.getRosTopic(), rosMainNode);

      MultiSenseSensorManager multiSenseSensorManager = new MultiSenseSensorManager(modelFactory, robotConfigurationDataBuffer, rosMainNode,
                                                                                    sensorSuitePacketCommunicator, ppsTimestampOffsetProvider,
                                                                                    multisenseLeftEyeCameraParameters, multisenseLidarParameters,
                                                                                    multisenseStereoParameters, sensorInformation.setupROSParameterSetters());

      DRCRobotCameraParameters leftFishEyeCameraParameters = sensorInformation.getCameraParameters(AtlasSensorInformation.BLACKFLY_LEFT_CAMERA_ID);
      DRCRobotCameraParameters rightFishEyeCameraParameters = sensorInformation.getCameraParameters(AtlasSensorInformation.BLACKFLY_RIGHT_CAMERA_ID);

      FisheyeCameraReceiver leftFishEyeCameraReceiver = new FisheyeCameraReceiver(modelFactory, leftFishEyeCameraParameters, robotConfigurationDataBuffer,
                                                                                  sensorSuitePacketCommunicator, ppsTimestampOffsetProvider, rosMainNode);
      FisheyeCameraReceiver rightFishEyeCameraReceiver = new FisheyeCameraReceiver(modelFactory, rightFishEyeCameraParameters, robotConfigurationDataBuffer,
                                                                                   sensorSuitePacketCommunicator, ppsTimestampOffsetProvider, rosMainNode);

      leftFishEyeCameraReceiver.start();
      rightFishEyeCameraReceiver.start();
      lidarScanPublisher.start();
      stereoVisionPointCloudPublisher.start();

      ppsTimestampOffsetProvider.attachToRosMainNode(rosMainNode);

      multiSenseSensorManager.initializeParameterListeners();

      rosMainNode.execute();
      while (!rosMainNode.isStarted())
      {
         System.out.println("waiting for " + rosMainNode.getDefaultNodeName() + " to start. ");
         ThreadTools.sleep(2000);
      }
   }

   private ScanTransformer createCustomScanTransformer()
   {
      return new ScanTransformer()
      {
         private final int chestIMUIndex = findChestIMUIndex();
         private final IMUPacket chestIMUPacket = new IMUPacket();
         private final RigidBodyTransform transform = new RigidBodyTransform();
         private final RigidBodyTransform transformFromIMUToWorld = new RigidBodyTransform();

         @Override
         public void transform(long robotTimestamp, FullHumanoidRobotModel fullRobotModel, RobotConfigurationDataBuffer robotConfigurationDataBuffer,
                               ReferenceFrame scanPointsFrame, ScanData scanDataToTransformToWorld)
         {
            IMUDefinition chestIMUDefinition = fullRobotModel.getIMUDefinitions()[chestIMUIndex];
            ReferenceFrame chestIMUFrame = chestIMUDefinition.getIMUFrame();
            robotConfigurationDataBuffer.getIMUPacket(true, robotTimestamp, chestIMUIndex, chestIMUPacket);

            scanPointsFrame.getTransformToDesiredFrame(transform, chestIMUFrame);
            chestIMUFrame.getTransformToDesiredFrame(transformFromIMUToWorld, ReferenceFrame.getWorldFrame());
            transformFromIMUToWorld.setRotation(chestIMUPacket.orientation);

            transform.multiply(transformFromIMUToWorld);
            scanDataToTransformToWorld.transform(transform);
         }
      };
   }

   private int findChestIMUIndex()
   {
      FullRobotModel fullRobotModel = modelFactory.createFullRobotModel();
      IMUDefinition[] imuDefinitions = fullRobotModel.getIMUDefinitions();

      for (int i = 0; i < imuDefinitions.length; i++)
      {
         if (imuDefinitions[i].getName().equals(sensorInformation.getChestImu()))
            return i;
      }
      throw new RuntimeException("Could not find the chest IMU");
   }

   @Override
   public void connect() throws IOException
   {
      sensorSuitePacketCommunicator.connect();
   }
}

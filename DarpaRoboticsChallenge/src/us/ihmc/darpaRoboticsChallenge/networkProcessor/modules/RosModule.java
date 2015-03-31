package us.ihmc.darpaRoboticsChallenge.networkProcessor.modules;

import java.io.IOException;
import java.net.URI;

import us.ihmc.SdfLoader.SDFFullRobotModel;
import us.ihmc.communication.kryo.IHMCCommunicationKryoNetClassList;
import us.ihmc.communication.net.ObjectCommunicator;
import us.ihmc.communication.packetCommunicator.PacketCommunicator;
import us.ihmc.communication.packets.dataobjects.RobotConfigurationData;
import us.ihmc.communication.packets.sensing.LocalizationPacket;
import us.ihmc.communication.util.NetworkPorts;
import us.ihmc.darpaRoboticsChallenge.drcRobot.DRCRobotModel;
import us.ihmc.darpaRoboticsChallenge.ros.RosRobotConfigurationDataPublisher;
import us.ihmc.darpaRoboticsChallenge.ros.RosSCSCameraPublisher;
import us.ihmc.darpaRoboticsChallenge.ros.RosSCSLidarPublisher;
import us.ihmc.darpaRoboticsChallenge.ros.RosTfPublisher;
import us.ihmc.ihmcPerception.IHMCETHRosLocalizationUpdateSubscriber;
import us.ihmc.ihmcPerception.RosLocalizationServiceClient;
import us.ihmc.sensorProcessing.parameters.DRCRobotLidarParameters;
import us.ihmc.sensorProcessing.parameters.DRCRobotSensorInformation;
import us.ihmc.utilities.ros.PPSTimestampOffsetProvider;
import us.ihmc.utilities.ros.RosMainNode;


public class RosModule
{
   private static final boolean DEBUG = false;

   private static final String ROS_NODE_NAME = "networkProcessor/rosModule";

//   private final KryoLocalPacketCommunicator rosModulePacketCommunicator = new KryoLocalPacketCommunicator(new IHMCCommunicationKryoNetClassList(),
//         PacketDestination.ROS_MODULE.ordinal(), "RosModule");
   
   private final PacketCommunicator rosModulePacketCommunicator = PacketCommunicator.createIntraprocessPacketCommunicator(NetworkPorts.ROS_MODULE,
         new IHMCCommunicationKryoNetClassList());
   
   private final RosMainNode rosMainNode;
   private final PPSTimestampOffsetProvider ppsTimestampOffsetProvider;
   private final DRCRobotSensorInformation sensorInformation;
   
   public RosModule(DRCRobotModel robotModel, URI rosCoreURI, ObjectCommunicator simulatedSensorCommunicator)
   {
      rosMainNode = new RosMainNode(rosCoreURI, ROS_NODE_NAME, true);
      String rosTopicPrefix = robotModel.getSimpleRobotName().toLowerCase();
      
      ppsTimestampOffsetProvider = robotModel.getPPSTimestampOffsetProvider();
      ppsTimestampOffsetProvider.attachToRosMainNode(rosMainNode);
      rosModulePacketCommunicator.attachListener(RobotConfigurationData.class, ppsTimestampOffsetProvider);
      
      sensorInformation = robotModel.getSensorInformation();

      RosTfPublisher tfPublisher = new RosTfPublisher(rosMainNode);

      RosRobotConfigurationDataPublisher robotConfigurationPublisher = new RosRobotConfigurationDataPublisher(robotModel, rosModulePacketCommunicator,
            rosMainNode, ppsTimestampOffsetProvider, rosTopicPrefix, tfPublisher);
      
      if(simulatedSensorCommunicator != null)
      {
         publishSimulatedCameraAndLidar(robotModel.createFullRobotModel(), sensorInformation, tfPublisher, simulatedSensorCommunicator);
         
         DRCRobotLidarParameters[] lidarParameters = sensorInformation.getLidarParameters();
         if (lidarParameters.length > 0)
         {
            DRCRobotLidarParameters primaryLidar = lidarParameters[0];
            robotConfigurationPublisher.setAdditionalJointStatePublishing(primaryLidar.getLidarSpindleJointTopic(), primaryLidar.getLidarSpindleJointName());
         }
      }
      
      if(sensorInformation.setupROSLocationService())
      {
         setupRosLocalization();
      }

//      setupFootstepServiceClient();
//      setupFootstepPathPlannerService();

      try
      {
         rosModulePacketCommunicator.connect();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      
      System.out.flush();
      rosMainNode.execute();
      printIfDebug("Finished creating ROS Module.");
   }

   private void publishSimulatedCameraAndLidar(SDFFullRobotModel fullRobotModel, DRCRobotSensorInformation sensorInformation, RosTfPublisher tfPublisher, ObjectCommunicator localObjectCommunicator)
   {
      if (sensorInformation.getCameraParameters().length > 0)
      {
         new RosSCSCameraPublisher(localObjectCommunicator, rosMainNode, ppsTimestampOffsetProvider, sensorInformation.getCameraParameters());
      }

      if (sensorInformation.getLidarParameters().length > 0)
      {
         new RosSCSLidarPublisher(localObjectCommunicator, rosMainNode, ppsTimestampOffsetProvider, fullRobotModel,
               sensorInformation.getLidarParameters(), tfPublisher);
      }
   }

   private void setupRosLocalization()
   {
      new IHMCETHRosLocalizationUpdateSubscriber(rosMainNode, rosModulePacketCommunicator, ppsTimestampOffsetProvider);
      RosLocalizationServiceClient rosLocalizationServiceClient = new RosLocalizationServiceClient(rosMainNode);
      rosModulePacketCommunicator.attachListener(LocalizationPacket.class, rosLocalizationServiceClient);
   }

//   private void setupFootstepServiceClient()
//   {
//      RosFootstepServiceClient rosFootstepServiceClient = new RosFootstepServiceClient(rosModulePacketCommunicator, rosMainNode, physicalProperties.getAnkleHeight());
//      rosModulePacketCommunicator.attachListener(SnapFootstepPacket.class, rosFootstepServiceClient);
//   }
//
//   private void setupFootstepPathPlannerService()
//   {
//      FootstepPlanningParameterization footstepParameters = new BasicFootstepPlanningParameterization();
//      FootstepPathPlannerService footstepPathPlannerService;
////    footstepPathPlannerService = new AStarPathPlannerService(rosMainNode, footstepParameters, physicalProperties.getAnkleHeight(), fieldObjectCommunicator);
////    footstepPathPlannerService = new DStarPathPlannerService(rosMainNode, footstepParameters, physicalProperties.getAnkleHeight(), fieldObjectCommunicator);
//      footstepPathPlannerService = new ADStarPathPlannerService(rosMainNode, footstepParameters, physicalProperties.getAnkleHeight(), rosModulePacketCommunicator);
//      rosModulePacketCommunicator.attachListener(FootstepPlanRequestPacket.class, footstepPathPlannerService);
//   }

   private void printIfDebug(String str)
   {
      if(DEBUG)
      {
         System.out.println("[DEBUG] " + str);
      }
   }
   
}

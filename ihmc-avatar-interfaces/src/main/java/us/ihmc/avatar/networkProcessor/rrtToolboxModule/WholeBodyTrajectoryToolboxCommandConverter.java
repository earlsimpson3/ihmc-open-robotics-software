package us.ihmc.avatar.networkProcessor.rrtToolboxModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller_msgs.msg.dds.KinematicsToolboxRigidBodyMessage;
import us.ihmc.communication.controllerAPI.CommandConversionInterface;
import us.ihmc.communication.controllerAPI.command.Command;
import us.ihmc.euclid.interfaces.Settable;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.humanoidRobotics.communication.kinematicsToolboxAPI.KinematicsToolboxRigidBodyCommand;
import us.ihmc.humanoidRobotics.communication.wholeBodyTrajectoryToolboxAPI.WholeBodyTrajectoryToolboxAPI;
import us.ihmc.humanoidRobotics.frames.HumanoidReferenceFrames;
import us.ihmc.robotModels.FullHumanoidRobotModel;
import us.ihmc.robotics.screwTheory.InverseDynamicsJoint;
import us.ihmc.robotics.screwTheory.RigidBody;
import us.ihmc.robotics.screwTheory.ScrewTools;
import us.ihmc.sensorProcessing.frames.ReferenceFrameHashCodeResolver;

/**
 * This class allows the retrieve the rigid-body from its hash code when converting a
 * {@link KinematicsToolboxRigidBodyMessage} into a {@link KinematicsToolboxRigidBodyCommand}.
 * 
 * @author Sylvain Bertrand
 *
 */
public class WholeBodyTrajectoryToolboxCommandConverter implements CommandConversionInterface
{
   private final Map<Long, RigidBody> rigidBodyNamedBasedHashMap = new HashMap<>();
   private final ReferenceFrameHashCodeResolver referenceFrameHashCodeResolver;

   public WholeBodyTrajectoryToolboxCommandConverter(FullHumanoidRobotModel fullRobotModel)
   {
      referenceFrameHashCodeResolver = new ReferenceFrameHashCodeResolver(fullRobotModel, new HumanoidReferenceFrames(fullRobotModel));

      RigidBody rootBody = ScrewTools.getRootBody(fullRobotModel.getElevator());
      RigidBody[] allRigidBodies = ScrewTools.computeSupportAndSubtreeSuccessors(rootBody);
      for (RigidBody rigidBody : allRigidBodies)
         rigidBodyNamedBasedHashMap.put(rigidBody.getNameBasedHashCode(), rigidBody);
   }

   public WholeBodyTrajectoryToolboxCommandConverter(RigidBody rootBody)
   {
      List<ReferenceFrame> referenceFrames = new ArrayList<>();
      for (InverseDynamicsJoint joint : ScrewTools.computeSubtreeJoints(rootBody))
      {
         referenceFrames.add(joint.getFrameAfterJoint());
         referenceFrames.add(joint.getFrameBeforeJoint());
      }

      for (RigidBody rigidBody : ScrewTools.computeSupportAndSubtreeSuccessors(rootBody))
      {
         referenceFrames.add(rigidBody.getBodyFixedFrame());
      }

      referenceFrameHashCodeResolver = new ReferenceFrameHashCodeResolver(referenceFrames);

      RigidBody[] allRigidBodies = ScrewTools.computeSupportAndSubtreeSuccessors(rootBody);
      for (RigidBody rigidBody : allRigidBodies)
         rigidBodyNamedBasedHashMap.put(rigidBody.getNameBasedHashCode(), rigidBody);
   }

   public RigidBody getRigidBody(long hashMap)
   {
      return rigidBodyNamedBasedHashMap.get(hashMap);
   }

   /**
    * Only converting {@link KinematicsToolboxRigidBodyMessage}.
    */
   @Override
   public <C extends Command<?, M>, M extends Settable<M>> boolean isConvertible(C command, M message)
   {
      if (command instanceof WholeBodyTrajectoryToolboxAPI<?>)
         return true;
      return false;
   }

   /**
    * Retrieves the end-effector and convert the message into its command counterpart.
    */
   //@SuppressWarnings("unchecked")
   @Override
   public <C extends Command<?, M>, M extends Settable<M>> void process(C command, M message)
   {
      WholeBodyTrajectoryToolboxAPI<M> wholeBodyTrajectoryCommand = (WholeBodyTrajectoryToolboxAPI<M>) command;
      wholeBodyTrajectoryCommand.set(message, rigidBodyNamedBasedHashMap, referenceFrameHashCodeResolver);
   }
}

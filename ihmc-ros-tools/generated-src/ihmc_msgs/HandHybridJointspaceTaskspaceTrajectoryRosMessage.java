package ihmc_msgs;

public interface HandHybridJointspaceTaskspaceTrajectoryRosMessage extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ihmc_msgs/HandHybridJointspaceTaskspaceTrajectoryRosMessage";
  static final java.lang.String _DEFINITION = "## HandHybridJointspaceTaskspaceTrajectoryRosMessage\n# This message commands the controller to move the chest in both taskspace amd jointspace to the\n# desired orientation and joint angles while going through the specified trajectory points.\n\n# Specifies the side of the robot that will execute the trajectory.\nint8 robot_side\n\n# The taskspace trajectory information.\nihmc_msgs/SE3TrajectoryRosMessage taskspace_trajectory_message\n\n# The jointspace trajectory information.\nihmc_msgs/JointspaceTrajectoryRosMessage jointspace_trajectory_message\n\n# A unique id for the current message. This can be a timestamp or sequence number. Only the unique id\n# in the top level message is used, the unique id in nested messages is ignored. Use\n# /output/last_received_message for feedback about when the last message was received. A message with\n# a unique id equals to 0 will be interpreted as invalid and will not be processed by the controller.\nint64 unique_id\n\n\n";
  byte getRobotSide();
  void setRobotSide(byte value);
  ihmc_msgs.SE3TrajectoryRosMessage getTaskspaceTrajectoryMessage();
  void setTaskspaceTrajectoryMessage(ihmc_msgs.SE3TrajectoryRosMessage value);
  ihmc_msgs.JointspaceTrajectoryRosMessage getJointspaceTrajectoryMessage();
  void setJointspaceTrajectoryMessage(ihmc_msgs.JointspaceTrajectoryRosMessage value);
  long getUniqueId();
  void setUniqueId(long value);
}

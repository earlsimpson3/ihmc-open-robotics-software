package us.ihmc.exampleSimulations.groundTruthinator;

import java.util.ArrayList;

import us.ihmc.euclid.referenceFrame.FramePose3D;
import us.ihmc.euclid.referenceFrame.FrameVector3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple3D.Point3D;

public class GroundTruthinator
{
   ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();
   private final ArrayList<GroundTruthinatorSensor> sensors = new ArrayList<>();
   private final ArrayList<FrameVector3D> estimatedCableVectors = new ArrayList<FrameVector3D>();
   private final ArrayList<FrameVector3D> correctionVectors = new ArrayList<FrameVector3D>();

   public void addSensor(GroundTruthinatorSensor sensor)
   {
      this.sensors.add(sensor);
   }

   public void addSensor(Point3D sensorPosition, Point3D attachmentPosition)
   {
      GroundTruthinatorSensor sensor = new GroundTruthinatorSensor(sensorPosition, attachmentPosition);
      addSensor(sensor);

   }

   public int getNumberOfSensors()
   {
      return sensors.size();
   }

   public GroundTruthinatorSensor getSensor(int sensorIndex)
   {
      return sensors.get(sensorIndex);
   }

   /** Assumes sensed cable lengths are set before calling this
    *
    */
   public void estimateObjectPose(FramePose3D estimatedPose, double epsilon)
   {
      estimateObjectPose(estimatedPose, 0.999, epsilon);
   }

   public void estimateObjectPose(FramePose3D estimatedPose, double rate, double epsilon)
   {
      System.out.println(".");
      int numberOfSensors = getNumberOfSensors();

      // Initial guess at zero.
//      estimatedPose.setToZero(ReferenceFrame.getWorldFrame());

      computeEstimatedCableLengthsFromObjectPose(estimatedPose);

      double[] errorCableLengths = getErrorCableLengths();

      correctionVectors.clear();

      FrameVector3D averageCorrectionVector = new FrameVector3D(worldFrame);

      double squaredError = 0.0;

      for (int i=0; i<numberOfSensors; i++)
      {
         FrameVector3D correctionVector = new FrameVector3D(estimatedCableVectors.get(i));
         correctionVector.normalize();
         double errorCableLength = errorCableLengths[i];
         squaredError += errorCableLength * errorCableLength;

         correctionVector.scale(errorCableLength);

         averageCorrectionVector.add(correctionVector);
      }


      averageCorrectionVector.scale(1.0 / ((double) numberOfSensors));

      averageCorrectionVector.scale(rate);
      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setTranslation(averageCorrectionVector);

      estimatedPose.applyTransform(transform);

      if (squaredError > epsilon) estimateObjectPose(estimatedPose, rate * 0.999, epsilon);
   }

   private double[] getErrorCableLengths()
   {
      int numberOfSensors = getNumberOfSensors();

      double[] ret = new double[numberOfSensors];

      for (int i=0; i<numberOfSensors; i++)
      {
         GroundTruthinatorSensor sensor = sensors.get(i);
         ret[i] = sensor.getSensedCableLength() - sensor.getEstimatedCableLength();
      }

      return ret;
   }


   private final Point3D sensorPositionInWorldFrame = new Point3D();
   private final Point3D attachmentPositionInRobotFrame = new Point3D();
   private final Point3D attachmentPositionInWorldFrame = new Point3D();
   private final RigidBodyTransform transform = new RigidBodyTransform();

   public void computeEstimatedCableLengthsFromObjectPose(FramePose3D estimatedObjectPoseInWorld)
   {
      estimatedCableVectors.clear();

      estimatedObjectPoseInWorld.checkReferenceFrameMatch(ReferenceFrame.getWorldFrame());
      estimatedObjectPoseInWorld.get(transform);

      int numberOfSensors = getNumberOfSensors();

      for (int i = 0; i < numberOfSensors; i++)
      {
         FrameVector3D cableVector = new FrameVector3D(ReferenceFrame.getWorldFrame());
         estimatedCableVectors.add(cableVector);

         GroundTruthinatorSensor sensor = getSensor(i);

         sensor.getSensorPositionInWorldFrame(sensorPositionInWorldFrame);
         sensor.getAttachmentPositionInRobotFrame(attachmentPositionInRobotFrame);

         attachmentPositionInWorldFrame.set(attachmentPositionInRobotFrame);
         transform.transform(attachmentPositionInWorldFrame);

         cableVector.sub(attachmentPositionInWorldFrame, sensorPositionInWorldFrame);
         double cableLength = cableVector.length();

         sensor.setEstimatedCableLength(cableLength);
      }
   }

   public void setSensedCableLengths(double[] sensedCableLengths)
   {
      for (int i=0; i<getNumberOfSensors(); i++)
      {
         GroundTruthinatorSensor sensor = sensors.get(i);
         sensor.setSensedCableLength(sensedCableLengths[i]);
      }
   }

   public double[] getEstimatedCableLengths()
   {
      int numberOfSensors = getNumberOfSensors();
      double[] ret = new double[numberOfSensors];

      for (int i=0; i<numberOfSensors; i++)
      {
         ret[i] = sensors.get(i).getEstimatedCableLength();
      }

      return ret;
   }
}

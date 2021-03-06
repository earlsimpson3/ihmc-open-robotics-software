package us.ihmc.robotics.geometry.frameObjects;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.FramePoint3D;
import us.ihmc.euclid.referenceFrame.FrameVector3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.interfaces.Point3DBasics;
import us.ihmc.euclid.tuple3D.interfaces.Point3DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DBasics;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;
import us.ihmc.robotics.geometry.interfaces.EuclideanWaypointInterface;
import us.ihmc.robotics.geometry.transformables.EuclideanWaypoint;

public class FrameEuclideanWaypoint extends FrameGeometryObject<FrameEuclideanWaypoint, EuclideanWaypoint>
      implements EuclideanWaypointInterface<FrameEuclideanWaypoint>
{
   private final EuclideanWaypoint geometryObject;
   
   public FrameEuclideanWaypoint()
   {
      super(new EuclideanWaypoint());
      geometryObject = getGeometryObject();
   }

   public FrameEuclideanWaypoint(ReferenceFrame referenceFrame)
   {
      this();
      this.referenceFrame = referenceFrame;
   }

   @Override
   public void setPosition(Point3DReadOnly position)
   {
      geometryObject.setPosition(position);
   }

   public void setPosition(FramePoint3D position)
   {
      checkReferenceFrameMatch(position);
      geometryObject.setPosition(position);
   }

   @Override
   public void setLinearVelocity(Vector3DReadOnly linearVelocity)
   {
      geometryObject.setLinearVelocity(linearVelocity);
   }

   public void setLinearVelocity(FrameVector3D linearVelocity)
   {
      checkReferenceFrameMatch(linearVelocity);
      geometryObject.setLinearVelocity(linearVelocity);
   }

   public void set(Point3DReadOnly position, Vector3DReadOnly linearVelocity)
   {
      geometryObject.set(position, linearVelocity);
   }

   public void setIncludingFrame(ReferenceFrame referenceFrame, Point3DReadOnly position, Vector3DReadOnly linearVelocity)
   {
      setToZero(referenceFrame);
      geometryObject.set(position, linearVelocity);
   }

   public void set(FramePoint3D position, FrameVector3D linearVelocity)
   {
      checkReferenceFrameMatch(position);
      checkReferenceFrameMatch(linearVelocity);
      geometryObject.set(position, linearVelocity);
   }

   public void setIncludingFrame(FramePoint3D position, FrameVector3D linearVelocity)
   {
      position.checkReferenceFrameMatch(linearVelocity);
      setToZero(position.getReferenceFrame());
      geometryObject.set(position, linearVelocity);
   }

   public void set(EuclideanWaypointInterface<?> euclideanWaypoint)
   {
      geometryObject.set(euclideanWaypoint);
   }

   public void setIncludingFrame(ReferenceFrame referenceFrame, EuclideanWaypointInterface<?> euclideanWaypoint)
   {
      setToZero(referenceFrame);
      geometryObject.set(euclideanWaypoint);
   }

   @Override
   public void setPositionToZero()
   {
      geometryObject.setPositionToZero();
   }

   @Override
   public void setLinearVelocityToZero()
   {
      geometryObject.setLinearVelocityToZero();
   }

   @Override
   public void setPositionToNaN()
   {
      geometryObject.setPositionToNaN();
   }

   @Override
   public void setLinearVelocityToNaN()
   {
      geometryObject.setLinearVelocityToNaN();
   }

   public double positionDistance(FrameEuclideanWaypoint frameEuclideanWaypoint)
   {
      checkReferenceFrameMatch(frameEuclideanWaypoint);
      return geometryObject.positionDistance(frameEuclideanWaypoint.geometryObject);
   }

   @Override
   public void getPosition(Point3DBasics positionToPack)
   {
      geometryObject.getPosition(positionToPack);
   }

   public void getPosition(FramePoint3D positionToPack)
   {
      checkReferenceFrameMatch(positionToPack);
      geometryObject.getPosition(positionToPack);
   }

   public void getPositionIncludingFrame(FramePoint3D positionToPack)
   {
      positionToPack.setToZero(getReferenceFrame());
      geometryObject.getPosition(positionToPack);
   }

   @Override
   public void getLinearVelocity(Vector3DBasics linearVelocityToPack)
   {
      geometryObject.getLinearVelocity(linearVelocityToPack);
   }

   public void getLinearVelocity(FrameVector3D linearVelocityToPack)
   {
      checkReferenceFrameMatch(linearVelocityToPack);
      geometryObject.getLinearVelocity(linearVelocityToPack);
   }

   public void getLinearVelocityIncludingFrame(FrameVector3D linearVelocityToPack)
   {
      linearVelocityToPack.setToZero(getReferenceFrame());
      geometryObject.getLinearVelocity(linearVelocityToPack);
   }

   public void get(Point3DBasics positionToPack, Vector3DBasics linearVelocityToPack)
   {
      geometryObject.get(positionToPack, linearVelocityToPack);
   }

   public void get(FramePoint3D positionToPack, FrameVector3D linearVelocityToPack)
   {
      getPosition(positionToPack);
      getLinearVelocity(linearVelocityToPack);
   }

   public void getIncludingFrame(FramePoint3D positionToPack, FrameVector3D linearVelocityToPack)
   {
      getPositionIncludingFrame(positionToPack);
      getLinearVelocityIncludingFrame(linearVelocityToPack);
   }

   public void get(EuclideanWaypointInterface<?> euclideanWaypoint)
   {
      euclideanWaypoint.setPosition(geometryObject.getPosition());
      euclideanWaypoint.setLinearVelocity(geometryObject.getLinearVelocity());
   }
}

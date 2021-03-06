package us.ihmc.robotics.robotController;

import us.ihmc.simulationconstructionset.util.RobotController;
import us.ihmc.yoVariables.registry.YoVariableRegistry;

/**
 * Designed for convenience in tests if you don't want to implement everything.
 */
public class RobotControllerAdapter implements RobotController
{
   private final YoVariableRegistry registry;
   
   public RobotControllerAdapter(YoVariableRegistry registry)
   {
      this.registry = registry;
   }
   
   @Override
   public void initialize()
   {
   }

   @Override
   public YoVariableRegistry getYoVariableRegistry()
   {
      return registry;
   }

   @Override
   public String getName()
   {
      return null;
   }

   @Override
   public String getDescription()
   {
      return null;
   }

   @Override
   public void doControl()
   {
   }
}

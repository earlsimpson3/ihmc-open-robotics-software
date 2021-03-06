package us.ihmc.quadrupedRobotics.controller.force;

import controller_msgs.msg.dds.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import us.ihmc.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationTest;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.quadrupedRobotics.*;
import us.ihmc.quadrupedRobotics.communication.QuadrupedMessageTools;
import us.ihmc.quadrupedRobotics.controller.QuadrupedControlMode;
import us.ihmc.quadrupedRobotics.input.managers.QuadrupedTeleopManager;
import us.ihmc.quadrupedRobotics.model.QuadrupedInitialOffsetAndYaw;
import us.ihmc.robotics.testing.YoVariableTestGoal;
import us.ihmc.simulationConstructionSetTools.util.environments.SteppingStonesEnvironment;
import us.ihmc.simulationConstructionSetTools.util.simulationrunner.GoalOrientedTestConductor;
import us.ihmc.simulationconstructionset.SimulationConstructionSetParameters;
import us.ihmc.simulationconstructionset.util.simulationRunner.BlockingSimulationRunner;
import us.ihmc.tools.MemoryTools;

import java.io.IOException;
import java.util.List;

public abstract class QuadrupedWalkOverSteppingStonesTest implements QuadrupedMultiRobotTestInterface
{
   private GoalOrientedTestConductor conductor;
   private QuadrupedForceTestYoVariables variables;
   private QuadrupedTeleopManager stepTeleopManager;
   private QuadrupedTestFactory quadrupedTestFactory;

   @Before
   public void setup()
   {
      MemoryTools.printCurrentMemoryUsageAndReturnUsedMemoryInMB(getClass().getSimpleName() + " before test.");
      quadrupedTestFactory = createQuadrupedTestFactory();
      quadrupedTestFactory.setUseNetworking(true);
   }

   @After
   public void tearDown()
   {
      quadrupedTestFactory.close();
      conductor.concludeTesting();
      conductor = null;
      variables = null;
      stepTeleopManager = null;

      MemoryTools.printCurrentMemoryUsageAndReturnUsedMemoryInMB(getClass().getSimpleName() + " after test.");
   }

   @ContinuousIntegrationTest(estimatedDuration = 103.5)
   @Test(timeout = 520000)
   public void testWalkOverSteppingStones() throws IOException, BlockingSimulationRunner.SimulationExceededMaximumTimeException
   {
      SteppingStonesEnvironment environment = new SteppingStonesEnvironment();
      SimulationConstructionSetParameters simulationConstructionSetParameters = SimulationConstructionSetParameters.createFromSystemProperties();
      simulationConstructionSetParameters.setUseAutoGroundGraphics(false);
      QuadrupedInitialOffsetAndYaw initialOffset = new QuadrupedInitialOffsetAndYaw(new Vector3D(environment.getStartPosition()), environment.getStartYaw());

      quadrupedTestFactory.setScsParameters(simulationConstructionSetParameters);
      quadrupedTestFactory.setInitialOffset(initialOffset);
      quadrupedTestFactory.setControlMode(QuadrupedControlMode.FORCE);

      quadrupedTestFactory.setTerrainObject3D(environment.getTerrainObject3D());
      conductor = quadrupedTestFactory.createTestConductor();
      variables = new QuadrupedForceTestYoVariables(conductor.getScs());
      stepTeleopManager = quadrupedTestFactory.getStepTeleopManager();

      QuadrupedTestBehaviors.readyXGait(conductor, variables, stepTeleopManager);

      List<QuadrupedTimedStepMessage> steps = getSteps(environment.getBaseBlockFrame());
      QuadrupedTimedStepListMessage message = QuadrupedMessageTools.createQuadrupedTimedStepListMessage(steps, false);
      stepTeleopManager.publishTimedStepListToController(message);

      int number = message.getQuadrupedStepList().size();
      QuadrupedTimedStepMessage lastMessage0 = message.getQuadrupedStepList().get(number - 1);
      QuadrupedTimedStepMessage lastMessage1 = message.getQuadrupedStepList().get(number - 2);
      QuadrupedTimedStepMessage lastMessage2 = message.getQuadrupedStepList().get(number - 3);
      QuadrupedTimedStepMessage lastMessage3 = message.getQuadrupedStepList().get(number - 4);


      Point3D finalPoint = new Point3D();
      finalPoint.scaleAdd(0.25, lastMessage0.getQuadrupedStepMessage().getGoalPosition(), finalPoint);
      finalPoint.scaleAdd(0.25, lastMessage1.getQuadrupedStepMessage().getGoalPosition(), finalPoint);
      finalPoint.scaleAdd(0.25, lastMessage2.getQuadrupedStepMessage().getGoalPosition(), finalPoint);
      finalPoint.scaleAdd(0.25, lastMessage3.getQuadrupedStepMessage().getGoalPosition(), finalPoint);

      conductor.addWaypointGoal(YoVariableTestGoal.doubleWithinEpsilon(variables.getRobotBodyX(), finalPoint.getX(), 0.15));
      conductor.addWaypointGoal(YoVariableTestGoal.doubleWithinEpsilon(variables.getRobotBodyY(), finalPoint.getY(), 0.15));

      conductor.addTerminalGoal(QuadrupedTestGoals.timeInFuture(variables, lastMessage0.getTimeInterval().getEndTime() + 3.0));
      conductor.simulate();


      conductor.concludeTesting();
   }

   public abstract List<QuadrupedTimedStepMessage> getSteps(ReferenceFrame baseBlockFrame);
}

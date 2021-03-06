package us.ihmc.humanoidBehaviors.stateMachine;

import us.ihmc.humanoidBehaviors.behaviors.AbstractBehavior;
import us.ihmc.humanoidBehaviors.behaviors.simpleBehaviors.BehaviorAction;
import us.ihmc.robotics.stateMachine.factories.StateMachineFactory;
import us.ihmc.ros2.Ros2Node;
import us.ihmc.yoVariables.providers.DoubleProvider;

public abstract class StateMachineBehavior<E extends Enum<E>> extends AbstractBehavior
{
   private final Class<E> keyType;
   private BehaviorStateMachine<E> stateMachine = null;

   public StateMachineBehavior(String robotName, String stateMachineName, Class<E> keyType, DoubleProvider timeProvider, Ros2Node ros2Node)
   {
      this(robotName, null, stateMachineName, keyType, timeProvider, ros2Node);
   }

   public StateMachineBehavior(String robotName, String namePrefix, String stateMachineName, Class<E> keyType,
                               DoubleProvider timeProvider, Ros2Node ros2Node)
   {
      super(robotName, namePrefix, ros2Node);
      this.keyType = keyType;
   }

   protected void setupStateMachine()
   {
      StateMachineFactory<E, BehaviorAction> stateMachineFactory = new StateMachineFactory<>(keyType);
      E initialBehaviorKey = configureStateMachineAndReturnInitialKey(stateMachineFactory);
      stateMachine = new BehaviorStateMachine<>(stateMachineFactory.build(initialBehaviorKey));
   }

   protected abstract E configureStateMachineAndReturnInitialKey(StateMachineFactory<E, BehaviorAction> factory);

   public BehaviorStateMachine<E> getStateMachine()
   {
      return stateMachine;
   }

   @Override
   public void onBehaviorEntered()
   {
      stateMachine.initialize();
   }

   @Override
   public void onBehaviorPaused()
   {
      stateMachine.pause();
   }

   public void onBehaviorResumed()
   {
      stateMachine.resume();
   }

   public void onBehaviorAborted()
   {
      stateMachine.stop();
   }

   @Override
   public void doControl()
   {
      stateMachine.doControlAndTransitions();
   }

   @Override
   public boolean isDone()
   {
      //if your current state has finished and there is no transition out of that state... the entire state machine is finished

      if (stateMachine.getCurrentBehavior().isDone() && stateMachine.isCurrentBehaviorTerminal())
      {
         return true;
      }
      return false;

   }

}

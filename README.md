minebot
=======

This is the main Minebot repo

How to add a new command:
* Add a new command class and register it in AIChatController. The class needs an AICommand-Annotation. Most commands use "minebot" as base cmmand.
* At at least one method with an AICommandInvocation-Annotation. 
* Add parameters to that method. Each parameter needs a AICommandParameter-Annotation that is used to generate the help text and tab completion. It can have any supported type (int, enums, color, block, ...) but should not allow ambigious command lines.
* Implement that method. You can either implement a strategy that should now be used or do the stuff directly in the method.

How to implement a new strategy:
* Let a new class implement AIStrategy
* Implement the search task method. It should search new stuff to do and send the tasks to the passed AIHelper calling it's addTask method. If it does not send any tasks, it is finished.
* Mind that, due to server lags or other problems, the search method might be called before all previously assigned tasks have been worked on. You should handle this.

How to implement a new searching strategy (the easy way)
* Extend MovePathfinder. Look at the other examples on how to do it, basically you just have to rate each destination and can add a task that should be done when a destination is reached. The lumberjack strategy is a good example.

How to implement a new task:
* Let a Class implement an AITask
* Add a isFinished method that returns true if the task is done.
* Add a runTick method, that is called every tick and should work towards the goal. You will find a lot of helpful methods in the AIHelper.

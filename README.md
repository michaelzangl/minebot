minebot
=======

Minebot is a mod for minecraft that takes control of the player and automatically does tasks for you. It simulates keyboard and mouse input to interact with the word.

Minebot is written to be played on normal minecraft in survival mode. Creative mode and some mods partially work but are not targeted.

The bot is controlled by giving it commands using the chat line. For advanced uses, it can be controlled using [simple scripts](https://github.com/michaelzangl/minebot/wiki/Run) or the [Javascript API](https://github.com/michaelzangl/minebot/wiki/Javascript-API).

Have a look at the [Getting Started Wiki Page](https://github.com/michaelzangl/minebot/wiki) to see how to use the bot.

This repo also contains an independent bow aiming helper.

Download + Installing (Stable version)
=====================

--------------
* Install [Forge mod loader](http://files.minecraftforge.net/#Downloads).

* Get the latest [release](https://github.com/michaelzangl/minebot/releases).

* Extract the ZIP file and put the jar files in your minecraft mods directory.

Building (Latest version)
========

The developer version may contain more bugs than the real version.

Building Minebot is straight forward. You need linux, git and the normal java development tools.

* Clone/Download this repo
* Run ./release.sh
* Extract that ZIP file in your minecraft mods directory

Developing Minebot
==================

I won't develop this mod any further. Minecraft 1.8.9 is the last supported version. A baisc but incomplete port is available for Minecraft 1.11.2.
But I will be accepting PRs to this repository.

I use eclipse neon for development. You can simply import Minebot as existing project.

The main classes of Minebot have some Javadoc in them. Most other classes are undocumented but should explain themselves.

How to add a new command:
* Add a new command class and register it in AIChatController. The class needs an AICommand-Annotation. Most commands use "minebot" as base cmmand.
* At at least one method with an AICommandInvocation-Annotation. 
* Add parameters to that method. Each parameter needs a AICommandParameter-Annotation that is used to generate the help text and tab completion. It can have any supported type (int, enums, color, block, ...) but should not allow ambigious command lines.
* Implement that method. You can either implement a strategy that should now be used or do the stuff directly in the method.

How to implement a new strategy:
* Let a new class extend TaskStrategy
* Implement the search task method. It should search new stuff to do and send the tasks to the passed AIHelper calling it's addTask method. If it does not send any tasks, it is finished.
* Mind that, due to server lags or other problems, the search method might be called before all previously assigned tasks have been worked on. You should handle this.

How to implement a new searching strategy (the fast way)
* Extend MovePathfinder. Look at the other examples on how to do it, basically you just have to rate each destination and can add a task that should be done when a destination is reached.
* Always work on the local world object when pathfinding. This allows the bot to pre-search tasks while it is still working on the old ones.

How to implement a new task:
* Let a Class extend an AITask
* Add a isFinished method that returns true if the task is done.
* Add a runTick method, that is called every tick and should work towards the goal. You will find a lot of helpful methods in the AIHelper.
* There are many optional methods. Have a look at the AITask documentation.

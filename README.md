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

Getting Help
============
Use the [issue tracker](https://github.com/michaelzangl/minebot/issues) for bugs or to get help with your stuff. I'm not available in the minecraft forum any more (it's just to slow and blown-up).

Building (Latest version)
========

The developer version may contain more bugs than the real version.

Building Minebot is straight forward. You need linux, git and the normal java development tools.

* Clone/Download this repo
* Run ./release.sh
* Extract that ZIP file in your minecraft mods directory

If you encounter exceptions during the build, try running this in the minebot directory:
`./gradlew --no-daemon build`

Developing Minebot
==================

I won't develop this mod any further. Minecraft 1.8.9 is the last supported version. A baisc but incomplete port is available for Minecraft 1.11.2.
But I will be accepting PRs to this repository.

I use eclipse neon for development. You can simply import Minebot as existing project.

================== Message from 1.16.3 Porter (Vaccinate) =============
Some kind of law message saying I'm not responsible if you get banned for botting anywhere etc.
Not accountable for anything that goes wrong here, it's your choice if you choose to use the code
and all the consequences of doing so.
I was mainly focused on Minebot and not AimBow.
Another point: There are some slight mapping issues with functions, do take a look at documentation
https://forge.yue.moe/javadoc/1.16.3/overview-summary.html
https://gist.github.com/gigaherz/2dfa77c6efc7d1248ef88ec1920c0a93#file-1152to1161-xml
Especially helpful for 1.16.3 development links.

Using IntelliJ IDE & Gradle:
Setting up:
I have commented out the maps and the stats sections - I did delete instead of comment out some code
(I know, my bad) but if you look through the first commit and ctrl-F for map and stats you should find
most of the calls.
Step 1: Clone the repo
Step 2: Open the project with IntelliJ
Step 3: Inside IntelliJ, Navigate to minebot/Minebot/src/build.gradle
Step 4: Right click it and select "Import Gradle Project"
Step 5: That should open a section with the gradle project, Minebot, and a little elephant to the left
Step 6: Expand Minebot, Tasks, fg_runs, genIntelliJRuns.
Step 7: Double click, it will set your IntelliJ up to be ready to run Minecraft and import the project
Step 8: Expand Minebot, Tasks, fg_runs, double click runClient
Step 9: When in IntelliJ, Set up your SDK to use Java 1.8 (I believe it's the only one working with
Minecraft at the given time)
This will run the code and open a Minecraft launcher with this version of Minecraft on it.
Happy development!

If I'm gone when it comes to implementing 1.17.x or future versions, essentially:
First you need to upgrade the build.gradle and the mods.toml. These can be found
in Minebot/src/main/resources/META-INF. For 1.16.3 it was important that we
updated the mods.toml to include a license="x" portion for mods to run.
I assumed it was GNU based on Michael's comments. When you're here, increase
loaderVersion="[xx,)" to the valid one for the version. If you're not sure, find some other
mod's github and check what they're using for the version you're trying to port to.

Then, you'll need to update the build.gradle - once again see what other modders use here, but
look for mappings channel: 'snapshot', version: 'xxxx' for your version and then the 
dependencies { minecraft '...' } needs your forge version in it.

Now, you can "Reimport all gradle projects" in your IntelliJ Gradle window (Above the little elephant)

If there are many errors, unlucky. Go through each one by one - if it's a fault import, look
for the new mapping in the documentation for your version. See one of the links to know
what I'm talking about. Then, one by one, replace all references to it.
Good luck & Happy coding,
- Vaccinate 04/11/2020
===================== End Message from 1.16.3 Porter =========================


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

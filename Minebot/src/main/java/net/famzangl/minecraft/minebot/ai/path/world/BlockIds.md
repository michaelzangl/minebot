# Block ID / Helpers

Minecraft uses BlockState Objects a lot. Although they are a nice abstraction, they are slow.

This bot needs a very fast test to see if a block is contained in a set of allowed BlockStates

For this, we use a BlockSet class. It allows fast isIn checks.

The checks are run against the raw integer data stored in the minecraft world binary files, but BlockSet attempts to abstract this away.

You can do conversion between BlockState and id using Block.getStateById / Block.getStateId 
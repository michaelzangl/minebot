var colors = {};
var totalSheep = 0;
var sheepToLeave = 20; // Total of 320 sheep. Brings you to level 30 by feeding once.
minescript.getEntities(net.minecraft.entity.passive.EntitySheep, 100).forEach(function(sheep) {
  if (sheep.getColor() in colors)
    colors[sheep.getColor()]++;
  else
    colors[sheep.getColor()] = 1;
  totalSheep++;
});

// Print a list of sheep
minescript.displayChat("Sheep around you: " + totalSheep);
sorted = [];
for (color in colors) {
	sorted.push(color);
}
sorted.sort(function(a, b) {return -colors[a] + colors[b];});
for (var i = 0; i < sorted.length; i++) {
  minescript.displayChat(sorted[i] + ": " + colors[sorted[i]] + " (" + Math.round(100 * colors[sorted[i]] / totalSheep) + "%)");
}

// Now kill those left over
for (color in colors) {
  var toKill = colors[color] - sheepToLeave;
  minescript.setDescription("Killing " + toKill + " " + color + " sheep");
  if (toKill > 0) {
    minescript.doStrategy(minescript.stack(
      minescript.strategy("minebot", "eat"),
      minescript.strategy("minebot", "kill", "sheep", color, toKill + "")));
  }
}

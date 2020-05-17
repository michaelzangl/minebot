/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.strategy.*;

public enum SafeStrategyRule {
    NONE {
        @Override
        public AIStrategy makeSafe(AIStrategy strategy) {
            // NOP
            return strategy;
        }
    },
    DEFEND {
        @Override
        public AIStrategy makeSafe(AIStrategy strategy) {
            final StrategyStack stack = new StrategyStack();
            stack.addStrategy(new AbortOnDeathStrategy());
            stack.addStrategy(new DamageTakenStrategy());
            stack.addStrategy(new PlayerComesActionStrategy());
            stack.addStrategy(new CreeperComesActionStrategy());
            stack.addStrategy(new EatStrategy());
            stack.addStrategy(strategy);
            return new StackStrategy(stack);
        }
    },
    DEFEND_MINING {
        @Override
        public AIStrategy makeSafe(AIStrategy strategy) {
            final StrategyStack stack = new StrategyStack();
            stack.addStrategy(new AbortOnDeathStrategy());
            stack.addStrategy(new DoNotSuffocateStrategy());
            stack.addStrategy(new DamageTakenStrategy());
            stack.addStrategy(new PlayerComesActionStrategy());
            stack.addStrategy(new CreeperComesActionStrategy());
            stack.addStrategy(new EatStrategy());
            stack.addStrategy(new PlaceTorchStrategy());
            stack.addStrategy(strategy);
            return new StackStrategy(stack);
        }
    };

    public abstract AIStrategy makeSafe(AIStrategy strategy);
}

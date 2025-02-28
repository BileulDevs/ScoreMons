package com.darcosse.scoremons.fabric;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.darcosse.scoremons.fabric.stats.ScoreboardStats;
import net.fabricmc.api.ModInitializer;

public class ScoreMons implements ModInitializer {

    @Override
    public void onInitialize() {
        ScoreboardStats.registerStats();

        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGHEST, ScoreboardStats.registerCapturedPokemon());
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGHEST, ScoreboardStats.registerCapturedShinyPokemon());
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, ScoreboardStats.battleVictory());
    }
}
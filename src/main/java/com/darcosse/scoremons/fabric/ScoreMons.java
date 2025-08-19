package com.darcosse.scoremons.fabric;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.darcosse.scoremons.fabric.config.ConfigManager;
import com.darcosse.scoremons.fabric.stats.ScoreboardStats;
import net.fabricmc.api.ModInitializer;

public class ScoreMons implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();
        ScoreboardStats.registerStats();

        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGHEST, ScoreboardStats.registerCapturedPokemon());
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGHEST, ScoreboardStats.registerCapturedShinyPokemon());
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, ScoreboardStats.battleVictory());
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.HIGHEST, ScoreboardStats.revivedFossil());
        CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(Priority.HIGHEST, ScoreboardStats.registerCaughtCount());
    }
}
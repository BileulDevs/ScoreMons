package com.darcosse.scoremons.fabric.stats;

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ScoreboardStats {
    public static final ResourceLocation POKEMON_CAUGHT = ResourceLocation.tryParse("pokemon_caught");
    public static final ResourceLocation SHINY_POKEMON_CAUGHT = ResourceLocation.tryParse("shiny_pokemon_caught");
    public static final ResourceLocation BATTLE_WON = ResourceLocation.tryParse("battle_won");

    public static void registerStats() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, POKEMON_CAUGHT, POKEMON_CAUGHT);
        Stats.CUSTOM.get(POKEMON_CAUGHT);

        Registry.register(BuiltInRegistries.CUSTOM_STAT, SHINY_POKEMON_CAUGHT, SHINY_POKEMON_CAUGHT);
        Stats.CUSTOM.get(SHINY_POKEMON_CAUGHT);

        Registry.register(BuiltInRegistries.CUSTOM_STAT, BATTLE_WON, BATTLE_WON);
        Stats.CUSTOM.get(BATTLE_WON);
    }

    public static Function1<? super PokemonCapturedEvent, Unit> registerCapturedPokemon() {
        return event -> {
            Player player = event.getPlayer();

            if (player instanceof ServerPlayer serverPlayer) {
                player.awardStat(Stats.CUSTOM.get(POKEMON_CAUGHT));
            }

            return Unit.INSTANCE;
        };
    }

    public static Function1<? super PokemonCapturedEvent, Unit> registerCapturedShinyPokemon() {
        return event -> {
            Player player = event.getPlayer();

            if(event.getPokemon().getShiny()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    player.awardStat(Stats.CUSTOM.get(SHINY_POKEMON_CAUGHT));

                    for (Player p : player.getServer().getPlayerList().getPlayers()) {
                        p.sendSystemMessage(
                                Component.literal(
                                        player.getName().getString() + " a capturé un " + event.getPokemon().getSpecies().getName() + " shiny !"
                                )
                        );
                    }
                }
            }

            return Unit.INSTANCE;
        };
    }

    public static Function1<? super BattleVictoryEvent, Unit> battleVictory() {
        return event -> {

            if(event.getBattle().isPvP()) {

                Player player1 = event.getBattle().getPlayers().getFirst();

                Player player = player1.getServer().getPlayerList().getPlayer(event.getWinners().getFirst().getUuid());

                if (player instanceof ServerPlayer serverPlayer) {
                    Player p = serverPlayer.server.getPlayerList().getPlayer(event.getWinners().getFirst().getUuid());
                    p.awardStat(Stats.CUSTOM.get(BATTLE_WON));
                }

            }
                return Unit.INSTANCE;
        };
    }

}

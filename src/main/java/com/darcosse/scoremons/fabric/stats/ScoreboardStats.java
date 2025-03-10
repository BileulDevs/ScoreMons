package com.darcosse.scoremons.fabric.stats;

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ScoreboardStats {
    public static final ResourceLocation POKEMON_CAUGHT = ResourceLocation.tryParse("pokemon_caught");
    public static final ResourceLocation SHINY_POKEMON_CAUGHT = ResourceLocation.tryParse("shiny_pokemon_caught");
    public static final ResourceLocation BATTLE_WON = ResourceLocation.tryParse("battle_won");
    public static final ResourceLocation POKEMON_REGISTERED = ResourceLocation.tryParse("pokemon_registered");

    public static void registerStats() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, POKEMON_CAUGHT, POKEMON_CAUGHT);
        Stats.CUSTOM.get(POKEMON_CAUGHT);

        Registry.register(BuiltInRegistries.CUSTOM_STAT, SHINY_POKEMON_CAUGHT, SHINY_POKEMON_CAUGHT);
        Stats.CUSTOM.get(SHINY_POKEMON_CAUGHT);

        Registry.register(BuiltInRegistries.CUSTOM_STAT, BATTLE_WON, BATTLE_WON);
        Stats.CUSTOM.get(BATTLE_WON);

        Registry.register(BuiltInRegistries.CUSTOM_STAT, POKEMON_REGISTERED, POKEMON_REGISTERED);
        Stats.CUSTOM.get(POKEMON_REGISTERED);
    }

    public static Function1<? super PokemonCapturedEvent, Unit> registerCapturedPokemon() {
        return event -> {
            Player player = event.getPlayer();

            if (player instanceof ServerPlayer serverPlayer) {
                player.awardStat(Stats.CUSTOM.get(POKEMON_CAUGHT));

                if(event.getPokemon().isLegendary() || event.getPokemon().isMythical() || event.getPokemon().isUltraBeast()) {
                    for (Player p : player.getServer().getPlayerList().getPlayers()) {
                        p.sendSystemMessage(
                                Component.literal(
                                        player.getName().getString() + " a capturé un " + event.getPokemon().getSpecies().getTranslatedName().getString() + " !"
                                ).withStyle(Style.EMPTY.withColor(0x7f32a8))
                        );
                    }
                }
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
                                        player.getName().getString() + " a capturé un " + event.getPokemon().getSpecies().getTranslatedName().getString() + " shiny !"
                                ).withStyle(Style.EMPTY.withColor(0xc49e33))
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

    public static Function1<? super FossilRevivedEvent, Unit> revivedFossil() {
        return event -> {
            Player player = event.getPlayer();

            if(event.getPokemon().getShiny()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    player.awardStat(Stats.CUSTOM.get(SHINY_POKEMON_CAUGHT));
                    player.awardStat(Stats.CUSTOM.get(POKEMON_CAUGHT));

                    for (Player p : player.getServer().getPlayerList().getPlayers()) {
                        p.sendSystemMessage(
                                Component.literal(
                                        player.getName().getString() + " a ressuscité un " + event.getPokemon().getSpecies().getTranslatedName().getString() + " shiny !"
                                ).withStyle(Style.EMPTY.withColor(0xc49e33))
                        );
                    }
                }
            }

            return Unit.INSTANCE;
        };
    }

}

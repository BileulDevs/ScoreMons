package com.darcosse.scoremons.fabric.stats;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.pokedex.CaughtCount;
import com.darcosse.scoremons.fabric.config.ConfigManager;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

                if (ConfigManager.shouldBroadcastLegendaryCaught() &&
                        (event.getPokemon().isLegendary() || event.getPokemon().isMythical() || event.getPokemon().isUltraBeast())) {

                    for (Player p : player.getServer().getPlayerList().getPlayers()) {
                        p.sendSystemMessage(
                                Component.translatable(
                                        "scoremons.message.legendary_caught",
                                        player.getName().getString(),
                                        Component.translatable("pokemon.species." + event.getPokemon().getSpecies().showdownId())
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

                    if (ConfigManager.shouldBroadcastShinyCaught()) {
                        for (Player p : player.getServer().getPlayerList().getPlayers()) {
                            p.sendSystemMessage(
                                    Component.translatable(
                                            "scoremons.message.shiny_caught",
                                            player.getName().getString(),
                                            Component.translatable("pokemon.species." + event.getPokemon().getSpecies().showdownId())
                                    ).withStyle(Style.EMPTY.withColor(0xc49e33))
                            );
                        }
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

                    if (ConfigManager.shouldBroadcastShinyFossilRevived()) {
                        for (Player p : player.getServer().getPlayerList().getPlayers()) {
                            p.sendSystemMessage(
                                    Component.translatable(
                                            "scoremons.message.shiny_fossil_revived",
                                            player.getName().getString(),
                                            Component.translatable("pokemon.species." + event.getPokemon().getSpecies().getName())
                                    ).withStyle(Style.EMPTY.withColor(0xc49e33))
                            );
                        }
                    }
                }
            }

            return Unit.INSTANCE;
        };
    }

    public static Function1<? super PokedexDataChangedEvent, Unit> registerCaughtCount() {
        return event -> {
            UUID playerUUID = event.getPlayerUUID();

            MinecraftServer server = FabricLoader.getInstance()
                    .getGameInstance() instanceof MinecraftServer
                    ? (MinecraftServer) FabricLoader.getInstance().getGameInstance()
                    : null;

            if (server != null) {
                ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
                if (player != null) {
                    updateCaughtCount(player);
                }
            } else {
                updateCaughtCountLocal(playerUUID);
            }

            return Unit.INSTANCE;
        };
    }

    private static void updateCaughtCount(ServerPlayer player) {
        int count = Cobblemon.playerDataManager.getPokedexData(player.getUUID()).getDexCalculatedValue(
                ResourceLocation.tryParse("cobblemon:national"), CaughtCount.INSTANCE
        );

        int currentCount = player.getStats().getValue(Stats.CUSTOM.get(POKEMON_REGISTERED));
        int toUpdate = count - currentCount;

        for (int i = 0; i < toUpdate; i++) {
            player.awardStat(Stats.CUSTOM.get(POKEMON_REGISTERED));
        }
    }

    private static void updateCaughtCountLocal(UUID playerUUID) {
        int count = Cobblemon.playerDataManager.getPokedexData(playerUUID).getDexCalculatedValue(
                ResourceLocation.tryParse("cobblemon:national"), CaughtCount.INSTANCE
        );
    }
}
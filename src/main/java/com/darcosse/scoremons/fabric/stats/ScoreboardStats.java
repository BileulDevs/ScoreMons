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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class ScoreboardStats {

    private static final String MODID = "scoremons";

    // IDs Yarn = Identifier (pas ResourceLocation)
    public static final Identifier POKEMON_CAUGHT      =  Identifier.of(MODID, "pokemon_caught");
    public static final Identifier SHINY_POKEMON_CAUGHT=  Identifier.of(MODID, "shiny_pokemon_caught");
    public static final Identifier BATTLE_WON          =  Identifier.of(MODID, "battle_won");
    public static final Identifier POKEMON_REGISTERED  =  Identifier.of(MODID, "pokemon_registered");

    /** Enregistrer les stats custom (Yarn) */
    public static void registerStats() {
        Registry.register(Registries.CUSTOM_STAT, POKEMON_CAUGHT, POKEMON_CAUGHT);
        Registry.register(Registries.CUSTOM_STAT, SHINY_POKEMON_CAUGHT, SHINY_POKEMON_CAUGHT);
        Registry.register(Registries.CUSTOM_STAT, BATTLE_WON, BATTLE_WON);
        Registry.register(Registries.CUSTOM_STAT, POKEMON_REGISTERED, POKEMON_REGISTERED);

        // Force la création des Stat<?> correspondantes
        Stats.CUSTOM.getOrCreateStat(POKEMON_CAUGHT);
        Stats.CUSTOM.getOrCreateStat(SHINY_POKEMON_CAUGHT);
        Stats.CUSTOM.getOrCreateStat(BATTLE_WON);
        Stats.CUSTOM.getOrCreateStat(POKEMON_REGISTERED);
    }

    /** +1 quand un Pokémon est capturé ; annonce si légendaire/mythique/ultra */
    public static Function1<? super PokemonCapturedEvent, Unit> registerCapturedPokemon() {
        return event -> {
            PlayerEntity player = event.getPlayer();
            if (player instanceof ServerPlayerEntity sp) {
                sp.incrementStat(Stats.CUSTOM.getOrCreateStat(POKEMON_CAUGHT));

                if (ConfigManager.shouldBroadcastLegendaryCaught()
                        && (event.getPokemon().isLegendary()
                        || event.getPokemon().isMythical()
                        || event.getPokemon().isUltraBeast())) {

                    Text msg = Text.translatable(
                            "scoremons.message.legendary_caught",
                            sp.getName().getString(),
                            Text.translatable("pokemon.species." + event.getPokemon().getSpecies().showdownId())
                    ).formatted(Formatting.LIGHT_PURPLE);

                    for (ServerPlayerEntity p : sp.getServer().getPlayerManager().getPlayerList()) {
                        p.sendMessage(msg, false);
                    }
                }
            }
            return Unit.INSTANCE;
        };
    }

    /** +1 shiny capturé ; annonce si activée */
    public static Function1<? super PokemonCapturedEvent, Unit> registerCapturedShinyPokemon() {
        return event -> {
            PlayerEntity player = event.getPlayer();
            if (event.getPokemon().getShiny() && player instanceof ServerPlayerEntity sp) {
                sp.incrementStat(Stats.CUSTOM.getOrCreateStat(SHINY_POKEMON_CAUGHT));

                if (ConfigManager.shouldBroadcastShinyCaught()) {
                    Text msg = Text.translatable(
                            "scoremons.message.shiny_caught",
                            sp.getName().getString(),
                            Text.translatable("pokemon.species." + event.getPokemon().getSpecies().showdownId())
                    ).formatted(Formatting.GOLD);

                    for (ServerPlayerEntity p : sp.getServer().getPlayerManager().getPlayerList()) {
                        p.sendMessage(msg, false);
                    }
                }
            }
            return Unit.INSTANCE;
        };
    }

    /** +1 victoire PvP */
    public static Function1<? super BattleVictoryEvent, Unit> battleVictory() {
        return event -> {
            if (event.getBattle().isPvP()) {
                UUID winnerId = event.getWinners().getFirst().getUuid();
                PlayerEntity any = event.getBattle().getPlayers().getFirst();
                MinecraftServer server = any.getServer();
                if (server != null) {
                    ServerPlayerEntity winner = server.getPlayerManager().getPlayer(winnerId);
                    if (winner != null) {
                        winner.incrementStat(Stats.CUSTOM.getOrCreateStat(BATTLE_WON));
                    }
                }
            }
            return Unit.INSTANCE;
        };
    }

    /** Synchronise la stat "pokemon_registered" avec le nombre du Pokédex */
    public static Function1<? super PokedexDataChangedEvent, Unit> registerCaughtCount() {
        return event -> {
            UUID uuid = event.getPlayerUUID();

            MinecraftServer server = Cobblemon.INSTANCE.getImplementation().server();
            if (server == null) return Unit.INSTANCE;

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                updateCaughtCount(player);
            }
            return Unit.INSTANCE;
        };
    }

    public static Function1<? super FossilRevivedEvent, Unit> revivedFossil() {
        return event -> {
            var player = event.getPlayer();

            if (event.getPokemon().getShiny() && player instanceof ServerPlayerEntity sp) {
                sp.incrementStat(Stats.CUSTOM.getOrCreateStat(SHINY_POKEMON_CAUGHT));
                sp.incrementStat(Stats.CUSTOM.getOrCreateStat(POKEMON_CAUGHT));

                if (ConfigManager.shouldBroadcastShinyFossilRevived()) {
                    Text message = Text.translatable(
                            "scoremons.message.shiny_fossil_revived",
                            sp.getName().getString(),
                            Text.translatable("pokemon.species." + event.getPokemon().getSpecies().getName())
                    ).styled(style -> style.withColor(0xc49e33));

                    for (ServerPlayerEntity p : sp.getServer().getPlayerManager().getPlayerList()) {
                        p.sendMessage(message, false);
                    }
                }
            }
            return Unit.INSTANCE;
        };
    }

    private static void updateCaughtCount(ServerPlayerEntity player) {
        int pokedexCount = Cobblemon.INSTANCE.getPlayerDataManager()
                .getPokedexData(player.getUuid())
                .getDexCalculatedValue( Identifier.of("cobblemon", "national"), CaughtCount.INSTANCE);

        Stat<Identifier> stat = Stats.CUSTOM.getOrCreateStat(POKEMON_REGISTERED);

        player.resetStat(stat);
        for (int i = 0; i < pokedexCount; i++) {
            player.incrementStat(stat);
        }
    }
}

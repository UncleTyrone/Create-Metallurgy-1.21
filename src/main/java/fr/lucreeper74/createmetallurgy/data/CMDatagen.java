package fr.lucreeper74.createmetallurgy.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy.CastingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy.FoundryRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipe.vanilla.CMStandardRecipeGen;
import fr.lucreeper74.createmetallurgy.ponders.CMPonders;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CMDatagen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        addExtraRegistrateData();

        if (event.includeServer()) {
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
            gen.addProvider(true,
                    (DataProvider.Factory<CMStandardRecipeGen>) packOutput -> new CMStandardRecipeGen(packOutput,
                            lookupProvider));
            gen.addProvider(true,
                    (DataProvider.Factory<CastingRecipeGen>) packOutput -> new CastingRecipeGen(packOutput,
                            lookupProvider));
            gen.addProvider(true,
                    (DataProvider.Factory<FoundryRecipeGen>) packOutput -> new FoundryRecipeGen(packOutput,
                            lookupProvider));

            CMProcessingRecipesGen.registerAll(gen, output, lookupProvider);
            DataProvider.Factory<CMGenEntriesProvider> factory = packOutput -> new CMGenEntriesProvider(packOutput,
                    event.getLookupProvider());
            gen.addProvider(true, factory);

            // Add KubeJS recipe schema provider
            event.addProvider(new RecipeSchemaProvider("Create Metallurgy Recipe Schemas", event) {
                @Override
                public void add(HolderLookup.Provider lookup) {
                    add(ResourceLocation.fromNamespaceAndPath(CreateMetallurgy.MOD_ID, "casting_in_basin"), builder -> {
                        builder.mappings("castingInBasin", "casting");
                    });

                    add(ResourceLocation.fromNamespaceAndPath(CreateMetallurgy.MOD_ID, "casting_in_table"), builder -> {
                        builder.mappings("castingInTable", "casting");
                    });

                    add(ResourceLocation.fromNamespaceAndPath(CreateMetallurgy.MOD_ID, "grinding"), builder -> {
                        builder.mappings("grinding", "grinding");
                    });

                    add(ResourceLocation.fromNamespaceAndPath(CreateMetallurgy.MOD_ID, "alloying"), builder -> {
                        builder.mappings("alloying", "alloying");
                    });

                    add(ResourceLocation.fromNamespaceAndPath(CreateMetallurgy.MOD_ID, "melting"), builder -> {
                        builder.mappings("melting", "melting");
                    });
                }
            });
        }
    }

    private static void addExtraRegistrateData() {
        CreateMetallurgy.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;
            provideDefaultLang("interface", langConsumer);
            provideDefaultLang("tooltips", langConsumer);
            providePonderLang(langConsumer);
        });
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/createmetallurgy/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        // Register this since FMLClientSetupEvent does not run during datagen
        PonderIndex.addPlugin(new CMPonders());
        PonderIndex.getLangAccess().provideLang(CreateMetallurgy.MOD_ID, consumer);
    }
}

package coffee.amo.attribution;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AttributeUnificationJSONListener extends SimpleJsonResourceReloadListener {
    public static final Map<ResourceLocation, List<ResourceLocation>> ATTRIBUTE_UNIFICATION_MAP = new HashMap<>();
    public static Gson GSON = new Gson();
    public AttributeUnificationJSONListener() {
        super(GSON, "replacement");
    }

    public static void register(AddReloadListenerEvent event){
        event.addListener(new AttributeUnificationJSONListener());
    }

    public static ResourceLocation getReplacement(ResourceLocation name) {
        AtomicReference<ResourceLocation> replacement = new AtomicReference<>(name);
        ATTRIBUTE_UNIFICATION_MAP.forEach((key, value) -> {
            if(value.contains(name)){
                replacement.set(key);
            }
        });
        return replacement.get();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager rm, ProfilerFiller profiler) {
        ATTRIBUTE_UNIFICATION_MAP.clear();
        object.forEach((key, value) -> {
            JsonObject file = value.getAsJsonObject();
            file.entrySet().forEach(entry -> {
                ResourceLocation attribute = new ResourceLocation(entry.getKey());
                if(ATTRIBUTE_UNIFICATION_MAP.containsKey(attribute)) {
                    Attribution.LOGGER.warn("Duplicate attribute unification entry for " + attribute.getPath());
                }
                JsonArray array = entry.getValue().getAsJsonArray();
                List<ResourceLocation> list = new ArrayList<>();
                array.forEach(element -> {
                    list.add(new ResourceLocation(element.getAsString()));
                });
                ATTRIBUTE_UNIFICATION_MAP.put(attribute, list);
            });
        });
        System.out.println(ATTRIBUTE_UNIFICATION_MAP);
    }

    public static boolean shouldBeReplaced(String attr){
        AtomicBoolean result = new AtomicBoolean(false);
        ATTRIBUTE_UNIFICATION_MAP.values().forEach(list -> {
            if(list.contains(ResourceLocation.tryParse(attr))){
                result.set(true);
            }
        });
        return result.get();
    }
}

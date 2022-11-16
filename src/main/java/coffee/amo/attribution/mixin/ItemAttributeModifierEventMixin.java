package coffee.amo.attribution.mixin;

import coffee.amo.attribution.AttributeUnificationJSONListener;
import com.google.common.collect.Multimap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemAttributeModifierEvent.class)
public abstract class ItemAttributeModifierEventMixin {

    @Shadow public abstract Multimap<Attribute, AttributeModifier> getModifiers();

    @Shadow protected abstract Multimap<Attribute, AttributeModifier> getModifiableMap();

    @Inject(method = "addModifier", at = @At("HEAD"), remap = false, cancellable = true)
    private void itemAttributeModifierEventMixin(Attribute attribute, AttributeModifier modifier, CallbackInfoReturnable<Boolean> cir){
        String name = attribute.getDescriptionId();
        if(AttributeUnificationJSONListener.shouldBeReplaced(name)){
            attribute = ForgeRegistries.ATTRIBUTES.getValue(AttributeUnificationJSONListener.getReplacement(ResourceLocation.tryParse(name)));
        }
        cir.setReturnValue(getModifiableMap().put(attribute, modifier));
    }
}

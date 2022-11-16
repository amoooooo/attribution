package coffee.amo.attribution.mixin;

import coffee.amo.attribution.AttributeUnificationJSONListener;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "addAttributeModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;save()Lnet/minecraft/nbt/CompoundTag;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void itemAttributeModifierMixin(Attribute pAttribute, AttributeModifier pModifier, EquipmentSlot pSlot, CallbackInfo ci, ListTag listtag){
        String name = pAttribute.getDescriptionId();
        if(AttributeUnificationJSONListener.shouldBeReplaced(name)){
            pAttribute = ForgeRegistries.ATTRIBUTES.getValue(AttributeUnificationJSONListener.getReplacement(ResourceLocation.tryParse(name)));
        }
        CompoundTag compoundtag = pModifier.save();
        compoundtag.putString("AttributeName", ForgeRegistries.ATTRIBUTES.getKey(pAttribute).toString());
        if(pSlot != null){
            compoundtag.putString("Slot", pSlot.getName());
        }
        listtag.add(compoundtag);
        ci.cancel();
    }
}

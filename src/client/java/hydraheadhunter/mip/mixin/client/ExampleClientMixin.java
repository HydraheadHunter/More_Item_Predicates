package hydraheadhunter.mip.mixin.client;

import hydraheadhunter.mip.iTemplateMixin;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ExampleClientMixin implements iTemplateMixin {

}
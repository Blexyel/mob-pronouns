package gay.rooot.mobpronouns.mixin;

import gay.rooot.mobpronouns.stuff.WokeInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin<T extends Entity> {
    @Shadow
    protected abstract void renderLabelIfPresent(
        T entity,
        Text text,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        float tickDelta
    );

    @Shadow
    protected boolean hasLabel(T entity) {
        return true;
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (!(entity instanceof WokeInterface woke)) return;
        if (entity.isPlayer()) return;
        if (!(entity instanceof MobEntity mob) || !mob.isMobOrPlayer()) return;

        String pronouns = woke.getPronouns();
        if (pronouns == null || pronouns.isEmpty()) return;

        double distanceSq = entity.squaredDistanceTo(MinecraftClient.getInstance().cameraEntity);
        if (distanceSq > 4096.0) return; // Match vanilla label distance

        matrices.push();

        // Slight downward offset so pronouns appear below the original name
        matrices.translate(0.0, -0.25, 0.0);

        this.renderLabelIfPresent(
            entity,
            Text.literal(pronouns),
            matrices,
            vertexConsumers,
            light,
            tickDelta
        );

        matrices.pop();
    }
}
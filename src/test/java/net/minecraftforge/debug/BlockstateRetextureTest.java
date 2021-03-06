package net.minecraftforge.debug;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = BlockstateRetextureTest.MODID, name = "BlockstateRetextureTest", version = BlockstateRetextureTest.VERSION, acceptableRemoteVersions = "*", clientSideOnly = true)
public class BlockstateRetextureTest
{
    public static final String MODID = "forge_blockstate_retexture_test";
    public static final String VERSION = "1.0";
    static final boolean ENABLED = false;

    private static ResourceLocation fenceName = new ResourceLocation("minecraft", "fence");
    private static ModelResourceLocation fenceLocation = new ModelResourceLocation(fenceName, "east=true,north=false,south=false,west=true");
    private static ResourceLocation stoneName = new ResourceLocation("minecraft", "stone");
    private static ModelResourceLocation stoneLocation = new ModelResourceLocation(stoneName, "normal");

    private static Function<ResourceLocation, TextureAtlasSprite> textureGetter = location ->
    {
        assert location != null;
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    };

    @Mod.EventBusSubscriber(modid = MODID, value = Side.CLIENT)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void onModelBakeEvent(ModelBakeEvent event)
        {
            if (!ENABLED)
            {
                return;
            }

            IModel fence = ModelLoaderRegistry.getModelOrLogError(fenceLocation, "Error loading fence model");
            IModel stone = ModelLoaderRegistry.getModelOrLogError(stoneLocation, "Error loading planks model");
            IModel retexturedFence = fence.retexture(ImmutableMap.of("texture", "blocks/log_oak"));
            IModel retexturedStone = stone.retexture(ImmutableMap.of("all", "blocks/diamond_block"));

            IBakedModel fenceResult = retexturedFence.bake(fence.getDefaultState(), DefaultVertexFormats.BLOCK, textureGetter);
            IBakedModel stoneResult = retexturedStone.bake(stone.getDefaultState(), DefaultVertexFormats.BLOCK, textureGetter);

            event.getModelRegistry().putObject(fenceLocation, fenceResult);
            event.getModelRegistry().putObject(stoneLocation, stoneResult);
        }
    }
}

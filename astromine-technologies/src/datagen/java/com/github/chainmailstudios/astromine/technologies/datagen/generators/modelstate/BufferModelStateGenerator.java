package com.github.chainmailstudios.astromine.technologies.datagen.generators.modelstate;

import net.minecraft.block.Block;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.Texture;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.util.Identifier;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.datagen.generator.modelstate.onetime.GenericBlockModelStateGenerator;
import me.shedaniel.cloth.api.datagen.v1.ModelStateData;

public class BufferModelStateGenerator extends GenericBlockModelStateGenerator {
	private final MachineTier type;

	public BufferModelStateGenerator(MachineTier type, Block... blocks) {
		super(blocks);
		this.type = type;
	}

	public static Identifier getTextureId(MachineTier type) {
		switch (type) {
			case PRIMITIVE:
				return AstromineCommon.identifier("primitive_machine");
			case BASIC:
				return AstromineCommon.identifier("basic_machine");
			case ADVANCED:
				return AstromineCommon.identifier("advanced_machine");
			case ELITE:
				return AstromineCommon.identifier("elite_machine");
			case CREATIVE:
				return AstromineCommon.identifier("creative_machine");
			default:
				return AstromineCommon.identifier("advanced_machine");
		}
	}

	public static Identifier getSubId(Identifier main, String suffix) {
		return new Identifier(main.getNamespace(), "block/" + main.getPath() + suffix);
	}

	public Texture getTexture(Block block) {
		return (new Texture()).put(TextureKey.SIDE, getSubId(getTextureId(type), "_side")).put(TextureKey.TOP, Texture.getSubId(block, "_top")).put(TextureKey.BOTTOM, getSubId(getTextureId(type), "_bottom"));
	}

	@Override
	public String getGeneratorName() {
		return "buffer_modelstate";
	}

	@Override
	public void generate(ModelStateData data) {
		blocks.forEach((block) -> {
			Texture texture = getTexture(block);
			Identifier identifier = Models.CUBE_BOTTOM_TOP.upload(block, texture, data::addModel);
			data.addState(block, ModelStateData.createSingletonBlockState(block, identifier));
			data.addSimpleBlockItemModel(block);
		});
	}
}

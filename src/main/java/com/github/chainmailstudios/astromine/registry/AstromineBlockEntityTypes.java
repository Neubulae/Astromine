package com.github.chainmailstudios.astromine.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.block.entity.*;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class AstromineBlockEntityTypes {
	public static BlockEntityType<HolographicBridgeProjectorBlockEntity> HOLOGRAPHIC_BRIDGE = register("holographic_bridge", HolographicBridgeProjectorBlockEntity::new, AstromineBlocks.HOLOGRAPHIC_BRIDGE_PROJECTOR);

	public static BlockEntityType<VentBlockEntity> vent = register("vent", VentBlockEntity::new, AstromineBlocks.VENT);

	public static BlockEntityType<FluidTankBlockEntity> FLUID_TANK = register("fluid_tank", FluidTankBlockEntity::new, AstromineBlocks.FLUID_TANK);

	public static BlockEntityType<FuelGeneratorBlockEntity> FUEL_GENERATOR = register("fuel_generator", FuelGeneratorBlockEntity::new, AstromineBlocks.FUEL_GENERATOR);

	public static BlockEntityType<EnergyWireConnectorBlockEntity> ENERGY_WIRE_CONNECTOR = register("energy_wire_connector", EnergyWireConnectorBlockEntity::new, AstromineBlocks.ENERGY_WIRE_CONNECTOR);

	public static void initialize() {
		// Unused.
	}

	/**
	 * @param name            Name of BlockEntityType instance to be registered
	 * @param supplier        Supplier of BlockEntity to use for BlockEntityType
	 * @param supportedBlocks Blocks the BlockEntity can be attached to
	 * @return Registered BlockEntityType
	 */
	public static <B extends BlockEntity> BlockEntityType<B> register(String name, Supplier<B> supplier, Block... supportedBlocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, AstromineCommon.identifier(name), BlockEntityType.Builder.create(supplier, supportedBlocks).build(null));
	}
}

/*
 * MIT License
 * 
 * Copyright (c) 2020 Chainmail Studios
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.chainmailstudios.astromine.common.block.conveyor;

import com.github.chainmailstudios.astromine.common.block.conveyor.entity.DoubleMachineBlockEntity;
import com.github.chainmailstudios.astromine.common.block.conveyor.entity.SplitterBlockEntity;
import com.github.chainmailstudios.astromine.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.common.conveyor.ConveyableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SplitterBlock extends HorizontalFacingBlock implements BlockEntityProvider, ConveyableBlock {
	public SplitterBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new SplitterBlockEntity();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		updateDiagonals(world, this, blockPos);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DoubleMachineBlockEntity) {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ((DoubleMachineBlockEntity) blockEntity).getLeftStack());
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ((DoubleMachineBlockEntity) blockEntity).getRightStack());
				((DoubleMachineBlockEntity) blockEntity).setRemoved(true);
			}

			super.onStateReplaced(state, world, pos, newState, notify);
		}

		updateDiagonals(world, this, pos);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
		Direction direction = blockState.get(FACING);
		DoubleMachineBlockEntity machineBlockEntity = (DoubleMachineBlockEntity) world.getBlockEntity(blockPos);

		BlockPos leftPos = blockPos.offset(direction.rotateYCounterclockwise());
		BlockPos rightPos = blockPos.offset(direction.rotateYClockwise());

		BlockEntity leftBlockEntity = world.getBlockEntity(leftPos);
		if (leftBlockEntity instanceof Conveyable && ((Conveyable) leftBlockEntity).validInputSide(direction.rotateYClockwise()))
			machineBlockEntity.setLeft(true);
		else
			machineBlockEntity.setLeft(false);

		BlockEntity rightBlockEntity = world.getBlockEntity(rightPos);
		if (rightBlockEntity instanceof Conveyable && ((Conveyable) rightBlockEntity).validInputSide(direction.rotateYCounterclockwise()))
			machineBlockEntity.setRight(true);
		else
			machineBlockEntity.setRight(false);
	}
}

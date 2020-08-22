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

package com.github.chainmailstudios.astromine.technologies.common.screenhandler;

import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import com.github.chainmailstudios.astromine.common.screenhandler.base.ComponentEnergyItemScreenHandler;
import com.github.chainmailstudios.astromine.registry.AstromineScreenHandlers;
import com.github.vini2003.blade.common.data.Position;
import com.github.vini2003.blade.common.data.Size;
import com.github.vini2003.blade.common.widget.base.SlotWidget;

public class BlockPlacerScreenHandler extends ComponentEnergyItemScreenHandler {
	public BlockPlacerScreenHandler(int syncId, PlayerEntity player, BlockPos position) {
		super(AstromineTechnologiesScreenHandlers.BLOCK_PLACER, syncId, player, position);
	}

	@Override
	public void initialize(int width, int height) {
		super.initialize(width, height);

		SlotWidget slot = new SlotWidget(0, blockEntity);
		slot.setPosition(Position.of(mainTab, mainTab.getWidth() / 2F - 9F, 26));
		slot.setSize(Size.of(18, 18));

		mainTab.addWidget(slot);
	}
}
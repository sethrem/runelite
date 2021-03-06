/*
 * Copyright (c) 2017, Tyler <http://github.com/tylerthardy>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.runepouch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.api.Query;
import net.runelite.api.Varbits;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.QueryRunner;

public class RunepouchOverlay extends Overlay
{
	private static final Varbits[] AMOUNT_VARBITS =
	{
		Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3
	};
	private static final Varbits[] RUNE_VARBITS =
	{
		Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3
	};

	private final RuneImageCache runeImageCache = new RuneImageCache();

	private final QueryRunner queryRunner;
	private final Client client;
	private final RunepouchConfig config;
	private final TooltipManager tooltipManager;

	@Inject
	RunepouchOverlay(QueryRunner queryRunner, Client client, RunepouchConfig config, TooltipManager tooltipManager)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.tooltipManager = tooltipManager;
		this.queryRunner = queryRunner;
		this.client = client;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics, java.awt.Point point)
	{
		if (!config.enabled())
		{
			return null;
		}

		Query query = new InventoryWidgetItemQuery().idEquals(ItemID.RUNE_POUCH);
		WidgetItem[] items = queryRunner.runQuery(query);
		if (items.length == 0)
		{
			return null;
		}

		WidgetItem runePouch = items[0];
		Point location = runePouch.getCanvasLocation();
		if (location == null)
		{
			return null;
		}

		assert AMOUNT_VARBITS.length == RUNE_VARBITS.length;

		graphics.setFont(FontManager.getRunescapeSmallFont());

		StringBuilder tooltipBuilder = new StringBuilder();
		for (int i = 0; i < AMOUNT_VARBITS.length; i++)
		{
			Varbits amountVarbit = AMOUNT_VARBITS[i];

			int amount = client.getSetting(amountVarbit);
			if (amount <= 0)
			{
				continue;
			}

			Varbits runeVarbit = RUNE_VARBITS[i];
			int runeId = client.getSetting(runeVarbit);

			tooltipBuilder
				.append(amount)
				.append(" <col=ffff00>")
				.append(runeImageCache.getName(runeId))
				.append("</col></br>");

			if (config.showOnlyOnHover())
			{
				continue;
			}

			graphics.setColor(Color.black);
			graphics.drawString("" + formatNumber(amount), location.getX() + (config.showIcons() ? 13 : 1),
				location.getY() + 14 + graphics.getFontMetrics().getHeight() * i);

			graphics.setColor(config.fontColor());
			graphics.drawString("" + formatNumber(amount), location.getX() + (config.showIcons() ? 12 : 0),
				location.getY() + 13 + graphics.getFontMetrics().getHeight() * i);

			if (!config.showIcons())
			{
				continue;
			}

			BufferedImage runeImg = runeImageCache.getImage(runeId);
			if (runeImg != null)
			{
				OverlayUtil.renderImageLocation(graphics,
					new Point(location.getX(), location.getY() + 2 + (graphics.getFontMetrics().getHeight()) * i),
					runeImg);
			}
		}

		if (runePouch.getCanvasBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			tooltipManager.add(new Tooltip(tooltipBuilder.toString()));
		}
		return null;
	}

	private static String formatNumber(int amount)
	{
		return amount < 1000 ? String.valueOf(amount) : amount / 1000 + "K";
	}
}

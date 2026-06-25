package kami.gg.souppvp.util.menu.pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kami.gg.souppvp.util.menu.Button;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public abstract class FilterablePaginatedMenu<T> extends PaginatedMenu {

	@Getter private final List<PageFilter<T>> filters;
	@Getter @Setter private int scrollIndex = 0;

	public FilterablePaginatedMenu(Player player, String title, int size) {
		super(player, title, size);
		this.filters = generateFilters();
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		buttons.put(7, new PageFilterButton<>(this));
		return buttons;
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		return getFilteredButtons();
	}

	public abstract Map<Integer, Button> getFilteredButtons();

	public List<PageFilter<T>> generateFilters() {
		return new ArrayList<>();
	}

}

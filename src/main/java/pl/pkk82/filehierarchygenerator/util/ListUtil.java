package pl.pkk82.filehierarchygenerator.util;

import java.util.List;
import java.util.ListIterator;

public class ListUtil {

	@SafeVarargs
	public static <E> void insertBetween(List<E> list, E... elements) {
		if (list != null && list.size() > 1) {
			ListIterator<E> iIterator = list.listIterator();
			if (iIterator.hasNext()) {
				iIterator.next();
				if (iIterator.hasNext()) {
					for (E newElement : elements) {
						iIterator.add(newElement);
					}
				}
			}
		}
	}

	@SafeVarargs
	public static <E> void insertBefore(List<E> list, E... elements) {
		if (list != null) {
			ListIterator<E> iIterator = list.listIterator();
			for (E newElement : elements) {
				iIterator.add(newElement);
			}
		}
	}

	@SafeVarargs
	public static <E> void insertAfter(List<E> list, E... elements) {
		if (list != null) {
			ListIterator<E> iIterator = list.listIterator(list.size());
			for (E newElement : elements) {
				iIterator.add(newElement);
			}
		}
	}
}

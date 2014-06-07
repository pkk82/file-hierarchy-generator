package pl.pkk82.filehierarchygenerator.util;


import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import com.google.common.collect.Lists;

import org.assertj.core.api.ListAssert;
import org.junit.Test;

public class ListUtilTest {

	private List<String> list;

	@Test
	public void shouldNotInsertBetweenWhenNull() {
		givenListAs(null);
		whenInsertBetween("b");
		thenList().isNull();
	}

	@Test
	public void shouldNotInsertBetweenWhenOneElement() {
		givenListAs(Lists.newArrayList("a"));
		whenInsertBetween("b");
		thenList().containsOnly("a");
	}

	@Test
	public void shouldInsertBetweenWhenTwoElements() {
		givenListAs(Lists.newArrayList("a", "d"));
		whenInsertBetween("b", "c");
		thenList().containsOnly("a", "b", "c", "d");
	}

	@Test
	public void shouldInsertBetweenWhenThreeElements() {
		givenListAs(Lists.newArrayList("a", "d", "g"));
		whenInsertBetween("x", "y");
		thenList().containsOnly("a", "x", "y", "d", "x", "y", "g");
	}

	@Test
	public void shouldNotInsertBeforeWhenNull() {
		givenListAs(null);
		whenInsertBefore("x", "y");
		thenList().isNull();
	}

	@Test
	public void shouldInsertBefore() {
		givenListAs(Lists.newArrayList("c"));
		whenInsertBefore("a", "b");
		thenList().containsOnly("a", "b", "c");
	}


	@Test
	public void shouldNotInsertAfterWhenNull() {
		givenListAs(null);
		whenInsertAfter("x", "y");
		thenList().isNull();
	}

	@Test
	public void shouldInsertAfter() {
		givenListAs(Lists.newArrayList("a"));
		whenInsertBefore("b", "c");
		thenList().containsOnly("a", "b", "c");
	}

	private void givenListAs(List<String> list) {
		this.list = list;
	}

	private void whenInsertBetween(String... elements) {
		ListUtil.insertBetween(list, elements);
	}

	private void whenInsertBefore(String... elements) {
		ListUtil.insertBefore(list, elements);
	}

	private void whenInsertAfter(String... elements) {
		ListUtil.insertAfter(list, elements);
	}

	private ListAssert<String> thenList() {
		return then(list);
	}
}
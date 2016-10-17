package lotusprize;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class IteratorTest {
	
	public static void main(String[]args){
		List<Integer> list = Lists.newArrayList(1,2,3,4);
		Iterator<Integer> it = list.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
			it.remove();
		}
		String s = "hello\nworld";
		System.out.println(s);
		System.out.println(s.replaceAll("\n", "<br>"));
		
		Set<Integer> s1= Sets.newHashSet(1,2,3,4);
		Set<Integer> s2= Sets.newHashSet(2,3,5);
//		System.out.println(Sets.difference(s1, s2));
		System.out.println(s2.removeAll(s1));
		System.out.println(s2);
		List<Integer> l1 = Lists.newArrayList(1,2,3);
		List<Integer> l2 = Lists.newArrayList(2,3,4);
		l2.removeAll(l1);
		System.out.println(l1);
		
	}

}

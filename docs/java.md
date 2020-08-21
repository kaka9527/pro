java泛型中T、E、K、V、？等含义
	 E - Element (在集合中使用，因为集合中存放的是元素)，E是对各方法中的泛型类型进行限制，以保证同一个对象调用不同的方法时，操作的类型必定是相同的。E可以用其它任意字母代替
	 T - Type（Java 类），T代表在调用时的指定类型。会进行类型推断
	 K - Key（键）
	 V - Value（值）
	 N - Number（数值类型）
	 ？ -  表示不确定的java类型，是类型通配符，代表所有类型。？不会进行类型推断
泛型类定义
	//紧跟类名后面
	public class Test<T>{}
泛型方法定义
	//紧跟修饰符后面（public）
	public <T> T Test1(T t){}
?通配符使用
	//变量赋值或变量声明时候使用
	List<?> list;
	List<? extends Number> uNumberList;
	List<? super Integer> intgerList;
	注：List<? extends T>和List <? super T>有什么区别

	List<? extends T>可以接受任何继承自T的类型的List，
	List<? super T>可以接受任何T的父类构成的List。
	例如List<? extends Number>可以接受List<Integer>或List<Float>
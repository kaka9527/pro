java������T��E��K��V�����Ⱥ���
	 E - Element (�ڼ�����ʹ�ã���Ϊ�����д�ŵ���Ԫ��)��E�ǶԸ������еķ������ͽ������ƣ��Ա�֤ͬһ��������ò�ͬ�ķ���ʱ�����������ͱض�����ͬ�ġ�E����������������ĸ����
	 T - Type��Java �ࣩ��T�����ڵ���ʱ��ָ�����͡�����������ƶ�
	 K - Key������
	 V - Value��ֵ��
	 N - Number����ֵ���ͣ�
	 �� -  ��ʾ��ȷ����java���ͣ�������ͨ����������������͡���������������ƶ�
�����ඨ��
	//������������
	public class Test<T>{}
���ͷ�������
	//�������η����棨public��
	public <T> T Test1(T t){}
?ͨ���ʹ��
	//������ֵ���������ʱ��ʹ��
	List<?> list;
	List<? extends Number> uNumberList;
	List<? super Integer> intgerList;
	ע��List<? extends T>��List <? super T>��ʲô����

	List<? extends T>���Խ����κμ̳���T�����͵�List��
	List<? super T>���Խ����κ�T�ĸ��๹�ɵ�List��
	����List<? extends Number>���Խ���List<Integer>��List<Float>
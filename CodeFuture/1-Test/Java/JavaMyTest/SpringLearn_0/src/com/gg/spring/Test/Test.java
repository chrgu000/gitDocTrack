package com.gg.spring.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
 
import com.gg.spring.bean.HelloWorld;
 
/**
 * ������
 * @author sjf0115
 *
 */
public class Test {
 
	private static ApplicationContext context;
	private static HelloWorld helloWorld;
	
	public static void main(String[] args) {
		//һ��Spring��ܵ��÷�ʽ
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 2. ��IOC�����л�ȡBeanʵ��
		helloWorld = (HelloWorld)context.getBean("helloworld");
		// 3.����sayHello����
		helloWorld.sayHello();
		
		//������ͨ������ʽ
//		helloWorld = new HelloWorld();
//		helloWorld.sayHello();
	}
}
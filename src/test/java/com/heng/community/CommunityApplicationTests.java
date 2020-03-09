package com.heng.community;

import com.heng.community.dao.AlphaDao;
import com.heng.community.dao.AlphaDaoImpl;
import com.heng.community.service.AlphaService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private  ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	@Autowired
	@Qualifier("alphaHibernate")
	private AlphaDao alphaDao;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
	}

	@Test
	public void   testApplicationContext() {
		System.out.println(applicationContext);
		AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		alphaDao=applicationContext.getBean("alphaHibernate",AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testAlphaService(){
		AlphaService alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}



}

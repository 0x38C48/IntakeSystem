package com.student_online.IntakeSystem;

import com.student_online.IntakeSystem.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IntakeSystemApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	void testJwt(){
		String obj = "hello world";
		String token = JwtUtil.generate(obj);
		System.out.println(token);
		JwtUtil.CLAIMS claims = JwtUtil.getClaims(token);
		System.out.println(claims.studentNumber);
	}

}

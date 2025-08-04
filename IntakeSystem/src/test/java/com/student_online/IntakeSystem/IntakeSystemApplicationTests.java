package com.student_online.IntakeSystem;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.pljo.StationTree;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.utils.JwtUtil;
import com.student_online.IntakeSystem.utils.StationUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class IntakeSystemApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	void stationtest(){
		List<Station> stations = MAPPER.station.getChildren(1);
		System.out.println(stations.size());
	}

}

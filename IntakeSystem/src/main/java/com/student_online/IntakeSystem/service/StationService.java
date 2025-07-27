package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    @Autowired
    private StationMapper stationMapper;

    public ResponseEntity<Result> createStation(@NotNull Station station) {
        String name=station.getName();
        if(stationMapper.getStationByName(name)!=null)return ResponseUtil.build(Result.error(409,"该模块已存在"));
        else{
            stationMapper.createStation(station);
            return ResponseUtil.build(Result.ok());
        }

    }

    public ResponseEntity<Result> updateStation(@NotNull Station station) {
        stationMapper.updateStation(station);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> deleteStation( int stationId) {
        try {
            stationMapper.deleteStationById(stationId);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(404, "模块不存在"));
        }
    }

    public ResponseEntity<Result> getStationById( int stationId) {
        Station station=stationMapper.getStationById(stationId);
        if(station==null)return ResponseUtil.build(Result.error(404,"未找到该模块"));
        else return ResponseUtil.build(Result.success(station,"返回模块"));
    }

    public ResponseEntity<Result> getStationByName(@NotNull String name) {
        Station station=stationMapper.getStationByName(name);
        if(station==null)return ResponseUtil.build(Result.error(404,"未找到该模块"));
        else return ResponseUtil.build(Result.success(station,"返回模块"));
    }

    public ResponseEntity<Result> getStationByParentId( int pid) {
        List<Station> result=stationMapper.getStationByParentId(pid);
        if(result.isEmpty())return ResponseUtil.build(Result.error(404,"未找到它的子模块"));
        else return ResponseUtil.build(Result.success(result,"返回子模块"));
    }

}

package com.student_online.IntakeSystem.mapper;
import com.student_online.IntakeSystem.model.po.Station;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StationMapper {
    @Insert("insert into station (name,p_id,description) values (#{name},#{pId},#{description})")
    void createStation(Station station);

    @Select("select * from station where id=#{id}")
    Station getStationById(int id);

    @Select("select * from station where name=#{name}")
    Station getStationByName(String name);

    @Select("select * from station where p_id=#{pid}")
    List<Station> getStationByParentId(int pid);

    @Delete("delete from station where id=#{id}")
    void deleteStationById(int id);

    @Update("update station set name=#{name},p_id=#{p_id},description=#{description}")
    void updateStation(Station station);
}

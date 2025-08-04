package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Station;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StarMapper {
    
    @Select("""
            select * from station where id IN (\
            SELECT station_id FROM star WHERE u_id = (\
            SELECT uid FROM user WHERE username = #{username}\
            ) \
            )""")
    List<Station> getStarByUsername(String username);
    
    @Select("""
            select EXISTS(SELECT 1 FROM star WHERE u_id = (\
                SELECT uid FROM user WHERE username = #{username}\
                ) and station_id = #{stationId}\
                )
            """)
    boolean isStarred(@Param("username") String username,@Param("stationId") Integer stationId);
    
    @Insert("""
            insert into star (u_id, station_id) values (\
            (SELECT uid FROM user WHERE username = #{username}), #{stationId}\
            )""")
    void addStar(@Param("username") String username,@Param("stationId") Integer stationId);
    
    @Delete("""
            delete from star where u_id = (\
            SELECT uid from user WHERE username = #{username}\
            ) and station_id = #{stationId}
            """)
    void deleteStar(@Param("username") String username,@Param("stationId") Integer stationId);
}

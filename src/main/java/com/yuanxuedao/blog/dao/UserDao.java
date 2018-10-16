package com.yuanxuedao.blog.dao;
import com.yuanxuedao.blog.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {
    String TABLE_NAEM = " user ";
    String INSERT_FIELDS = " name, password, salt, headurl, role ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    String SELECT_USER_MSG = " name, headurl, role ";
    @Insert({"insert into",TABLE_NAEM,"(",INSERT_FIELDS,") values (#{name},#{password},#{salt},#{headurl},#{role})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insertUser(User users);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAEM,"where id=#{id}"})
    public User seletById(int id);

    @Select({"select", SELECT_USER_MSG, "from", TABLE_NAEM, "where id = #{id}"})
    public User selectUserMsgById(int id);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAEM,"where name=#{name}"})
    public User seletByName(@Param("name") String name);

    @Delete({"delete from",TABLE_NAEM,"where id=#{id}"})
    public void deleteById(int id);

}

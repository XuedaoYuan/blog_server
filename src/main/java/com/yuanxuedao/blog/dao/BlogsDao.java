package com.yuanxuedao.blog.dao;

import com.yuanxuedao.blog.pojo.Blogs;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BlogsDao {
    String TABLE_NAME = " blogs ";
    String INSERT_FIELDS = " title, createtime, content, tags, isshow ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS, ") values (#{title}, #{createtime}, #{content}, #{tags}, #{isshow})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insertBlogs(Blogs blogs);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    public Blogs selectById(int id);

    @Select({"select id, title, createtime, isshow from", TABLE_NAME})
    public List<Blogs> selectAllTitles();

    @Select({"select * from", TABLE_NAME})
    public List<Blogs> selectAll();

    @Update({"update", TABLE_NAME, "set title = #{title}, createtime = #{createtime}, content = #{content}, tags = #{tags}, isshow = #{isshow} where id = #{id}"})
    public Blogs updateById(Blogs blogs);

    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
    public void deleteById(int id);


}

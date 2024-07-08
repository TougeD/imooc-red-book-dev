package com.imooc.mapper;

import com.github.pagehelper.Page;
import com.imooc.annotation.AutoFill;
import com.imooc.enums.OperationType;
import com.imooc.pojo.Vlog;
import com.imooc.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
public interface VlogMapper {


    /**
     * 新增Vlog
     * @param vlog
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into vlog (id, vloger_id, url, cover, title, width, height, like_counts, comments_counts, is_private, created_time, updated_time) VALUE " +
            "(#{id},#{vlogerId},#{url},#{cover},#{title},#{width},#{height},#{likeCounts},#{commentsCounts},#{isPrivate},#{createdTime},#{updatedTime})")
    void add(Vlog vlog);

    /**
     * 查询vlog
     * @param map
     * @return
     */
    Page<IndexVlogVO> queryBatch(Map<String,Object> map);

    /**
     * 通过Id查询vlog
     * @param map
     * @return
     */
    List<IndexVlogVO> queryVlogDetailById(Map<String,Object> map);

    /**
     * 更新Vlog
     * @param vlog
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateByVlog(Vlog vlog);

    /**
     * 查询我的视频
     * @param vlog
     * @return
     */
    Page<Vlog> queryBatchByVlog(Vlog vlog);
}
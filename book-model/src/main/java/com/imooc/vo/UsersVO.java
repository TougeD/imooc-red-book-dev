package com.imooc.vo;

import com.imooc.pojo.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class UsersVO extends Users {

    //用户token 传递给前端
    private String userToken;
    //我关注人的总数
    private Integer myFollowsCounts;
    //我粉丝的总量
    private Integer myFansCounts;
    //喜欢我视频的总数
//    private Integer myLikedVlogCounts;
    //所有喜欢我的总数
    private Integer totalLikeMeCounts;
}

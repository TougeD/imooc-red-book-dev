package com.imooc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotNull(message = "手机号不能为空")
    @Length(min = 11,max = 11,message = "手机号长度位数不对")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;

}

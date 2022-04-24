package com.th1024.community.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author izumisakai
 * @create 2022-04-07 17:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Emp {
    private String name;
    private Integer age;
}

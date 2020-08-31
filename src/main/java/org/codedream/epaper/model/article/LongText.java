package org.codedream.epaper.model.article;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 长文本统一储存片段（用于数据库容量自动管理）
 */
@Data
@Entity
@Table(name = "long_text")
public class LongText {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 创建时间
    private Date createTime = new Date();

    // SHA512 哈希值
    private String sha512Hash;

    // 长文本内容
    @Column(columnDefinition = "LONGTEXT")
    private String text = "";

}

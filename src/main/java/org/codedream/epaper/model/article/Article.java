package org.codedream.epaper.model.article;

import lombok.Data;
import org.codedream.epaper.model.user.User;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.lang.invoke.LambdaConversionException;
import java.util.*;

/**
 * 章处理结构
 */
@Data
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 创建时间
    private Date createTime = new Date();

    // 对应的文件
    private Integer fileId = null;

    private Integer totalLength = 0;

    // 待处理的句的数目
    private Integer sentencesNumber = 0;

    // 段列表
    @ElementCollection
    private List<Integer> paragraphList = new ArrayList<>();

    // 段集合
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Paragraph> paragraphs = new HashSet<>();


    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private User user = null;

    // 是否预处理
    private boolean preprocess = false;

    // 是否深度处理
    private boolean deepProcess = false;


}

package org.codedream.epaper.model.article;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * 句处理结构
 */
@Data
@Entity
@Table(name = "sentence")
public class Sentence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 原始文本
    private String text = "";

    // 文本哈希值(用于缓存)
    private String sha512Hash = "";

    // 创建时间
    private Date createTime = new Date();

    // 分词表
    @ElementCollection
    private List<Integer> phraseList = new ArrayList<>();

    // 词集合
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Phrase> phrases = new HashSet<>();

    // 预处理标志位
    private boolean preprocess = false;

    // 深度处理标志位
    private boolean deepProcess = false;

    @Override
    public int hashCode(){
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence = (Sentence) o;
        return this.id.equals(sentence.id);
    }
}

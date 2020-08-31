package org.codedream.epaper.model.article;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 段处理结构（中间结构）
 */
@Data
@Entity
@Table(name = "paragraph")
public class Paragraph {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 文本
    @Column(columnDefinition = "LONGTEXT")
    private String text = "";

    // 哈希值
    private String sha512Hash;

    // 句列表
    @ElementCollection
    private List<Integer> sentenceList = new ArrayList<>();

    // 句集合
    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    private Set<Sentence> sentences = new HashSet<>();

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
        Paragraph paragraph = (Paragraph) o;
        return this.id.equals(paragraph.id);
    }

}

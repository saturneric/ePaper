package org.codedream.epaper.model.article;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 词处理结构
 */
@Data
@Entity
@Table(name = "phrase")
public class Phrase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 文本
    private String text;

    // 词性
    private String pos;

    // 词向量
    @ElementCollection
    private List<Float> vec = new ArrayList<>();

    // 语素列表
    @ElementCollection
    private List<Integer> basicPhraseList = new ArrayList<>();

    // 语素集合
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Phrase> basicPhrase = new HashSet<>();


    @Override
    public int hashCode(){
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phrase phrase = (Phrase) o;
        return this.id.equals(phrase.id);
    }

}

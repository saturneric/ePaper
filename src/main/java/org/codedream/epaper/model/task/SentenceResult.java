package org.codedream.epaper.model.task;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sentence_result")
public class SentenceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer sentenceId;

    private boolean isNeutral = false;

    private boolean isPositive = false;

    private boolean isNegative = false;

    private Float dnn;

    @ElementCollection
    private List<Float> possibilities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<CorrectionResult> correctionResults = new ArrayList<>();

    private boolean initStatus = true;

}

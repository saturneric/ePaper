package org.codedream.epaper.model.task;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class CorrectionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    Integer startPos;

    Integer length;

    String correctionText;

}

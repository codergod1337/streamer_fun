package io.github.codergod1337.streamplay.model;


import io.github.codergod1337.streamplay.enums.UsableState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawData {

    public enum DataType {
        TEXT, IMAGE, AUDIO, SYMBOL
    }


    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, length = 69)
    private String value; // z. B. „go“ oder später Bildpfad / Audio-Token

    @Enumerated(EnumType.STRING)
    private DataType type;


    private Long usageCounter;

    private UsableState usableState;

    @ManyToOne
    private User moderatedBy;


}

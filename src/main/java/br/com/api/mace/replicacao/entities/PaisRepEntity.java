package br.com.api.mace.replicacao.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pais_rep")
public class PaisRepEntity extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 10)
    private String sigla;
}

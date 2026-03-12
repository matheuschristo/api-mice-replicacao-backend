package br.com.api.mice.replicacao.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDjangoDTO {

    private Long id;
    private String nome;
    private String email;
    private Boolean ativo;
    private Long empresaId;
    private Long filialId;
    private LocalDateTime updatedAt;
}

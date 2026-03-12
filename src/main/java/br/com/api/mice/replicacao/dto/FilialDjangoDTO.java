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
public class FilialDjangoDTO {

    private Long id;
    private String nome;
    private String codigo;
    private Long empresaId;
    private Long cidadeId;
    private LocalDateTime updatedAt;
}

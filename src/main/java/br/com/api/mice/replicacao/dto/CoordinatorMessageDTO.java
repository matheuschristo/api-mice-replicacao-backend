package br.com.api.mice.replicacao.dto;

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
public class CoordinatorMessageDTO {

    private String leaderId;
    private String host;
    private Integer porta;
}

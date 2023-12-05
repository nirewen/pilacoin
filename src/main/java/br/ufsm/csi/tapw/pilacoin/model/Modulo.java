package br.ufsm.csi.tapw.pilacoin.model;

import br.ufsm.csi.tapw.pilacoin.types.AbstractSetting;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Modulo {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<AbstractSetting<?>> settings;
}

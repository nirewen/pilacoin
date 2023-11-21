package br.ufsm.csi.tapw.pilacoin.constants;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Component
public class Properties {
    @Value("${pilacoin.username}")
    public String username;
    @Value("${pilacoin.home}")
    public String home;
    public Path homePath;

    @PostConstruct
    public void init() {
        this.homePath = Paths.get(this.home).toAbsolutePath();
    }
}

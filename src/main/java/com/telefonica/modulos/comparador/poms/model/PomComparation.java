package com.telefonica.modulos.comparador.poms.model;

import java.util.Optional;

public class PomComparation {
    Optional<String> pom1Version = Optional.empty();
    Optional<String> pom2Version = Optional.empty();

    public PomComparation(Optional<String> pom1Version, Optional<String> pom2Version) {
        super();
        this.pom1Version = pom1Version;
        this.pom2Version = pom2Version;
    }

    public Optional<String> getPom1Version() {
        return pom1Version;
    }

    public void setPom1Version(Optional<String> pom1Version) {
        this.pom1Version = pom1Version;
    }

    public Optional<String> getPom2Version() {
        return pom2Version;
    }

    public void setPom2Version(Optional<String> pom2Version) {
        this.pom2Version = pom2Version;
    }


}

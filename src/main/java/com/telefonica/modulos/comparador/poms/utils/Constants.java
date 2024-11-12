package com.telefonica.modulos.comparador.poms.utils;

public class Constants {
    public static final String NAME_JSON_DEPENDENCIES = "dependencies";
    public static final String NAME_JSON_VERSION = "version";
    public static final String JSON_LIB_COCO = "coco.";
    public static final String TELEFONICA_PREFIX = "@telefonica/";
    public static final String PLANTILLA_DEPENDENCIA_POM = """
                        <dependency>
                            <groupId>${GROUP_ID}</groupId>
                            <artifactId>${ARTIFACT_ID}</artifactId>
                            <version>${VERSION}</version>
                        </dependency>\
                """;
    public static final String PLANTILLA_DEPENDENCIA_PACKAGE_JSON = "    \"@telefonica/${GROUP_ID}.${ARTIFACT_ID}\": \"${VERSION}\",";
    public static final String GROUP_TELEFONICA = "com/telefonica/";
}

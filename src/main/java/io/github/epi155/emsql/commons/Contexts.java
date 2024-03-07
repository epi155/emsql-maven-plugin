package io.github.epi155.emsql.commons;

public class Contexts {
    private Contexts() {}
    
    public static volatile MethodContext mc;
    public static volatile ClassContext cc;
    public static final int IMAX = 4;
    public static final String REQUEST = "PS";
    public static final String RESPONSE = "RS";
}

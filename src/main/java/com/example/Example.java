package com.example;

import javax.script.ScriptEngine;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

public class Example {

    private static final ThreadLocal<ScriptEngine> engines = new ThreadLocal<>();
    private static final int RUNS = 100;
    private static final int THREADS_NUMBER = 2;

    public static void main(String[] args) {
        new Example().execute();
    }

    private void execute() {
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new Thread(this::task).start();
        }
    }

    private void task() {
        ScriptEngine engine = getEngine();
        try {
            engine.eval("fr <- function(x) {   ## Rosenbrock Banana function\\n\" +\n"
                    + "    x1 <- x[1]\n"
                    + "    x2 <- x[2]\n"
                    + "    100 * (x2 - x1 * x1) ^ 2 + (1 - x1) ^ 2\n"
                    + "}");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        for (int i = 0; i < Example.RUNS; i++) {
            try {
                SEXP result = (SEXP) engine.eval("optim(c(-1.2,1), fr,  method = \"L-BFGS\")");
                System.out.println(result);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private ScriptEngine getEngine() {
        ScriptEngine engine = engines.get();
        if (engine == null) {
            RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
            engine = factory.getScriptEngine();
            engines.set(engine);
        }
        return engine;
    }
}

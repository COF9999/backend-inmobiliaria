package com.realstate.habitar.infraestructure.advicers.launch;

import java.util.function.Supplier;

public class launchExeptions {
    public static <T extends Exception> void launchExeption(Supplier<T> supplier) throws T {
        throw supplier.get();
    }
}

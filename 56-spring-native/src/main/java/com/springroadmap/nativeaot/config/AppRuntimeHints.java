package com.springroadmap.nativeaot.config;

import com.springroadmap.nativeaot.domain.Message;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(Message.class, MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}

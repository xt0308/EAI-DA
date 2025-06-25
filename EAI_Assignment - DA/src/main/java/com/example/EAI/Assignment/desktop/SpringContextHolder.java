package com.example.EAI.Assignment.desktop;

import org.springframework.context.ApplicationContext;

public class SpringContextHolder {
    private static ApplicationContext context;

    public static void setContext(ApplicationContext ctx) {
        context = ctx;
    }

    public static ApplicationContext getContext() {
        return context;
    }
} 
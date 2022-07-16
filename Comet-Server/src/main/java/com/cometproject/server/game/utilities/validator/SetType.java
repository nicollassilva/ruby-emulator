package com.cometproject.server.game.utilities.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum SetType
{
    Hr("hr"),
    Hd("hd"),
    Ch("ch"),
    Lg("lg"),
    Sh("sh"),
    Ha("ha"),
    He("he"),
    Ea("ea"),
    Fa("fa"),
    Ca("ca"),
    Wa("wa"),
    Cc("cc"),
    Cp("cp"),
    Hrb("hrb"),
    Bd("bd"),
    Ey("ey"),
    Fc("fc"),
    Lh("lh"),
    Rh("rh"),
    Ls("ls"),
    Rs("rs"),
    Lc("lc"),
    Rc("rc"),
    ;

    private final String code;

    SetType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    private static final Logger log = LogManager.getLogger(SetType.class.getName());
    private static final Set<String> enums = new HashSet<>(values().length);
    static {
        for (final SetType type : SetType.values()) {
            if(enums.contains(type.code)) {
                log.error("Duplicated setType code: '{}'", type.code);
            }else {
                enums.add(type.code);
            }
        }
    }


    public static SetType fromString(String code) throws Exception {
        for (final SetType type : values()) {
            if(type.code.equals(code))
                return type;
        }

        throw new Exception("invalid code: " + code);
    }
}
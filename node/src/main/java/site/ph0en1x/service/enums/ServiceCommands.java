package site.ph0en1x.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String cmd;


    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }

    public static ServiceCommands fromValue(String v) {
        for (ServiceCommands c: ServiceCommands.values()) {
            if (c.cmd.equals(v)) {
                return c;
            }
        }
        return null;
    }
}

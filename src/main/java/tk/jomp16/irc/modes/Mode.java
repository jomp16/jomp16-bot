package tk.jomp16.irc.modes;

public enum Mode {
    NORMAL(""),
    VOICE("+v"),
    OP("+o"),
    DEVOICE("-v"),
    DEOP("-o");
    private String mode;

    private Mode(String s) {
        this.mode = s;
    }

    public static Mode getMode(String mode) {
        for (Mode modes : values()) {
            if (modes.mode.equals(mode)) {
                return modes;
            }
        }

        return NORMAL;
    }
}
package uos.capstone.dms.domain.user;

public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String type;

    Role(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

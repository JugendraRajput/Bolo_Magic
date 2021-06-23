package bolomagic.in.AdaptorAndParse;

public class ReferParse {
    private final String referDate;
    private final String friendName;
    private final String friendUID;

    public ReferParse(String referDate, String friendName, String friendUID) {
        this.referDate = referDate;
        this.friendName = friendName;
        this.friendUID = friendUID;
    }

    public String getReferDate() {
        return referDate;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendUID() {
        return friendUID;
    }
}
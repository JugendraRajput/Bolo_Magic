package bolomagic.in.AdaptorAndParse;

public class QuizListParse {
    public String quizID;
    public String quizType;
    public String quizName;
    public String entryFee;
    public String quizStartTime;
    public String quizEndTime;
    public String totalJoined;
    public String maxJoined;
    public String isJoined;
    public String minimumJoin;

    public QuizListParse(String quizID, String quizType, String quizName, String entryFee, String quizStartTime, String quizEndTime, String totalJoined, String maxJoined, String isJoined, String minimumJoin) {
        this.quizID = quizID;
        this.quizType = quizType;
        this.quizName = quizName;
        this.entryFee = entryFee;
        this.quizStartTime = quizStartTime;
        this.quizEndTime = quizEndTime;
        this.totalJoined = totalJoined;
        this.maxJoined = maxJoined;
        this.isJoined = isJoined;
        this.minimumJoin = minimumJoin;
    }

    public String getQuizID() {
        return quizID;
    }

    public String getQuizType() {
        return quizType;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public String getQuizStartTime() {
        return quizStartTime;
    }

    public String getQuizEndTime() {
        return quizEndTime;
    }

    public String getTotalJoined() {
        return totalJoined;
    }

    public String getMaxJoined() {
        return maxJoined;
    }

    public String getIsJoined() {
        return isJoined;
    }

    public String getMinimumJoin() {
        return minimumJoin;
    }

    public void setTotalJoined(String totalJoined) {
        this.totalJoined = totalJoined;
    }

    public void setIsJoined(String isJoined) {
        this.isJoined = isJoined;
    }
}
